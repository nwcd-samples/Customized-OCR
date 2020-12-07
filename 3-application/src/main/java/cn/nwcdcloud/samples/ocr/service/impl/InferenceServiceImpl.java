package cn.nwcdcloud.samples.ocr.service.impl;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Service;

import cn.nwcdcloud.commons.constant.CommonConstants;
import cn.nwcdcloud.commons.lang.Result;
import cn.nwcdcloud.samples.ocr.service.InferenceService;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.ListRolesRequest;
import software.amazon.awssdk.services.iam.model.ListRolesResponse;
import software.amazon.awssdk.services.iam.model.Role;
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
import software.amazon.awssdk.utils.StringUtils;

@Service
public class InferenceServiceImpl implements InferenceService {

	@Override
	public Result invokeEndpoint(String endpointName, String body) {
		Result result = new Result();
		SdkBytes requestBody = SdkBytes.fromUtf8String(body);
		InvokeEndpointRequest request = InvokeEndpointRequest.builder().endpointName(endpointName)
				.contentType("application/json").body(requestBody).build();
		SageMakerRuntimeClient client = SageMakerRuntimeClient.create();
		InvokeEndpointResponse response = client.invokeEndpoint(request);
		result.setData(response.body().asUtf8String());
		return result;
	}
	

	@Override
	public Result invokeEndpoint(String endpointName,String contentType, InputStream inputStream) {
		Result result = new Result();
		SdkBytes requestBody = SdkBytes.fromInputStream(inputStream);
		InvokeEndpointRequest request = InvokeEndpointRequest.builder().endpointName(endpointName)
				.contentType(contentType).body(requestBody).build();
		SageMakerRuntimeClient client = SageMakerRuntimeClient.create();
		InvokeEndpointResponse response = client.invokeEndpoint(request);
		result.setData(response.body().asUtf8String());
		return result;
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
				.initialInstanceCount(1).modelName(modelName).variantName("AllTraffic").build();
		CreateEndpointConfigRequest request = CreateEndpointConfigRequest.builder().endpointConfigName(endpointName)
				.productionVariants(new ProductionVariant[] { productionVariant }).build();
		client.createEndpointConfig(request);
		return result;
	}

	private Result createModel(String imageUri, String endpointName) {
		Result result = new Result();
		String roleArn = getRoleArn();
		if (StringUtils.isBlank(roleArn)) {
			result.setCode(20);
			result.setMsg("获取SageMaker Role为空，请打开SageMaker控制台后重新执行");
			return result;
		}
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
		String modelName = endpointName + "-inference-" + df.format(new Date());
		SageMakerClient client = SageMakerClient.create();
		ContainerDefinition container = ContainerDefinition.builder().image(imageUri).build();
		CreateModelRequest request = CreateModelRequest.builder().modelName(modelName).executionRoleArn(roleArn)
				.primaryContainer(container).build();
		client.createModel(request);
		result.setData(modelName);
		return result;
	}

	private String getRoleArn() {
		String roleArn = "";
		//IAM必须明确指定Region为cn-north-1
		IamClient client = IamClient.builder().region(Region.CN_NORTH_1).build();
		ListRolesRequest request = ListRolesRequest.builder().pathPrefix("/service-role").build();
		ListRolesResponse response = client.listRoles(request);
		for (Role role : response.roles()) {
			if (role.roleName().startsWith("AmazonSageMaker-ExecutionRole-")) {
				roleArn = role.arn();
				break;
			}
		}
		return roleArn;
	}

	@Override
	public String getEndpointStatus(String endpointName) {
		String result = "None";
		SageMakerClient client = SageMakerClient.create();
		DescribeEndpointRequest request = DescribeEndpointRequest.builder().endpointName(endpointName).build();
		try {
			DescribeEndpointResponse response = client.describeEndpoint(request);
			result = response.endpointStatusAsString();
		} catch (SageMakerException e) {
			result = "None";
		}
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
}
