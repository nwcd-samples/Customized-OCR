function inference(type,showJson){
	generatePresignedUrl(type,showJson);
}

function generatePresignedUrl(type,showJson){
    $("#loading-icon").show();
	$.post("/generatePresignedUrl",
			  {},
			  function(result) {
			      if (result.code == 1) {
					  data=result.data;
					  console.log(data);
					  upload(type,data,showJson);
				  }else{
				  	alert(result.msg);
				  }
			  },
			  "json");
}

function upload(type,data,showJson){
	var file = document.getElementById('upload').files[0];
	if(!file){
	    $("#loading-icon").hide();
		alert("请先选择需要识别的图片");
		return false;
	}
	$.ajax({
		url : data.url, 
		type : 'PUT', 
		data : file, 
		processData : false, 
		contentType : "image/jpeg",
		beforeSend:function(){
			console.log("正在进行，请稍候");
		},
		success : function(result) {
			predict(type,data.keyName,showJson);
		}, 
		error : function(result) { 
			console.log(result);
		}
	});
}

var globalData;
function predict(type,keyName,showJson){
	var url = "/inference/predict";
	if(showJson){
		url += "/"+type;
	}
	$.get(url,
		  {"keyName":keyName},
		  function(result) {
			  console.log(result);
		      if (result.code == 1) {
			      if(showJson){
					  $("#loading-icon").hide();
			    	  alert(JSON.stringify(result.data));
			      }else{
			    	  globalData = result.data;
			    	  analysis(type,globalData);
			      }
		      }else{
				  $("#loading-icon").hide();
		    	  alert(result.msg);
		      }
		  },
		  "json");
}

function analysis(type,fullData){
	console.log(fullData);
	$.post("/inference/analysis/"+type,
			{"fullData":JSON.stringify(fullData)},
			function(result) {
			    if (result.code == 1) {
			    	displayResult(fullData,result.data);
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