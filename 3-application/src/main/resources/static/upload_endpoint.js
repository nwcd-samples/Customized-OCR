function showImg(){
	$("#showImg").attr("src",window.URL.createObjectURL(document.getElementById('upload').files[0]));
}

function inference(){
	predictBinary();
}

function predictBinary(){
    $("#loading-icon").show();
	var upload = document.getElementById('upload');
	var file = upload.files[0];
	$.ajax({
		url : "/inference/predictBinary", 
		type : 'POST', 
		data : file, 
		processData : false,
		// 告诉jQuery不要去设置Content-Type请求头
		contentType : false,
		dataType:"json",
		beforeSend:function(){
			console.log("正在进行，请稍候");
		},
		success : function(result) {
			console.log(JSON.stringify(result));
			console.log(result);
			get_data(result);
			$("#loading-icon").hide()
		}, 
		error : function(responseStr) { 
			console.log(responseStr);
		}
	});
}