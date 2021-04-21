package cn.nwcdcloud.samples.ocr.parse;

import java.io.InputStream;
import java.util.*;

import org.ho.yaml.Yaml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class ParseFactory {
	private final Logger logger = LoggerFactory.getLogger(ParseFactory.class);

	private int pageWidth;
	private int pageHeight;
	private String configFilePath;

	public ParseFactory(int pageWidth, int pageHeight, String configFilePath) {
		this.pageWidth = pageWidth;
		this.pageHeight = pageHeight;
		this.configFilePath = configFilePath;
	}

	public JSONObject extractValue(List<JSONObject> blockItemList) {
		Map configMap = readConfig(this.configFilePath);


		ParseHorizontalWorker horizontalWorker = new ParseHorizontalWorker(pageWidth, pageHeight, blockItemList);
		ParseTablesWorker tablesWorker = new ParseTablesWorker(pageWidth, pageHeight);
		JSONArray keyValueArray = new JSONArray();
		JSONArray tableArray = new JSONArray();
		JSONObject jsonResult = new JSONObject();


		List targetList = (ArrayList) configMap.get("Targets");
		for (Object item : targetList) {
			// 识别单个元素
			HashMap newItem = (HashMap) item;
			String recognitionType = newItem.getOrDefault("RecognitionType", "default").toString();
			if ("default".equals(recognitionType)) {
				JSONObject resultItem = horizontalWorker.parse(newItem, blockItemList);
				if(resultItem != null) {
					keyValueArray.add(resultItem);
				}
			}else if ("table".equals(recognitionType)) {
				JSONObject result = tablesWorker.parse(newItem, blockItemList);
				if(result != null){
					tableArray.add(result);
				}
			}
		}

		jsonResult.put("keyValueList", keyValueArray);
		jsonResult.put("tableList", tableArray);

		return jsonResult;
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

}
