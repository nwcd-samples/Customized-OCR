package cn.nwcdcloud.samples.ocr.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.nwcdcloud.commons.constant.CommonConstants;
import cn.nwcdcloud.commons.http.HttpClientUtils;
import cn.nwcdcloud.commons.lang.Result;
import cn.nwcdcloud.samples.ocr.service.InferenceService;
import cn.nwcdcloud.samples.ocr.service.S3Service;
import software.amazon.awssdk.core.SdkBytes;

@Service
@ConditionalOnProperty(name = "recognition.type", havingValue = "2")
public class InferenceTextinServiceImpl extends AbstractInferenceService implements InferenceService {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private S3Service s3Service;
	@Value("${recognition.appId}")
	private String appId;
	@Value("${recognition.secretKey}")
	private String secretKey;
	final String url = "https://api.textin.com/robot/v1.0/api/text_recognize_3d1";

	private String doPredict(byte[] content) {
		CloseableHttpClient httpClient = (CloseableHttpClient) HttpClientUtils.getHttpClient();
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("x-ti-app-id", appId);
		httpPost.setHeader("x-ti-secret-code", secretKey);
		try {
			httpPost.setEntity(new ByteArrayEntity(content));
			CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
			String result = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
			if (logger.isDebugEnabled()) {
				logger.debug("请求Textin结果:{}", result);
			}
			return result;
		} catch (IOException e) {
			logger.warn("请求Textin出错", e);
			return null;
		} finally {
			// 使用连接池后，这里不能关闭
//			httpClient.close();
		}
	}

	private Map<String, Object> getPage(List<String> listId) {
		Map<String, Object> mapPage = new HashMap<>();
		mapPage.put("BlockType", "PAGE");
		mapPage.put("Page", 1);
		mapPage.put("Id", UUID.randomUUID().toString());
		Map<String, Object> mapGeometry = new HashMap<>();
		Map<String, Double> mapBoundingBox = new HashMap<>();
		mapBoundingBox.put("Width", 1.0);
		mapBoundingBox.put("Height", 1.0);
		mapBoundingBox.put("Left", 0.0);
		mapBoundingBox.put("Top", 0.0);
		mapGeometry.put("BoundingBox", mapBoundingBox);
		List<Map<String, Double>> listPolygon = new ArrayList<>();
		Map<String, Double> mapPolygon0 = new HashMap<>();
		mapPolygon0.put("X", 0.0);
		mapPolygon0.put("Y", 0.0);
		listPolygon.add(mapPolygon0);
		Map<String, Double> mapPolygon1 = new HashMap<>();
		mapPolygon1.put("X", 1.0);
		mapPolygon1.put("Y", 0.0);
		listPolygon.add(mapPolygon1);
		Map<String, Double> mapPolygon2 = new HashMap<>();
		mapPolygon2.put("X", 1.0);
		mapPolygon2.put("Y", 1.0);
		listPolygon.add(mapPolygon2);
		Map<String, Double> mapPolygon3 = new HashMap<>();
		mapPolygon3.put("X", 0.0);
		mapPolygon3.put("Y", 1.0);
		listPolygon.add(mapPolygon3);
		mapGeometry.put("Polygon", listPolygon);
		mapPage.put("Geometry", mapGeometry);
		Map<String, Object> mapRelationships = new HashMap<>();
		mapRelationships.put("Type", "CHILD");
		mapRelationships.put("Ids", listId);
		mapPage.put("Relationships", mapRelationships);
		return mapPage;
	}

	/**
	 * 正常的
	 */
	private void getPolygon0(List<Map<String, Double>> listPolygon, JSONArray listPosition, int width, int height) {
		for (int j = 0; j <= 6; j = j + 2) {
			Map<String, Double> mapPolygon = new HashMap<>();
			mapPolygon.put("X", listPosition.getDouble(j) / width);
			mapPolygon.put("Y", listPosition.getDouble(j + 1) / height);
			listPolygon.add(mapPolygon);
		}
	}

	/**
	 * 顺时针旋转90度
	 */
	private void getPolygon90(List<Map<String, Double>> listPolygon, JSONArray listPosition, int width, int height) {
		// x、y交换，Y位置需要用1.0减去当前位置，2次反向输出位置顺序后，结果不用再反向输出
		for (int j = 0; j <= 6; j = j + 2) {
			Map<String, Double> mapPolygon = new HashMap<>();
			mapPolygon.put("Y", 1.0 - listPosition.getDouble(j) / width);
			mapPolygon.put("X", listPosition.getDouble(j + 1) / height);
			listPolygon.add(mapPolygon);
		}
	}

	/**
	 * 顺时针旋转180度
	 */
	private void getPolygon180(List<Map<String, Double>> listPolygon, JSONArray listPosition, int width, int height) {
		// X、Y不需要交换，X、Y位置需要用1.0减去当前位置
		for (int j = 0; j <= 6; j = j + 2) {
			Map<String, Double> mapPolygon = new HashMap<>();
			mapPolygon.put("X", 1.0 - listPosition.getDouble(j) / width);
			mapPolygon.put("Y", 1.0 - listPosition.getDouble(j + 1) / height);
			listPolygon.add(mapPolygon);
		}
	}

	/**
	 * 顺时针旋转270度
	 */
	private void getPolygon270(List<Map<String, Double>> listPolygon, JSONArray listPosition, int width, int height) {
		// x、y交换，X位置需要用1.0减去当前位置，2次反向输出位置顺序后，结果不用再反向输出
		for (int j = 0; j <= 6; j = j + 2) {
			Map<String, Double> mapPolygon = new HashMap<>();
			mapPolygon.put("Y", listPosition.getDouble(j) / width);
			mapPolygon.put("X", 1.0 - listPosition.getDouble(j + 1) / height);
			listPolygon.add(mapPolygon);
		}
	}

	private void setBoundingBox(List<Map<String, Double>> listPolygon, Map<String, Double> mapBoundingBox) {
		mapBoundingBox.put("Left", listPolygon.get(0).get("X"));
		mapBoundingBox.put("Top", listPolygon.get(0).get("Y"));
		mapBoundingBox.put("Width", listPolygon.get(1).get("X") - listPolygon.get(0).get("X"));
		mapBoundingBox.put("Height", listPolygon.get(2).get("Y") - listPolygon.get(1).get("Y"));
	}

	/**
	 * 
	 * @param original
	 * @return
	 */
	private Result convertToTextract(final String original) {
		Result result = new Result();
		JSONObject jsonRoot = JSON.parseObject(original);
		int code = jsonRoot.getIntValue("code");
		if (code != 200) {
			result.setCode(5);
			result.setMsg(jsonRoot.getString("message"));
			return result;
		}
		JSONObject jsonResult = jsonRoot.getJSONObject("result");
		int width = jsonResult.getIntValue("rotated_image_width");
		int height = jsonResult.getIntValue("rotated_image_height");
		int angle = jsonResult.getIntValue("image_angle");

		Map<String, Object> mapRoot = new HashMap<>();
		mapRoot.put("JobStatus", "SUCCEEDED");
		Map<String, Integer> mapDocumentMetadata = new HashMap<>();
		mapDocumentMetadata.put("Pages", 1);
		mapRoot.put("DocumentMetadata", mapDocumentMetadata);

		List<Map<String, Object>> listBlocks = new LinkedList<>();
		List<String> listId = new ArrayList<>();
		Map<String, Object> mapPage = getPage(listId);
		listBlocks.add(mapPage);

		JSONArray listLine = jsonResult.getJSONArray("lines");
		for (int i = 0; i < listLine.size(); i++) {
			JSONObject jsonLine = listLine.getJSONObject(i);
			String text = jsonLine.getString("text");
			if (!StringUtils.hasLength(text)) {
				continue;
			}
			Map<String, Object> mapBlock = new HashMap<>();
			mapBlock.put("BlockType", "WORD");
			mapBlock.put("Confidence", jsonLine.getDouble("score"));
			mapBlock.put("Text", text);
			mapBlock.put("Page", 1);
			String id = UUID.randomUUID().toString();
			mapBlock.put("Id", id);
			listId.add(id);
			JSONArray listPosition = jsonLine.getJSONArray("position");
			Map<String, Object> mapGeometry = new HashMap<>();

			Map<String, Double> mapBoundingBox = new HashMap<>();
			List<Map<String, Double>> listPolygon = new ArrayList<>();
			if (angle == 90) {
				getPolygon90(listPolygon, listPosition, width, height);
			} else if (angle == 180) {
				getPolygon180(listPolygon, listPosition, width, height);
			} else if (angle == 270) {
				getPolygon270(listPolygon, listPosition, width, height);
			} else {
				getPolygon0(listPolygon, listPosition, width, height);
			}
			setBoundingBox(listPolygon, mapBoundingBox);
			mapGeometry.put("BoundingBox", mapBoundingBox);
			mapGeometry.put("Polygon", listPolygon);
			mapBlock.put("Geometry", mapGeometry);
			listBlocks.add(mapBlock);
		}

		mapRoot.put("Blocks", listBlocks);
		result.setData(mapRoot);
		return result;
	}

	@Override
	public Result predict(String bucketName, String keyName) {
		Result result = new Result();
		byte[] imageByte = s3Service.getContent(null, bucketName, keyName);
		String entity = doPredict(imageByte);
		Result resultTextract = convertToTextract(entity);
		if (resultTextract.getCode() != CommonConstants.NORMAL) {
			return resultTextract;
		}
		String textract = JSONObject.toJSONString(resultTextract.getData());
		JSONObject data = addImageInfoJson(textract, bucketName, keyName);
		result.setData(data);
		return result;
	}

	@Override
	public Result predict(String contentType, InputStream inputStream) {
		Result result = new Result();
		try {
			SdkBytes requestBody = SdkBytes.fromInputStream(inputStream);
			byte[] imageByte = requestBody.asByteArray();
			String entity = doPredict(imageByte);
			Result resultTextract = convertToTextract(entity);
			if (resultTextract.getCode() != CommonConstants.NORMAL) {
				return resultTextract;
			}
			String textract = JSONObject.toJSONString(resultTextract.getData());
			JSONObject data = addImageInfoByte(textract, imageByte);
			result.setData(data);
		} catch (Exception e) {
			logger.warn("请求Textin报错", e);
			throw e;
		}
		return result;
	}
}
