package cn.nwcdcloud.samples.ocr.service;

import com.alibaba.fastjson.JSONObject;

import cn.nwcdcloud.commons.lang.Result;

public interface AnalysisService {
	Result analyse(String type, JSONObject data);
}
