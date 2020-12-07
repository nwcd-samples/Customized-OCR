package cn.nwcdcloud.samples.ocr.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.nwcdcloud.samples.ocr.service.InferenceService;

@RequestMapping("inference")
@Controller
public class InferenceController {
	@Autowired
	private InferenceService inferenceService;
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
		return inferenceService.deploy(endpointName, imageUri, instanceType).toString();
	}

	@PostMapping("/remove")
	@ResponseBody
	public String remove() {
		return inferenceService.remove(endpointName).toString();
	}

	@GetMapping("/getEndpointStatus")
	@ResponseBody
	public String getEndpointStatus() {
		String result = inferenceService.getEndpointStatus(endpointName);
		return result;
	}

	@PostMapping("/predict")
	@ResponseBody
	public String predict(String keyName) {
		JSONObject jsonRequest = new JSONObject();
		jsonRequest.put("bucket", bucketName);
		jsonRequest.put("image_uri", new String[] { keyName });
		String result = inferenceService.invokeEndpoint(endpointName, jsonRequest.toJSONString()).getData().toString();
		JSONObject jsonResponse = JSONArray.parseArray(result).getJSONObject(0);
		return jsonResponse.toString();
	}

	@PostMapping("/predictBinary")
	@ResponseBody
	public String predictBinary(HttpServletRequest request) {
		try {
			String result = inferenceService
					.invokeEndpoint(endpointName, request.getContentType(), request.getInputStream()).getData()
					.toString();
			JSONObject jsonResponse = JSONArray.parseArray(result).getJSONObject(0);
			return jsonResponse.toString();
		} catch (Exception e) {
			return "{}";
		}
	}
}
