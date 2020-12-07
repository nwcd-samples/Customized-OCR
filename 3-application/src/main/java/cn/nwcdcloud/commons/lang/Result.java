package cn.nwcdcloud.commons.lang;

import java.io.Serializable;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import cn.nwcdcloud.commons.config.JsonConfig;
import cn.nwcdcloud.commons.constant.CommonConstants;

/**
 * JSON返回结果
 * 
 */
public class Result implements Serializable {
	private static final long serialVersionUID = -9053032671064679853L;
	private int code = CommonConstants.NORMAL;
	private String msg;
	private long timestamp = System.currentTimeMillis();
	private Object data;

	public Result() {
	}

	public Result(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public Result(String msg) {
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return JSONObject.toJSONString(this, SerializerFeature.DisableCircularReferenceDetect);
	}

	public String toJsString() {
		return JSONObject.toJSONString(this, JsonConfig.getSerializeConfig(),
				SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteMapNullValue);
	}
}
