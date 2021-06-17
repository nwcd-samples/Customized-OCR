package cn.nwcdcloud.samples.ocr.service.impl;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

import cn.nwcdcloud.commons.lang.Result;
import cn.nwcdcloud.samples.ocr.service.InferenceService;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.sagemakerruntime.SageMakerRuntimeClient;
import software.amazon.awssdk.services.sagemakerruntime.model.InvokeEndpointRequest;
import software.amazon.awssdk.services.sagemakerruntime.model.InvokeEndpointResponse;
import software.amazon.awssdk.services.sagemakerruntime.model.ModelErrorException;

@Service
@ConditionalOnProperty(name = "recognition.type", havingValue = "1")
public class InferenceSageMakerServiceImpl extends AbstractInferenceService implements InferenceService {
	@Value("${endpointName}")
	private String endpointName;

	@Override
	public Result predict(String bucketName, String keyName) {
		Result result = new Result();
		JSONObject jsonRequest = new JSONObject();
		jsonRequest.put("bucket", bucketName);
		jsonRequest.put("image_uri", new String[] { keyName });
		SdkBytes requestBody = SdkBytes.fromUtf8String(jsonRequest.toJSONString());
		InvokeEndpointRequest request = InvokeEndpointRequest.builder().endpointName(endpointName)
				.contentType("application/json").body(requestBody).build();
		SageMakerRuntimeClient client = SageMakerRuntimeClient.create();
		InvokeEndpointResponse response = client.invokeEndpoint(request);
		String responseResult = trimArray(response.body().asUtf8String());
		JSONObject data = addImageInfoJson(responseResult, bucketName, keyName);
		result.setData(data);
		return result;
	}

	@Override
	public Result predict(String contentType, InputStream inputStream) {
		Result result = new Result();
		SdkBytes requestBody = SdkBytes.fromInputStream(inputStream);
		InvokeEndpointRequest request = InvokeEndpointRequest.builder().endpointName(endpointName)
				.contentType(contentType).body(requestBody).build();
		SageMakerRuntimeClient client = SageMakerRuntimeClient.create();
		try {
			InvokeEndpointResponse response = client.invokeEndpoint(request);
			String responseResult = trimArray(response.body().asUtf8String());
			JSONObject data = addImageInfoByte(responseResult, requestBody.asByteArray());
			result.setData(data);
		} catch (ModelErrorException e) {
			if (e.getMessage().indexOf("413 Request Entity Too Large") != -1) {
				result.setCode(25);
				result.setMsg("待识别图片过大，上限为10MB");
				return result;
			}
			throw e;
		}
		return result;
	}

	private String trimArray(final String original) {
		if (original.startsWith("[") && original.endsWith("]")) {
			return original.substring(1, original.length() - 1);
		}
		return original;
	}
}
