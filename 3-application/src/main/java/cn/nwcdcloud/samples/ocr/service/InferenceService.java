package cn.nwcdcloud.samples.ocr.service;

import com.alibaba.fastjson.JSONArray;

import cn.nwcdcloud.commons.lang.Result;

public interface InferenceService {
	Result analyse(String type, JSONArray data);
}
