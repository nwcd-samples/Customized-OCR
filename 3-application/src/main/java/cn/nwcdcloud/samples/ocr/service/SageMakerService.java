package cn.nwcdcloud.samples.ocr.service;

import cn.nwcdcloud.commons.lang.Result;

public interface SageMakerService {
	Result deploy();

	Result remove();

	/**
	 * 
	 * @return None 不存在<br>
	 *         InService 服务中<br>
	 *         Creating 创建中
	 * 
	 */
	Result getEndpointStatus();
}
