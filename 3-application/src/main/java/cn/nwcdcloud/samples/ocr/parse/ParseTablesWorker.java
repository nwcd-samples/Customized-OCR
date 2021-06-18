package cn.nwcdcloud.samples.ocr.parse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.nwcdcloud.samples.ocr.parse.ParseFactory.Cell;


import java.text.DecimalFormat;
import java.util.*;

import static cn.nwcdcloud.samples.ocr.parse.ConfigConstants.DEBUG_PARSE_TABLE;

public class ParseTablesWorker {

    private final Logger logger = LoggerFactory.getLogger(ParseTablesWorker.class);
    private DefaultValueConfig mDefaultConfig ;
    private DecimalFormat mDecimalFormat = new DecimalFormat("#0.000");

    public ParseTablesWorker(Map<String, ?> rootConfig) {
        this.mDefaultConfig = new DefaultValueConfig((Map<String, ?>)rootConfig.get("DefaultValue"));
    }

    /**
     * 处理表格元素
     */
    public JSONObject parse(HashMap rootMap, List<JSONObject> blockItemList) {

        if(DEBUG_PARSE_TABLE){
            logger.debug("");
            logger.debug("【Table 查找】 【{}】\nconfig配置: {}", rootMap.get("Name"), rootMap);
        }
        //step 0. 找到用来定位的列元素 最少有一个， 可以有多个， 不建议太多，太多以后， 出错的可能会比较大， 会有匹配不到的问题。
        List<JSONObject> locationColumnList = findTableHeadColumns(rootMap, blockItemList);
        if (locationColumnList == null || locationColumnList.size() == 0) {
            logger.debug(" 【finish】没有找到表头定位的元素   请检查配置文件， 或者关键字配置");
            return null;
        }

        //step 1. 查找表头元素
        List<JSONObject> columnBlockItemList = findColumnBlockItem(rootMap, blockItemList, locationColumnList);

        //step 2. 调整列元素的左右分割区间
        adjustColumnBlockItemMargin(columnBlockItemList, blockItemList);

        //step 3. 通过主列 往下迭代找元素， 找到行划分。
        int mainColumnIndex = ParseUtils.findMainColumnIndex(columnBlockItemList);

        if(DEBUG_PARSE_TABLE) {
            logger.debug("【5. 查找到主列】：主列index={}   Text [{}] ", mainColumnIndex, columnBlockItemList.get(mainColumnIndex).getString("text"));
        }
        List<JSONObject> rowList = findRowSplitByMainColumn( rootMap, blockItemList, columnBlockItemList.get(mainColumnIndex));

        //step 4. 所有列通过行划分， 找到对应元素，

        int rowCount = rowList.size();
        int columnCount = columnBlockItemList.size();
        List<JSONArray> resultList = findCellByColumnAndRow(blockItemList, rowList, columnBlockItemList);

        JSONArray headTitleArray = new JSONArray();
        for(JSONObject item : columnBlockItemList){
            // displayColumnName 列的显示名称和 key 不一定保持一致
            headTitleArray.add(item.getString("displayColumnName"));
        }
        //step 5. FIXME: 判断行结尾的情况,设置结束的关键字。

        JSONObject resultObject = new JSONObject();
        resultObject.put("name", rootMap.get("Name").toString());
        resultObject.put("rowCount", rowCount);
        resultObject.put("columnCount", columnCount);
        resultObject.put("rowList", resultList);
        resultObject.put("heads", headTitleArray);
        return resultObject;
    }

    /**
     * 寻找表头元素, 会判断表头的坐标范围， 一个页面里面， 可能会有多个重复的表格出现。
     * @param rootMap
     * @param blockItemList
     * @return
     */
    private List<JSONObject> findColumnBlockItem(HashMap rootMap, List<JSONObject> blockItemList, List<JSONObject> locationColumnList) {

        //设置一个最大值
        double topBorder = 1.0;
        double bottomBorder = 0;

        for(int i=0; i< locationColumnList.size(); i++){
            JSONObject item = locationColumnList.get(i);
            double yMin = item.getDouble("yMin");
            double yMax = item.getDouble("yMax");
            if(yMin < topBorder){
                topBorder = yMin;
            }
            if(yMax > bottomBorder){
                bottomBorder = yMax;
            }
        }


        List<JSONObject> columnBlockItemList = new ArrayList<>();
        if(!rootMap.containsKey("Columns")){
            throw new IllegalArgumentException(" Table 类型， 需要配置  'Columns' 元素");
        }

        List columnList = (ArrayList) rootMap.getOrDefault("Columns", new ArrayList<>());
        if(columnList == null || columnList.size() == 0){
            throw new IllegalArgumentException(" Table 类型， 需要配置  'Columns' 元素");
        }
        for (Object item : columnList) {
            HashMap configMap = (HashMap) item;
            if(!configMap.containsKey("ColumnName")){
                throw new IllegalArgumentException("需要设置 ColumnsName ");
            }

            List<String> keyList = (List) configMap.getOrDefault("KeyWordList", new ArrayList<>());

            String displayColumnName = configMap.get("ColumnName").toString();
            boolean isMainColumn = (boolean) configMap.getOrDefault("MainColumn", false);

            keyList.add(displayColumnName);
            JSONObject blockItem = findColumnByKeyList(configMap, blockItemList, keyList, topBorder, bottomBorder);
            if(blockItem != null){
                blockItem.put("displayColumnName", displayColumnName);
                blockItem.put("config", configMap);
                blockItem.put("isMainColumn", isMainColumn);
                columnBlockItemList.add(blockItem);
            }
        }
        if(DEBUG_PARSE_TABLE) {
            for (int i=0; i<columnBlockItemList.size(); i++) {

                logger.debug("【3.{} 找到表头元素】 ColumnName=[{}] {} ", i+1, columnBlockItemList.get(i).getString("displayColumnName"),
                        BlockItemUtils.generateBlockItemString(columnBlockItemList.get(i)));
            }
        }

        return columnBlockItemList;
    }


    /**
     * 寻找两个用于定位的表头元素。
     * @param rootMap
     * @param blockItemList
     * @return
     */
    private List<JSONObject> findTableHeadColumns(HashMap rootMap, List<JSONObject> blockItemList) {


        List columnList = (ArrayList) rootMap.get("Columns");
        List<JSONObject> locationColumnList = new ArrayList();
        for (Object item : columnList) {
            HashMap configMap = (HashMap) item;

            if((boolean)configMap.getOrDefault("Location", false)){

               List keyWordList = (List) configMap.getOrDefault("KeyWordList", new ArrayList<>());
               keyWordList.add(configMap.get("ColumnName").toString());
                //keyword 名称进行排重操作
               Set<String> keySet = new HashSet<>(keyWordList);
               keyWordList = new ArrayList<>(keySet);


               JSONObject object = new JSONObject();
               object.put("keyWordList", keyWordList);
               object.put("config", configMap);
               locationColumnList.add(object);
            }

        }


        if (locationColumnList == null  || locationColumnList.size() ==0 ) {
            throw new IllegalArgumentException("没有配置  'Location ' 用来定位表头, 请检查配置文件 ");
        }
        if(DEBUG_PARSE_TABLE) {

            for(int i=0; i< locationColumnList.size(); i++){
                logger.debug("【1.{} 用来定位的表头配置信息】 {} ", i+1 , locationColumnList.get(i));
            }

        }

        return findTableByKeys(blockItemList, locationColumnList);
    }


    /**
     * 通过两个关键字进行元素的查找。
     *
     * @param blockItemList
     * @param locationColumnList
     * @return
     */
    private List<JSONObject> findTableByKeys(List<JSONObject> blockItemList, List<JSONObject> locationColumnList) {


        List<JSONObject> resultItemList = new ArrayList<>();
        for(JSONObject locationColumn : locationColumnList){
            List<String> keyWords = (List<String>) locationColumn.get("keyWordList");
            HashMap config = (HashMap) locationColumn.get("config");
            boolean findFlag = false;  // 找到一个符合要求的元素 就退出循环， 关键字和位置都符合要求。
            for(String keyWord: keyWords){
                keyWord = keyWord.replaceAll(" ", "");
                for (int i = 0; i < blockItemList.size(); i++) {
                    JSONObject tempBlockItem = blockItemList.get(i);
                    String text = tempBlockItem.getString("text").trim();
                    //key 比较去除掉一些特殊字符
                    if(BlockItemUtils.compareString(keyWord, text)){
                        //Column 列头元素范围检测
                        if(BlockItemUtils.isValidRange(mDefaultConfig, config, tempBlockItem)){
                            resultItemList.add(tempBlockItem);
                            findFlag = true;
//                            logger.debug("找到列头定位元素 【{}】   {} ", tempBlockItem.getString("text"), BlockItemUtils.generateBlockItemString(tempBlockItem) );
                        }else{
//                            logger.warn("表头元素[{}]坐标范围不正确， 请检查XRangeMin ... YRangeMin  等参数配置。 {} ", tempBlockItem.getString("text"), config );
                        }
                    }
                }
                if(findFlag){
                    continue;
                }
            }
            if(findFlag){
                continue;
            }
        }

        if(DEBUG_PARSE_TABLE) {
            for (int i = 0; i < resultItemList.size(); i++) {
                logger.debug("【2.{} 找到用于定位表头的元素】 {} ", i + 1, BlockItemUtils.generateBlockItemString(resultItemList.get(i)));
            }
        }
        return resultItemList;
    }

    /**
     * 根据key top bottom 查找元素
     * @param blockItemList
     * @param keyList
     * @param top
     * @param bottom
     * @return
     */
    private JSONObject findColumnByKeyList(HashMap configMap, List<JSONObject> blockItemList, List<String> keyList, double top, double bottom) {
        for (int i = 0; i < blockItemList.size(); i++) {
            JSONObject tempBlockItem = blockItemList.get(i);
            String text = tempBlockItem.getString("text").trim();
            for (String key: keyList){

                if (BlockItemUtils.compareString(key, text) && tempBlockItem.getDouble("yMin") >= top - 0.05
                        && tempBlockItem.getDouble("yMax") < bottom + 0.05) {

                    //检测元素坐标范围 , 同一个关键字  可能出现在多个表格中。
                    if(BlockItemUtils.isValidRange(mDefaultConfig, configMap, tempBlockItem)){
                        return tempBlockItem;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 重新设置元素的左右边界
     * @param columnBlockItemList
     */
    private void adjustColumnBlockItemMargin(List<JSONObject> columnBlockItemList, List<JSONObject> blockItemList){
        // 对元素从左到右排序
        columnBlockItemList.sort(new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject jsonObject, JSONObject t1) {
                return jsonObject.getInteger("left") - t1.getInteger("left")  ;
            }
        });


        for(int i=0 ; i<columnBlockItemList.size(); i++ ){
            JSONObject currentItem = columnBlockItemList.get(i);

            double leftColumnRight = 0;
            double rightColumnLeft = 0;

            if(i ==0 ){
                leftColumnRight = 0;
            }else{
                JSONObject leftItem = columnBlockItemList.get(i-1);
                leftColumnRight = leftItem.getDouble("xMax");
            }

            if (i== columnBlockItemList.size()-1){
                rightColumnLeft = BlockItemUtils.findRightMostPoz(blockItemList);
            }else {
                JSONObject rightItem = columnBlockItemList.get(i+1);
                rightColumnLeft = rightItem.getDouble("xMin");
            }
            adjustSingleItem( leftColumnRight, rightColumnLeft, currentItem);
        }
    }

    /**
     * 根据设置的属性  near、middle、 far 三个属性， 进行列的划分。
     *
     * MarginLeftType 说明:
     *  near:  以Column 左边作为列的划分                范围最近
     *  middle:  以Column 到下一个Column 中点          中间范围
     *  far:  以Column 左边Column元素的右边作为分界点    范围最远
     *
     * MarginRightType 说明
     *   near:  以Column 右边作为列的划分               范围最近
     *   middle:  以Column 到下一个Column 中点         中间范围
     *   far:  以Column 右边Column元素的左边作为分界点    范围最远
     *
     * @param leftColumnRight  左边元素的右边界
     * @param rightColumnLeft  右边元素的左边界
     * @param currentItem      当前元素
     */
    private void adjustSingleItem(double leftColumnRight, double rightColumnLeft, JSONObject currentItem){
        HashMap infoMap = (HashMap) currentItem.get("config");

        String marginLeftType = mDefaultConfig.getTableColumnValue(infoMap, "MarginLeftType" , ConfigConstants.TABLE_MARGIN_TYPE_MIDDLE).toString();
        String marginRightType = mDefaultConfig.getTableColumnValue(infoMap, "MarginRightType" , ConfigConstants.TABLE_MARGIN_TYPE_MIDDLE).toString();

        float moveLeftRatio = Float.valueOf(mDefaultConfig.getTableColumnValue(infoMap, "MoveLeftRatio", ConfigConstants.TABLE_DEFAULT_MARGIN_LEFT_RATIO).toString());
        float moveRightRatio = Float.valueOf(mDefaultConfig.getTableColumnValue(infoMap, "MoveRightRatio", ConfigConstants.TABLE_DEFAULT_MARGIN_RIGHT_RATIO).toString());


        double xMinBorder = 0;
        if(ConfigConstants.TABLE_MARGIN_TYPE_NEAR.equals(marginLeftType)){
            xMinBorder = currentItem.getDouble("xMin");
            xMinBorder -= ConfigConstants.PARSE_CELL_ERROR_RANGE_MIN;  // near 情况下， 加一些冗余， 防止误差
        }else if(ConfigConstants.TABLE_MARGIN_TYPE_MIDDLE.equals(marginLeftType)){
            xMinBorder = (currentItem.getDouble("xMin") + leftColumnRight)/2 ;
        }else if(ConfigConstants.TABLE_MARGIN_TYPE_FAR.equals(marginLeftType)){
            xMinBorder = leftColumnRight ;
        }else {
            throw new IllegalArgumentException("["+infoMap.get("ColumnName")+"] marginLeftType 类型配置不正确 只能为 near, middle, far 三种类型 ");
        }

        xMinBorder += currentItem.getDouble("widthRate") * moveLeftRatio;



        double xMaxBorder = 0;
        if(ConfigConstants.TABLE_MARGIN_TYPE_NEAR.equals(marginRightType)){
            xMaxBorder = currentItem.getDouble("xMax");
            xMaxBorder += ConfigConstants.PARSE_CELL_ERROR_RANGE_MIN;  // near 情况下， 加一些冗余， 防止误差
        }else if(ConfigConstants.TABLE_MARGIN_TYPE_MIDDLE.equals(marginRightType)){
            xMaxBorder = (currentItem.getDouble("xMax") + rightColumnLeft) /2;
        }else if(ConfigConstants.TABLE_MARGIN_TYPE_FAR.equals(marginRightType)){
            xMaxBorder = rightColumnLeft;
        }else {
            throw new IllegalArgumentException("["+infoMap.get("ColumnName")+"] marginRightType 类型配置不正确 只能为 near, middle, far 三种类型 ");
        }
        xMaxBorder += currentItem.getDouble("widthRate") * moveRightRatio;

        currentItem.put("xMinBorder", xMinBorder);
        currentItem.put("xMaxBorder",xMaxBorder);

        if(DEBUG_PARSE_TABLE) {

            logger.debug("【4.列划分】width={} original [{}, {}]  border:[{}, {}]  left config:[type={}, radio={}], right config:[type={}, radio={}]  [{}] ",
                    mDecimalFormat.format(currentItem.getDouble("widthRate")),
                    mDecimalFormat.format(currentItem.getDouble("xMin")),
                    mDecimalFormat.format(currentItem.getDouble("xMax")),
                    mDecimalFormat.format(xMinBorder), mDecimalFormat.format(xMaxBorder),
                    marginLeftType, moveLeftRatio,
                    marginRightType, moveRightRatio,
                    currentItem.getString("text"));
        }

    }

    /**
     * 按照主列 找到行划分， 尽可能多地进行查找， 后面会综合其他列的情况， 进行排除。
     * @param blockItemList
     * @param mainColumnBlockItem
     */
    private List<JSONObject> findRowSplitByMainColumn(HashMap configMap, List<JSONObject> blockItemList , JSONObject mainColumnBlockItem){


        List<JSONObject> resList = new ArrayList<>();

        double yMinBorder = mainColumnBlockItem.getDouble("yMin");



        //根据主列的 top  left  right 向下查找元素
        int rowCount =0;
        int totalRowCount = 0;
        for (int i=0; i< blockItemList.size(); i++){

            JSONObject item = blockItemList.get(i);
//                    logger.debug("【8.{} 根据主列查找元素】yMin: [{}] item yMin: [{}] item[{}, {}] , range[{} , {}]  Text [{}]   ",
//                            i+1, mDecimalFormat.format(yMinBorder),
//                            mDecimalFormat.format(item.getDouble("yMin")),
//                            mDecimalFormat.format(item.getDouble("xMin")),
//                            mDecimalFormat.format(item.getDouble("xMax")),
//                            mDecimalFormat.format(xMinBorder), mDecimalFormat.format(xMaxBorder),item.getString("text")
//                    );

            if( ParseUtils.isContainItemInRow(item, mainColumnBlockItem)
                    && item.getDouble("yMin") > yMinBorder
                    && !item.getString("text").equals(mainColumnBlockItem.getString("text"))){
                totalRowCount ++;
                if(DEBUG_PARSE_TABLE){
                    logger.debug("【6.{}主列 查找行元素 】 {}", totalRowCount, BlockItemUtils.generateBlockItemString(item));
                }

                if(item.getString("id").equals(mainColumnBlockItem.getString("id"))){
                    continue;
                }
                //如果新找到的行元素和上一个行元素 高度过近， 算一行
                if(resList.size()>0 && item.getDouble("yMin")+ item.getDouble("heightRate")/2 < resList.get(resList.size()-1).getDouble("yMax")){
                    continue;
                }else if(resList.size() ==0 &&  item.getDouble("yMin") <
                        mainColumnBlockItem.getDouble("yMin") + mainColumnBlockItem.getDouble("heightRate")/2){
                    continue;
                }
                rowCount ++;
                if(DEBUG_PARSE_TABLE){
                    logger.debug("【\t6.{}  找到主列 行元素 】 {}", rowCount, BlockItemUtils.generateBlockItemString(item));
                }
                resList.add(item);
            }
        }

        resList.sort(new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject j1, JSONObject j2) {
                return j1.getInteger("y") - j2.getInteger("y");
            }
        });

        double maxRowHeightRatio = Double.parseDouble(mDefaultConfig.getKeyValue(configMap, "MaxRowHeightRatio", ConfigConstants.TABLE_MAX_ROW_HEIGHT_RATIO).toString());
        int maxRowCount = Integer.parseInt(mDefaultConfig.getKeyValue(configMap, "MaxRowCount", ConfigConstants.TABLE_MAX_ROW_COUNT).toString());


        double maxRowHeight = maxRowHeightRatio * mainColumnBlockItem.getDouble("heightRate");
        if(DEBUG_PARSE_TABLE){
            logger.debug("【7.】 最高行高度={}  待比对的行元素{}个 ", mDecimalFormat.format(maxRowHeight), resList.size());
        }

        List<JSONObject> newResList = new ArrayList<>();
        for(int i =0; i< resList.size() && i<maxRowCount; i++){
            JSONObject item = resList.get(i);

            boolean skipItemFlag = false;
            double topBorder = 0;
            // 当前行元素大于 行高的限制 ， 结束循环
            if(item.getDouble("yMax") - item.getDouble("yMin") > maxRowHeight){
                if(DEBUG_PARSE_TABLE){
                    logger.debug("【7.{}  停止循环】 最高行高度={}  当前行高={}  Text=[{}] ", i, mDecimalFormat.format(maxRowHeight),
                            item.getDouble("yMax") - item.getDouble("yMin"), item.getString("text"));
                }
                break;
            }
            if(i ==0){

                if(item.getDouble("yMin") > mainColumnBlockItem.getDouble("yMax") + maxRowHeight ){
                    break;
                }
                topBorder = (item.getDouble("yMin") < mainColumnBlockItem.getDouble("yMax")
                        ? item.getDouble("yMin")
                        : mainColumnBlockItem.getDouble("yMax"));
            }else {
                // 该block 与上一行的block 求中点 ， 再往上移动若干像素， 控制误差。
                topBorder = (resList.get(i-1).getDouble("yMax") + item.getDouble("yMin"))/2 - 0.001  ;
            }

            double bottomBorder = 0;
            if(i < resList.size()-1){
                //如果距离下一行高度差过大, 停止循环
                if(item.getDouble("yMax")  + maxRowHeight < resList.get(i+1).getDouble("yMin") ){
                    if(DEBUG_PARSE_TABLE){
                        logger.debug("【7.{}停止循环 下一个行元素距离过远】yMax={} maxRowHeight={}  下一个元素yMin={}    [{}]--Next[{}] ",
                                i,
                                mDecimalFormat.format(item.getDouble("yMax")),
                                mDecimalFormat.format(maxRowHeight),
                                mDecimalFormat.format(resList.get(i+1).getDouble("yMin")),
                                item.getString("text"),
                                resList.get(i+1).getString("text")
                        );
                    }

                    bottomBorder =  item.getDouble("yMin")  + maxRowHeight;
                    skipItemFlag = true;
                }else{
                    bottomBorder = (resList.get(i+1).getDouble("yMin") + item.getDouble("yMax"))/2 + 0.005;

                }
            }

            item.put("topBorder", topBorder);
            item.put("bottomBorder", bottomBorder);
            newResList.add(item);
            if(skipItemFlag){
                break;//下一行过远 停止循环
            }

        }
        if(newResList.size()>0){

            JSONObject item = newResList.get(newResList.size() -1);
            Double bottomBorder = item.getDouble("yMax") + item.getDouble("heightRate") + 0.001;
            item.put("bottomBorder", bottomBorder);
        }


        if(DEBUG_PARSE_TABLE) {
            for (int i=0; i< newResList.size(); i++) {
                JSONObject item  =  newResList.get(i);
                logger.debug("【8.{} 行划分】 Border [yMin={}, yMax={}] self[yMin={}, yMax={}]  [{}] ",
                        i+1,
                        mDecimalFormat.format(item.getDouble("topBorder")), mDecimalFormat.format(item.getDouble("bottomBorder")),
                        mDecimalFormat.format(item.getDouble("yMin")), mDecimalFormat.format(item.getDouble("yMax")),
                        item.getString("text") );
            }
        }
        return newResList;
    }

    /**
     * 通过行划分和列划分， 查找单元格。
     * @param blockItemList
     * @param rowList
     * @param columnList
     * @return
     */
    private List<JSONArray> findCellByColumnAndRow(List<JSONObject> blockItemList,
                                List<JSONObject> rowList, List<JSONObject> columnList){


        int rowCount = rowList.size();
        int columnCount = columnList.size();
        Cell[][] tableArray = new Cell[rowCount][columnCount];

        for(int i=0; i<rowList.size(); i++){
            JSONObject rowItem = rowList.get(i);
            double top = rowItem.getDouble("topBorder") - 0.01;
            double bottom = rowItem.getDouble("bottomBorder") + 0.01;
            for (int j=0; j< columnList.size(); j ++ ){

                Cell cell = tableArray[i][j];
                if(cell == null){
                    cell = new Cell();
                }
                tableArray[i][j] = cell;

                JSONObject columnItem = columnList.get(j);
                double left = columnItem.getDouble("xMinBorder");
                double right = columnItem.getDouble("xMaxBorder");


                if(DEBUG_PARSE_TABLE) {
                    logger.debug("【9.{} 第{}行 cell取值范围】\t[yMin={}, yMax={}, xMin={}, xMax={}]  [row={}, column={}]   列名：[{}]",
                           i+1,i+1,
                            mDecimalFormat.format(top), mDecimalFormat.format(bottom),
                            mDecimalFormat.format(left),mDecimalFormat.format(right),
                            i, j, columnItem.getString("text"));
                }
                List<JSONObject> cellList = new ArrayList<>();
                for(JSONObject item: blockItemList){

                    if( ParseUtils.isContainItemInRow(item, columnItem)  && item.getDouble("yMin")>= top &&
                       item.getDouble("yMax")<= bottom ){
                        cellList.add(item);
                        if(DEBUG_PARSE_TABLE ){
                            logger.debug("\t【找到第{}元素】\t{} ",
                                    cellList.size(), BlockItemUtils.generateBlockItemString(item));
                        }


                        if(item.getFloat("Confidence") < cell.confidence){
                            cell.confidence = item.getFloat("Confidence");
                        }
                    }
                }//End for

                //Sort list
                cellList.sort(new BlockItemComparator(ConfigConstants.COMPARE_HEIGHT_RATE));

                for (JSONObject item: cellList){
                    cell.text += (" " + item.getString("text"));
                }
                // 根据设置的格式，进行字符串处理
                JSONObject configMap = columnItem.getJSONObject("config");
                String valueType = configMap.getString("ValueType");
                //如果多个元素连起来， 取到 X 的偏移值， 从左边取元素， 还是从右边取元素，
                int direction = ParseUtils.checkParseCellValueDirection(cellList, columnItem);
                cell.text = ParseUtils.processBlockValue(valueType, cell.text, direction);
            }
        }

        List<JSONArray> resList = new ArrayList<>();
        if(DEBUG_PARSE_TABLE ){
            System.out.println("---------------------------------------------------------------------------------------------------");

            for(int i=0; i<columnList.size(); i++ ){
                System.out.printf("| %20s ",columnList.get(i).getString("displayColumnName"));
            }
            System.out.println("");
        }

        for (int i=0; i< rowCount; i++){
            JSONArray rowArray =  new JSONArray();


            for(int j=0; j< columnCount; j++){
                JSONObject object = new JSONObject();
                String text  = tableArray[i][j].text;
                if(text== null){
                    text = "";
                }else{
                    text = text.trim();
                }
                object.put("text", text);
                object.put("confidence", tableArray[i][j].confidence);
                if(DEBUG_PARSE_TABLE && logger.isDebugEnabled()) {
                    System.out.printf("| %20s ", tableArray[i][j].text);
                }
                rowArray.add(object);
            }
            resList.add(rowArray);
            if(DEBUG_PARSE_TABLE ){
                System.out.println("  ");
            }
        }

        if(DEBUG_PARSE_TABLE ){
            System.out.println("---------------------------------------------------------------------------------------------------");
        }

        return resList;

    }

}