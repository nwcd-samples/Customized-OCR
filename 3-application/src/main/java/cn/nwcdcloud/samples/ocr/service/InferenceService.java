package cn.nwcdcloud.samples.ocr.service;

import java.io.InputStream;

import cn.nwcdcloud.commons.lang.Result;

public interface InferenceService {
	Result invokeEndpoint(String endpointName, String body);

	Result invokeEndpoint(String endpointName,String contentType, InputStream inputStream);

	Result deploy(String endpointName, String imageUri, String instanceType);

	Result remove(String endpointName);

	/**
	 * 
	 * @param endpointName
	 * @return None 不存在<br>
	 * 
	 */
	String getEndpointStatus(String endpointName);
}
