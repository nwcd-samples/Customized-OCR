package cn.nwcdcloud.commons.config;

import java.math.BigInteger;

import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.ToStringSerializer;

public class JsonConfig {
	private static SerializeConfig serializeConfig;
	static {
		// 大Long可以处理为String，小long不行
		serializeConfig = new SerializeConfig();
		serializeConfig.put(BigInteger.class, ToStringSerializer.instance);
		serializeConfig.put(Long.class, ToStringSerializer.instance);
		serializeConfig.put(Long.TYPE, ToStringSerializer.instance);
	}

	public static SerializeConfig getSerializeConfig() {
		return serializeConfig;
	}
}
