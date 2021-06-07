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
	// 默认值配置类
	DefaultValueConfig mDefaultConfig ;

	public ParseHorizontalWorker(Map<String, ?> rootConfig, int pageWidth, int pageHeight) {
		this.pageWidth = pageWidth;
		this.pageHeight = pageHeight;
		mDefaultConfig = new DefaultValueConfig((Map<String, ?>)rootConfig.get("DefaultValue"));

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


		int maxLength = Integer.parseInt(mDefaultConfig.getKeyValue(configMap, "LengthMax", ConfigConstants.ITEM_LENGTH_MAX).toString());

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
			int maxLineCount = Integer.parseInt(mDefaultConfig.getKeyValue(configMap, "LineCountMax", ConfigConstants.ITEM_LINE_COUNT_MAX).toString());
			if (maxLineCount > 1) { //多行的情况
				ParseFactory.Cell cell = findMultiLineBlockItemValue(configMap, blockItemList, blockItem, maxLineCount, true);

				if( cell.text.length() < keyWord.length()){
					resultItem.put("value", cell.text);
					resultItem.put("confidence", cell.confidence);
				}else{
					resultItem.put("value", cell.text.substring(keyWord.length()));
					resultItem.put("confidence", cell.confidence);
				}

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
		JSONObject targetBlockItem = null;
		int targetIndex = -1;
		String targetKeyWord = "";
		for (Object key : keyWordList) {
			String keyWord = key.toString();
			boolean isAllMatchFlag = keyWord.startsWith("*");
			if(isAllMatchFlag){
				keyWord = keyWord.substring(1);
			}
			for (int i = 0; i < blockItemList.size(); i++) {
				// 判断关键字
				JSONObject blockItem = blockItemList.get(i);
				String blockText = blockItem.getString("text");
				int index = -1;
//				logger.info("Find keys:     keyword {}          blockText {} ", keyWord, blockText);
				if(isAllMatchFlag){
					index = blockText.indexOf(keyWord);
				}else {
					if(blockText.startsWith(keyWord)){
						index = 0;
					}
				}
//				logger.debug(" Text:  {}   key: {}   {} ", blockText, keyWord, index);
				// 判断范围
				if (index < 0 || !BlockItemUtils.isValidRange(mDefaultConfig, configMap, blockItem)) {
					continue;
				}

				targetBlockItem = blockItem;
				targetIndex = index;
				targetKeyWord = keyWord;
				break;
			}


		}
//        logger.debug("关键字查找【{}】 findFlag:  {}   index: {}  keyType:   {} ", targetBlockItem.getString("text"),
//				findFlag, targetIndex,  targetBlockItem);

		ParseItemResult parseItemResult = new ParseItemResult();
		parseItemResult.index = targetIndex;
		parseItemResult.blockItem = targetBlockItem;
		parseItemResult.keyWord = targetKeyWord;

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

		int maxLineCount =  Integer.parseInt(mDefaultConfig.getKeyValue(configMap, "LineCountMax", ConfigConstants.ITEM_LINE_COUNT_MAX).toString());


		JSONObject borderItem = BlockItemUtils.getBlockItemBorder(mDefaultConfig, configMap, blockItem);
		logger.debug("key-value 分离，关键字【{}】- value 的边界范围  original: [t={}, b={}, l={}, r={} ], border: [t={} b={}, l={}, r={} ]",
				blockItem.getString("text"), blockItem.getInteger("top"), blockItem.getInteger("bottom"),
				blockItem.getInteger("left"), blockItem.getInteger("right"),
				borderItem.getInteger("topBorder"),
				borderItem.getInteger("bottomBorder"),
				borderItem.getInteger("leftBorder"),
				borderItem.getInteger("rightBorder"));

		// 如果是多行， 找右边多行的元素，进行文本合并
		if (maxLineCount > 1) {
			return findMultiLineBlockItemValue(configMap, blockItemList, blockItem, maxLineCount, false);
		}



		for (int i = 0; i < blockItemList.size(); i++) {
			JSONObject tempBlockItem = blockItemList.get(i);
				// 检查候选元素的范围 是否符合要求
			if ( BlockItemUtils.checkBlockItemRangeValidation(tempBlockItem, borderItem) ) {

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
	private ParseFactory.Cell findMultiLineBlockItemValue(HashMap configMap, List<JSONObject> blockItemList, JSONObject blockItem, int maxLineCount, boolean isContainSelf) {

		List<JSONObject> contentBlockItemList = new ArrayList<>();

		JSONObject borderItem = BlockItemUtils.getBlockItemBorder(mDefaultConfig, configMap, blockItem);
		for (int i = 0; i < blockItemList.size(); i++) {
			JSONObject curItem = blockItemList.get(i);
			if (!isContainSelf && blockItem.getString("id").equals(curItem.getString("id"))) {
				continue;
			}

			// 行高的范围判断， 后期可以优化 和范围判断合并。
			if (curItem.getInteger("bottom") <
					blockItem.getInteger("bottom")+ maxLineCount * (blockItem.getInteger("height")
							+ ConfigConstants.PARSE_CELL_ERROR_RANGE_MIN)) {
				//范围的判断
				if(BlockItemUtils.checkBlockItemRangeValidation(curItem, borderItem)){
					contentBlockItemList.add(curItem);
				}

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
		String subKeyWord;
		JSONObject blockItem;

	}

}
