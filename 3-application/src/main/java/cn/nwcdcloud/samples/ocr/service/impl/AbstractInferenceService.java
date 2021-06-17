package cn.nwcdcloud.samples.ocr.service.impl;

import java.util.UUID;

import org.ehcache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.nwcdcloud.samples.ocr.constant.ImageType;
import cn.nwcdcloud.samples.ocr.constant.OcrConstants;

@Service
public abstract class AbstractInferenceService {
	@Autowired
	private Cache<String, Object> cacheImageByte;

	protected JSONObject addImageInfoByte(final String original, byte[] content) {
		JSONObject jsonObject = JSON.parseObject(original);
		String imageId = getUUID();
		cacheImageByte.put(imageId, content);
		jsonObject.put(OcrConstants.IMAGE_TYPE, ImageType.Byte.getId());
		jsonObject.put(OcrConstants.IMAGE_CONTENT, imageId);
		return jsonObject;
	}

	protected JSONObject addImageInfoJson(final String original, String bucketName, String keyName) {
		JSONObject jsonContent = new JSONObject();
		jsonContent.put(OcrConstants.BUCKET_NAME, bucketName);
		jsonContent.put(OcrConstants.KEY_NAME, keyName);
		JSONObject jsonObject = JSON.parseObject(original);
		jsonObject.put(OcrConstants.IMAGE_TYPE, ImageType.JSON.getId());
		jsonObject.put(OcrConstants.IMAGE_CONTENT, jsonContent);
		return jsonObject;
	}

	protected String getUUID() {
		return UUID.randomUUID().toString().replace("-", "").toUpperCase();
	}
}
