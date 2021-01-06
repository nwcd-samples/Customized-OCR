package cn.nwcdcloud.samples.ocr.controller;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

import cn.nwcdcloud.commons.lang.Result;
import cn.nwcdcloud.samples.ocr.service.InferenceService;
import cn.nwcdcloud.samples.ocr.service.SageMakerService;

@RequestMapping("inference")
@Controller
public class InferenceController {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private InferenceService inferenceService;
	@Autowired
	private SageMakerService sageMakerService;
	@Value("${bucketName}")
	private String bucketName;
	@Value("${imageUri}")
	private String imageUri;
	@Value("${endpointName}")
	private String endpointName;
	@Value("${instanceType}")
	private String instanceType;

	@PostMapping("/deploy")
	@ResponseBody
	public String deploy() {
		return sageMakerService.deploy(endpointName, imageUri, instanceType).toString();
	}

	@PostMapping("/remove")
	@ResponseBody
	public String remove() {
		return sageMakerService.remove(endpointName).toString();
	}

	@GetMapping("/getEndpointStatus")
	@ResponseBody
	public String getEndpointStatus() {
		String result = sageMakerService.getEndpointStatus(endpointName).toString();
		return result;
	}

	@PostMapping("/predict")
	@ResponseBody
	public String predict(HttpServletRequest request) {
		String contentType = request.getContentType();
		try {
			if ("application/json".equalsIgnoreCase(contentType)) {
				String body = getRequestContent(request);
				return sageMakerService.invokeEndpoint(endpointName, body).toString();
			} else if (contentType.startsWith("image")) {
				return sageMakerService.invokeEndpoint(endpointName, contentType, request.getInputStream()).toString();
			} else {
				String keyName = request.getParameter("keyName");
				JSONObject jsonRequest = new JSONObject();
				jsonRequest.put("bucket", bucketName);
				jsonRequest.put("image_uri", new String[] { keyName });
				Result result = sageMakerService.invokeEndpoint(endpointName, jsonRequest.toJSONString());
				return result.toString();
			}
		} catch (Exception e) {
			logger.warn("图片推理报错", e);
			Result result = new Result();
			result.setCode(10);
			result.setMsg("推理报错");
			return result.toString();
		}
	}

	@PostMapping("/predict/{type}")
	@ResponseBody
	public String predict(@PathVariable("type") String type, HttpServletRequest request) {
		String contentType = request.getContentType();
		try {
			if ("application/json".equalsIgnoreCase(contentType)) {
				String body = getRequestContent(request);
				return inferenceService.predict(type, endpointName, body).toString();
			} else if (contentType.startsWith("image")) {
				return inferenceService.predict(type, endpointName, contentType, request.getInputStream()).toString();
			} else {
				String keyName = request.getParameter("keyName");
				JSONObject jsonRequest = new JSONObject();
				jsonRequest.put("bucket", bucketName);
				jsonRequest.put("image_uri", new String[] { keyName });
				Result result = inferenceService.predict(type, endpointName, jsonRequest.toJSONString());
				return result.toString();
			}
		} catch (Exception e) {
			logger.warn("图片推理报错", e);
			Result result = new Result();
			result.setCode(10);
			result.setMsg("推理报错");
			return result.toString();
		}
	}

	@PostMapping("/analysis/{type}")
	@ResponseBody
	public String analyse(@PathVariable("type") String type, String fullData) {
		Result result = inferenceService.analyse(type, fullData);
		return result.toString();
	}

	private String getRequestContent(HttpServletRequest request) {
		// 获取POST数据
		BufferedReader bufReader = null;
		try {
			bufReader = request.getReader();
			String line = null;
			StringBuffer sbBody = new StringBuffer();
			while ((line = bufReader.readLine()) != null) {
				sbBody.append(line);
			}
			return sbBody.toString();
		} catch (IOException e) {
			logger.info("获取POST数据时出错", e);
			return null;
		} finally {
			if (bufReader != null) {
				try {
					bufReader.close();
				} catch (IOException e) {
					logger.info("关闭获取POST数据流时出错", e);
				}
			}
		}
	}
}
