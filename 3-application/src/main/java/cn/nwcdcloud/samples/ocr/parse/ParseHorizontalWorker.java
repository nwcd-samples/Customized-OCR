package cn.nwcdcloud.samples.ocr.parse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ParseHorizontalWorker {
	private final Logger logger = LoggerFactory.getLogger(ParseHorizontalWorker.class);

	private int pageWidth;
	private int pageHeight;
	private List<JSONObject> blockItemList;

	public ParseHorizontalWorker(int pageWidth, int pageHeight, List<JSONObject> blockItemList) {
		this.pageWidth = pageWidth;
		this.pageHeight = pageHeight;
		this.blockItemList = blockItemList;
	}
	/**
	 * 处理单个水平元素
	 * @param configMap
	 * @param blockItemList
	 * @return
	 */
	public JSONObject parse(HashMap configMap, List<JSONObject> blockItemList) {
		//step 1. 通过关键字进行 key 元素的定位
		if(!configMap.containsKey("Name")){
			throw new IllegalArgumentException(" 配置文件必须包含  'Name' 选项 ");
		}
		JSONObject keyBlockItemResult = findKeyBlockItem(configMap, blockItemList);
		JSONObject blockItem = keyBlockItemResult.getJSONObject("blockItem");
		if (blockItem == null) {
			logger.warn("doHorizontal   没有找到 : text {}  ", configMap.get("Name"));
			return null;
		}

		int index = keyBlockItemResult.getInteger("index");
		String text = blockItem.getString("text");
		if(text.endsWith(":") || text.endsWith("：")){
			text = text.replaceAll(":", "");
			text = text.replaceAll("：", "");
		}

		String keyWord = keyBlockItemResult.getString("keyWord");
		logger.debug("index {}  text {} text length: {}   keyWord {} ", index, text, text.length(), keyWord);

		// case 1. 关键字和值 在一个单元格里面

		JSONObject resultItem = new JSONObject();

		resultItem.put("name", configMap.get("Name"));
		resultItem.put("confidence", blockItem.getString("Confidence"));


		int maxLength = Integer.valueOf(configMap.getOrDefault("MaxLength", 10).toString());
		if(keyBlockItemResult.get("subKeyWord") != null ){
			// case  1:   'key1'   'key2value'
			int lastIndex = text.length() > index  + maxLength
					? index  + maxLength
					: text.length();
			resultItem.put("value", text.substring(index , lastIndex));
		}else if (index + keyWord.length() < text.length()) {
			// case  2:   'key:value'
			// key和value 在一个单元格里
			int maxLineCount =  Integer.valueOf(configMap.getOrDefault("MaxLineCount", 1).toString());
			if (maxLineCount > 1) { //多行的情况
				String mergeValue = findMultiLineBlockItemValue(blockItem, maxLineCount, true);
				resultItem.put("value", mergeValue.substring(keyWord.length()));
			} else {
				logger.info("--------------   4  index: {}  keyword length : {} ", index, keyWord.length());
				int lastIndex = text.length() > index + keyWord.length() + maxLength
						? index + keyWord.length() + maxLength
						: text.length();
				resultItem.put("value", text.substring(index + keyWord.length(), lastIndex));
			}
		} else {
			// value 单独在一个单元格里
			// case  3:   'key' 'value'
			String blockItemValue = findNextRightBlockItemValue(configMap, blockItem);

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
	 * 定位关键字
	 *
	 * @param configMap
	 * @param blockItemList
	 */
	private JSONObject findKeyBlockItem(HashMap configMap, List<JSONObject> blockItemList) {


		List keyWordList = (List) configMap.getOrDefault("KeyWordList", new ArrayList<>());
		keyWordList.add(configMap.get("Name"));
		//keyword 名称进行排重操作
		Set<String> keySet = new HashSet<>(keyWordList);
		keyWordList = new ArrayList<>(keySet);


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
				if (!BlockItemUtils.isValidRange(configMap, blockItem, this.pageWidth,  this.pageHeight)) {
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
				 JSONObject result = findResultBySplitKeywords(configMap, blockItemList, keyWord);
				 if(result.getJSONObject("blockItem") != null){
				 	return result;
				 }
			}

		}
        logger.debug("关键字查找  findFlag:  {}   index: {}  keyType:   {} ", findFlag, targetIndex,  targetBlockItem);

		JSONObject result = new JSONObject();
		result.put("index", targetIndex);
		result.put("blockItem", targetBlockItem);
		result.put("keyWord", targetKeyWord);
		result.put("keyType", "single"); // 关键字在一个单元格里面
		return result;

	}

	/**
	 * 如果没有找到， 尝试将关键字拆分开， 然后进行查找， 第一个字符和最后一个字符
	 * @param configMap
	 * @param blockItemList
	 * @return
	 */
	private JSONObject findResultBySplitKeywords(HashMap configMap, List<JSONObject> blockItemList, String targetKeyWord){

		if(targetKeyWord == null || targetKeyWord.length()<2){
			logger.warn("关键字设置不正确 {} ", JSON.toJSON(configMap));
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
	 * 找到[key]元素右边一个[value]单元格
	 *
	 * @param configMap
	 * @param blockItem
	 * @return
	 */
	private String findNextRightBlockItemValue(HashMap configMap, JSONObject blockItem) {
		int minDistance = Integer.MAX_VALUE;
		JSONObject minDistanceBlockItem = null;

		int maxLineCount =  Integer.valueOf(configMap.getOrDefault("MaxLineCount", 1).toString());

		double topOffsetRadio = (double) configMap.getOrDefault("TopOffsetRadio", 1.2d);
		double bottomOffsetRadio = (double) configMap.getOrDefault("BottomOffsetRadio", 1.2d);
		double leftOffsetRadio = (double) configMap.getOrDefault("LeftOffsetRadio", 0.0d);
		double rightOffsetRadio = (double) configMap.getOrDefault("RightOffsetRadio", 5d);

		// 如果是多行， 找右边多行的元素，进行文本合并
		if (maxLineCount > 1) {
			return findMultiLineBlockItemValue(blockItem, maxLineCount, false);
		}
		int topBorder = blockItem.getInteger("top") - (int) (topOffsetRadio * blockItem.getInteger("height"));
		int bottomBorder = blockItem.getInteger("bottom") + (int) (bottomOffsetRadio * blockItem.getInteger("height"));
		int leftBorder = blockItem.getInteger("right") - (int) (leftOffsetRadio * blockItem.getInteger("width"));
		int rightBorder = blockItem.getInteger("right") + (int) (rightOffsetRadio * blockItem.getInteger("width"));
		logger.info("{}  original: [t={}, b={}, l={}, r={} ], border: [t={} b={}, l={}, r={} ]",
				blockItem.getString("text"), blockItem.getInteger("top"), blockItem.getInteger("bottom"),

				blockItem.getInteger("left"), blockItem.getInteger("right"),
				topBorder, bottomBorder, leftBorder, rightBorder);


		for (int i = 0; i < this.blockItemList.size(); i++) {
			JSONObject tempBlockItem = this.blockItemList.get(i);

//			int yAbs = Math.abs(tempBlockItem.getInteger("y") - blockItem.getInteger("y"));
			if (tempBlockItem.getInteger("left") > blockItem.getInteger("x")
					&&  tempBlockItem.getInteger("top") >= topBorder
			        &&  tempBlockItem.getInteger("bottom") <= bottomBorder
					&&  tempBlockItem.getInteger("left") >= leftBorder
					&&  tempBlockItem.getInteger("right") <= rightBorder
			) {

				// 在上下左右范围内， 找到离 key 元素 最近的元素 作为value 。
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
				//FIXME:  多行的时候， 可以设置行高的参数， 现在hard code， 行高 * (height + 3)
				contentBlockItemList.add(curItem);
			}
		}

		Collections.sort(contentBlockItemList, new Comparator<JSONObject>() {
			@Override
			public int compare(JSONObject jsonObject, JSONObject t1) {
				return jsonObject.getInteger("y") - t1.getInteger("y");
			}
		}); // 按y大小排序

		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < contentBlockItemList.size(); i++) {
			stringBuilder.append(contentBlockItemList.get(i).getString("text"));
		}
		return stringBuilder.toString();
	}

}
