package cn.nwcdcloud.samples.ocr.service.impl.textract;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.nwcdcloud.commons.http.HttpClientUtils;

public class InferenceTest {

	private static final Logger logger = LoggerFactory.getLogger(InferenceTest.class);

	public static void main(String[] args) throws ParseException, IOException {
		if (args.length != 2) {
			logger.error("请输入输入路径");
			return;
		}
		String dirInput = args[0];
		String dirOutput = args[1];
		File folderInput = new File(dirInput);
		File folderOutput = new File(dirOutput);
		if (!folderOutput.exists()) {
			folderOutput.mkdirs();
		}
		CloseableHttpClient httpClient = (CloseableHttpClient) HttpClientUtils.getHttpClient();
		String url = "http://127.0.0.1/inference/predict";

		for (String fileName : folderInput.list()) {
			if (!isImage(fileName)) {
				continue;
			}
			HttpPost httpPost = new HttpPost(url);
			httpPost.setHeader("Content-Type", "image/jpeg");
			httpPost.setEntity(new FileEntity(new File(dirInput + File.separator + fileName)));
			CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
			String result = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
			JSONObject jsonResult = JSON.parseObject(result);
			JSONObject jsonData = jsonResult.getJSONObject("data");
			String filePrefix = fileName.split("\\.")[0];
			String fileNameResult = dirOutput + File.separator + filePrefix + ".json";
			BufferedWriter out = new BufferedWriter(new FileWriter(fileNameResult));
			out.write(jsonData.toJSONString());
			out.close();
			logger.info("生成{}", fileNameResult);
		}
	}

	private static boolean isImage(String fileName) {
		String[] extNames = { "jpg", "jpeg", "bmp", "png" };
		fileName = fileName.toLowerCase();
		for (String extName : extNames) {
			if (fileName.endsWith(extName)) {
				return true;
			}
		}
		return false;
	}

}
