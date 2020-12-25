function showImg(){
	$("#showImg").attr("src",window.URL.createObjectURL(document.getElementById('upload').files[0]));
}

function inference(type,showJson){
	predict(type,showJson);
}

function predict(type,showJson){
    $("#loading-icon").show();
	var upload = document.getElementById('upload');
	var file = upload.files[0];
	var url = "/inference/predict";
	if(showJson){
		url += "/"+type;
	}
	$.ajax({
		url : url, 
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
			//console.log(result);
		    if (result.code == 1) {
		    	if(showJson){
				    $("#loading-icon").hide();
		    		alert(result.data);
		    	}else{
			    	data = JSON.parse(result.data);
			    	analysis(type,data);
		    	}
		    }else{
		    	alert(result.msg);
		    }
		}, 
		error : function(responseStr) { 
			console.log(responseStr);
		    $("#loading-icon").hide();
		}
	});
}

function analysis(type,fullData){
	//console.log(fullData);
	$.post("/inference/analysis/"+type,
			{"fullData":JSON.stringify(fullData)},
			function(result) {
			    if (result.code == 1) {
			    	displayResult(fullData,JSON.parse(result.data));
				}else{
					alert(result.msg);
				}
			    $("#loading-icon").hide();
			},
			"json");
}