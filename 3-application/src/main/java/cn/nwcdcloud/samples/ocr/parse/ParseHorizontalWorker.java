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

	public ParseHorizontalWorker(int pageWidth, int pageHeight) {
		this.pageWidth = pageWidth;
		this.pageHeight = pageHeight;
	}

	/**
	 * 处理单个水平元素
	 * @param configMap 配置文件Map
	 * @param blockItemList  元素列表
	 * @return  解析后的json 文件
	 */
	public JSONObject parse(HashMap configMap, List<JSONObject> blockItemList) {
		//step 0. 通过关键字进行 key 元素的定位
		if(!configMap.containsKey("Name")){
			throw new IllegalArgumentException(" 配置文件必须包含  'Name' 选项 ");
		}
		ParseItemResult parseItemResult = findKeyBlockItem(configMap, blockItemList);
		if (parseItemResult.blockItem == null) {
//			logger.warn("Parse key-value   没有找到 : text {}  ", configMap.get("Name"));
			return null;
		}

		logger.info("找到关键字  {} " ,  BlockItemUtils.generateBlockItemString(parseItemResult.blockItem));

		int index = parseItemResult.index;
		JSONObject blockItem = parseItemResult.blockItem;
		String text = parseItemResult.blockItem.getString("text");
		if(text.endsWith(":") || text.endsWith("：")){
			text = text.replaceAll(":", "");
			text = text.replaceAll("：", "");
		}

		String keyWord = parseItemResult.keyWord;
//		logger.debug("index {}  text {} text length: {}   keyWord {} ", index, text, text.length(), keyWord);

		// case 1. 关键字和值 在一个单元格里面

		JSONObject resultItem = new JSONObject();

		resultItem.put("name", configMap.get("Name"));
		resultItem.put("confidence", blockItem.getString("Confidence"));


		int maxLength = Integer.parseInt(configMap.getOrDefault("LengthMax", ConfigConstants.ITEM_LENGTH_MAX).toString());
		if(parseItemResult.subKeyWord != null ){
//			logger.debug("----------- case  1.     key  value  分开");
			// case  1:   'key1'   'key2value'  [姓        名：张三]    解决关键字分成两块的情况
//			logger.debug("'key1'   'key2value' 分类的情况   第二个key = {}    text: {} ", parseItemResult.subKeyWord , text);
			int lastIndex = text.length() > index  + maxLength
					? index  + maxLength
					: text.length();
			resultItem.put("value", text.substring(index , lastIndex));
		}else if (index + keyWord.length() < text.length()) {
//			logger.debug("----------- case  2.     key：value 在一个单元格");
			// case  2:   'key:value'
			// key和value 在一个单元格里
			int maxLineCount =  Integer.parseInt(configMap.getOrDefault("LineCountMax", ConfigConstants.ITEM_LINE_COUNT_MAX).toString());
			if (maxLineCount > 1) { //多行的情况
				ParseFactory.Cell cell = findMultiLineBlockItemValue(blockItemList, blockItem, maxLineCount, true);
				resultItem.put("value", cell.text.substring(keyWord.length()));
				resultItem.put("confidence", cell.confidence);
			} else {
//				logger.debug(" Key Value 在同一个单元格内 index: {}  keyword length : {} ", index, keyWord.length());
				int lastIndex = text.length() > index + keyWord.length() + maxLength
						? index + keyWord.length() + maxLength
						: text.length();
				resultItem.put("value", text.substring(index + keyWord.length(), lastIndex));
			}
		} else {
			// value 单独在一个单元格里
			// case  3:   'key'   'value'
//			logger.debug("----------- case  3.     'key'   'value'  分开 ");
			ParseFactory.Cell cell = findNextRightBlockItemValue(blockItemList,  configMap, blockItem);

			if (cell == null || cell.text == null) {
//				logger.debug("没有找到Value  {} " , BlockItemUtils.generateBlockItemString(blockItem));
				return null;
			}
			resultItem.put("value", cell.text);
			resultItem.put("confidence", cell.confidence);
		}

		if (ConfigConstants.PARSE_KEY_TYPE_MULTIPLE.equals(parseItemResult.keyType)) {
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
	private ParseItemResult findKeyBlockItem(HashMap configMap, List<JSONObject> blockItemList) {


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
//				logger.debug(" Text:  {}   key: {}   {} ", blockText, keyWord, index);
				// 判断范围
				if (index < 0 || !BlockItemUtils.isValidRange(configMap, blockItem, this.pageWidth,  this.pageHeight)) {
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
				ParseItemResult result = findResultBySplitKeywords(configMap, blockItemList, keyWord);
				 if(result != null && result.blockItem != null){
				 	return result;
				 }
			}

		}
//        logger.debug("关键字查找【{}】 findFlag:  {}   index: {}  keyType:   {} ", targetBlockItem.getString("text"),
//				findFlag, targetIndex,  targetBlockItem);

		ParseItemResult parseItemResult = new ParseItemResult();
		parseItemResult.index = targetIndex;
		parseItemResult.blockItem = targetBlockItem;
		parseItemResult.keyType = ConfigConstants.PARSE_KEY_TYPE_SINGLE;  // 关键字在一个单元格里面
		parseItemResult.keyWord = targetKeyWord;

		return parseItemResult;

	}

	/**
	 * 如果没有找到， 尝试将关键字拆分开， 然后进行查找， 第一个字符和最后一个字符
	 *
	 * 比较极端的情况  例如 【名    称】 两个字符离的比较远， 识别成了两个元素， 用'名' 和'称' 两个字同时去匹配。
	 * @param configMap
	 * @param blockItemList
	 * @return
	 */
	private ParseItemResult findResultBySplitKeywords(HashMap configMap, List<JSONObject> blockItemList, String targetKeyWord){

		if(targetKeyWord == null || targetKeyWord.length()<2){
			logger.warn("关键字【{}】长度不够， 不需要进行拆分 ", JSON.toJSON(configMap));
			return null;
		}


		int lastKeyLength =1;
		String startKey = targetKeyWord.substring(0,lastKeyLength);
		String endKey = targetKeyWord.substring(targetKeyWord.length()-lastKeyLength);

		JSONObject startBlockItem = null;


//		logger.debug("关键字=[{}] 被分成了两个， 一起进行查找，  firstKey=[{}] , endKey=[{}]", targetKeyWord , startKey, endKey);
		// 找到开始的关键字
		for (int i = 0; i < blockItemList.size(); i++) {
			JSONObject blockItem = blockItemList.get(i);
			String blockText = blockItem.getString("text").trim();
			if(blockText.equals(startKey)){
				startBlockItem = blockItem;
			}
		}

		if(startBlockItem == null){

			ParseItemResult parseItemResult = new ParseItemResult();
			parseItemResult.index = lastKeyLength;
			parseItemResult.keyType = ConfigConstants.PARSE_KEY_TYPE_SINGLE;  // 关键字在一个单元格里面
			parseItemResult.keyWord = targetKeyWord;
			parseItemResult.subKeyWord = endKey;

			return parseItemResult;
		}
		logger.debug("关键字=[{}] 被分成了两个，找到第一个元素， firstKey=[{}] , endKey=[{}] startBlockItem={} ", targetKeyWord , startKey, endKey, JSON.toJSON(startBlockItem));
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
		logger.debug("关键字=[{}] 被分成了两个，找到第一个元素， firstKey=[{}] , endKey=[{}] endBlockItem= {} ", targetKeyWord , startKey, endKey, JSON.toJSON(targetBlockItem));


		ParseItemResult parseItemResult = new ParseItemResult();
		parseItemResult.index = 1;
		parseItemResult.blockItem = targetBlockItem;
		parseItemResult.keyType = ConfigConstants.PARSE_KEY_TYPE_SINGLE;  // 关键字在一个单元格里面
		parseItemResult.keyWord = targetKeyWord;
		parseItemResult.subKeyWord = endKey;

		return parseItemResult;

	}

	/**
	 * 找到[key]元素右边一个[value]单元格
	 * @param blockItemList  元素列表
	 * @param configMap 配置文件
	 * @param blockItem  块元素
	 * @return
	 */
	private ParseFactory.Cell findNextRightBlockItemValue(List<JSONObject> blockItemList, HashMap configMap, JSONObject blockItem) {
		int minDistance = Integer.MAX_VALUE;
		JSONObject minDistanceBlockItem = null;

		int maxLineCount =  Integer.parseInt(configMap.getOrDefault("LineCountMax", 1).toString());

		double topOffsetRadio = BlockItemUtils.getDoubleValueFromConfig(configMap, "TopOffsetRadio", ConfigConstants.ITEM_OFFSET_TOP_RADIO);
		double bottomOffsetRadio = BlockItemUtils.getDoubleValueFromConfig(configMap, "BottomOffsetRadio", ConfigConstants.ITEM_OFFSET_BOTTOM_RADIO);
		double leftOffsetRadio = BlockItemUtils.getDoubleValueFromConfig(configMap, "LeftOffsetRadio", ConfigConstants.ITEM_OFFSET_LEFT_RADIO);
		double rightOffsetRadio = BlockItemUtils.getDoubleValueFromConfig(configMap, "RightOffsetRadio", ConfigConstants.ITEM_OFFSET_RIGHT_RADIO);
		// 如果是多行， 找右边多行的元素，进行文本合并
		if (maxLineCount > 1) {
			return findMultiLineBlockItemValue(blockItemList, blockItem, maxLineCount, false);
		}
		int topBorder = blockItem.getInteger("top") - (int) (topOffsetRadio * blockItem.getInteger("height"));
		int bottomBorder = blockItem.getInteger("bottom") + (int) (bottomOffsetRadio * blockItem.getInteger("height"));
		//ConfigConstants.PARSE_CELL_ERROR_RANGE_MAX 加一个误差值
		int leftBorder = blockItem.getInteger("x") - (int) (leftOffsetRadio * blockItem.getInteger("width") );
		int rightBorder = blockItem.getInteger("right") + (int) (rightOffsetRadio * blockItem.getInteger("width"));
		logger.debug("key-value 分离，关键字【{}】- value 的边界范围  original: [t={}, b={}, l={}, r={} ], border: [t={} b={}, l={}, r={} ]",
				blockItem.getString("text"), blockItem.getInteger("top"), blockItem.getInteger("bottom"),
				blockItem.getInteger("left"), blockItem.getInteger("right"),
				topBorder, bottomBorder, leftBorder, rightBorder);


		for (int i = 0; i < blockItemList.size(); i++) {
			JSONObject tempBlockItem = blockItemList.get(i);

//			int yAbs = Math.abs(tempBlockItem.getInteger("y") - blockItem.getInteger("y"));
			if (    tempBlockItem.getInteger("top") >= topBorder
			        &&  tempBlockItem.getInteger("bottom") <= bottomBorder
					&&  tempBlockItem.getInteger("left") >= leftBorder
					&&  tempBlockItem.getInteger("right") <= rightBorder
			) {

				// 在上下左右范围内， 找到离 key 元素 最近的元素 作为value, 寻找直线距离最近的元素
				int tempDistance = (int)(Math.pow((double) (blockItem.getInteger("y") - tempBlockItem.getInteger("y")), 2)
						+ Math.pow((double) (blockItem.getInteger("right") - tempBlockItem.getInteger("left")), 2));

				if (tempDistance < minDistance) {
					minDistance = tempDistance;
					minDistanceBlockItem = tempBlockItem;
				}
			}
		}
		if(minDistanceBlockItem == null){
			return null;
		}
		ParseFactory.Cell cell = new ParseFactory.Cell();
		cell.text = minDistanceBlockItem.getString("text");
		cell.confidence = minDistanceBlockItem.getFloat("Confidence");
		return cell;
	}

	/**
	 * 找到多行的元素
	 *
	 * @param blockItem
	 * @param maxLineCount
	 * @param isContainSelf 是否包含元素本身， 处理Key和Value 在一起的情况
	 * @return
	 */
	private ParseFactory.Cell findMultiLineBlockItemValue(List<JSONObject> blockItemList, JSONObject blockItem, int maxLineCount, boolean isContainSelf) {

		List<JSONObject> contentBlockItemList = new ArrayList<>();


		for (int i = 0; i < blockItemList.size(); i++) {
			JSONObject curItem = blockItemList.get(i);
			if (!isContainSelf && blockItem.getString("id").equals(curItem.getString("id"))) {
				continue;
			}

			if (curItem.getInteger("top") > blockItem.getInteger("top") - blockItem.getInteger("height")
					&& curItem.getInteger("bottom") <
					blockItem.getInteger("bottom")+ maxLineCount * (blockItem.getInteger("height") + ConfigConstants.PARSE_CELL_ERROR_RANGE_MIN)) {
				contentBlockItemList.add(curItem);
			}
		}

		Collections.sort(contentBlockItemList, new Comparator<JSONObject>() {
			@Override
			public int compare(JSONObject jsonObject, JSONObject t1) {
				return jsonObject.getInteger("y") - t1.getInteger("y");
			}
		}); // 按y大小排序

		//多行元素， 寻找置信度最小的作为置信度
		StringBuilder stringBuilder = new StringBuilder();
		float minConfidence  = 1.0f;
		for (int i = 0; i < contentBlockItemList.size(); i++) {
			stringBuilder.append(contentBlockItemList.get(i).getString("text"));
			float confidence = contentBlockItemList.get(i).getFloat("Confidence");
			if(confidence < minConfidence){
				minConfidence = confidence;
			}
		}
		ParseFactory.Cell cell = new ParseFactory.Cell();
		cell.text = stringBuilder.toString();
		cell.confidence = minConfidence;
		return cell ;
	}

	private static class ParseItemResult{
		int index;
		String keyWord;
		String keyType;
		String subKeyWord;
		JSONObject blockItem;

	}

}
