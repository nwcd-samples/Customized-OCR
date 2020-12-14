package cn.nwcdcloud.samples.ocr.service;

import com.alibaba.fastjson.JSONArray;

import cn.nwcdcloud.commons.lang.Result;

public interface TextractService {
	void init();
	
	Result parse(JSONArray jsonArray);
}
