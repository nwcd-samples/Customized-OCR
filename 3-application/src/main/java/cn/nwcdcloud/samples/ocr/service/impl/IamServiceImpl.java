package cn.nwcdcloud.samples.ocr.service.impl;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.nwcdcloud.commons.lang.Result;
import cn.nwcdcloud.samples.ocr.parse.FileUtils;
import cn.nwcdcloud.samples.ocr.service.IamService;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.AttachRolePolicyRequest;
import software.amazon.awssdk.services.iam.model.CreatePolicyRequest;
import software.amazon.awssdk.services.iam.model.CreatePolicyResponse;
import software.amazon.awssdk.services.iam.model.CreateRoleRequest;
import software.amazon.awssdk.services.iam.model.CreateRoleResponse;
import software.amazon.awssdk.services.iam.model.IamException;
import software.amazon.awssdk.services.iam.model.ListRolesRequest;
import software.amazon.awssdk.services.iam.model.ListRolesResponse;
import software.amazon.awssdk.services.iam.model.Role;

@Service
public class IamServiceImpl implements IamService {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public Result createRole() {
		Result result = new Result();
		DateFormat df = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
		String timestamp = df.format(new Date());

		// IAM必须明确指定Region为cn-north-1
		IamClient iam = IamClient.builder().region(Region.CN_NORTH_1).build();
		try {
			String policyName = "AmazonSageMaker-ExecutionPolicy-" + timestamp;
			InputStream isExecutionPolicy = this.getClass().getClassLoader()
					.getResourceAsStream("SageMakerConfig/ExecutionPolicy.json");
			String executionPolicyDocument = FileUtils.readFile(isExecutionPolicy);
			isExecutionPolicy.close();
			CreatePolicyRequest createPolicyRequest = CreatePolicyRequest.builder().policyName(policyName)
					.policyDocument(executionPolicyDocument).build();
			CreatePolicyResponse createPolicyResponse = iam.createPolicy(createPolicyRequest);

			String roleName = "AmazonSageMaker-ExecutionRole-" + timestamp;
			InputStream isAssumeRolePolicy = this.getClass().getClassLoader()
					.getResourceAsStream("SageMakerConfig/AssumeRolePolicy.json");
			String assumeRolePolicyDocument = FileUtils.readFile(isAssumeRolePolicy);
			isAssumeRolePolicy.close();
			CreateRoleRequest createRoleRequest = CreateRoleRequest.builder().path("/service-role/").roleName(roleName)
					.assumeRolePolicyDocument(assumeRolePolicyDocument).build();
			CreateRoleResponse createRoleResponse = iam.createRole(createRoleRequest);
			result.setData(createRoleResponse.role().arn());
			logger.info("Create Role Arn:{}", createRoleResponse.role().arn());

			AttachRolePolicyRequest attachRolePolicyRequest = AttachRolePolicyRequest.builder().roleName(roleName)
					.policyArn("arn:aws-cn:iam::aws:policy/AmazonSageMakerFullAccess").build();
			iam.attachRolePolicy(attachRolePolicyRequest);
			attachRolePolicyRequest = AttachRolePolicyRequest.builder().roleName(roleName)
					.policyArn(createPolicyResponse.policy().arn()).build();
			iam.attachRolePolicy(attachRolePolicyRequest);
		} catch (Exception e) {
			logger.error("创建Role失败", e);
			result.setCode(24);
			result.setMsg("创建Role失败，请确认当前访问密钥/EC2角色有相关权限");
			return result;
		}
		return result;
	}

	@Override
	public Result getRoleArn() {
		Result result = new Result();
		// IAM必须明确指定Region为cn-north-1
		IamClient client = IamClient.builder().region(Region.CN_NORTH_1).build();
		ListRolesRequest request = ListRolesRequest.builder().pathPrefix("/service-role").build();
		try {
			ListRolesResponse response = client.listRoles(request);
			for (Role role : response.roles()) {
				if (role.roleName().startsWith("AmazonSageMaker-ExecutionRole-")) {
					result.setData(role.arn());
					logger.info("Get Role Arn:{}", role.arn());
					return result;
				}
			}
		} catch (IamException e) {
			result.setCode(23);
			result.setMsg("获取Role失败，请确认当前访问密钥/EC2角色有访问IAM权限");
			logger.warn("获取Role失败");
			return result;
		}
		result = createRole();
		try {
			// 创建角色后，需要延时下才能get
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			logger.warn("中断失败", e);
		}
		return result;
	}
}
