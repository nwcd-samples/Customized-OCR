function showImg(){
	$("#showImg").attr("src",window.URL.createObjectURL(document.getElementById('upload').files[0]));
}

function inference(){
	generatePresignedUrl();
}

function generatePresignedUrl(){
    $("#loading-icon").show();
	$.post("/generatePresignedUrl",
			  {},
			  function(result) {
			      if (result.code==1) {
					  data=result.data;
					  console.log(data);
					  upload(data);
				  }else{
				  	alert(result.msg);
				  }
			  },
			  "json");
}

function upload(data){
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
			predict(data.keyName);
		}, 
		error : function(result) { 
			console.log(result);
		}
	});
}

function predict(keyName){
	$.post("/inference/predict",
		  {"keyName":keyName},
		  function(result) {
			  console.log(result);
			  get_data(result);
			  $("#loading-icon").hide()
		  },
		  "json");
}