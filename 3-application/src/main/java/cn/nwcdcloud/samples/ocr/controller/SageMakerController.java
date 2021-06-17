package cn.nwcdcloud.samples.ocr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.nwcdcloud.samples.ocr.service.SageMakerService;

@RequestMapping("sagemaker")
@Controller
public class SageMakerController {
//	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private SageMakerService sageMakerService;

	@PostMapping("/deploy")
	@ResponseBody
	public String deploy() {
		return sageMakerService.deploy().toString();
	}

	@PostMapping("/remove")
	@ResponseBody
	public String remove() {
		return sageMakerService.remove().toString();
	}

	@GetMapping("/getEndpointStatus")
	@ResponseBody
	public String getEndpointStatus() {
		String result = sageMakerService.getEndpointStatus().toString();
		return result;
	}
}
