package cn.nwcdcloud.samples.ocr.service.impl.textract;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;

import cn.nwcdcloud.commons.lang.Result;
import cn.nwcdcloud.samples.ocr.service.InferenceService;
import cn.nwcdcloud.samples.ocr.service.TextractService;

@Service
public class IdServiceImpl implements TextractService {
	@Autowired
	private InferenceService inferenceService;

	@Override
	public Result parse(JSONArray jsonArray) {
		Result result = new Result();
		String data = "[[{\"name\":\"姓名\",\"value\":\"张三\"},{\"name\":\"性别\",\"value\":\"男\"}]]";
		result.setData(data);
		return result;
	}

	@Override
	@PostConstruct
	public void init() {
		inferenceService.addTextractService("id2", this);
	}
}
