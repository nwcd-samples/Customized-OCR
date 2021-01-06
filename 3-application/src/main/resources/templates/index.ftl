<!doctype html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>NICO Web 演示</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.4.1/dist/css/bootstrap.min.css" integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">
    <script src="https://cdn.staticfile.org/jquery/3.2.1/jquery.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js" integrity="sha384-Q6E9RHvbIyZFJoft+2mJbHaEWldlvI9IOYy5n3zV9zzTtmI3UksdQRVvoxMfooAo" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.4.1/dist/js/bootstrap.min.js" integrity="sha384-wfSDF2E50Y2D1uUdj0O3uMBJnjuUD4Ih7YwaYd1iqfktj0Uod8GCExl3Og8ifwB6" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/vue/dist/vue.js"></script>

</head>
<body class="d-flex flex-column h-100">
<div class="container" id="main" style="800px">

    <h1 class="mt-3 " style="width:100%;text-align:center;margin:30px" >NICO Web 演示</h1>

    <div class="row">
        <div class="col-sm">

            <div style="margin-top:20px">
                OCR推理服务状态: <b id="status">None</b>
            </div>

            <div style="margin-top:20px">
                <input type="button" id="deploy" onclick="deploy();" value="创建OCR推理服务" disabled="disabled">
                <input type="button" id="remove" onclick="remove();" value="删除OCR推理服务" disabled="disabled">
            </div>





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


    <script>
$(function(){
	getStatus();
});

function getStatus(){
	$.get("inference/getEndpointStatus",
		  function(result) {
		      if (result.code == 1) {
			      console.log("获取status:"+result.data);
			      changeStatus(result.data);
		      }else{
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
	$.post('inference/deploy',{},
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
		$.post('inference/remove');
		setTimeout("getStatus()",2*1000);
	}
}
</script>
</body>
</html>