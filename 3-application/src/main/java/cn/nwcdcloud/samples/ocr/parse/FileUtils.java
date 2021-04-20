package cn.nwcdcloud.samples.ocr.parse;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

public class FileUtils {

	private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

	public static JSONObject readJsonObject(String filePath) {
		String result = readFile(filePath);
		if (result == null) {
			return null;
		}
		// 获取json
		try {
			JSONObject jsonObject = JSONObject.parseObject(result);
			System.out.println(JSON.toJSONString(jsonObject));
			return jsonObject;
		} catch (JSONException e) {
			logger.error(e.getMessage());
		}
		return null;

	}

	public static JSONArray readJsonArray(String filePath) {
		String result = readFile(filePath);
		if (result == null) {
			return null;
		}
		// 获取json
		try {
			JSONArray jsonArray = JSONObject.parseArray(result);
			System.out.println(JSON.toJSONString(jsonArray));
			return jsonArray;
		} catch (JSONException e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	public static String readFile(String filePath) {
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream(filePath);
			return readFile(fileInputStream);
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	public static String readFile(InputStream inputStream) {
		BufferedReader reader = null;
		StringBuilder stringBuilder = new StringBuilder();
		try {
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
			reader = new BufferedReader(inputStreamReader);
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				stringBuilder.append(tempString);
			}
			return stringBuilder.toString();
		} catch (IOException e) {
			logger.error(e.getMessage());
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}
		}
		return null;
	}

}
