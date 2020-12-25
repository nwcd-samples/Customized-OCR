function showImg(){
	$("#showImg").attr("src",window.URL.createObjectURL(document.getElementById('upload').files[0]));
}

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
	var upload = document.getElementById('upload');
	var file = upload.files[0];
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

function predict(type,keyName,showJson){
	var url = "/inference/predict";
	if(showJson){
		url += "/"+type;
	}
	$.post(url,
		  {"keyName":keyName},
		  function(result) {
			  console.log(result);
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
		  "json");
}

function analysis(type,fullData){
	console.log(fullData);
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