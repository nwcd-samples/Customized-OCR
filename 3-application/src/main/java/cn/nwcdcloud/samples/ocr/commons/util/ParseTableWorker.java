package cn.nwcdcloud.samples.ocr.commons.util;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ParseTableWorker {

    private final Logger logger = LoggerFactory.getLogger(ParseTableWorker.class);

    /**
     * 处理表格元素
     */
    public List<JSONObject> parse(HashMap item, List<JSONObject> blockItemList) {

        List keyWordStartList = (List) item.get("KeyWordStartList");
        List keyWordEndList = (List) item.get("KeyWordEndList");

        for(int i=0; i< keyWordStartList.size(); i++){
            String startKey = keyWordStartList.get(i).toString();
            for(int j=0; j<keyWordEndList.size(); j++){
                String endKey = keyWordEndList.get(j).toString();
                List<JSONObject> resultList = findTableByKeys(blockItemList, startKey, endKey);
                if(resultList!=null){
                    return resultList;
                }
            }
        }
        return null;
    }



    private List<JSONObject>  findTableByKeys(List<JSONObject> blockItemList, String startKey, String endKey){

        JSONObject startBlockItem = null;
        JSONObject endBlockItem = null;

        for(int i=0; i<blockItemList.size(); i++){
            JSONObject tempBlockItem = blockItemList.get(i);
            String text = tempBlockItem.getString("text").trim();
            if(startKey.equals(text)){
                startBlockItem = tempBlockItem;
            }else if(endKey.equals(text)){
                endBlockItem = tempBlockItem;
            }
        }

        if(startBlockItem == null || endBlockItem == null){
            return null;
        }
        logger.info(" 找到匹配的关键字  start key [{}]   end key [{}]-----------  ", startKey, endKey);
        return mergeColumnList(findColumnBlockItem(startBlockItem, blockItemList),findColumnBlockItem(endBlockItem, blockItemList) , startKey);

    }

    /**
     *
     * @param blockItem
     * @param blockItemList
     * @return
     */
    private List<JSONObject> findColumnBlockItem(JSONObject blockItem, List<JSONObject> blockItemList){


        List<JSONObject> columnBlockItemList = new ArrayList<>();
        int findCount = 0;

        for(int i=0; i<blockItemList.size(); i++){
            JSONObject tempBlockItem = blockItemList.get(i);

            if(tempBlockItem.getInteger("y") > blockItem.getInteger("y")&&
                    tempBlockItem.getInteger("right") > blockItem.getInteger("left")
                    && tempBlockItem.getInteger("left") < blockItem.getInteger("right") + blockItem.getInteger("width")
            ){

                findCount ++;
                columnBlockItemList.add(tempBlockItem);
            }
        }
        logger.info(" 关键字列表 [{}]  一共找到{}个元素 ", blockItem.getString("text"),  findCount);
        return columnBlockItemList;

    }


    private List<JSONObject>  mergeColumnList(List<JSONObject> startColumnList, List<JSONObject> endColumnList, String startKey){

        if(startColumnList.size() == 0 || endColumnList.size() ==0 ){
            return null;
        }

        List<JSONObject> resultItemList = new ArrayList<>();
        List<JSONObject> addedItemList = new ArrayList<>();

        for(JSONObject startItem : startColumnList){

//            var min_distance = 1000000
//            var min_distance_item = null
            int minDistance = 1000000;
            JSONObject minDistanceItem = null;
            for(JSONObject endItem: endColumnList){
                boolean addedFlag = false;
                for(JSONObject addedItem: addedItemList){
                    if(endItem.getString("id").equals(addedItem.getString("id"))){
                        addedFlag = true;
                        break;
                    }
                }
                if(addedFlag){
                    continue;
                }

                //找到垂直方向最近的一个， 并且y值相差不超过高度
                Integer distance = Math.abs(startItem.getInteger("y") - endItem.getInteger("y"));
                if (distance < minDistance  &&
                        Math.abs(startItem.getInteger("y")- endItem.getInteger("y")) < endItem.getInteger("height")){
                    minDistance = distance;
                    minDistanceItem = endItem;
                }
            }

            if(minDistanceItem !=null){
                logger.info("[{}] ===== [{}]", startItem.getString("text"), minDistanceItem.getString("text"));
                addedItemList.add(minDistanceItem);

                JSONObject resultItem = new JSONObject();
                resultItem.put("name", startKey+" - "+ startItem.getString("text"));
                resultItem.put("value", minDistanceItem.getString("text"));
                resultItemList.add(resultItem);
            }
        }

        logger.info(" 找到 {} 个行元素 ", resultItemList.size());
        return  resultItemList;
    }

}
