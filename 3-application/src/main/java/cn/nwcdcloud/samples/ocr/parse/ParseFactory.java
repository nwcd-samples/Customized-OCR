package cn.nwcdcloud.samples.ocr.parse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ho.yaml.Yaml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class ParseFactory {
	private final Logger logger = LoggerFactory.getLogger(ParseFactory.class);

	private int pageWidth;
	private int pageHeight;
	private String configType;
	private String templateDir;

	public ParseFactory(int pageWidth, int pageHeight, String configType) {
		this.pageWidth = pageWidth;
		this.pageHeight = pageHeight;
		this.configType = configType;
	}

	public ParseFactory(int pageWidth, int pageHeight, String configType, String templateDir) {
		this.pageWidth = pageWidth;
		this.pageHeight = pageHeight;
		this.configType = configType;
		this.templateDir = templateDir;
	}

	public JSONObject extractValue(List<JSONObject> blockItemList) {
		Map configMap = readConfig(this.configType, this.templateDir);

		ParseHorizontalWorker horizontalWorker = new ParseHorizontalWorker(pageWidth, pageHeight);
		ParseTablesWorker tablesWorker = new ParseTablesWorker(pageWidth, pageHeight);
		JSONArray keyValueArray = new JSONArray();
		JSONArray tableArray = new JSONArray();
		JSONObject jsonResult = new JSONObject();

		List targetList = (ArrayList) configMap.get("Targets");
		for (Object item : targetList) {

			HashMap newItem = (HashMap) item;
			String recognitionType = newItem.getOrDefault("RecognitionType", "default").toString();
			if ("default".equals(recognitionType)) {
				// 识别单个key-value元素
				JSONObject resultItem = horizontalWorker.parse(newItem, blockItemList);
				if (resultItem != null) {
					keyValueArray.add(resultItem);
				}
			} else if ("table".equals(recognitionType)) {
				JSONObject result = tablesWorker.parse(newItem, blockItemList);
				if (result != null) {
					tableArray.add(result);
				}
			} else {

				throw new IllegalArgumentException(" 没有  '" + recognitionType + "' 的识别类型， 请检查 RecognitionType 配置 ");
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

	private Map readConfig(String configType, String templateDir) {
		String configPath = "config/" + configType + ".yaml";
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(configPath);
		if (is == null && StringUtils.hasLength(templateDir)) {
			configPath = templateDir + configType + ".yaml";
			try {
				is = new FileInputStream(new File(configPath));
			} catch (FileNotFoundException e) {
				logger.error("配置文件不存在:{}" + configPath, e);
				return null;
			}
		}
		Map rootMap = null;
		try {
			rootMap = Yaml.loadType(is, HashMap.class);

		} catch (Exception e) {
			logger.error("读取配置文件出错:{}" + configPath, e);
		}
		return rootMap;
	}

	/**
	 * Cell类 封装页面使用
	 */
	public static class Cell {
		public Cell() {
			text = "";
			confidence = 1.0f;
		}

		public String text;
		public float confidence;
	}

}
