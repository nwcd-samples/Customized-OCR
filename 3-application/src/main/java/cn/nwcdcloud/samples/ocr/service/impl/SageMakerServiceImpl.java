package cn.nwcdcloud.samples.ocr.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.nwcdcloud.commons.constant.CommonConstants;
import cn.nwcdcloud.commons.lang.Result;
import cn.nwcdcloud.samples.ocr.service.IamService;
import cn.nwcdcloud.samples.ocr.service.SageMakerService;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.sagemaker.SageMakerClient;
import software.amazon.awssdk.services.sagemaker.model.ContainerDefinition;
import software.amazon.awssdk.services.sagemaker.model.CreateEndpointConfigRequest;
import software.amazon.awssdk.services.sagemaker.model.CreateEndpointRequest;
import software.amazon.awssdk.services.sagemaker.model.CreateModelRequest;
import software.amazon.awssdk.services.sagemaker.model.DeleteEndpointConfigRequest;
import software.amazon.awssdk.services.sagemaker.model.DeleteEndpointRequest;
import software.amazon.awssdk.services.sagemaker.model.DescribeEndpointRequest;
import software.amazon.awssdk.services.sagemaker.model.DescribeEndpointResponse;
import software.amazon.awssdk.services.sagemaker.model.ProductionVariant;
import software.amazon.awssdk.services.sagemaker.model.SageMakerException;

@Service
public class SageMakerServiceImpl implements SageMakerService {
	@Value("${instanceCount}")
	private int instanceCount;
	@Value("${imageUri}")
	private String imageUri;
	@Value("${endpointName}")
	private String endpointName;
	@Value("${instanceType}")
	private String instanceType;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private IamService iamService;

	@Override
	public Result deploy() {
		Result result = createModel(imageUri, endpointName);
		if (result.getCode() != CommonConstants.NORMAL) {
			return result;
		}

		String modelName = (String) result.getData();
		result = createEndpointConfig(endpointName, modelName, instanceType);
		if (result.getCode() != CommonConstants.NORMAL) {
			return result;
		}
		result = createEndpoint(endpointName);
		return result;
	}

	private Result createEndpoint(String endpointName) {
		Result result = new Result();
		SageMakerClient client = SageMakerClient.create();
		CreateEndpointRequest request = CreateEndpointRequest.builder().endpointName(endpointName)
				.endpointConfigName(endpointName).build();
		client.createEndpoint(request);
		return result;
	}

	private Result createEndpointConfig(String endpointName, String modelName, String instanceType) {
		Result result = new Result();
		SageMakerClient client = SageMakerClient.create();
		ProductionVariant productionVariant = ProductionVariant.builder().instanceType(instanceType)
				.initialInstanceCount(instanceCount).modelName(modelName).variantName("AllTraffic").build();
		CreateEndpointConfigRequest request = CreateEndpointConfigRequest.builder().endpointConfigName(endpointName)
				.productionVariants(new ProductionVariant[] { productionVariant }).build();
		client.createEndpointConfig(request);
		return result;
	}

	private Result createModel(String imageUri, String endpointName) {
		Result result = iamService.getRoleArn();
		if (result.getCode() != CommonConstants.NORMAL) {
			return result;
		}
		String roleArn = (String) result.getData();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
		String modelName = endpointName + "-inference-" + df.format(new Date());
		SageMakerClient client = SageMakerClient.create();
		ContainerDefinition container = ContainerDefinition.builder().image(imageUri).build();
		CreateModelRequest request = CreateModelRequest.builder().modelName(modelName).executionRoleArn(roleArn)
				.primaryContainer(container).build();
		try {
			client.createModel(request);
		} catch (Exception e) {
			logger.error("创建endpoint出错", e);
			result.setCode(22);
			result.setMsg("创建endpoint出错");
			return result;
		}
		result.setData(modelName);
		return result;
	}

	@Override
	public Result getEndpointStatus() {
		Result result = new Result();
		String status = "None";
		try {
			SageMakerClient client = SageMakerClient.create();
			DescribeEndpointRequest request = DescribeEndpointRequest.builder().endpointName(endpointName).build();
			DescribeEndpointResponse response = client.describeEndpoint(request);
			status = response.endpointStatusAsString();
		} catch (SdkClientException e) {
			result.setCode(10);
			result.setMsg("未设置访问密钥，请先进行配置");
			return result;
		} catch (SageMakerException e) {
			if (e.getMessage().indexOf("The request signature") != -1) {
				result.setCode(10);
				result.setMsg("访问密钥设置错误或已失效，请重新进行配置");
				return result;
			}
			if (e.getMessage().indexOf("is not authorized to perform") != -1) {
				result.setCode(10);
				result.setMsg("当前访问密钥无权访问SageMaker endpoint，请重新进行配置");
				return result;
			}
			if (e.getMessage().indexOf("Could not find endpoint") == -1) {
				logger.warn("获取endpoint时报错", e);
			}
			status = "None";
		}
		result.setData(status);
		return result;
	}

	@Override
	public Result remove() {
		Result result = new Result();
		SageMakerClient client = SageMakerClient.create();
		DeleteEndpointRequest request = DeleteEndpointRequest.builder().endpointName(endpointName).build();
		client.deleteEndpoint(request);
		DeleteEndpointConfigRequest request2 = DeleteEndpointConfigRequest.builder().endpointConfigName(endpointName)
				.build();
		client.deleteEndpointConfig(request2);
		return result;
	}

}
