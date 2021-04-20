package cn.nwcdcloud.samples.ocr.commons.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ParseTablesWorker {

    private final Logger logger = LoggerFactory.getLogger(ParseTablesWorker.class);

    /**
     * 处理表格元素
     */
    public JSONObject parse(HashMap rootMap, List<JSONObject> blockItemList) {

        //step 0. 找到用来定位的两个列元素  Start  End
        List<JSONObject> res = findTableHeadColumns(rootMap, blockItemList);
        if (res == null || res.size() != 2) {
            logger.warn(" 没有找到表头定位的元素   ");
            return null;
        }
        JSONObject columnStartBlockItem = res.get(0);
        JSONObject columnEndBlockItem = res.get(1);


        int top = (columnStartBlockItem.getInteger("top") < columnEndBlockItem.getInteger("top") ?
                columnStartBlockItem.getInteger("top"): columnEndBlockItem.getInteger("top") );
        int bottom = (columnStartBlockItem.getInteger("bottom") > columnEndBlockItem.getInteger("bottom") ?
                columnStartBlockItem.getInteger("bottom"):columnEndBlockItem.getInteger("bottom"));

        //step 1. 查找表头元素
        List<JSONObject> columnBlockItemList = new ArrayList<>();
        List columnList = (ArrayList) rootMap.get("Columns");
        for (Object item : columnList) {
            HashMap newItem = (HashMap) item;
            if ("start".equals(newItem.get("IndexType"))) {
                columnStartBlockItem.put("info", newItem);
                columnBlockItemList.add(columnStartBlockItem);
            } else if ("end".equals(newItem.get("IndexType"))) {
                columnEndBlockItem.put("info", newItem);
                columnBlockItemList.add(columnEndBlockItem);
            }else{
                List<String> keyList = (List) newItem.get("KeyWordList");
                JSONObject blockItem = findColumnByKeyList(blockItemList, keyList, top, bottom);
                if(blockItem != null){
                    blockItem.put("info", newItem);
                    columnBlockItemList.add(blockItem);
                }else{
                    logger.warn("没有找到表头元素  : "+ newItem);
                }
            }
        }
        //step 2. 调整列元素的左右分割区间
        adjustColumnBlockItemMargin(columnBlockItemList, blockItemList);

        //step 3. 通过主列 往下迭代找元素， 找到行划分。
        int mainColumnIndex = (int) rootMap.get("MainColumnIndex");

        if(mainColumnIndex >= columnBlockItemList.size()){
            throw new IllegalArgumentException(" 主列设置错误， 最大列数为"+columnBlockItemList.size()+" , 设置的值= "+ mainColumnIndex);
        }
        logger.info(" 主列： {}   Text [{}] ", mainColumnIndex,  columnBlockItemList.get(mainColumnIndex).getString("text"));

        List<JSONObject> rowList = findRowSplitByMainColumn( blockItemList, columnBlockItemList.get(mainColumnIndex));
        //step 3. 所有列通过行划分， 找到对应元素，

        String tableName = rootMap.get("Name").toString();
        int rowCount = rowList.size();
        int columnCount = columnBlockItemList.size();
        List<JSONArray> resultList = findCellByColumnAndRow(blockItemList, rowList, columnBlockItemList, mainColumnIndex);

        JSONArray headTitleArray = new JSONArray();
        for(JSONObject item : columnBlockItemList){
            headTitleArray.add(item.getString("text"));
        }
        //step 4. 判断行结尾的情况
        JSONObject resultObject = new JSONObject();
        resultObject.put("name", tableName);
        resultObject.put("rowCount", rowCount);
        resultObject.put("columnCount", columnCount);
        resultObject.put("rowList", resultList);
        resultObject.put("heads", headTitleArray);


        return resultObject;
    }

    /**
     * 寻找两个用于定位的表头元素。
     */
    private List<JSONObject> findTableHeadColumns(HashMap rootMap, List<JSONObject> blockItemList) {

        List keyWordStartList = null;
        List keyWordEndList = null;

        List columnList = (ArrayList) rootMap.get("Columns");
        int startFindCount = 0;
        int endFindCount = 0;
        for (Object item : columnList) {
            HashMap newItem = (HashMap) item;
            System.out.println(item);
            if ("start".equals(newItem.get("IndexType"))) {
                startFindCount ++;
                keyWordStartList = (List) newItem.get("KeyWordList");
            } else if ("end".equals(newItem.get("IndexType"))) {
                endFindCount ++;
                keyWordEndList = (List) newItem.get("KeyWordList");
            }
        }

        if(startFindCount ==0 || endFindCount== 0){
            throw new IllegalArgumentException("没有配置 IndexType， 'start' 和 'end' 两个属性， 用来定位表头 ");
        }

        if (keyWordStartList == null || keyWordEndList == null) {
            logger.warn("没有找到 'start' 和 'end' 两个属性的表头元素，  请检查关键字设置");
        }


        for (int i = 0; i < keyWordStartList.size(); i++) {
            String startKey = keyWordStartList.get(i).toString();
            for (int j = 0; j < keyWordEndList.size(); j++) {
                String endKey = keyWordEndList.get(j).toString();
                List<JSONObject> resList = findTableByKeys(blockItemList, startKey, endKey);
                if (resList != null && resList.size() == 2) {
                    return resList;
                }
            }
        }
        return null;
    }


    /**
     * 通过两个关键字进行元素的查找。
     *
     * @param blockItemList
     * @param startKey
     * @param endKey
     * @return
     */
    private List<JSONObject> findTableByKeys(List<JSONObject> blockItemList, String startKey, String endKey) {

        JSONObject startBlockItem = null;
        JSONObject endBlockItem = null;

        for (int i = 0; i < blockItemList.size(); i++) {
            JSONObject tempBlockItem = blockItemList.get(i);
            String text = tempBlockItem.getString("text").trim();
            if (startKey.equals(text)) {
                startBlockItem = tempBlockItem;
            } else if (endKey.equals(text)) {
                endBlockItem = tempBlockItem;
            }
        }
        if (startBlockItem == null || endBlockItem == null) {
            return null;
        }
        List<JSONObject> res = new ArrayList<>();
        logger.info(" 找到匹配的关键字  start key [{}]   end key [{}]-----------  ", startKey, endKey);
        res.add(startBlockItem);
        res.add(endBlockItem);

        return res;
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
                if (key.equals(text) && tempBlockItem.getInteger("top") >= top - 30 && tempBlockItem.getInteger("bottom") < bottom + 30) {
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
        HashMap infoMap = (HashMap) currentItem.get("info");

//        MarginLeftType: 2
//        MoveLeftRatio: 0.1
//        MarginRightType: 2
//        MoveRightRatio: 0.1

        int marginLeftType = Integer.valueOf(infoMap.get("MarginLeftType").toString());
        int marginRightType = Integer.valueOf(infoMap.get("MarginRightType").toString());
        float moveLeftRatio = Float.valueOf(infoMap.get("MoveLeftRatio").toString());
        float moveRightRatio = Float.valueOf(infoMap.get("MoveRightRatio").toString());




//        # MarginLeftType 说明
//        # 1:  以Column 左边作为列的划分                 范围最近
//        # 2:  以Column 到下一个Column 中点             中间范围
//        # 3:  以Column 左边Column元素的右边作为分界点    范围最远

        int leftBorder = 0;
        switch (marginLeftType){
            case 1:
                leftBorder = currentItem.getInteger("left");
                break;
            case 2:
                leftBorder = (currentItem.getInteger("left") + leftColumnRight)/2 ;
                break;
            case 3:
                leftBorder = leftColumnRight ;
                break;
            default:

                throw new IllegalArgumentException(" marginLeftType 类型配置不正确 ");
        }
        leftBorder -= currentItem.getInteger("width") * moveLeftRatio;

//        # MarginRightType 说明
//        # 1:  以Column 右边作为列的划分                 范围最近
//        # 2:  以Column 到下一个Column 中点             中间范围
//        # 3:  以Column 右边Column元素的左边作为分界点    范围最远

        int rightBorder = 0;
        switch (marginRightType){
            case 1:
                rightBorder = currentItem.getInteger("right");
                break;
            case 2:
                rightBorder = (currentItem.getInteger("right") + rightColumnLeft) /2;
                break;
            case 3:
                rightBorder = rightColumnLeft;
                break;
            default:
                throw new IllegalArgumentException(" marginRightType 类型配置不正确 ");
        }

        rightBorder += currentItem.getInteger("width") * moveRightRatio;
        currentItem.put("leftBorder", leftBorder);
        currentItem.put("rightBorder",rightBorder);
        logger.info("{} :  width={} original [{}, {}]  border:[{}, {}]  left:[t={}, r={}], right:[t={}, r={}]  ", currentItem.getString("text"),
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
    private List<JSONObject> findRowSplitByMainColumn(List<JSONObject> blockItemList , JSONObject mainColumnBlockItem){


        List<JSONObject> resList = new ArrayList<>();
        for (int i=0; i< blockItemList.size(); i++){

            int left = mainColumnBlockItem.getInteger("leftBorder");
            int right = mainColumnBlockItem.getInteger("rightBorder");


            JSONObject item = blockItemList.get(i);
            if(item.getInteger("top") > mainColumnBlockItem.getInteger("bottom") &&
                    item.getInteger("left")> left && item.getInteger("right")< right ){
                resList.add(item);
            }
        }
        resList.sort(new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject jsonObject, JSONObject t1) {
                return jsonObject.getInteger("top") - t1.getInteger("top");
            }
        });

        for(int i =0; i< resList.size(); i++){
            JSONObject item = resList.get(i);

            int topBorder = 0;
            if(i ==0){
                topBorder = mainColumnBlockItem.getInteger("bottom");
            }else {
                // 该block 与上一行的block 求中点 ， 再往上移动若干像素， 控制误差。
                topBorder = (resList.get(i-1).getInteger("bottom") + item.getInteger("top"))/2 - 5  ;
            }

            int bottomBorder = 0;
            if(i == resList.size()-1){
                bottomBorder = item.getInteger("bottom") + item.getInteger("height") + 5;
            }else{

                bottomBorder = (resList.get(i+1).getInteger("top") + item.getInteger("bottom"))/2 + 5;
            }


            logger.info("{} item: [t={}, b={}] Boarder[t={}, b={}]", item.getString("text"), item.getInteger("top"),
                    item.getInteger("bottom"),
                    topBorder, bottomBorder);

            item.put("topBorder", topBorder);
            item.put("bottomBorder", bottomBorder);
        }

        return resList;
    }

    /**
     * 通过行划分和列划分， 查找单元格。
     * @return
     */
    private List<JSONArray> findCellByColumnAndRow(List<JSONObject> blockItemList,
                                List<JSONObject> rowList, List<JSONObject> columnList, int mainColumnIndex){


        int rowCount = rowList.size();
        int columnCount = columnList.size();
        Cell[][] tableArray = new Cell[rowCount][columnCount];

        for(int i=0; i<rowList.size(); i++){
            JSONObject rowItem = rowList.get(i);
            int top = rowItem.getInteger("topBorder");
            int bottom = rowItem.getInteger("bottomBorder");
            logger.info("      ");
            for (int j=0; j< columnList.size(); j ++ ){

                Cell cell = tableArray[i][j];
                if(cell == null){
                    cell = new Cell();
                }
                tableArray[i][j] = cell;


                JSONObject columnItem = columnList.get(j);
                int left = columnItem.getInteger("leftBorder");
                int right = columnItem.getInteger("rightBorder");

                for(JSONObject item: blockItemList){

                    if(item.getInteger("top")> top &&
                       item.getInteger("bottom")< bottom &&
                       item.getInteger("left")> left &&
                       item.getInteger("right") < right){
                        logger.info("[left={}, right={}], [top={}, bottom={}] , [{}]", left , right, top, bottom, item.getString("text"));

                        cell.text += item.getString("text");
                        if(item.getFloat("Confidence") < cell.confidence){
                            cell.confidence = item.getFloat("Confidence");
                        }
                    }
                }
            }

        }

        List<JSONArray> resList = new ArrayList<>();
        for (int i=0; i< rowCount; i++){
            JSONArray rowArray =  new JSONArray();
            for(int j=0; j< columnCount; j++){
                JSONObject object = new JSONObject();
                object.put("text", tableArray[i][j].text);
                object.put("confidence", tableArray[i][j].confidence);
                System.out.printf("| %20s ",tableArray[i][j].text);
                rowArray.add(object);
            }
            resList.add(rowArray);
            System.out.println("  ");
        }
        return resList;
    }

    private class Cell{
        public Cell() {
            text = "";
            confidence = 1.0f;
            count = 0;
        }

        String text;
        float confidence;
        int count;
    }

}