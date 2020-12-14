package cn.nwcdcloud.samples.ocr.service;

import java.io.InputStream;

import cn.nwcdcloud.commons.lang.Result;

public interface InferenceService {
	Result analyse(String type, String data);

	Result predict(String type, String endpointName, String body);

	Result predict(String type, String endpointName, String contentType, InputStream inputStream);

	void addTextractService(String key, TextractService textractService);
}
