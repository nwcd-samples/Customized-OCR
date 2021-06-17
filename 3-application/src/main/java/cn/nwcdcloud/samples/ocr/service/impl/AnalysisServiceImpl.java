package cn.nwcdcloud.samples.ocr.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.nwcdcloud.commons.lang.Result;
import cn.nwcdcloud.samples.ocr.constant.OcrConstants;
import cn.nwcdcloud.samples.ocr.parse.BlockItemUtils;
import cn.nwcdcloud.samples.ocr.parse.ParseFactory;
import cn.nwcdcloud.samples.ocr.service.AnalysisService;

@Service
public class AnalysisServiceImpl implements AnalysisService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${templateDir}")
	private String templateDir;

	@Override
	public Result analyse(String type, JSONObject jsonData) {
		if (logger.isDebugEnabled()) {
			logger.debug(JSON.toJSONString(jsonData));
		}
		Result result = new Result();
		List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonData);
		ParseFactory parseJsonUtil = new ParseFactory(type, templateDir);
		JSONObject resultJson = parseJsonUtil.extractValue(blockItemList, jsonData.getIntValue(OcrConstants.IMAGE_TYPE),
				jsonData.getString(OcrConstants.IMAGE_CONTENT));
		if (logger.isDebugEnabled()) {
			logger.debug(resultJson.toJSONString());
		}
		result.setData(resultJson);
		return result;
	}

}
