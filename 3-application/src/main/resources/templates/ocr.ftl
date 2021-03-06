<!DOCTYPE html>
<html lang="en">
<head>

    <meta charset="utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <meta name="description" content="" />
    <meta name="author" content="" />

    <title>${title}</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.4.1/dist/css/bootstrap.min.css" integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">
    
	<script src="https://cdn.staticfile.org/jquery/3.2.1/jquery.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js" integrity="sha384-Q6E9RHvbIyZFJoft+2mJbHaEWldlvI9IOYy5n3zV9zzTtmI3UksdQRVvoxMfooAo" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.4.1/dist/js/bootstrap.min.js" integrity="sha384-wfSDF2E50Y2D1uUdj0O3uMBJnjuUD4Ih7YwaYd1iqfktj0Uod8GCExl3Og8ifwB6" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/vue/dist/vue.js"></script>
    <script src="/static/jcanvas.min.js"></script>
    <script src="/static/upload_${uploadType}.js"></script>
</head>
<body style="width:1600px;margin:0 auto;">

<div class="container-fluid" id="main" style="width:100%">

    <header class="blog-header py-3">
        <div class="row flex-nowrap justify-content-between align-items-center">
            <div class="col-4 pt-1">
            </div>
            <div class="col-4 text-center">
                <h3>${title}</h3>
            </div>
            <div class="col-4 d-flex justify-content-end align-items-center">
                <span class="input-group-text">置信度</span><input type="number" @change='change_confidence_threshold'  οninput="value=value.replace(/[^\d]/g,'')"  v-model="confidence_threshold"    placeholder="置信度">
                <a class="btn btn-sm btn-outline-secondary" style="height:42px;margin-right:5px;padding-top:8px" href="/">返回首页</a>
                <a class="btn btn-sm btn-outline-secondary" target="_blank" href="https://www.amazonaws.cn/"><img src="../static/logo.jpg" /></a>
                <a class="btn btn-sm btn-outline-secondary" style="height:42px;margin-left:5px;padding-top:8px" target="_blank" href="https://github.com/nwcd-samples/Customized-OCR">Github</a>

            </div>
        </div>
    </header>


    <div>
            <nav class="navbar navbar-light bg-light">
                <form class="form-inline">
					<input type="file" id="upload" onchange="showImg();">
					<a type="button"   onclick="inference('${type}',false);" class="btn btn-outline-success my-2 my-sm-0" >识别</a>
					&nbsp;
					<a type="button" value="" onclick="inference('${type}',true);"  class="btn btn-outline-success my-2 my-sm-0">仅显示JSON</a>
					&nbsp;
					<a type="button" value="" onclick="onlyAnalysis('${type}');"  class="btn btn-outline-success my-2 my-sm-0">仅解析</a>
					&nbsp;
					<a type="button" value="" onclick="loadLocalJson();"  class="btn btn-outline-success my-2 my-sm-0">载入本地JSON</a>
					<div class="spinner-grow text-dark" role="status" id="loading-icon" style="display:none">
					    <span class="sr-only">Loading...</span>
					</div>
                    <p  class="text-primary" style="margin-left:30px;margin-top:10px;font-size:14px">
                        <span id="click_point_coord_span"> </span>
                    </p>
                </form>
            </nav>
            <div >
                <div style="font-size:13px"><span>&nbsp;</span> <span id="click_block_coord_span"> </span></div>
            </div>
    </div>

    <div class="row" >
        <div class="col-md-7" class="overflow-auto" id="myCanvasParent" >
            <canvas id="myCanvas" width="900" height="1200"
                  style="border:1px solid #000000;">
        </canvas>
        </div>

        <!-- Right Start -->
        <div class="col-md-5">
            <div>
                <img id="showImg" width="500px">
            </div>

            <div>
                <span>&nbsp;</span>
            </div>

            <div  >
                <h5> 键值对列表</h5>
                <table class="table">
                    <thead class="thead-dark">
                    <tr>
                        <th scope="col">名称</th>
                        <th scope="col">值</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr v-for="(result, index) of result_list" >
                        <td><small v-text="result.name"></small></td>
                        <td><small v-text="result.value"></small></td>
                    </tr>

                    </tbody>
                </table>
            </div>

            <div   v-for=" table  of table_list" >
                <h5>表格名称： <b><span  v-text="table.name" ></span></b></h5>

                <table  class="table" >
                <thead class="thead-dark">
                <tr>
                    <th scope="col"  v-for="head of table.heads">
                        <span v-text="head"></span>
                    </th>
                </tr>
                </thead>

                    <tr v-for="row of table.rowList">
                        <td v-for="cell of row">
                            <small v-text="cell.text"></small>
                            <!-- <small  v-text="cell.confidence" ></small> -->
                        </td>
                    </tr>
                </table>
            </div>




        </div>

        <!-- Modal  start -->
        <div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="myModalLabel">提示信息</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="modal-body" id="myModalContent">

                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal">关闭</button>
<!--                        <button type="button" class="btn btn-primary">Save changes</button>-->
                    </div>
                </div>
            </div>
        </div>
        <!-- Modal  end -->
        <!-- Right End -->
    </div>


</div>

</body>
<script src="/static/util.js"></script>
<script src="/static/id/ui.js"></script>
<script src="/static/id/index.js"></script>
<script src="/static/id/utils.js"></script>
<script type="text/javascript">
$(document).keyup(function(event){
	  if(event.keyCode ==13){
		  onlyAnalysis('${type}');
	  }
	});
$("body").on('paste', function (event) {
    //详细可查看clipboardData属性的使用方式
    var data = (event.clipboardData || event.originalEvent.clipboardData);
    var items = data.items;
    for(var index in items){
        var item = items[index];
        //该功能仅在endpoint方式下使用
        if (item.kind == 'file' && "${uploadType}" == "endpoint") {
        	imageFile = item.getAsFile();
        	console.log(imageFile);
        	$("#showImg").attr("src",window.URL.createObjectURL(imageFile));
        	predict("${type}",false,imageFile)
            break;
        }
    }
});


images = ["png","jpg","jpeg","bmp","gif"];
function showImg(){
	var file = document.getElementById('upload').files[0];
	var extName = file.name.toLowerCase();
	var isImage = false;
	for(var i=0;i<images.length;i++){
		if(extName.endsWith(images[i])){
			$("#showImg").attr("src",window.URL.createObjectURL(file));
			isImage = true;
			break;
		}
	}
	if(!isImage){
		$("#showImg").attr("src","");
	}
}

var globalData;
function loadLocalJson(){
	var reader = new FileReader();
    reader.onload = function() 
    {
    	globalData = JSON.parse(this.result);
    	analysis("${type}",globalData);
    }
    var f = document.getElementById("upload").files[0];
    reader.readAsText(f);
}
</script>

</html>