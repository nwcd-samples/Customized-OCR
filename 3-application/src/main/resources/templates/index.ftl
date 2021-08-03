<!doctype html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>NICO</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.4.1/dist/css/bootstrap.min.css" integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">
    <script src="https://cdn.staticfile.org/jquery/3.2.1/jquery.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js" integrity="sha384-Q6E9RHvbIyZFJoft+2mJbHaEWldlvI9IOYy5n3zV9zzTtmI3UksdQRVvoxMfooAo" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.4.1/dist/js/bootstrap.min.js" integrity="sha384-wfSDF2E50Y2D1uUdj0O3uMBJnjuUD4Ih7YwaYd1iqfktj0Uod8GCExl3Og8ifwB6" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/vue/dist/vue.js"></script>

</head>
<body class="d-flex flex-column h-100">
<div class="container" id="main" style="800px">

    <h1 class="mt-3 " style="width:100%;text-align:center;margin:30px" >NICO(${version!})</h1>

    <div class="row">
        <div class="col-sm">
			<#if recognitionType == 1>
			
            <div style="margin-top:20px">
                ${endpointName}推理服务状态: <b id="status">获取状态中</b>
            </div>

            <div style="margin-top:20px">
                <input type="button" id="deploy" onclick="deploy();" value="创建OCR推理服务" disabled="disabled">
                <input type="button" id="remove" onclick="remove();" value="删除OCR推理服务" disabled="disabled">
                <input type="button" id="remove" onclick="createRole();" value="创建Role" style="display:none;"><br>
                PS:创建OCR推理服务大约需要7分钟。OCR推理服务创建后开始计费，不使用时，请及时删除。
            </div>
			</#if>




            <div class="list-group" style="margin-top:50px;width:300px">
                <a href="#" class="list-group-item list-group-item-action active">
                    卡证文字识别
                </a>
                <a href="ocr?type=id" class="list-group-item list-group-item-action">身份证</a>
                <a href="ocr?type=business_license" class="list-group-item list-group-item-action">营业执照</a>
            </div>



            <div class="list-group" style="margin-top:5px;width:300px">
                <a href="#" class="list-group-item list-group-item-action active">
                    财务票据文字识别
                </a>
                <a href="ocr?type=invoice" class="list-group-item list-group-item-action">发票识别</a>
                <a href="ocr?type=train_ticket" class="list-group-item list-group-item-action">火车票识别</a>
                <a href="ocr?type=outpatient" class="list-group-item list-group-item-action">门诊收据</a>


            </div>


        </div>

        <div class="col-sm">
             <div>
                <h3>架构说明</h3>
                <small>OCR推理服务部署在SageMaker endpoint上。<br>
                上传图片时，支持两种方式：上传到Web服务，然后推送图片流到推理服务（默认）；上传到S3，推理服务从S3获取图片。</small>
            </div>
        </div>
    </div>

</div>


<#if recognitionType == 1>
<script>
$(function(){
	getStatus();
});

function getStatus(){
	$.get("sagemaker/getEndpointStatus",
		  function(result) {
		      if (result.code == 1) {
			      console.log("获取status:"+result.data);
			      changeStatus(result.data);
		      }else{
		    	  $("#status").html("获取状态失败");
				  alert(result.msg);
		      }
		  },
		  "json");
}

function changeStatus(status){
	globalStatus=status;
	$("#status").html(status);
	switch(status){
	case "None":
		$("#deploy").attr("disabled",false);
		$("#remove").attr("disabled",true);
		break;
	case "Deleting":
		$("#deploy").attr("disabled",true);
		$("#remove").attr("disabled",true);
		setTimeout("getStatus()",3*1000);
		break;
	case "Creating":
		$("#deploy").attr("disabled",true);
		$("#remove").attr("disabled",true);
		setTimeout("getStatus()",30*1000);//不采用setInterval主要是避免刷新页面
		break;
	case "InService":
	case "Failed":
		$("#deploy").attr("disabled",true);
		$("#remove").attr("disabled",false);
		break;
	default:
		$("#deploy").attr("disabled",true);
		$("#remove").attr("disabled",true);
		break;
	}
}

function deploy(){
	$("#deploy").attr("disabled",true);
	$.post('sagemaker/deploy',{},
		    function(result) {
				if (result.code == 1) {
					setTimeout("getStatus()",3*1000);
				}else{
					alert(result.msg);
				}
	        },
	        "json");
}

function remove(){
	if(confirm('确认删除Endpoint?')){
		$("#remove").attr("disabled",true);
		$.post('sagemaker/remove');
		setTimeout("getStatus()",2*1000);
	}
}

function createRole(){
	$.post("createRole",
		  function(result) {
		      if (result.code == 1) {
			      alert("创建成功");
		      }else{
				  alert(result.msg);
		      }
		  },
		  "json");
}
</script>
</#if>
</body>
</html>