<!doctype html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>OCR</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.4.1/dist/css/bootstrap.min.css" integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js" integrity="sha384-Q6E9RHvbIyZFJoft+2mJbHaEWldlvI9IOYy5n3zV9zzTtmI3UksdQRVvoxMfooAo" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.4.1/dist/js/bootstrap.min.js" integrity="sha384-wfSDF2E50Y2D1uUdj0O3uMBJnjuUD4Ih7YwaYd1iqfktj0Uod8GCExl3Og8ifwB6" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/vue/dist/vue.js"></script>
	<script src="https://cdn.staticfile.org/jquery/3.2.1/jquery.min.js"></script>
</head>
<body>

<div class="container" id="main" style="width:100%">

<h1>NICO web 演示</h1>

Endpoint Status:<b id="status">None</b><br>
<input type="button" id="deploy" onclick="deploy();" value="创建Endpoint" disabled="disabled">
<input type="button" id="remove" onclick="remove();" value="删除Endpoint" disabled="disabled">
<br>
<a href="id">身份证</a> <a href="license">营业执照</a>

</div>
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
	if(confirm('确认删除Endpoint?')){
		$("#remove").attr("disabled",true);
		$.post('inference/remove');
		setTimeout("getStatus()",2*1000);
	}
}
</script>
</body>
</html>