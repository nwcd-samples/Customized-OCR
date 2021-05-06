package cn.nwcdcloud.samples.ocr.service.impl;

import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.nwcdcloud.commons.lang.Result;
import cn.nwcdcloud.samples.ocr.service.S3Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.utils.StringUtils;

@Service
public class S3ServiceImpl implements S3Service {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public Result generatePresignedUrl(String region, String bucketName, String keyName, Integer expTime) {
		Result result = new Result();
		if (StringUtils.isBlank(region)) {
			region = "cn-northwest-1";
		}
		if (expTime == null) {
			expTime = 10;
		}

		PutObjectRequest objectRequest = PutObjectRequest.builder().bucket(bucketName).key(keyName)
				.contentType("image/jpeg").build();

		PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
				.signatureDuration(Duration.ofMinutes(expTime)).putObjectRequest(objectRequest).build();
		try {
			PresignedPutObjectRequest presignedRequest = S3Presigner.create().presignPutObject(presignRequest);
			URL url = presignedRequest.url();
			Map<String, String> data = new HashMap<>();
			data.put("url", url.toString());
			data.put("keyName", keyName);
			result.setData(data);
		} catch (Exception e) {
			logger.warn("获取PreURL出错", e);
			result.setCode(20);
			result.setMsg("获取presignedURL出错");
		}
		return result;
	}

	public byte[] getContent(String region, String bucketName, String keyName) {
		if (StringUtils.isBlank(region)) {
			region = "cn-northwest-1";
		}
		S3Client s3 = S3Client.builder().region(Region.of(region)).build();
		GetObjectRequest objectRequest = GetObjectRequest.builder().key(keyName).bucket(bucketName).build();
		ResponseBytes<GetObjectResponse> objectBytes = s3.getObjectAsBytes(objectRequest);
		byte[] data = objectBytes.asByteArray();
		return data;
	}

}
