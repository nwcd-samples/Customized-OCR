package cn.nwcdcloud.samples.ocr.commons.util;

import java.io.InputStream;
import java.util.*;

import com.alibaba.fastjson.JSON;
import org.ho.yaml.Yaml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class ParseJsonWorker {
	private final Logger logger = LoggerFactory.getLogger(ParseJsonWorker.class);

	private int pageWidth;
	private int pageHeight;
	private List<JSONObject> blockItemList;
	private String configFilePath;

	public ParseJsonWorker(int pageWidth, int pageHeight, List<JSONObject> blockItemList, String configFilePath) {
		this.pageWidth = pageWidth;
		this.pageHeight = pageHeight;
		this.blockItemList = blockItemList;
		this.configFilePath = configFilePath;
	}

	public JSONArray extractValue(List<JSONObject> blockItemList) {
		Map configMap = readConfig(this.configFilePath);

		ParseTableWorker tableWorker = new ParseTableWorker();
		ParseTablesWorker tablesWorker = new ParseTablesWorker();
		JSONArray resultArray = new JSONArray();
		List targetList = (ArrayList) configMap.get("Targets");
		for (Object item : targetList) {
			// 识别单个元素
			HashMap newItem = (HashMap) item;
			if ("horizontal".equals(newItem.get("RecognitionType"))) {
				JSONObject resultItem = doHorizontal(newItem, blockItemList);
				if(resultItem != null) {
					resultArray.add(resultItem);
				}
			}else if ("table".equals(newItem.get("RecognitionType"))) {
				List<JSONObject> resultList = tableWorker.parse(newItem, blockItemList);
				if(resultList != null){
					resultArray.addAll(resultList);
				}
			}else if ("tables".equals(newItem.get("RecognitionType"))) {
				List<JSONArray> resultList = tablesWorker.parse(newItem, blockItemList);
				if(resultList != null){
					resultArray.addAll(resultList);
				}
			}
		}
		return resultArray;
	}

	/**
	 * 定位关键字
	 * 
	 * @param item
	 * @param blockItemList
	 */
	private JSONObject findKeyBlockItem(HashMap item, List<JSONObject> blockItemList) {
//        logger.info("name: {}  key-word-list: {}", item.get("name") , item.get("key-word-list"));
//        logger.info("recognition-type: {} ", item.get("recognition-type"));

		List keyWordList = (List) item.get("KeyWordList");

		// case1. key， 单个元素里面包含了关键字， 或者以关键字开头
		boolean findFlag = false;
		JSONObject targetBlockItem = null;
		int targetIndex = -1;
		String targetKeyWord = "";
		for (Object key : keyWordList) {
			String keyWord = key.toString();

			for (int i = 0; i < blockItemList.size(); i++) {
				// 判断关键字
				JSONObject blockItem = blockItemList.get(i);
				String blockText = blockItem.getString("text");
				int index = blockText.indexOf(keyWord);
				if (index < 0) {
					continue;
				}
//				logger.debug(" Text:  {}   key: {}   {} ", blockText, keyWord, index);

				// 判断范围
				if (!isValidRange(item, blockItem)) {
					continue;
				}
				findFlag = true;
				targetBlockItem = blockItem;
				targetIndex = index;
				targetKeyWord = keyWord;
				break;
			}
			if(!findFlag ){
				//如果没有找到， 尝试将关键字拆分开， 然后进行查找， 第一个字符和最后一个字符
				 JSONObject result = findResultBySplitKeywords(item, blockItemList, keyWord);
				 if(result.getJSONObject("blockItem") != null){
				 	return result;
				 }
			}

		}
        logger.info(" findFlag:  {}   index: {}    {} ", findFlag, targetIndex,  targetBlockItem);

		JSONObject result = new JSONObject();
		result.put("index", targetIndex);
		result.put("blockItem", targetBlockItem);
		result.put("keyWord", targetKeyWord);
		result.put("keyType", "single"); // 关键字在一个单元格里面
		return result;

	}

	/**
	 * 如果没有找到， 尝试将关键字拆分开， 然后进行查找， 第一个字符和最后一个字符
	 * @param item
	 * @param blockItemList
	 * @return
	 */
	private JSONObject findResultBySplitKeywords(HashMap item, List<JSONObject> blockItemList, String targetKeyWord){

		if(targetKeyWord == null || targetKeyWord.length()<2){
			logger.warn("关键字设置不正确 {} ", JSON.toJSON(item));
			return null;
		}

		int lastKeyLength =1;
		String startKey = targetKeyWord.substring(0,lastKeyLength);
		String endKey = targetKeyWord.substring(targetKeyWord.length()-lastKeyLength, targetKeyWord.length() );

		JSONObject startBlockItem = null;

		logger.debug("findResultBySplitKeywords  {}   first {} , last {}", targetKeyWord , startKey, endKey);
		// 找到开始的关键字
		for (int i = 0; i < blockItemList.size(); i++) {
			JSONObject blockItem = blockItemList.get(i);
			String blockText = blockItem.getString("text").trim();
			if(blockText.equals(startKey)){
				startBlockItem = blockItem;
			}
		}

		if(startBlockItem == null){
			JSONObject result = new JSONObject();
			result.put("index", lastKeyLength);
			result.put("blockItem", null);
			result.put("keyWord", targetKeyWord);
			result.put("keyType", "single"); // 关键字在一个单元格里面
			result.put("subKeyWord", endKey);
			return result;
		}

		logger.info(" find start blockItem {} ", JSON.toJSON(startBlockItem));

		JSONObject targetBlockItem = null;

		//找到后一个关键字
		for (int i = 0; i < blockItemList.size(); i++) {
			JSONObject blockItem = blockItemList.get(i);
			String blockText = blockItem.getString("text").trim();
			if(blockText.startsWith(endKey) &&
					blockItem.getInteger("y") > startBlockItem.getInteger("y") - startBlockItem.getInteger("height")&&
					blockItem.getInteger("y") < startBlockItem.getInteger("y") + startBlockItem.getInteger("height")
			){
				targetBlockItem = blockItem;
			}
		}


		logger.info(" find targetBlockItem  blockItem {} ", JSON.toJSON(targetBlockItem));
		JSONObject result = new JSONObject();
		result.put("index", 1);
		result.put("blockItem", targetBlockItem);
		result.put("keyWord", targetKeyWord);
		result.put("keyType", "split"); // 关键字不在一个单元格里面
		result.put("subKeyWord", endKey);
		return result;

	}

	/**
	 * 处理单个水平元素
	 * @param item
	 * @param blockItemList
	 * @return
	 */
	private JSONObject doHorizontal(HashMap item, List<JSONObject> blockItemList) {
		JSONObject keyBlockItemResult = findKeyBlockItem(item, blockItemList);
		JSONObject blockItem = keyBlockItemResult.getJSONObject("blockItem");
		if (blockItem == null) {
			logger.warn("doHorizontal   没有找到 : text {}  ", item.get("Name"));
			return null;
		}

		int index = keyBlockItemResult.getInteger("index");
		String text = blockItem.getString("text");
		String keyWord = keyBlockItemResult.getString("keyWord");
		logger.debug("index {}  text {} text length: {}   keyWord {} ", index, text, text.length(), keyWord);

		// case 1. 关键字和值 在一个单元格里面

		JSONObject resultItem = new JSONObject();

		resultItem.put("name", item.get("Name"));
		resultItem.put("score", blockItem.getString("Confidence"));


		if(keyBlockItemResult.get("subKeyWord") != null ){
			// case  1:   'key1'   'key2value'
			int lastIndex = text.length() > index  + (int) item.get("MaxLength")
					? index  + (int) item.get("MaxLength")
					: text.length();
			resultItem.put("value", text.substring(index , lastIndex));
		}else if (index + keyWord.length() < text.length()) {
			// case  2:   'key:value'
			// key和value 在一个单元格里
			int maxLineCount = (int) item.get("MaxLineCount");
			if (maxLineCount > 1) { //多行的情况
				logger.info("--------------   1");
				String mergeValue = findMultiLineBlockItemValue(blockItem, maxLineCount, true);
				resultItem.put("value", mergeValue.substring(keyWord.length()));
			} else {
				logger.info("--------------   2  index: {}  keyword: {} ", index, keyWord.length());
				int lastIndex = text.length() > index + keyWord.length() + (int) item.get("MaxLength")
						? index + keyWord.length() + (int) item.get("MaxLength")
						: text.length();
				resultItem.put("value", text.substring(index + keyWord.length(), lastIndex));
			}
		} else {
			// value 单独在一个单元格里
			// case  3:   'key' 'value'
			String blockItemValue = findNextRightBlockItemValue(item, blockItem);

			if (blockItemValue == null) {
				return null;
			}
			resultItem.put("value", blockItemValue);
		}

		if ("split".equals(keyBlockItemResult.getString("keyType"))) {
			// key 在多个单元格里， 取最后一个单元格 第一个字符开始的内容
			resultItem.put("value", blockItem.getString("text").substring(1));
		}

		if (resultItem.getString("value").startsWith(":") || resultItem.getString("value").startsWith("：")) {
			resultItem.put("value", resultItem.getString("value").substring(1));
		}
		return resultItem;
	}

	/**
	 * 读取配置文件
	 * 
	 * @param configPath
	 * @return
	 */

	private Map readConfig(String configPath) {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(configPath);

		Map rootMap = null;
		try {
			rootMap = Yaml.loadType(is, HashMap.class);

		} catch (Exception e) {
			logger.error("读取配置文件出错:{}" + configPath, e);
		}
		return rootMap;
	}

	/**
	 * 检测目标元素 坐标范围的合法性
	 * 
	 * @param item
	 * @param blockItem
	 * @return
	 */
	private boolean isValidRange(HashMap item, JSONObject blockItem) {
		int left = (int) ((double) item.get("XRangeMin") * this.pageWidth);
		int right = (int) ((double) item.get("XRangeMax") * this.pageWidth);

		int top = (int) ((double) item.get("YRangeMin") * this.pageHeight);
		int bottom = (int) ((double) item.get("YRangeMax") * this.pageHeight);
//        logger.info("x: [{}, {}]  y: [{}, {}]", left, right, top, bottom);
//        logger.info("x: {}    y: {} ", blockItem.getInteger("x"),
//                blockItem.getInteger("y"));

		int x = blockItem.getInteger("x");
		int y = blockItem.getInteger("y");

		if (x > right || x < left || y < top || y > bottom) {
			return false;
		}
		return true;
	}

	/**
	 * 找到右边一个单元格
	 */

	private String findNextRightBlockItemValue(HashMap item, JSONObject blockItem) {

		int minDistance = 1000000;
		JSONObject minDistanceBlockItem = null;

		int maxLineCount = (int) item.get("MaxLineCount");
		// 如果是多行， 找右边多行的元素，进行文本合并
		if (maxLineCount > 1) {
			return findMultiLineBlockItemValue(blockItem, maxLineCount, false);
		}

		for (int i = 0; i < this.blockItemList.size(); i++) {
			JSONObject tempBlockItem = this.blockItemList.get(i);

			int yAbs = Math.abs(tempBlockItem.getInteger("y") - blockItem.getInteger("y"));
			if (tempBlockItem.getInteger("left") > blockItem.getInteger("x")
					&& yAbs < 2 * blockItem.getInteger("height")) {

				int tempDistance = Math.abs(blockItem.getInteger("y") - tempBlockItem.getInteger("y"))
						+ Math.abs(blockItem.getInteger("right") - tempBlockItem.getInteger("left"));

				if (tempDistance < minDistance) {
					minDistance = tempDistance;
					minDistanceBlockItem = tempBlockItem;
				}
			}
		}
		if(minDistanceBlockItem == null){
			return null;
		}
		return minDistanceBlockItem.getString("text");
	}

	/**
	 * 找到多行的元素
	 * 
	 * @param blockItem
	 * @param maxLineCount
	 * @param isContainSelf 是否包含元素本身， 处理Key和Value 在一起的情况
	 * @return
	 */
	private String findMultiLineBlockItemValue(JSONObject blockItem, int maxLineCount, boolean isContainSelf) {

		List<JSONObject> contentBlockItemList = new ArrayList<>();


		for (int i = 0; i < this.blockItemList.size(); i++) {

			JSONObject curItem = blockItemList.get(i);
			if (!isContainSelf && blockItem.getString("id").equals(curItem.getString("id"))) {
				continue;
			}



			if (curItem.getInteger("top") > blockItem.getInteger("top") - blockItem.getInteger("height")
					&& curItem.getInteger("bottom") < blockItem.getInteger("bottom")
							+ maxLineCount * (blockItem.getInteger("height") + 3)) {
//				logger.info("========================*************  {} ", curItem.getString("text"));
				contentBlockItemList.add(curItem);
			}
		}

		Collections.sort(contentBlockItemList, new Comparator<JSONObject>() {
			@Override
			public int compare(JSONObject jsonObject, JSONObject t1) {
				return jsonObject.getInteger("y") - t1.getInteger("y");
			}
		}); // 按年龄排序

		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < contentBlockItemList.size(); i++) {
			stringBuilder.append(contentBlockItemList.get(i).getString("text"));
		}

		logger.info(stringBuilder.toString()  );
		return stringBuilder.toString();

	}


}
