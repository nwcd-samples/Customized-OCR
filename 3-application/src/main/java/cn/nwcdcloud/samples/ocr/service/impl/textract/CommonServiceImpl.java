package cn.nwcdcloud.samples.ocr.service.impl.textract;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;

import cn.nwcdcloud.commons.lang.Result;
import cn.nwcdcloud.samples.ocr.service.impl.CommonService;

@Service
public class CommonServiceImpl implements CommonService {

	private static final String ID_SAMPLE_CONFIG_FILE = "/config/id.yaml";

	@Override
	public Result parse(String type, JSONArray jsonArray) {
		Result result = new Result();
		
		String data = "[[{\"name\":\"姓名\",\"value\":\"李四\"}]]";// TODO 需要返回多张图片结果
		result.setData(data);
		return result;
	}

}