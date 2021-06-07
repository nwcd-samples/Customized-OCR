package cn.nwcdcloud.samples.ocr.parse;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ParseHorizontalWorker {
	private final Logger logger = LoggerFactory.getLogger(ParseHorizontalWorker.class);

	// 默认值配置类
	DefaultValueConfig mDefaultConfig ;

	public ParseHorizontalWorker(Map<String, ?> rootConfig) {
		mDefaultConfig = new DefaultValueConfig((Map<String, ?>)rootConfig.get("DefaultValue"));
	}

	/**
	 * 处理单个水平元素
	 * @param configMap 配置文件Map
	 * @param blockItemList  元素列表
	 * @return  解析后的json 文件
	 */
	public JSONObject parse(HashMap configMap, List<JSONObject> blockItemList) {
		//step 1. 通过关键字进行 key 元素的定位
		if(!configMap.containsKey("Name")){
			throw new IllegalArgumentException(" 配置文件必须包含  'Name' 选项 ");
		}
		// step 2. 查找关键字元素
		ParseItemResult parseItemResult = findKeyBlockItem(configMap, blockItemList);
		if (parseItemResult.blockItem == null) {
//			logger.warn("Parse key-value   没有找到 : text {}  ", configMap.get("Name"));
			return null;
		}

		logger.info("找到关键字  {} " ,  BlockItemUtils.generateBlockItemString(parseItemResult.blockItem));

		int index = parseItemResult.index;
		JSONObject blockItem = parseItemResult.blockItem;
//		logger.debug("index {}  text {} text length: {}   keyWord {} ", index, text, text.length(), keyWord);

		JSONObject resultItem = new JSONObject();
		resultItem.put("name", configMap.get("Name"));


		//step 3. !parseItemResult.keySeparateFlag 是否包含自己的单元格。
		ParseFactory.Cell  cell = findValueBlocksCell(blockItemList,configMap, blockItem, !parseItemResult.keySeparateFlag);

		logger.debug("Target index: {}  text length: {}   Text: {}   keySeparateFlag: {} , cell.text: [{}]",
				index, blockItem.getString("text").length() ,blockItem.getString("text")
					, parseItemResult.keySeparateFlag, cell.text);

		//step 4.  如果key 和value 不是分离的， 需要除去开头的key。
		String value = cell.text;
		if(!parseItemResult.keySeparateFlag){
			// [key:value]
			logger.info("Index={} , value [{}], keyWord [{}]", index, value, parseItemResult.keyWord );
			value = value.substring(index);
			resultItem.put("confidence", blockItem.getString("Confidence"));
		}else {
			// [key] : [value]
			resultItem.put("confidence", cell.confidence);
		}
		//step 5. 最大字符限制
		int maxLength = Integer.parseInt(mDefaultConfig.getKeyValue(configMap, "LengthMax", ConfigConstants.ITEM_LENGTH_MAX).toString());
		if( value.length() > maxLength){
			value = value.substring(0, maxLength);
		}

		resultItem.put("value", BlockItemUtils.removeInvalidChar(value));

		return resultItem;
	}


	/**
	 * 定位关键字
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
				targetIndex = index ;
				targetKeyWord = keyWord;
				break;
			}

		}

		ParseItemResult parseItemResult = new ParseItemResult();
		parseItemResult.index = targetIndex + targetKeyWord.length() ;
		parseItemResult.blockItem = targetBlockItem;
		parseItemResult.keyWord = targetKeyWord;

		if(targetBlockItem == null){
			return parseItemResult;
		}
		logger.debug("[Find index]   targetIndex: {}  Text: [{}]", targetIndex ,targetBlockItem.getString("text"));
		if(parseItemResult.index == targetBlockItem.getString("text").length()){
			//key 是独立的
			parseItemResult.keySeparateFlag = true;
			logger.debug("关键字查找【{}】    index: {}  keyType:   {} ", targetBlockItem.getString("text"),
					targetIndex, targetBlockItem);
		}

		return parseItemResult;

	}

	/**
	 * 查找Value 元素的文本和置信度
	 * @param blockItemList
	 * @param configMap
	 * @param blockItem
	 * @return
	 */
	private ParseFactory.Cell findValueBlocksCell(List<JSONObject> blockItemList, HashMap configMap, JSONObject blockItem, boolean isContainSelf) {
		JSONObject rangeObject = BlockItemUtils.findValueRange(mDefaultConfig,configMap, blockItem);


		int maxLineCount = Integer.parseInt(mDefaultConfig.getKeyValue(configMap, "LineCountMax", ConfigConstants.ITEM_LINE_COUNT_MAX).toString());
		List<JSONObject> contentBlockItemList = new ArrayList<>();

		for (int i = 0; i < blockItemList.size(); i++) {
			JSONObject curItem = blockItemList.get(i);
//			logger.debug("-------------------   1  {} " , curItem.getString("text"));
			if (!isContainSelf && blockItem.getString("id").equals(curItem.getString("id"))) {
				continue;
			}
			// 行高的范围判断， 后期可以优化 和范围判断合并。
			if (curItem.getDouble("yMax") <
					blockItem.getDouble("yMax")+ maxLineCount * (blockItem.getDouble("heightRate")
							+ ConfigConstants.PARSE_CELL_ERROR_RANGE_MIN)) {
//				logger.debug("-------------------   2 {} ", curItem.getString("text"));
				//范围的判断
				if(BlockItemUtils.checkBlockItemRangeValidation(curItem, rangeObject)){
					contentBlockItemList.add(curItem);
//					logger.debug("CurrItem: {} ", BlockItemUtils.generateBlockItemString(curItem));
				}

			}
		}


		contentBlockItemList.sort(new BlockItemComparator());
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
		logger.debug("findValueBlocksCell:   [{}]", cell.text);
		cell.confidence = minConfidence;
		return cell ;
	}

	private static class ParseItemResult{
		//关键字的其实位置， 0 表示从头开始
		int index;
		String keyWord;
		JSONObject blockItem;
		//表示当前Block的Text是否全是key， True 表示 和Value 分离 [key][value];  False [key:value] 合在一起
		boolean keySeparateFlag = false;

	}

}
