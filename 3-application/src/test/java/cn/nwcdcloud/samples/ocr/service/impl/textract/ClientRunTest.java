package cn.nwcdcloud.samples.ocr.service.impl.textract;

import java.io.File;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientRunTest extends Thread {
	private static Logger logger = LoggerFactory.getLogger(ClientRunTest.class);

	@Override
	public void run() {
		logger.debug("开始请求");
		long dateBegin = System.currentTimeMillis();
		String url = "http://127.0.0.1/inference/predict/invoice";
		HttpPost post = new HttpPost(url);
		File file = new File("C:\\Users\\nowfo\\Documents\\西云\\技术\\AI\\OCR\\待识别\\invoice.png");
		FileEntity requestEntity = new FileEntity(file, ContentType.IMAGE_JPEG);
		post.setEntity(requestEntity);
		CloseableHttpClient client = HttpClients.createDefault();
		try {
			CloseableHttpResponse response = client.execute(post);
			HttpEntity responseEntity = response.getEntity();
			String sResult = EntityUtils.toString(responseEntity, "UTF-8");
			logger.debug(sResult);
			long dateEnd = System.currentTimeMillis();
			logger.debug(String.format("启动完毕，花费时间：%s毫秒。", (dateEnd - dateBegin) ));
		} catch (Exception e) {
			logger.warn("请求异常", e);
		}
	}
}
