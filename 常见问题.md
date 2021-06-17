# 常见问题
## 推理节点如何弹性伸缩
在SageMaker Web控制台，选择推理->终端节点，点击ocr终端节点，在详情页面的**终端节点运行时设置**部分，选中**AllTraffic**前的单选框，再点击**配置Auto Scaling**。  
或直接点击连接：[宁夏区域](https://cn-northwest-1.console.amazonaws.cn/sagemaker/home?region=cn-northwest-1#/endpoints/ocr/autoscaling/AllTraffic)、
[北京区域](https://console.amazonaws.cn/sagemaker/home?region=cn-north-1#/endpoints/ocr/autoscaling/AllTraffic)。  
对于 Minimum capacity (最小容量)，键入您希望扩展策略维护的最小实例数。至少需要 1 个实例。  
对于 Maximum capacity (最大容量)，键入您希望扩展策略维护的最大实例数。  
对于 target value (目标值)，键入模型每分钟每个实例的平均调用次数。

## 如何设置S3存储桶跨源资源共享(CORS)
进入到S3存储桶的 **权限** 页签 **跨源资源共享(CORS)** 部分，点击编辑，输入以下内容，根据自己需要进行修改
```json
[
    {
        "AllowedHeaders": [
            "*"
        ],
        "AllowedMethods": [
            "PUT",
            "POST"
        ],
        "AllowedOrigins": [
            "*"
        ],
        "ExposeHeaders": []
    }
]
```

## 查看SageMaker日志
在SageMaker Web控制台，选择推理->终端节点，点击ocr终端节点，在详情页面的**监控**部分，点击**查看日志**。  
或直接访问CloudWatch Logs OCR连接：
[宁夏区域](https://cn-northwest-1.console.amazonaws.cn/cloudwatch/home?region=cn-northwest-1#logsV2:log-groups/log-group/$252Faws$252Fsagemaker$252FEndpoints$252Focr)、
[北京区域](https://console.amazonaws.cn/cloudwatch/home?region=cn-north-1#logsV2:log-groups/log-group/$252Faws$252Fsagemaker$252FEndpoints$252Focr)

## 使用textin做为识别后端
首先登录[https://www.textin.com/](https://www.textin.com/)。  
然后访问[https://www.textin.com/market/detail/text_recognize_3d1](https://www.textin.com/market/detail/text_recognize_3d1)开通通用文字识别。  
再访问[textin账号设置](https://www.textin.com/dashboard/userCenter/setting)，记录下APPID和SecretCode。  
在启动时，设置recognition.type=2，设置recognition.appId和recognition.secretKey为textin的APPID和SecretCode。  
请确认textin中有足够的余额。