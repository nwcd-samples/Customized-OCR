package cn.nwcdcloud.samples.ocr.service;

import java.io.InputStream;

import cn.nwcdcloud.commons.lang.Result;

public interface InferenceService {
	Result predict(String bucketName, String keyName);

	Result predict(String contentType, InputStream inputStream);
}
