var vue ;
var table_item_index = 0
$(function(){

    vue = new Vue({
            el: '#main',
            data:{
                blockItemList:[], //当前页面解析的block元素
                tableBlockList:[],
                result_list:[],      //返回的结果
                result_table_list:[],  //返回的列表结果， 保险名称
                currentTableBlock:{},
//                data_url:"https://dikers-html.s3.cn-northwest-1.amazonaws.com.cn/ocr_output/t001.json",
                data_url:"https://dikers-html.s3.cn-northwest-1.amazonaws.com.cn/ocr_output/id.json",
                data:{}

             },methods:{
                get_json:function(){   // 获取文件 文件保存在S3中
                    url = $("#json_url_input").val()
                    //alert(url)
                    get_data()
                }
             }
    })
});


function get_data(data){
//    vue.tableBlockList = new Array()
//    vue.currentTableBlock = {}
//    parse_data(data);
}


function displayResult(fullData,simpleData){


    console.log("------------***------------  ")
	vue.tableBlockList = new Array()
    vue.currentTableBlock = {}
    parse_data(fullData[0]);
    console.log("\n")
    console.log(JSON.stringify(fullData[0]))
    console.log("\n")

	console.info(simpleData[0]);
	vue.result_list = simpleData[0]
}