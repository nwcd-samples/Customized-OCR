package cn.nwcdcloud.samples.ocr.parse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.nwcdcloud.samples.ocr.parse.ParseFactory.Cell;

import java.util.*;

public class ParseTablesWorker {

    private final Logger logger = LoggerFactory.getLogger(ParseTablesWorker.class);
    private int pageWidth;
    private int pageHeight;

    public ParseTablesWorker(int pageWidth, int pageHeight) {
        this.pageWidth = pageWidth;
        this.pageHeight = pageHeight;
    }

    /**
     * 处理表格元素
     */
    public JSONObject parse(HashMap rootMap, List<JSONObject> blockItemList) {

        //step 0. 找到用来定位的两个列元素  Start  End
        List<JSONObject> locationColumnList = findTableHeadColumns(rootMap, blockItemList);
        if (locationColumnList == null || locationColumnList.size() == 0) {
            logger.warn(" 没有找到表头定位的元素   ");
            if(locationColumnList != null ){
                logger.warn("  表头定位元素 元素个数 ： {} ", locationColumnList.size());
            }
            return null;
        }

        //FIXME:  还需要考虑一个页面里面有多个相同表格的情况
        //step 1. 查找表头元素
        List<JSONObject> columnBlockItemList = findColumnBlockItem(rootMap, blockItemList, locationColumnList);

        //step 2. 调整列元素的左右分割区间
        adjustColumnBlockItemMargin(columnBlockItemList, blockItemList);

        //step 3. 通过主列 往下迭代找元素， 找到行划分。
        int mainColumnIndex = findMainColumnIndex(columnBlockItemList);

        if(mainColumnIndex >= columnBlockItemList.size()){
            throw new IllegalArgumentException(" 主列设置错误， 最大列数为"+columnBlockItemList.size()+" , 设置的值= "+ mainColumnIndex);
        }
        logger.info("查找到主列： {}   Text [{}] ", mainColumnIndex,  columnBlockItemList.get(mainColumnIndex).getString("text"));

        List<JSONObject> rowList = findRowSplitByMainColumn( rootMap, blockItemList, columnBlockItemList.get(mainColumnIndex));

        //step 3. 所有列通过行划分， 找到对应元素，

        int rowCount = rowList.size();
        int columnCount = columnBlockItemList.size();
        List<JSONArray> resultList = findCellByColumnAndRow(blockItemList, rowList, columnBlockItemList);

        JSONArray headTitleArray = new JSONArray();
        for(JSONObject item : columnBlockItemList){
            headTitleArray.add(item.getString("text"));
        }
        //step 4. FIXME: 判断行结尾的情况, 设置每个cell 是否能为空，然后同一行的Cell 一起进行判断， 符合标准的才算是一行。

        JSONObject resultObject = new JSONObject();
        resultObject.put("name", rootMap.get("Name").toString());
        resultObject.put("rowCount", rowCount);
        resultObject.put("columnCount", columnCount);
        resultObject.put("rowList", resultList);
        resultObject.put("heads", headTitleArray);
        return resultObject;
    }

    /**
     * 寻找表头元素
     * @param rootMap
     * @param blockItemList
     * @return
     */
    private List<JSONObject> findColumnBlockItem(HashMap rootMap, List<JSONObject> blockItemList, List<JSONObject> locationColumnList) {

        int topBorder = this.pageHeight;
        int bottomBorder = 0;

        for(int i=0; i< locationColumnList.size(); i++){
            JSONObject item = locationColumnList.get(i);
            int top = item.getInteger("top");
            int bottom = item.getInteger("bottom");
            if(top < topBorder){
                topBorder = top;
            }
            if(bottom > bottomBorder){
                bottomBorder = bottom;
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
            keyList.add(configMap.get("ColumnName").toString());
            JSONObject blockItem = findColumnByKeyList(blockItemList, keyList, topBorder, bottomBorder);
            if(blockItem != null){
//                    BlockItemUtils.isValidRange
                if(!BlockItemUtils.isValidRange(configMap, blockItem, this.pageWidth, this.pageWidth)){
                    logger.warn("表头元素[{}]坐标范围不正确， 请检查XRangeMin ... YRangeMin  等参数配置。 {} ", blockItem.getString("text"), configMap );
                    continue;
                }
                blockItem.put("config", configMap);
                columnBlockItemList.add(blockItem);
            }else{
                logger.warn("没有找到表头元素  : "+ configMap );
                for(String  key: keyList){
                    logger.warn("\t 未找到 【{}】 ",  key  );
                }
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

            if((boolean)configMap.getOrDefault("Location", false) == true){

               List<String> keyWordList = (List) configMap.getOrDefault("KeyWordList", new ArrayList<>());
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


        if (locationColumnList == null  ) {
            throw new IllegalArgumentException("没有配置  'Location ' 用来定位表头, 请检查配置文件 ");
        }

        if (locationColumnList.size() < 1 ) {
            throw new IllegalArgumentException("请检查 'Column '配置属性 'Location'个数， 至少为两个");
        }


        //FIXME: 验证 高度有效性

        return findTableByKeys(blockItemList, locationColumnList);
    }


    /**
     * 通过两个关键字进行元素的查找。
     *
     * @param blockItemList
     * @return
     */
    private List<JSONObject> findTableByKeys(List<JSONObject> blockItemList, List<JSONObject> locationColumnList) {


        List<JSONObject> resultItemList = new ArrayList<>();
        for(JSONObject locationColumn : locationColumnList){
            List<String> keyWords = (List<String>) locationColumn.get("keyWordList");
            HashMap config = (HashMap) locationColumn.get("config");
            boolean findFlag = false;
            for(String keyWord: keyWords){
                keyWord = keyWord.replaceAll(" ", "");
                for (int i = 0; i < blockItemList.size(); i++) {
                    JSONObject tempBlockItem = blockItemList.get(i);
                    String text = tempBlockItem.getString("text").trim();
                    if (keyWord.equals(text.replaceAll(" ", ""))) {

                        //检查范围
                        if(BlockItemUtils.isValidRange(config, tempBlockItem, this.pageWidth,  this.pageHeight)){
                            resultItemList.add(tempBlockItem);
                            findFlag = true;
                        }else{
                            logger.warn(" 位置检测 不正确 , 请检查配置  XRangeMin  YRangeMin...");
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
        logger.debug(" 定位元素size = {} ", resultItemList.size());
        for(JSONObject item : resultItemList){
            logger.info("定位元素  {} ", item.toJSONString());

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
    private JSONObject findColumnByKeyList(List<JSONObject> blockItemList, List<String> keyList, int top, int bottom) {
        for (int i = 0; i < blockItemList.size(); i++) {
            JSONObject tempBlockItem = blockItemList.get(i);
            String text = tempBlockItem.getString("text").trim();
            for (String key: keyList){
                key = key.replaceAll(" ", "");
                if (key.equals(text.replaceAll(" ","")) && tempBlockItem.getInteger("top") >= top - ConfigConstants.PARSE_CELL_ERROR_RANGE_MAX
                        && tempBlockItem.getInteger("bottom") < bottom + ConfigConstants.PARSE_CELL_ERROR_RANGE_MAX) {
                    return tempBlockItem;
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

            int leftColumnRight = 0;
            int rightColumnLeft = 0;

            if(i ==0 ){
                leftColumnRight = 0;
            }else{
                JSONObject leftItem = columnBlockItemList.get(i-1);
                leftColumnRight = leftItem.getInteger("right");
            }

            if (i== columnBlockItemList.size()-1){
                rightColumnLeft = BlockItemUtils.findRightMostPoz(blockItemList);
            }else {
                JSONObject rightItem = columnBlockItemList.get(i+1);
                rightColumnLeft = rightItem.getInteger("left");
            }
            adjustSingleItem( leftColumnRight, rightColumnLeft, currentItem);
        }
    }

    private void adjustSingleItem(int leftColumnRight, int rightColumnLeft, JSONObject currentItem){
        HashMap infoMap = (HashMap) currentItem.get("config");

        String marginLeftType = infoMap.getOrDefault("MarginLeftType", ConfigConstants.TABLE_MARGIN_TYPE_MIDDLE).toString();
        String marginRightType = infoMap.getOrDefault("MarginRightType", ConfigConstants.TABLE_MARGIN_TYPE_MIDDLE).toString();

        float moveLeftRatio = Float.parseFloat(infoMap.getOrDefault("MoveLeftRatio", ConfigConstants.TABLE_DEFAULT_MARGIN_LEFT_RATIO).toString());
        float moveRightRatio = Float.parseFloat(infoMap.getOrDefault("MoveRightRatio", ConfigConstants.TABLE_DEFAULT_MARGIN_RIGHT_RATIO).toString());

//        # MarginLeftType 说明
//        # near:  以Column 左边作为列的划分                 范围最近
//        # middle:  以Column 到下一个Column 中点             中间范围
//        # far:  以Column 左边Column元素的右边作为分界点    范围最远

        int leftBorder = 0;
        if(ConfigConstants.TABLE_MARGIN_TYPE_NEAR.equals(marginLeftType)){
            leftBorder = currentItem.getInteger("left");
        }else if(ConfigConstants.TABLE_MARGIN_TYPE_MIDDLE.equals(marginLeftType)){
            leftBorder = (currentItem.getInteger("left") + leftColumnRight)/2 ;
        }else if(ConfigConstants.TABLE_MARGIN_TYPE_FAR.equals(marginLeftType)){
            leftBorder = leftColumnRight ;
        }else {
            throw new IllegalArgumentException("["+infoMap.get("ColumnName")+"] marginLeftType 类型配置不正确 只能为 near, middle, far 三种类型 ");
        }

        leftBorder += currentItem.getInteger("width") * moveLeftRatio;

//        # MarginRightType 说明
//        # near:  以Column 右边作为列的划分                 范围最近
//        # middle:  以Column 到下一个Column 中点             中间范围
//        # far:  以Column 右边Column元素的左边作为分界点    范围最远

        int rightBorder = 0;
        if(ConfigConstants.TABLE_MARGIN_TYPE_NEAR.equals(marginRightType)){
            rightBorder = currentItem.getInteger("right");
        }else if(ConfigConstants.TABLE_MARGIN_TYPE_MIDDLE.equals(marginRightType)){
            rightBorder = (currentItem.getInteger("right") + rightColumnLeft) /2;
        }else if(ConfigConstants.TABLE_MARGIN_TYPE_FAR.equals(marginRightType)){
            rightBorder = rightColumnLeft;
        }else {
            throw new IllegalArgumentException("["+infoMap.get("ColumnName")+"] marginRightType 类型配置不正确 只能为 near, middle, far 三种类型 ");
        }
        rightBorder += currentItem.getInteger("width") * moveRightRatio;

        currentItem.put("leftBorder", leftBorder);
        currentItem.put("rightBorder",rightBorder);
        logger.debug("【DEBUG】列划分 {} :  width={} original [{}, {}]  border:[{}, {}]  left config:[type={}, radio={}], right config:[type={}, radio={}]  ",
                currentItem.getString("text"),
                currentItem.getInteger("width"),
                currentItem.getInteger("left"),
                currentItem.getInteger("right"),
                leftBorder, rightBorder,
                marginLeftType, moveLeftRatio,
                marginRightType, moveRightRatio);


    }

    /**
     * 按照主列 找到行划分， 尽可能多地进行查找， 后面会综合其他列的情况， 进行排除。
     * @param blockItemList
     * @param mainColumnBlockItem
     */
    private List<JSONObject> findRowSplitByMainColumn(HashMap configMap, List<JSONObject> blockItemList , JSONObject mainColumnBlockItem){


        List<JSONObject> resList = new ArrayList<>();
        int left = mainColumnBlockItem.getInteger("leftBorder");
        int right = mainColumnBlockItem.getInteger("rightBorder");
        int top = mainColumnBlockItem.getInteger("top");


        //根据主列的 top  left  right 向下查找元素
        for (int i=0; i< blockItemList.size(); i++){
            JSONObject item = blockItemList.get(i);
//            logger.info("### top: [{}]  item top: [{}]  [{}]", top, item.getInteger("top"), item.getString("text"));
            if(
                    item.getInteger("top") >  top
                    && item.getInteger("left")> left
                    && item.getInteger("right")< right &&
                    !item.getString("text").equals(mainColumnBlockItem.getString("text"))
                    ){
                resList.add(item);
            }
        }

        resList.sort(new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject jsonObject, JSONObject t1) {
                return jsonObject.getInteger("top") - t1.getInteger("top");
            }
        });
        // 排除掉Y值 增加过快的行。
        double maxRowHeightRatio = Double.valueOf(configMap.getOrDefault("MaxRowHeightRatio", ConfigConstants.TABLE_MAX_ROW_HEIGHT_RATIO).toString());
        int maxRowCount = Integer.valueOf(configMap.getOrDefault("MaxRowCount",  ConfigConstants.TABLE_MAX_ROW_COUNT).toString());

        int maxRowHeight = (int)maxRowHeightRatio * mainColumnBlockItem.getInteger("height");
        logger.debug("最高行高度  {}  找到行元素个数={} ", maxRowHeight, resList.size());



        List<JSONObject> newResList = new ArrayList<>();
        for(int i =0; i< resList.size() && i<maxRowCount; i++){
            JSONObject item = resList.get(i);

            boolean skipItemFlag = false;
            int topBorder = 0;
            if(i ==0){
                topBorder = (item.getInteger("top") < mainColumnBlockItem.getInteger("bottom")
                        ? item.getInteger("top")
                        : mainColumnBlockItem.getInteger("bottom"));
            }else {
                // 该block 与上一行的block 求中点 ， 再往上移动若干像素， 控制误差。
                topBorder = (resList.get(i-1).getInteger("bottom") + item.getInteger("top"))/2 - 3  ;
            }

            int bottomBorder = 0;
            if(i == resList.size()-1){
                bottomBorder = item.getInteger("bottom") + item.getInteger("height") + 3;
            }else{
                //如果距离下一行高度差过大, 停止循环
                //logger.debug("  bottom ={}  maxRowHeight={}  top={} ", item.getInteger("bottom")  ,  maxRowHeight, resList.get(i+1).getInteger("top"));
                if(item.getInteger("bottom")  + maxRowHeight < resList.get(i+1).getInteger("top") ){
                    bottomBorder =  item.getInteger("bottom")  + maxRowHeight;
                    skipItemFlag = true;
                }else{
                    bottomBorder = (resList.get(i+1).getInteger("top") + item.getInteger("bottom"))/2 + ConfigConstants.PARSE_CELL_ERROR_RANGE_MIN;

                }
            }

//            logger.debug("{} item: [t={}, b={}] Boarder[t={}, b={}]", item.getString("text"), item.getInteger("top"),
//                    item.getInteger("bottom"),
//                    topBorder, bottomBorder);

            item.put("topBorder", topBorder);
            item.put("bottomBorder", bottomBorder);
            newResList.add(item);
            if(skipItemFlag){
                break;//下一行过远 停止循环
            }
        }

        for (JSONObject item: newResList){
            logger.debug("【DEBUG】行划分 {} Border [top={}, bottom={}]   self[top={}, bottom={}]  {} ",item.getString("text"),
                    item.getInteger("topBorder"), item.getInteger("bottomBorder"),
                    item.getInteger("top"), item.getInteger("bottom"),
                    item.toJSONString());
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
            int top = rowItem.getInteger("topBorder") - ConfigConstants.PARSE_CELL_ERROR_RANGE_TOP;
            int bottom = rowItem.getInteger("bottomBorder") + ConfigConstants.PARSE_CELL_ERROR_RANGE_BOTTOM;
//            logger.info("      row ");
            for (int j=0; j< columnList.size(); j ++ ){

                Cell cell = tableArray[i][j];
                if(cell == null){
                    cell = new Cell();
                }
                tableArray[i][j] = cell;

                JSONObject columnItem = columnList.get(j);
                int left = columnItem.getInteger("leftBorder");
                int right = columnItem.getInteger("rightBorder");
//                logger.info(" cell : [{}]     row[{}]   cell[{}]  [left={}, right={}] ", i, j, left , right);
                for(JSONObject item: blockItemList){


                    if(item.getInteger("top")>= top &&
                       item.getInteger("bottom")<= bottom &&
                       item.getInteger("left")>= left &&
                       item.getInteger("right") <= right+10
                    ){
//                        logger.debug("[{}]   [left={}, right={}], [top={}, bottom={}]   ",
//                                item.getString("text"), left , right, top, bottom );
                        cell.text += (" " + item.getString("text"));
                        if(item.getFloat("Confidence") < cell.confidence){
                            cell.confidence = item.getFloat("Confidence");
                        }
                    }
                }
            }

        }

        List<JSONArray> resList = new ArrayList<>();

        if(ConfigConstants.DEBUG_FLAG){
            for(int i=0; i<columnList.size(); i++ ){
                System.out.printf("| %20s ",columnList.get(i).getString("text"));
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
                if(ConfigConstants.DEBUG_FLAG) {
                    System.out.printf("| %20s ", tableArray[i][j].text);
                }
                rowArray.add(object);
            }
            resList.add(rowArray);
            if(ConfigConstants.DEBUG_FLAG){
                System.out.println("  ");
            }
        }
        return resList;

    }

    private int findMainColumnIndex( List<JSONObject>  columnBlockItemList){

        int mainColumnIndex = ConfigConstants.TABLE_MAIN_COLUMN_INDEX;
        int count = 0;
        for(int i=0; i< columnBlockItemList.size(); i++){
            JSONObject item = columnBlockItemList.get(i);
            HashMap config = (HashMap) item.get("config");
            if((boolean)config.getOrDefault("MainColumn", false)){
                count ++;
                mainColumnIndex = i;
            }
//            logger.info("--------------- index: {} text:{}  isMainIndex: {}  ", i, item.getString("text"), config.getOrDefault("MainColumn", false));
        }

        if (count > 1){
            throw new IllegalArgumentException(" 'MainColumn' 只能设置给一个Column元素 (用来进行行定位) 目前设置了多个，请检查配置文件!");
        }
        return mainColumnIndex;
    }


}