package cn.nwcdcloud.samples.ocr.commons.util;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ParseTablesWorker {

    private final Logger logger = LoggerFactory.getLogger(ParseTablesWorker.class);

    /**
     * 处理表格元素
     */
    public List<JSONObject> parse(HashMap rootMap, List<JSONObject> blockItemList) {

        List<JSONObject> res = findTableHeadColumns(rootMap, blockItemList);
        if (res == null || res.size() != 2) {
            logger.warn(" 没有找到表头定位的元素   ");
            return null;
        }
        JSONObject columnStartBlockItem = res.get(0);
        JSONObject columnEndBlockItem = res.get(1);

        System.out.println("columnStartBlockItem: " + columnStartBlockItem);
        System.out.println("columnEndBlockItem: " + columnEndBlockItem);

        int top;
        int bottom;

        if (columnStartBlockItem.getInteger("top") < columnEndBlockItem.getInteger("top")) {
            top = columnStartBlockItem.getInteger("top");
        } else {
            top = columnEndBlockItem.getInteger("top");
        }
        if (columnStartBlockItem.getInteger("bottom") > columnEndBlockItem.getInteger("bottom")) {
            bottom = columnStartBlockItem.getInteger("bottom");
        } else {
            bottom = columnEndBlockItem.getInteger("bottom");
        }


        System.out.println("top     " + top);
        System.out.println("bottom  " + bottom);


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

        adjustColumnBlockItemMargin(columnBlockItemList, blockItemList);



        return null;
    }

    /**
     * 寻找两个用于定位的表头元素。
     */
    private List<JSONObject> findTableHeadColumns(HashMap rootMap, List<JSONObject> blockItemList) {

        List keyWordStartList = null;
        List keyWordEndList = null;

        List columnList = (ArrayList) rootMap.get("Columns");
        for (Object item : columnList) {
            HashMap newItem = (HashMap) item;
            System.out.println(item);
            if ("start".equals(newItem.get("IndexType"))) {
                keyWordStartList = (List) newItem.get("KeyWordList");
            } else if ("end".equals(newItem.get("IndexType"))) {
                keyWordEndList = (List) newItem.get("KeyWordList");
            }
        }

        if (keyWordStartList == null || keyWordEndList == null) {
            throw new IllegalArgumentException("没有配置 IndexType， 'start' 和 'end' 两个属性， 用来定位表头 ");
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
        System.out.println(currentItem.get("text") + "\tmarginLeftType: "+ marginLeftType
                + " \tmarginRightType: "+ marginRightType
                + " \tmoveLeftRatio: "+ moveLeftRatio
                + " \tmoveRightRatio: "+ moveRightRatio);



//        # MarginLeftType 说明
//        # 1:  以Column 左边作为列的划分                 范围最近
//        # 2:  以Column 到下一个Column 中点             中间范围
//        # 3:  以Column 左边Column元素的右边作为分界点    范围最远

        int marginLeft = 0;
        switch (marginLeftType){
            case 1:
                marginLeft = currentItem.getInteger("left");
                break;
            case 2:
                marginLeft = (currentItem.getInteger("left") + leftColumnRight)/2 ;
                break;
            case 3:
                marginLeft = leftColumnRight ;
                break;
            default:

                throw new IllegalArgumentException(" marginLeftType 类型配置不正确 ");
        }
        marginLeft -= currentItem.getInteger("width") * moveLeftRatio;

//        # MarginRightType 说明
//        # 1:  以Column 右边作为列的划分                 范围最近
//        # 2:  以Column 到下一个Column 中点             中间范围
//        # 3:  以Column 右边Column元素的左边作为分界点    范围最远

        int marginRight = 0;
        switch (marginRightType){
            case 1:
                marginRight = currentItem.getInteger("right");
                break;
            case 2:
                marginRight = (currentItem.getInteger("right") + rightColumnLeft) /2;
                break;
            case 3:
                marginRight = rightColumnLeft;
                break;
            default:
                throw new IllegalArgumentException(" marginRightType 类型配置不正确 ");
        }

        marginRight += currentItem.getInteger("width") * moveRightRatio;
        logger.info("{} :  width={}  [{}, {}] margin: [{}, {}] ", currentItem.getString("text"),
                currentItem.getInteger("width"),
                currentItem.getInteger("left"),
                currentItem.getInteger("right"),
                marginLeft, marginRight);



    }
}