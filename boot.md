# 启动参数说明
本文主要列举启动ocr.jar时，可选参数说明。  
默认启动命令：`java -jar ocr.jar`  
如果需要把上传的文件存放到S3，请使用命令：`java -jar ocr.jar --uploadType=s3 --bucketName=<BucketName>`  
**BucketName**为需要存放的S3名称。  
可选参数如下：

| 参数名 | 默认值 | 说明 |
| ----  | ---- | ---- |
|server.port|80|http服务端口号|
|recognition.type|1|后端识别类别，可选值为：1、2，分别代表使用SageMaker和Textin|
|recognition.appId|无|使用非SageMaker时配置appId|
|recognition.secretKey|无|使用非SageMaker时配置secretKey|
|imageUri|048912060910.dkr.ecr.cn-northwest-1.amazonaws.com.cn/nwcd/ocr-inference:2.0.2-2.0-5-cpu|OCR推理服务镜像，根据需要选择CPU或GPU，如果使用北京region，把 cn-northwest-1 改为 cn-north-1<br>使用GPU需要同时修改instanceType为GPU机型<br>可选值如下：<br>048912060910.dkr.ecr.cn-northwest-1.amazonaws.com.cn/nwcd/ocr-inference:2.0.2-2.0-5-cpu<br>048912060910.dkr.ecr.cn-northwest-1.amazonaws.com.cn/nwcd/ocr-inference:2.0.2-2.0-5-gpu<br>048912060910.dkr.ecr.cn-north-1.amazonaws.com.cn/nwcd/ocr-inference:2.0.2-2.0-5-cpu<br>048912060910.dkr.ecr.cn-north-1.amazonaws.com.cn/nwcd/ocr-inference:2.0.2-2.0-5-gpu|
|modelUri|s3://nwcd-samples/nico/model/inference-mobile.tar.gz|推理模型地址，可选值如下：<br>s3://nwcd-samples/nico/model/inference-mobile.tar.gz<br>s3://nwcd-samples/nico/model/inference-server.tar.gz|
|endpointName|ocr|SageMaker中endpoint名称|
|instanceType|ml.m5.large|SageMaker中endpoint推理机型，如果imageUri使用GPU版本，这里需要使用GPU机型<br>可选机型参见https://www.amazonaws.cn/sagemaker/pricing/ ，展开模型部署可见列表|
|instanceCount|1|SageMaker中endpoint推理初始机器数|
|uploadType|endpoint|上传文件类别，可选值为：endpoint、s3，分别表示上传图片到web服务和s3，如果配置为s3，则必须配置bucketName|
|bucketName|无|uploadType为s3时，需要配置bucketName|
|prefix|ocr/|uploadType为s3时，上传文件的前缀|
|templateDir|/data/ocr/config/|自定义模板文件存放路径。如果自定义模板和内置模板都定义了同一类别，优先使用自定义模板|