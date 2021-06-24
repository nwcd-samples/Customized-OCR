package cn.nwcdcloud.samples.ocr.parse;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.*;

import static cn.nwcdcloud.samples.ocr.parse.ConfigConstants.DEBUG_PARSE_KEY_VALUE;

public class ParseKeyValueWorker {
	private final Logger logger = LoggerFactory.getLogger(ParseKeyValueWorker.class);

	// 默认值配置类
	DefaultValueConfig mDefaultConfig ;

	public ParseKeyValueWorker(Map<String, ?> rootConfig) {
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
		if(DEBUG_PARSE_KEY_VALUE){
			logger.debug("");
			logger.debug("【KeyValue 查找】------【{}】\nconfig配置: {}", configMap.get("Name"), configMap);
		}
		// step 2. 查找关键字元素
		ParseItemResult parseItemResult = findKeyBlockItem(configMap, blockItemList);
		if (parseItemResult == null ) {
			return null;
		}
		if(DEBUG_PARSE_KEY_VALUE) {

			logger.debug("【1. 找到关键字】 key和value是否分离 [{}] 【{}】  ", parseItemResult.keySeparateFlag,
					BlockItemUtils.generateBlockItemString(parseItemResult.blockItem));
		}
		int index = parseItemResult.index;
		JSONObject blockItem = parseItemResult.blockItem;

		String text = parseItemResult.blockItem.getString("text");
		if(DEBUG_PARSE_KEY_VALUE) {
			logger.debug("【2. 拆分关键字】 index={}  textLength={} keyWord:[{}]  text:[{}]", index, text.length(), parseItemResult.keyWord, text);
		}
		JSONObject resultItem = new JSONObject();
		resultItem.put("name", configMap.get("Name"));


		//step 3. !parseItemResult.keySeparateFlag 是否包含自己的单元格。
		ParseFactory.Cell  cell = findValueBlocksCell(blockItemList,configMap, blockItem, !parseItemResult.keySeparateFlag ,
				parseItemResult.index);
		if(cell == null){
			return null;
		}
		if(DEBUG_PARSE_KEY_VALUE) {
			logger.debug("【4. 查找到的Value】 keyWord:[{}] value: [{}]", parseItemResult.keyWord, cell.text);
		}
		//step 4.  如果key 和value 不是分离的， 需要除去开头的key。
		String value = cell.text;
		if(!parseItemResult.keySeparateFlag){
			resultItem.put("confidence", blockItem.getString("Confidence"));
		}else {
			// [key] : [value]
			resultItem.put("confidence", cell.confidence);
		}
		//step 5. 最大字符限制

		if(DEBUG_PARSE_KEY_VALUE) {
			logger.debug("【3.1 查找到的Value】  value: [{}]", value);
		}
		value = BlockItemUtils.removeInvalidChar(value);
		int maxLength = Integer.parseInt(mDefaultConfig.getKeyValue(configMap, "LengthMax", ConfigConstants.ITEM_LENGTH_MAX).toString());

//		logger.warn(configMap.get("Name") + "maxLength  " + maxLength + "   "+ value);

		String valueType = configMap.getOrDefault("ValueType", "").toString();
		int direction = 0;// key-value 默认从左往右就可以，table 可能会有从右往左的情况。
		value = ParseUtils.processBlockValue(valueType, value, direction);

		if( value.length() > maxLength){
			value = value.substring(0, maxLength);
			if(DEBUG_PARSE_KEY_VALUE){
				logger.debug("【4.1 最大字符数限制】 maxLength={},  裁剪以后的 value=[{}], ", maxLength,  value );
			}
		}

		resultItem.put("value", value.trim());
		if(StringUtils.hasLength(resultItem.getString("value"))){
			if(DEBUG_PARSE_KEY_VALUE){
				logger.debug("【6. END 找到元素】  {} ", resultItem.toJSONString());
			}
			return resultItem;
		}else {
			if(DEBUG_PARSE_KEY_VALUE) {
				logger.debug("【6. END 未找到元素】 ");
			}
			return null;
		}
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
			//去掉特殊字符 进行比较
			keyWord = keyWord.replaceAll("[:： ]", "");
			if(isAllMatchFlag){
				keyWord = keyWord.substring(1);
			}
			for (int i = 0; i < blockItemList.size(); i++) {
				// 判断关键字
				JSONObject blockItem = blockItemList.get(i);
				String blockText = blockItem.getString("text");
				//去掉特殊字符 进行比较
				blockText = blockText.replaceAll("[:： ]", "");

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
				blockItem.put("text", blockText);
				targetBlockItem = blockItem;
				targetIndex = index ;
				targetKeyWord = keyWord;
				break;
			}

		}
		if(targetBlockItem == null){
			return null;
		}

		ParseItemResult parseItemResult = new ParseItemResult();
		parseItemResult.index = targetIndex + targetKeyWord.length() ;
		parseItemResult.blockItem = targetBlockItem;
		parseItemResult.keyWord = targetKeyWord;
		if(DEBUG_PARSE_KEY_VALUE) {
			logger.debug("【0. 查找到关键字的索引】 index={}  targetIndex={}  Text: [{}]", parseItemResult.index, targetIndex, targetBlockItem.getString("text"));
		}
		// 关键字最后一个字符可能是 '冒号'
		String tempString = BlockItemUtils.removeInvalidChar(targetBlockItem.getString("text"));

		if(DEBUG_PARSE_KEY_VALUE) {
			logger.debug("【0.1 关键字查找 】 [{}]  index={}  textLength={}   tempString=[{}]", targetBlockItem.getString("text"),
					targetIndex, tempString.length()  , tempString);
		}

		if(  parseItemResult.index >= tempString.length() ){
			//key 是独立的
			parseItemResult.index = tempString.length();
			parseItemResult.keySeparateFlag = true;
			if(DEBUG_PARSE_KEY_VALUE) {
				logger.debug("【0.2 关键字查找 】 [{}]  index={}  textLength={}  keySeparateFlag={}", targetBlockItem.getString("text"),
						targetIndex, tempString.length(), parseItemResult.keySeparateFlag);
			}
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
	private ParseFactory.Cell findValueBlocksCell(List<JSONObject> blockItemList, HashMap configMap, JSONObject blockItem, boolean isContainSelf,
												 int keyIndex) {
		JSONObject rangeObject = BlockItemUtils.findValueRange(mDefaultConfig,configMap, blockItem);
		if(DEBUG_PARSE_KEY_VALUE) {
			logger.debug("【2.1 Value 的取值范围】[{}]  Text[{}]  isContainSelf=[{}]", rangeObject.toJSONString(), blockItem.getString("text"), isContainSelf);
		}
		//兼容以前版本
		int maxLineCount = Integer.parseInt(mDefaultConfig.getKeyValue(configMap, "LineCountMax", ConfigConstants.ITEM_LINE_COUNT_MAX).toString());
		boolean multiLine = (boolean)mDefaultConfig.getKeyValue(configMap, "MultiLine", false) || maxLineCount >1 ;

		int lengthMax = Integer.parseInt(mDefaultConfig.getKeyValue(configMap, "LengthMax", ConfigConstants.ITEM_LENGTH_MAX).toString());
		List<JSONObject> contentBlockItemList = new ArrayList<>();

		//step 1. 找到Value  的取值范围。
		for (int i = 0; i < blockItemList.size(); i++) {
			JSONObject curItem = blockItemList.get(i);
			// 不包含key所在的blockItem
			if (blockItem.getString("id").equals(curItem.getString("id"))) {
				continue;
			}
			//范围的判断
			if(BlockItemUtils.checkBlockItemRangeValidation(curItem, rangeObject)){
				contentBlockItemList.add(curItem);
//					logger.warn("CurrItem: {} ", BlockItemUtils.generateBlockItemString(curItem));
			}

		}
		//未找到元素
		double compareHeightRate = ConfigConstants.COMPARE_HEIGHT_RATE;
		if(!multiLine  && configMap.get("ValueXRangeMax") == null){
			//尽量找坐标靠的比较近的元素
			compareHeightRate = 0.2d;
		}

		ParseFactory.Cell cell = new ParseFactory.Cell();
		// step 2. 如果没有设置ValueXRangeMax, 并且是单行， 默认只识别最近的单元格。
		if(DEBUG_PARSE_KEY_VALUE) {
			for (int i = 0; i < contentBlockItemList.size(); i++) {
				logger.debug("【2.2 查找到可选的Item】 [{}]", BlockItemUtils.generateBlockItemString(contentBlockItemList.get(i)));
			}
		}

		String keyBlockText = "";
		keyBlockText = blockItem.getString("text");
		keyBlockText = keyBlockText.substring(keyIndex  );
		keyBlockText = keyBlockText.replaceAll("[：:]", "");
		if(DEBUG_PARSE_KEY_VALUE) {
			logger.debug("【3.0 查找key里面包含的value值】  keyIndex=[{}]  候选元素个数=[{}]   Text=[{}]  Value=[{}]  ",
					keyIndex, contentBlockItemList.size(), blockItem.getString("text"), keyBlockText);
		}


		/**
		 *有两种情况会返回单个元素的value ，
		 * 1. 有多个候选元素， 但是剩余的value 已经大于最大字符
		 * 2. 指定范围内， 没有其它候选元素
		 */
		if( (contentBlockItemList.size()>0 && keyBlockText.length() >= lengthMax)
				|| (contentBlockItemList.size()==0  )
				|| (configMap.get("ValueXRangeMax") == null && !multiLine  &&  keyBlockText.length()>=1 )
		) {
			cell.text = keyBlockText ;
			cell.confidence = blockItem.getFloat("Confidence");
			if(DEBUG_PARSE_KEY_VALUE) {
				logger.debug("【3.1 [key:value] 在一起 单独返回】   Value=[{}]  ", keyBlockText);
			}
			return cell ;
		}

		contentBlockItemList.sort(new BlockItemComparator(compareHeightRate));

		if(!multiLine  && configMap.get("ValueXRangeMax") == null){
			JSONObject item = contentBlockItemList.get(0);
			cell.text = keyBlockText + item.getString("text");
			cell.confidence = item.getFloat("Confidence");

			if(DEBUG_PARSE_KEY_VALUE) {
				logger.debug("【3.2 [key:value1] [value2] 返回 value1 + value2】   value1=[{}]  value2=[{}]  ",
						keyBlockText, item.getString("text"));
			}
			return cell ;
		}

		StringBuilder stringBuilder = new StringBuilder();
		// step 3. 多行元素， 寻找置信度最小的作为置信度 。  查找所有落在区域里面的单元格， 进行合并，然后返回合并后的文字。
		float minConfidence  = 1.0f;
		if(StringUtils.hasLength(keyBlockText)){
			stringBuilder.append(keyBlockText);
		}


		List<String> stopWordList = (List) configMap.getOrDefault("StopWordList", new ArrayList<>());
		for (int i = 0; i < contentBlockItemList.size(); i++) {

			String text = contentBlockItemList.get(i).getString("text");
			//下一行停用词判断
			boolean stopFlag = false;
			for(String stopWord: stopWordList){
				if(StringUtils.hasLength(text) && text.startsWith(stopWord)){
					stopFlag = true;
					if(DEBUG_PARSE_KEY_VALUE) {
						logger.debug("【\t 3.2.1   停用词 [{}]  停止查找  】   Text [{}]    ", stopWord, text);
					}
				}

			}
			if(stopFlag){

				break;
			}

			stringBuilder.append(text);
			// 不是最后一个元素， 并且是英文结尾加空格
			if(i< contentBlockItemList.size()-1 && ParseUtils.isEnglishLastChar(text)){
				stringBuilder.append(" ");
			}

			float confidence = contentBlockItemList.get(i).getFloat("Confidence");
			if(confidence < minConfidence){
				minConfidence = confidence;
			}
		}


		cell.text = stringBuilder.toString();
		cell.confidence = minConfidence;


		if(DEBUG_PARSE_KEY_VALUE) {
			logger.debug("【3.3 [key-value 多行 返回 value1 + value2】   value=[{}]  ",
					cell.text);
		}
		return cell ;
	}

	private static class ParseItemResult{
		//关键字的起始位置， 0 表示从头开始
		int index;
		String keyWord;
		JSONObject blockItem;
		//表示当前Block的Text是否全是key， True 表示 和Value 分离 [key][value];  False [key:value] 合在一起
		boolean keySeparateFlag = false;

	}

}
