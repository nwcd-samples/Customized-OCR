package cn.nwcdcloud.samples.ocr.service.impl.textract;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.nwcdcloud.samples.ocr.parse.BlockItemUtils;
import cn.nwcdcloud.samples.ocr.parse.FileUtils;
import cn.nwcdcloud.samples.ocr.parse.ParseFactory;

public class KeyValueTest {
	private static final Logger logger = LoggerFactory.getLogger(KeyValueTest.class);

	@Test
	public void test() {

		String dir = GenerateKeyValueTest.class.getClass().getResource("/samples").getFile().toString();
		File folder = new File(dir);
		// 每个目录使用一个模板
		for (File fileType : folder.listFiles()) {
			if (fileType.isFile()) {
				continue;
			}
			String configType = fileType.getName();
			File[] files = fileType.listFiles();
			Arrays.sort(files);
			int i = 1;
			for (File file : files) {
				String fileJsonPath = file.getPath();
				if (!fileJsonPath.endsWith("json")) {
					continue;
				}
				String fileTxtPath = fileJsonPath.substring(0, fileJsonPath.length() - 4) + "txt";
				File fileTxt = new File(fileTxtPath);
				if (!fileTxt.exists()) {
					continue;
				}
				JSONObject jsonObject = FileUtils.readJsonObject(fileJsonPath);
				List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject);
				ParseFactory parseJsonUtil = new ParseFactory(configType);
				JSONObject resultObject = parseJsonUtil.extractValue(blockItemList);
				JSONArray resultArray = resultObject.getJSONArray("keyValueList");
				BufferedReader reader = null;
				FileInputStream inputStream = null;
				try {
					inputStream = new FileInputStream(fileTxtPath);
					InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
					reader = new BufferedReader(inputStreamReader);
					String tempString = null;
					while ((tempString = reader.readLine()) != null) {
						String[] all = tempString.split("=");
						String name = all[0];
						String value = all[1];
						boolean check = BlockItemUtils.checkKeyValueMap(resultArray, name, value);
						if (!check) {
							logger.info("检查文件：{}/{}", i, files.length);
							logger.error("模板:{},文件:{}, name:[{}] 正确:[{}] 实际:[{}]", configType, file.getName(),
									name, value, BlockItemUtils.getRealValueString(resultArray, name));
						}
						assert check;
					}
					i++;
				} catch (IOException e) {
					logger.error(e.getMessage());
				} finally {
					try {
						if (inputStream != null) {
							inputStream.close();
						}
						if (reader != null) {
							reader.close();
						}
					} catch (IOException e) {
						logger.error(e.getMessage());
					}
				}
			}
		}
	}
}
