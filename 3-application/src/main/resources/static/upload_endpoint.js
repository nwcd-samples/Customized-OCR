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
		url : "/inference/predictBinary/id", 
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
			console.log(result);
		    $("#loading-icon").hide();
		    if (result.code == 1) {
		    	data = JSON.parse(result.data);
		    	get_data(data[0]);
		    }else{
		    	alert(result.msg);
		    }
		}, 
		error : function(responseStr) { 
			console.log(responseStr);
		}
	});
}