package cn.nwcdcloud.samples.ocr.service.impl;

import java.io.InputStream;
import java.util.List;

import cn.nwcdcloud.samples.ocr.parse.ConfigConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.nwcdcloud.commons.constant.CommonConstants;
import cn.nwcdcloud.commons.lang.Result;
import cn.nwcdcloud.samples.ocr.parse.BlockItemUtils;
import cn.nwcdcloud.samples.ocr.parse.ParseFactory;
import cn.nwcdcloud.samples.ocr.service.InferenceService;
import cn.nwcdcloud.samples.ocr.service.SageMakerService;

@Service
public class InferenceServiceImpl implements InferenceService {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private SageMakerService sageMakerService;
	@Value("${templateDir}")
	private String templateDir;

	@Override
	public Result analyse(String type, String data) {
		JSONArray json = JSON.parseArray(data);
		if (logger.isDebugEnabled()) {
			logger.debug(JSON.toJSONString(json.getJSONObject(0)));
		}
		Result result = new Result();
		JSONObject jsonObject = json.getJSONObject(0);
		List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject, ConfigConstants.PAGE_WIDTH, ConfigConstants.PAGE_HEIGHT);
		ParseFactory parseJsonUtil = new ParseFactory(ConfigConstants.PAGE_WIDTH, ConfigConstants.PAGE_HEIGHT, type, templateDir);
		JSONArray resultArray = new JSONArray();
		resultArray.add(parseJsonUtil.extractValue(blockItemList));
		if (logger.isDebugEnabled()) {
			logger.debug(resultArray.toJSONString());
		}
		result.setData(resultArray.toJSONString());
		return result;
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
