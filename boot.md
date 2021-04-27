# 启动参数说明
本文主要列举启动ocr.jar时，可选参数说明。  
默认启动命令：`java -jar ocr.jar`  
如果需要把上传的文件存放到S3，请使用命令：`java -jar ocr.jar --uploadType=s3 --bucketName=<BucketName>`  
**BucketName**为需要存放的S3名称。  
可选参数如下：

| 参数名 | 默认值 | 说明 |
| ----  | ---- | ---- |
|server.port|80|http服务端口号|
|imageUri|048912060910.dkr.ecr.cn-northwest-1.amazonaws.com.cn/nwcd/ocr-inference:2.0.2-2.0-cpu|OCR推理服务镜像，根据需要选择CPU或GPU，如果使用北京region，把 cn-northwest-1 改为 cn-north-1<br>可选值如下：<br>048912060910.dkr.ecr.cn-northwest-1.amazonaws.com.cn/nwcd/ocr-inference:2.0.2-2.0-cpu<br>048912060910.dkr.ecr.cn-northwest-1.amazonaws.com.cn/nwcd/ocr-inference:2.0.2-2.0-gpu<br>048912060910.dkr.ecr.cn-north-1.amazonaws.com.cn/nwcd/ocr-inference:2.0.2-2.0-cpu<br>048912060910.dkr.ecr.cn-north-1.amazonaws.com.cn/nwcd/ocr-inference:2.0.2-2.0-gpu|
|endpointName|ocr|SageMaker中endpoint名称|
|instanceType|ml.m5.large|SageMaker中endpoint推理机型，如果imageUri使用GPU版本，这里需要使用GPU机型|
|instanceCount|1|SageMaker中endpoint推理初始机器数|
|uploadType|endpoint|上传文件类别，可选值为：endpoint、s3，分别表示上传图片到web服务和s3，如果配置为s3，则必须配置bucketName|
|bucketName|无|uploadType为s3时，需要配置bucketName|
|prefix|ocr/|uploadType为s3时，上传文件的前缀|
|templateDir|/data/ocr/config/|自定义模板文件存放路径。如果自定义模板和内置模板都定义了同一类别，优先使用自定义模板|