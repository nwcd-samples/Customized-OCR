package cn.nwcdcloud.samples.ocr.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.nwcdcloud.commons.lang.Result;
import cn.nwcdcloud.samples.ocr.constant.OcrConstants;
import cn.nwcdcloud.samples.ocr.parse.BlockItemUtils;
import cn.nwcdcloud.samples.ocr.parse.ConfigConstants;
import cn.nwcdcloud.samples.ocr.parse.ParseFactory;
import cn.nwcdcloud.samples.ocr.service.InferenceService;

@Service
public class InferenceServiceImpl implements InferenceService {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${templateDir}")
	private String templateDir;

	@Override
	public Result analyse(String type, JSONArray data) {
		if (logger.isDebugEnabled()) {
			logger.debug(JSON.toJSONString(data.getJSONObject(0)));
		}
		//TODO 目前只处理第1个
		Result result = new Result();
		JSONObject jsonObject = data.getJSONObject(0);
		List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject);
		ParseFactory parseJsonUtil = new ParseFactory(type, templateDir);
		JSONArray resultArray = new JSONArray();
		resultArray.add(parseJsonUtil.extractValue(blockItemList, jsonObject.getIntValue(OcrConstants.IMAGE_TYPE),
				jsonObject.getString(OcrConstants.IMAGE_CONTENT)));
		if (logger.isDebugEnabled()) {
			logger.debug(resultArray.toJSONString());
		}
		result.setData(resultArray);
		return result;
	}
}
