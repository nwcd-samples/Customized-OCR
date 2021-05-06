package cn.nwcdcloud.samples.ocr.service;

import cn.nwcdcloud.commons.lang.Result;

public interface S3Service {
	Result generatePresignedUrl(String region, String bucketName, String keyName, Integer expTime);
	public byte[] getContent(String region, String bucketName, String keyName);
}
