<!doctype html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>OCR</title>
	<script src="https://cdn.staticfile.org/jquery/3.2.1/jquery.min.js"></script>
</head>
<body>
Endpoint Status:<b id="status">None</b><br>
<input type="button" id="deploy" onclick="deploy();" value="创建Endpoint" disabled="disabled">
<input type="button" id="remove" onclick="remove();" value="删除Endpoint" disabled="disabled">
<br>
<a href="id">身份证</a> <a href="license">营业执照</a>
<script>
$(function(){
	getStatus();
});

function getStatus(){
	$.get("inference/getEndpointStatus",
		  function(result) {
			  console.log("获取status:"+result);
			  changeStatus(result);
		  });
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
	$.post('inference/deploy');
	setTimeout("getStatus()",3*1000);
}

function remove(){
	$("#remove").attr("disabled",true);
	$.post('inference/remove');
	setTimeout("getStatus()",2*1000);
}
</script>
</body>
</html>