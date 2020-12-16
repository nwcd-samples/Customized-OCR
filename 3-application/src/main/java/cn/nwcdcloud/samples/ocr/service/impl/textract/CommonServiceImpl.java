package cn.nwcdcloud.samples.ocr.service.impl.textract;

import cn.nwcdcloud.commons.utils.BlockItemUtil;
import cn.nwcdcloud.commons.utils.ParseJsonUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;

import cn.nwcdcloud.commons.lang.Result;
import cn.nwcdcloud.samples.ocr.service.impl.CommonService;

import java.util.List;

@Service
public class CommonServiceImpl implements CommonService {

	private static final String ID_SAMPLE_CONFIG_FILE = "/config/id.yaml";

	@Override
	public Result parse(String type, JSONArray jsonArray) {
		Result result = new Result();

		JSONObject jsonObject = (JSONObject) jsonArray.getJSONObject(0);
		List<JSONObject> blockItemList = BlockItemUtil.getBlockItemList(jsonObject, 1124, 800);
		ParseJsonUtil parseJsonUtil = new ParseJsonUtil(1124, 800, blockItemList);

		JSONArray resultArray = new JSONArray();
		resultArray.add(parseJsonUtil.extractValue(blockItemList));

//		String data = "[[{\"name\":\"姓名\",\"value\":\"李四\"}]]";
		// TODO 需要返回多张图片结果
		System.out.println("****************** "+ resultArray.toJSONString());
		result.setData(resultArray.toJSONString());
		return result;
	}

}