package cn.nwcdcloud.samples.ocr.service.impl.textract;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.nwcdcloud.samples.ocr.parse.BlockItemUtils;
import cn.nwcdcloud.samples.ocr.parse.FileUtils;
import cn.nwcdcloud.samples.ocr.parse.ParseFactory;

public class GenerateKeyValueTest {
	private static final Logger logger = LoggerFactory.getLogger(GenerateKeyValueTest.class);

	public static void main(String[] args) throws IOException {
		String dir = GenerateKeyValueTest.class.getClass().getResource("/samples").getFile().toString();
		File folder = new File(dir);
		// 每个目录使用一个模板
		for (File fileType : folder.listFiles()) {
			if (fileType.isFile()) {
				continue;
			}
			String configType = fileType.getName();
			for (File file : fileType.listFiles()) {
				String fileJsonPath = file.getPath();
				if (!fileJsonPath.endsWith("json")) {
					continue;
				}
				String fileTxtPath = fileJsonPath.substring(0, fileJsonPath.length() - 4) + "txt";
				File fileTxt = new File(fileTxtPath);
				// 不覆盖现有文件，只把没有的生成
				if (fileTxt.exists()) {
					continue;
				}
				JSONObject jsonObject = FileUtils.readJsonObject(fileJsonPath);
				List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject);
				ParseFactory parseJsonUtil = new ParseFactory(configType);
				JSONObject resultObject = parseJsonUtil.extractValue(blockItemList);
				JSONArray resultArray = resultObject.getJSONArray("keyValueList");
				BufferedWriter out = new BufferedWriter(new FileWriter(fileTxt));
				for (JSONObject item : resultArray.toJavaList(JSONObject.class)) {
					out.write(item.getString("name") + "=" + item.getString("value"));
					out.newLine();
				}
				out.close();
				logger.info("生成{}", fileTxtPath);
			}
		}
	}
}
