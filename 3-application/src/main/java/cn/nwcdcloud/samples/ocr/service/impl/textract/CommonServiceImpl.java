package cn.nwcdcloud.samples.ocr.service.impl.textract;

import java.util.List;

import cn.nwcdcloud.samples.ocr.parse.ParseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.nwcdcloud.commons.lang.Result;
import cn.nwcdcloud.samples.ocr.parse.BlockItemUtils;
import cn.nwcdcloud.samples.ocr.service.CommonService;

@Service
public class CommonServiceImpl implements CommonService {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public Result parse(String type, JSONArray jsonArray) {
		Result result = new Result();

		JSONObject jsonObject = (JSONObject) jsonArray.getJSONObject(0);
		List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject, 1124, 800);
		ParseFactory parseJsonUtil = new ParseFactory(1124, 800, blockItemList, "config/" + type + ".yaml");

		JSONArray resultArray = new JSONArray();
		resultArray.add(parseJsonUtil.extractValue(blockItemList));

		logger.debug(resultArray.toJSONString());
		result.setData(resultArray.toJSONString());
		return result;
	}

}