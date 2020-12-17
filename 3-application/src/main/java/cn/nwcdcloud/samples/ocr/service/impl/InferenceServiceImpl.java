package cn.nwcdcloud.samples.ocr.service.impl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import cn.nwcdcloud.commons.constant.CommonConstants;
import cn.nwcdcloud.commons.lang.Result;
import cn.nwcdcloud.samples.ocr.service.InferenceService;
import cn.nwcdcloud.samples.ocr.service.SageMakerService;
import cn.nwcdcloud.samples.ocr.service.TextractService;

@Service
public class InferenceServiceImpl implements InferenceService {
	private static Map<String, TextractService> mapTextractService = new HashMap<>();

	public void addTextractService(String key, TextractService textractService) {
		mapTextractService.put(key, textractService);
	}

	@Autowired
	private SageMakerService sageMakerService;
	@Autowired
	private CommonService commonService;

	@Override
	public Result analyse(String type, String data) {
		TextractService textractService = mapTextractService.get(type);
		JSONArray json = JSON.parseArray(data);
		System.out.println("\n");
		System.out.println(JSON.toJSONString(json.getJSONObject(0)));
		System.out.println("\n");
		if (textractService == null) {
			return commonService.parse(type, json);
		}
		return textractService.parse(json);
	}

	@Override
	public Result predict(String type, String endpointName, String body) {
		Result result = sageMakerService.invokeEndpoint(endpointName, body);
		if (result.getCode() != CommonConstants.NORMAL) {
			return result;
		}
		return analyse(type, (String) result.getData());
	}

	@Override
	public Result predict(String type, String endpointName, String contentType, InputStream inputStream) {
		Result result = sageMakerService.invokeEndpoint(endpointName, contentType, inputStream);
		if (result.getCode() != CommonConstants.NORMAL) {
			return result;
		}
		return analyse(type, (String) result.getData());
	}
}
