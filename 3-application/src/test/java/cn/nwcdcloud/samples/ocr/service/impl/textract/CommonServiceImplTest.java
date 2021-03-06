package cn.nwcdcloud.samples.ocr.service.impl.textract;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.nwcdcloud.samples.ocr.parse.BlockItemUtils;
import cn.nwcdcloud.samples.ocr.parse.ConfigConstants;
import cn.nwcdcloud.samples.ocr.parse.FileUtils;
import cn.nwcdcloud.samples.ocr.parse.ParseFactory;

public class CommonServiceImplTest {
	private static final Logger logger = LoggerFactory.getLogger(CommonServiceImplTest.class);
	private static final String ID_SAMPLE_JSON_OBJECT_FILE_1 = "/samples/id001.json";
	private static final String ID_SAMPLE_JSON_OBJECT_FILE_2 = "/samples/id002.json";
	private static final String ID_SAMPLE_JSON_OBJECT_FILE_3 = "/samples/id003.json";



	@Test
	public void parseId001() {

		String jsonObjectPath = this.getClass().getResource(ID_SAMPLE_JSON_OBJECT_FILE_1).getFile().toString();
		JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
		List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject);
		ParseFactory parseJsonUtil = new ParseFactory("id02");
		JSONObject resultObject = parseJsonUtil.extractValue(blockItemList);
		JSONArray  resultArray =  resultObject.getJSONArray("keyValueList");
		for(int i=0; i< resultArray.size() ; i++){
			JSONObject item = (JSONObject) resultArray.get(i);
			logger.info("name: [{}]  value: [{}]", item.getString("name"), item.getString("value"));
		}
		assert checkKeyValueMap(resultArray, "姓名", "代用名");
		assert checkKeyValueMap(resultArray, "性别", "男");
		assert checkKeyValueMap(resultArray, "民族", "汉");
		assert checkKeyValueMap(resultArray, "出生", "2013年05月06日");
		assert checkKeyValueMap(resultArray, "住址", "湖南省长沙市开福区巡道街幸福小区居民组");
		assert checkKeyValueMap(resultArray, "公民身份号码", "430512198908131367");

	}

	@Test
	public void parseId002() {

		String jsonObjectPath = this.getClass().getResource(ID_SAMPLE_JSON_OBJECT_FILE_2).getFile().toString();
		JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
		List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject);
		ParseFactory parseJsonUtil = new ParseFactory("id02");
		JSONObject resultObject = parseJsonUtil.extractValue(blockItemList);
        JSONArray  resultArray =  resultObject.getJSONArray("keyValueList");
		for(int i=0; i< resultArray.size() ; i++){
			JSONObject item = (JSONObject) resultArray.get(i);
			logger.info("name: [{}]  value: [{}]", item.getString("name"), item.getString("value"));
		}
		assert checkKeyValueMap(resultArray, "姓名", "李四");
		assert checkKeyValueMap(resultArray, "性别", "男");
		assert checkKeyValueMap(resultArray, "民族", "汉");
		assert checkKeyValueMap(resultArray, "出生", "1992年9月10日");
		assert checkKeyValueMap(resultArray, "住址", "北京市海淀区银网中心1号楼2202号");
		assert checkKeyValueMap(resultArray, "公民身份号码", "21112419920910123");

	}

	@Test
	public void parseId003() {

		String jsonObjectPath = this.getClass().getResource(ID_SAMPLE_JSON_OBJECT_FILE_3).getFile().toString();
		JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
		List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject);
		ParseFactory parseJsonUtil = new ParseFactory("id02");
		JSONObject resultObject = parseJsonUtil.extractValue(blockItemList);
		JSONArray  resultArray =  resultObject.getJSONArray("keyValueList");
		logger.info(resultArray.toJSONString());
		assert checkKeyValueMap(resultArray, "姓名", "韦小宝");
		assert checkKeyValueMap(resultArray, "性别", "男");
		assert checkKeyValueMap(resultArray, "民族", "汉");
		assert checkKeyValueMap(resultArray, "住址", "北京市东城区景山前街4号紫禁城敬事房");

		assert checkKeyValueMap(resultArray, "出生", "1654年12月20日");
		assert checkKeyValueMap(resultArray, "公民身份号码", "112044165412202438");

	}

	private boolean checkKeyValueMap(JSONArray array, String name, String value) {
		for (int i = 0; i < array.size(); i++) {
			String tempValue = array.getJSONObject(i).getString("value");
			String tempName = array.getJSONObject(i).getString("name");
//            logger.info("{}        {}    {} ", key, value, tempValue);
			if (value.equals(tempValue) && name.equals(tempName)) {
				return true;
			}
		}
		return false;

	}

}