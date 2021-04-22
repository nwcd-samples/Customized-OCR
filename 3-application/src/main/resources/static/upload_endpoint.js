function showImg(){
	$("#showImg").attr("src",window.URL.createObjectURL(document.getElementById('upload').files[0]));
}

function inference(type,showJson){
	var file = document.getElementById('upload').files[0];
	predict(type,showJson,file);
}

var globalData;
function predict(type,showJson,file){
    $("#loading-icon").show();
	if(!file){
	    $("#loading-icon").hide();
		alert("请先选择需要识别的图片");
		return false;
	}
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
		    		globalData = JSON.parse(result.data);
			    	analysis(type,globalData);
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

function onlyAnalysis(type){
	if(globalData){
		console.log("直接解析");
		analysis(type,globalData);
	}else{
		console.log("没有data，需要推理");
		inference(type,false);
	}
}