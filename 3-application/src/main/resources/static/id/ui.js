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
                fixed_position_list:[],
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
    myCanvas.on('click', function(e) {
        var p = getEventPosition(e);


    var xRadio = (parseFloat(p.x) / canvas_width).toFixed(3);
    var yRadio = (parseFloat(p.y) / canvas_height).toFixed(3);

        $("#click_point_coord_span").html("点：[x="+xRadio+", y="+yRadio +" ]")
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
    parse_data(fullData[0]);
    console.log("\n")
    console.log(JSON.stringify(fullData[0]))
    console.log("\n")

	console.info(simpleData[0]);
	console.log("      ",  JSON.stringify(simpleData[0]))
	vue.result_list = simpleData[0]['keyValueList']
	vue.fixed_position_list = simpleData[0]['fixedPositionList']
    vue.table_list = simpleData[0]['tableList']


}