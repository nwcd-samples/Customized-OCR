package cn.nwcdcloud.samples.ocr.service.impl.textract;

import java.util.List;

import cn.nwcdcloud.samples.ocr.enums.DocumentTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.nwcdcloud.commons.lang.Result;
import cn.nwcdcloud.samples.ocr.commons.util.BlockItemUtils;
import cn.nwcdcloud.samples.ocr.commons.util.ParseJsonWorker;
import cn.nwcdcloud.samples.ocr.service.impl.CommonService;

@Service
public class CommonServiceImpl implements CommonService {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public Result parse(String type, JSONArray jsonArray) {
		Result result = new Result();

		JSONObject jsonObject = (JSONObject) jsonArray.getJSONObject(0);
		List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject, 1124, 800);
		ParseJsonWorker parseJsonUtil = new ParseJsonWorker(1124, 800, blockItemList, DocumentTypeEnum.IDENTITY_CARD.getPath());

		JSONArray resultArray = new JSONArray();
		resultArray.add(parseJsonUtil.extractValue(blockItemList));

		// TODO 需要返回多张图片结果
		logger.debug(resultArray.toJSONString());
		result.setData(resultArray.toJSONString());
		return result;
	}

}