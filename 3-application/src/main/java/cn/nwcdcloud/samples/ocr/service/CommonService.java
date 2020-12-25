package cn.nwcdcloud.samples.ocr.service;

import com.alibaba.fastjson.JSONArray;

import cn.nwcdcloud.commons.lang.Result;

/**
 * 通用解析，即可通过配置文件即可配置出来的
 * 
 * @author nowfox
 *
 */
public interface CommonService {
	Result parse(String type, JSONArray jsonArray);
}
