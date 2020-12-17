package cn.nwcdcloud.samples.ocr.commons.util;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

import com.alibaba.fastjson.JSON;
import org.ho.yaml.Yaml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class ParseJsonUtils {
    private static final String  ID_SAMPLE_CONFIG_FILE =  "config/id.yaml";

    private  final Logger logger = LoggerFactory.getLogger(ParseJsonUtils.class);

    private int pageWidth;
    private int pageHeight;
    private List<JSONObject> blockItemList;

    public ParseJsonUtils(int pageWidth, int pageHeight , List<JSONObject> blockItemList) {
        this.pageWidth = pageWidth;
        this.pageHeight = pageHeight;
        this.blockItemList = blockItemList;
    }

    public JSONArray extractValue(List<JSONObject> blockItemList){
        Map configMap =  readConfig(ID_SAMPLE_CONFIG_FILE);

        JSONArray resultArray = new JSONArray();
        List targetList  = (ArrayList) configMap.get("Targets");
        for( Object item: targetList){
            JSONObject resultItem =  extractItem((HashMap) item, blockItemList);
            if(resultItem == null){
                continue;
            }
            resultArray.add(resultItem);
        }
        return resultArray;
    }


    private JSONObject  extractItem(HashMap item, List<JSONObject> blockItemList){


        if("horizontal".equals(item.get("RecognitionType"))){
             return doHorizontal(item, blockItemList);
        }
        return null;

    }

    /**
     * 定位关键字
     * @param item
     * @param blockItemList
     */
    private JSONObject findKeyBlockItem(HashMap item, List<JSONObject> blockItemList){
//        logger.info("name: {}  key-word-list: {}", item.get("name") , item.get("key-word-list"));
//        logger.info("recognition-type: {} ", item.get("recognition-type"));

        List keyWordList = (List) item.get("KeyWordList");

        //case1. key， 单个元素里面包含了关键字， 或者以关键字开头
        boolean findFlag = false;
        JSONObject targetBlockItem = null;
        int targetIndex = -1;
        String targetKeyWord = "";
        for(Object key: keyWordList){
            String keyWord = key.toString();

            for(int i=0; i<blockItemList.size(); i++ ){
                //判断关键字
                JSONObject blockItem = blockItemList.get(i);
                String blockText = blockItem.getString("text");
                int index = blockText.indexOf(keyWord);
                if(index  < 0 ){
                    continue;
                }
                logger.info(" Text:  {}   key: {}   {} ", blockText,  keyWord, index);

                //判断范围
                if(!isValidRange(item, blockItem)){
                    continue;
                }
                findFlag = true;
                targetBlockItem = blockItem;
                targetIndex = index;
                targetKeyWord = keyWord;
                break;
            }

        }
//        logger.info(" findFlag:  {}   index: {}    {} ", findFlag, targetIndex,  targetBlockItem);


        //case2. 取出一行的元素， 从前往后匹配关键字

        JSONObject result = new JSONObject();
        result.put("index", targetIndex);
        result.put("blockItem", targetBlockItem);
        result.put("keyWord", targetKeyWord);
        result.put("keyType", "single"); // 关键字在一个单元格里面
        return result;

    }



    private JSONObject doHorizontal(HashMap item, List<JSONObject> blockItemList){
        logger.info("doHorizontal : +++++++++++++ : text {}  ",  item.get("Name"));
        JSONObject keyBlockItemResult = findKeyBlockItem(item, blockItemList);
        JSONObject blockItem = keyBlockItemResult.getJSONObject("blockItem");
        if(blockItem == null){
            logger.warn("doHorizontal   没有找到 : text {}  ",  item.get("Name"));
            return null;
        }

        int index = keyBlockItemResult.getInteger("index");
        String text = blockItem.getString("text");
        String keyWord = keyBlockItemResult.getString("keyWord");
        logger.info("index {}  text {} text length: {}   keyWord {} ", index, text, text.length(), keyWord);

        //case 1. 关键字和值 在一个单元格里面

        JSONObject resultItem = new JSONObject();
        resultItem.put("name", item.get("Name"));

        // key和value 在一个单元格里
        if( index + keyWord.length() < text.length()){
            int maxLineCount = (int)item.get("MaxLineCount");
            logger.info(" [{}] Key value ®在一个单元格里  ---------- {} " , text, maxLineCount);
            if(maxLineCount > 1){
                String mergeValue = findMultiLineBlockItemValue(blockItem, maxLineCount, true);
                resultItem.put("value", mergeValue.substring(keyWord.length()));

            }else {

                int lastIndex = text.length() > index + keyWord.length() + (int)item.get("MaxLength") ?
                        index + keyWord.length() + (int)item.get("MaxLength"): text.length();
                logger.info("key {}  -------------- value {}  lastIndex {} ", item.get("name"),
                        text.substring( index+ keyWord.length(), lastIndex), lastIndex);

                resultItem.put("value", text.substring( index+ keyWord.length(), lastIndex));

            }

        }else {
            // value 单独在一个单元格里
            logger.info(" [{}]  value 单独在一个单元格里  --------------------------- " , text);
            String blockItemValue = findNextRightBlockItemValue(item, blockItem);

            if(blockItemValue ==  null){
                return null;
            }
            logger.info("key {}  -------------- value {}  ", item.get("name"), blockItemValue);
            resultItem.put("value", blockItemValue);

        }




        if("single".equals(keyBlockItemResult.getString("KeyType"))){
            //key 在单个单元格里

        }else {
//            多行

        }

        return resultItem;

    }


    /**
     * 读取配置文件
     * @param configPath
     * @return
     */

    private Map readConfig(String configPath) {
    	InputStream is=this.getClass().getClassLoader().getResourceAsStream(configPath);

    	Map rootMap = null;
        try {
            rootMap = Yaml.loadType(is, HashMap.class);


        } catch (Exception e) {
        	logger.error("读取配置文件出错:{}"+configPath,e);
        }
        return rootMap;
    }


    /**
     * 检测目标元素 坐标范围的合法性
     * @param item
     * @param blockItem
     * @return
     */
    private boolean isValidRange(HashMap item, JSONObject blockItem) {
        int left = (int) ((double)item.get("XRangeMin")  * this.pageWidth);
        int right = (int) ((double) item.get("XRangeMax")  * this.pageWidth);

        int top = (int) ((double) item.get("YRangeMin")  * this.pageHeight);
        int bottom = (int) ((double)item.get("YRangeMax")  * this.pageHeight);
//        logger.info("x: [{}, {}]  y: [{}, {}]", left, right, top, bottom);
//        logger.info("x: {}    y: {} ", blockItem.getInteger("x"),
//                blockItem.getInteger("y"));

        int x = blockItem.getInteger("x");
        int y = blockItem.getInteger("y");

        if(x>right || x < left || y < top || y>bottom){
            return false;
        }
        return true;
    }


    /**
     找到右边一个单元格
     */

    private String findNextRightBlockItemValue(HashMap item, JSONObject blockItem){

        int minDistance = 1000000;
        JSONObject minDistanceBlockItem = null;

        int maxLineCount = (int)item.get("MaxLineCount");
        //如果是多行， 找右边多行的元素，进行文本合并
        if(maxLineCount > 1){
            return findMultiLineBlockItemValue(blockItem, maxLineCount, false);
        }

        for(int i=0; i< this.blockItemList.size(); i++){
            JSONObject tempBlockItem = this.blockItemList.get(i);

            int yAbs = Math.abs(tempBlockItem.getInteger("y")- blockItem.getInteger("y"));
            if(tempBlockItem.getInteger("left") > blockItem.getInteger("x") &&
                 yAbs  <2 * blockItem.getInteger("height"))   {

                int tempDistance = Math.abs(blockItem.getInteger("y") - tempBlockItem.getInteger("y")) +
                        Math.abs(blockItem.getInteger("right") - tempBlockItem.getInteger("left"));

                if(tempDistance < minDistance){
                    minDistance = tempDistance;
                    minDistanceBlockItem = tempBlockItem;
                }
            }
        }
        return minDistanceBlockItem.getString("text");
    }

    /**
     * 找到多行的元素
     * @param blockItem
     * @param maxLineCount
     * @param isContainSelf  是否包含元素本身， 处理Key和Value 在一起的情况
     * @return
     */
    private String findMultiLineBlockItemValue(JSONObject blockItem, int maxLineCount, boolean isContainSelf){

        List<JSONObject> contentBlockItemList = new ArrayList<>();
        for(int i=0; i< this.blockItemList.size(); i++){
            JSONObject curItem = blockItemList.get(i);
            if(!isContainSelf && blockItem.getString("id").equals(curItem.getString("id"))){
                continue;
            }
            logger.info("{} ################## {}", JSON.toJSONString(curItem.getString("text")), JSON.toJSONString(curItem));
            if(curItem.getInteger("top") > blockItem.getInteger("top") - blockItem.getInteger("height")
              && curItem.getInteger("bottom")< blockItem.getInteger("bottom") + maxLineCount * (blockItem.getInteger("height")+3)){
                contentBlockItemList.add(curItem);
//                logger.info("################## {}", JSON.toJSONString(curItem));
            }
        }

        Collections.sort(contentBlockItemList, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject jsonObject, JSONObject t1) {
                return jsonObject.getInteger("y") - t1.getInteger("y");
            }
        }); // 按年龄排序


        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0; i< contentBlockItemList.size();i++){
            logger.info("++++++++++++ {} ", JSON.toJSON(contentBlockItemList.get(i)));
            stringBuilder.append(contentBlockItemList.get(i).getString("text"));
        }


        return stringBuilder.toString();

    }





}
