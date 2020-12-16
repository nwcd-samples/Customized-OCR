package cn.nwcdcloud.samples.ocr.service.impl;

import com.alibaba.fastjson.JSONArray;

import cn.nwcdcloud.commons.lang.Result;

public interface CommonService {
	Result parse(String type,JSONArray jsonArray);
}
