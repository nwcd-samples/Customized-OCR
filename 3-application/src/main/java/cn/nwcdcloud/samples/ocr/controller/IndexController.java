package cn.nwcdcloud.samples.ocr.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.nwcdcloud.samples.ocr.service.ManifestService;
import cn.nwcdcloud.samples.ocr.service.S3Service;
import software.amazon.awssdk.utils.StringUtils;

@Controller
public class IndexController {
	@Autowired
	private S3Service s3Service;
	@Autowired
	private ManifestService manifestService;
	@Value("${recognition.appId}")
	private String appId;
	@Value("${bucketName}")
	private String bucketName;
	@Value("${prefix}")
	private String prefix;
	@Value("${uploadType}")
	private String uploadType;
	@Value("${recognition.type}")
	private int recognitionType;
	private static Map<String, String> mapOcrType = new HashMap<>();
	{
		mapOcrType.put("id", "身份证识别");
		mapOcrType.put("invoice", "发票识别");
		mapOcrType.put("business_license", "营业执照识别");
		mapOcrType.put("train_ticket", "火车票");
	}

	@GetMapping("/")
	public ModelAndView index() {
		ModelAndView mv = new ModelAndView();
		System.out.println("appId" + appId);
		mv.addObject("version", manifestService.get("Implementation-Version"));
		mv.addObject("recognitionType", recognitionType);
		mv.setViewName("index");
		return mv;
	}

	@GetMapping("/ocr")
	public ModelAndView id(String type) {
		ModelAndView mv = new ModelAndView();
		mv.addObject("type", type);
		String title = mapOcrType.get(type);
		if (StringUtils.isBlank(title)) {
			title = type;
		}
		mv.addObject("title", title);
		mv.addObject("uploadType", uploadType);
		return mv;
	}

	@PostMapping("/generatePresignedUrl")
	@ResponseBody
	public String generatePresignedUrl() {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd/HH-mm-ss-SSS");
		String keyName = prefix + df.format(new Date()) + ".jpg";
		return s3Service.generatePresignedUrl(null, bucketName, keyName, 10).toString();
	}
}
