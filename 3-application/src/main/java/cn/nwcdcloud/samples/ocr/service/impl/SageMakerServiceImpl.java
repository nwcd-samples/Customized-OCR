package cn.nwcdcloud.samples.ocr.service.impl;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.ehcache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.nwcdcloud.commons.constant.CommonConstants;
import cn.nwcdcloud.commons.lang.Result;
import cn.nwcdcloud.samples.ocr.constant.ImageType;
import cn.nwcdcloud.samples.ocr.constant.OcrConstants;
import cn.nwcdcloud.samples.ocr.service.IamService;
import cn.nwcdcloud.samples.ocr.service.SageMakerService;
import software.amazon.awssdk.core.SdkBytes;
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
import software.amazon.awssdk.services.sagemakerruntime.SageMakerRuntimeClient;
import software.amazon.awssdk.services.sagemakerruntime.model.InvokeEndpointRequest;
import software.amazon.awssdk.services.sagemakerruntime.model.InvokeEndpointResponse;
import software.amazon.awssdk.services.sagemakerruntime.model.ModelErrorException;

@Service
public class SageMakerServiceImpl implements SageMakerService {
	@Value("${instanceCount}")
	private int instanceCount;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private IamService iamService;
	@Autowired
	private Cache<String, Object> cacheImageByte;

	@Override
	public Result invokeEndpoint(String endpointName, String body) {
		Result result = new Result();
		SdkBytes requestBody = SdkBytes.fromUtf8String(body);
		InvokeEndpointRequest request = InvokeEndpointRequest.builder().endpointName(endpointName)
				.contentType("application/json").body(requestBody).build();
		SageMakerRuntimeClient client = SageMakerRuntimeClient.create();
		InvokeEndpointResponse response = client.invokeEndpoint(request);
		JSONArray data = addImageInfoJson(response.body().asUtf8String(), body);
		result.setData(data);
		return result;
	}

	@Override
	public Result invokeEndpoint(String endpointName, String contentType, InputStream inputStream) {
		Result result = new Result();
		SdkBytes requestBody = SdkBytes.fromInputStream(inputStream);
		InvokeEndpointRequest request = InvokeEndpointRequest.builder().endpointName(endpointName)
				.contentType(contentType).body(requestBody).build();
		SageMakerRuntimeClient client = SageMakerRuntimeClient.create();
		try {
			InvokeEndpointResponse response = client.invokeEndpoint(request);
			JSONArray data = addImageInfoByte(response.body().asUtf8String(), requestBody.asByteArray());
			result.setData(data);
		} catch (ModelErrorException e) {
			if (e.getMessage().indexOf("413 Request Entity Too Large") != -1) {
				result.setCode(25);
				result.setMsg("待识别图片过大，上限为10MB");
				return result;
			}
			throw e;
		}
		return result;
	}

	/**
	 * 直接传输图片的只有1个
	 * 
	 * @param original
	 * @param imageType
	 * @param imageId
	 * @return
	 */
	private JSONArray addImageInfoByte(final String original, byte[] content) {
		JSONArray jsonArray = JSON.parseArray(original);
		JSONObject jsonObject = jsonArray.getJSONObject(0);
		String imageId = getUUID();
		cacheImageByte.put(imageId, content);
		jsonObject.put(OcrConstants.IMAGE_TYPE, ImageType.Byte.getId());
		jsonObject.put(OcrConstants.IMAGE_CONTENT, imageId);
		return jsonArray;
	}

	/**
	 * JSON格式的可能有多个
	 * 
	 * @param original
	 * @param imageId
	 * @return
	 */
	private JSONArray addImageInfoJson(final String original, String content) {
		JSONObject jsonContent = JSON.parseObject(content);
		JSONArray jsonArray = JSON.parseArray(original);
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			JSONObject jsonData = new JSONObject();
			jsonData.put("bucket", jsonContent.getString("bucket"));
			jsonData.put("image_uri", jsonContent.getJSONArray("image_uri").get(i));
			jsonObject.put(OcrConstants.IMAGE_TYPE, ImageType.JSON.getId());
			jsonObject.put(OcrConstants.IMAGE_CONTENT, jsonData);
		}
		return jsonArray;
	}

	@Override
	public Result deploy(String endpointName, String imageUri, String instanceType) {
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
	public Result getEndpointStatus(String endpointName) {
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
	public Result remove(String endpointName) {
		Result result = new Result();
		SageMakerClient client = SageMakerClient.create();
		DeleteEndpointRequest request = DeleteEndpointRequest.builder().endpointName(endpointName).build();
		client.deleteEndpoint(request);
		DeleteEndpointConfigRequest request2 = DeleteEndpointConfigRequest.builder().endpointConfigName(endpointName)
				.build();
		client.deleteEndpointConfig(request2);
		return result;
	}

	private String getUUID() {
		return UUID.randomUUID().toString().replace("-", "").toUpperCase();
	}
}
