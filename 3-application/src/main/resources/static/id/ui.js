var vue ;
var table_item_index = 0
$(function(){

    vue = new Vue({
            el: '#main',
            data:{
                blockItemList:[], //当前页面解析的block元素
                tableBlockList:[],
                result_list:[],      //返回的key-value结果
                table_list:[],
                result_table_list:[],  //返回的列表结果， 保险名称
                currentTableBlock:{},
                data_url:"",
                confidence_threshold: 0.8,
                data:{}

             },methods:{
                get_json:function(){   // 获取文件 文件保存在S3中
                    url = $("#json_url_input").val()
                    //alert(url)
                    get_data()
                },
                change_confidence_threshold:function(){
                    redraw_canvas()
                },

             }
    })

//    initClickEvent()
});


function initClickEvent(){
    var myCanvas =  $('#myCanvas')
    //初始化点击事件
    myCanvas.on('click', function(e) {
        var p = getEventPosition(e);


    var x = (parseFloat(p.x) / canvas_width).toFixed(3);
    var y = (parseFloat(p.y) / canvas_height).toFixed(3);
    $("#click_point_coord_span").html("点：[x="+x+", y="+y +" ]")

    //判断所有元素， 如果不在所有元素的范围内， 就清空log区域。

    var blockItemList = vue.blockItemList

    var findFlag = false;
    for(i =0 ; i<blockItemList.length; i++){
        var _blockItem = blockItemList[i]
        var top = (parseFloat(_blockItem['top']) / canvas_height).toFixed(3);
        var bottom = (parseFloat(_blockItem['bottom']) / canvas_height).toFixed(3);
        var left = (parseFloat(_blockItem['left']) / canvas_width).toFixed(3);
        var right = (parseFloat(_blockItem['right']) / canvas_width).toFixed(3);

        if(x >=left && x <=right && y>=top && y<=bottom){
            findFlag = true;
            break;
        }

    }

    if(!findFlag){
        $("#click_block_coord_span").html("")
    }


    });
}

function getEventPosition(ev){
  var x, y;
  if (ev.layerX || ev.layerX == 0) {
    x = ev.layerX;
    y = ev.layerY;
  } else if (ev.offsetX || ev.offsetX == 0) { // Opera
    x = ev.offsetX;
    y = ev.offsetY;
  }
  return {x: x, y: y};
}

function get_data(data){
//    vue.tableBlockList = new Array()
//    vue.currentTableBlock = {}
//    parse_data(data);
}


function displayResult(fullData,simpleData){


	vue.tableBlockList = new Array()
    vue.currentTableBlock = {}
    parse_data(fullData);
    console.log("\n")
    console.log(JSON.stringify(fullData))
    console.log("\n")

	console.info(simpleData);
	console.log("      ",  JSON.stringify(simpleData))
	vue.result_list = simpleData['keyValueList']
    vue.table_list = simpleData['tableList']


}