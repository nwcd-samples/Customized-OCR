package cn.nwcdcloud.samples.ocr.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.nwcdcloud.samples.ocr.service.S3Service;

@Controller
public class IndexController {
	@Autowired
	private S3Service s3Service;
	@Value("${bucketName}")
	private String bucketName;
	@Value("${prefix}")
	private String prefix;
	@Value("${uploadType}")
	private String uploadType;

	@GetMapping("/")
	public ModelAndView index() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("index");
		return mv;
	}

	@GetMapping("/id")
	public ModelAndView id() {
		ModelAndView mv = new ModelAndView();
		mv.addObject("uploadType", uploadType);
		return mv;
	}

	@GetMapping("/license")
	public ModelAndView license() {
		ModelAndView mv = new ModelAndView();
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
