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


/**

Vue  对象的结构
--tableBlockList[]                  //一共发现多少个相同的表格模板
  --tableBlock{}
    --id                            // 【用户输入】【需要保存的内容】
    --table_name                    // 表格名称 【用户输入】【需要保存的内容】
    --main_col_num                  //主列序号   【用户输入】【需要保存的内容】
    --table_type                    // 【用户输入】【需要保存的内容】 0: 横向表格   1: 纵向表格
    --thItems[]                     //【用户输入】 模板的定位元素， 用户点击选择的Block  首位两端的就可以， 最少两个
                                    // [ {left, right, top, bottom}, {left, right, top, bottom}]
    --th_count                      //【用户输入】 【需要保存的内容】   实际表格列数， 用户自己填入， 用户生成分割线
    --row_max_height                //【用户输入】 【需要保存的内容】   用户输入的行最大的可能高度， 辅助进行识别
    --th_x_poz_list                 //【用户输入】【需要保存的内容】  用户拖动后的分割线坐标
    --save_location_items           // 【需要保存的内容】

    --status                        // 当前状态 0:新创建  1: 生成了分割线  2:生成了这个表格匹配的模板
    --col_poz_list                  // 用来分割表头元素横线的 X 坐标 集合
    --row_poz_list                  // 用来分割行元素横线的 Y 坐标 集合
*/


function get_data(data){
    vue.tableBlockList = new Array()
    vue.currentTableBlock = {}
    parse_data(data);
}
