package cn.nwcdcloud.samples.ocr.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.nwcdcloud.commons.constant.CommonConstants;
import cn.nwcdcloud.commons.lang.Result;
import cn.nwcdcloud.samples.ocr.service.AnalysisService;
import cn.nwcdcloud.samples.ocr.service.InferenceService;

@RequestMapping("inference")
@Controller
public class InferenceController {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private InferenceService inferenceService;
	@Autowired
	private AnalysisService analysisService;
	@Value("${bucketName}")
	private String bucketName;

	private Result doPredict(HttpServletRequest request) {
		String contentType = request.getContentType();
		try {
			if (StringUtils.hasLength(contentType) && contentType.startsWith("image")) {
				return inferenceService.predict(contentType, request.getInputStream());
			} else {
				String requestBucketName = request.getParameter("bucketName");
				if (!StringUtils.hasLength(requestBucketName)) {
					requestBucketName = bucketName;
				}
				String keyName = request.getParameter("keyName");
				Result result = inferenceService.predict(requestBucketName, keyName);
				return result;
			}
		} catch (Exception e) {
			logger.warn("图片推理报错", e);
			Result result = new Result();
			result.setCode(10);
			result.setMsg("推理报错");
			return result;
		}
	}

	@RequestMapping("/predict")
	@ResponseBody
	public String predict(HttpServletRequest request) {
		return doPredict(request).toString();
	}

	@RequestMapping("/predict/{type}")
	@ResponseBody
	public String predict(@PathVariable("type") String type, HttpServletRequest request) {
		Result result = doPredict(request);
		if (result.getCode() != CommonConstants.NORMAL) {
			return result.toString();
		}
		return analysisService.analyse(type, (JSONObject) result.getData()).toString();
	}

	@RequestMapping("/analysis/{type}")
	@ResponseBody
	public String analyse(@PathVariable("type") String type, String fullData) {
		JSONObject data = JSON.parseObject(fullData);
		Result result = analysisService.analyse(type, data);
		return result.toString();
	}
}
