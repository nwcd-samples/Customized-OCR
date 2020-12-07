/**
根据ID找到元素
*/
function find_block_by_id(block_id){

   for(var i=0; i< vue.blockItemList.length; i++  ){
        if (block_id ==vue.blockItemList[i]['id'] ){
            return vue.blockItemList[i]
        }
   }
}


/**
根据 坐标  找到word 元素, 并且合并文本内容,
并且将这些元素拆分
*/
function merge_td_text_by_box_poz(table_id, box){

   var td_text = ''
   for(var blockItem of vue.blockItemList){
        if (blockItem['raw_block_type'] =='LINE'){
            continue
        }
        if ( blockItem['x'] > box['left'] && blockItem['x']<= box['right']
           &&  blockItem['y']> box['top'] && blockItem['y'] <= box['bottom']){
            td_text +=  ' '+ blockItem['text']

        }
   }
   return td_text
}


/**
重新调整canvas 的大小
*/
function reset_canvas(width, height){
    var canvas=document.getElementById("myCanvas");
    canvas.width=width
    canvas.height=height

}


/**
找到最宽的元素， 用它来进行页面的旋转
*/
function find_max_width_block(blockList){
        var max_width_block = null
        max_width = 0.0
        for(i =0 ; i< blockList.length; i++){
               width =  blockList[i]['Geometry']['BoundingBox']['Width']
               if(width> max_width){
                max_width = width
                max_width_block = blockList[i]
               }
        }
        return max_width_block;
}


/**
找到每一页空白的地方， 去除掉， 防止有偏移
**/
function init_page_margin_block(blockItemList){
        var min_top_block = null
        var page_top = 1
        var page_bottom = 0.0
        var page_left = 1
        var page_right = 0.0

        for(i =0 ; i< blockItemList.length; i++){
               var top =  blockItemList[i]['newPoly'][0]['y']
               if(top<page_top){
                    page_top = top
               }
               var left =  blockItemList[i]['newPoly'][0]['x']
               if(left<page_left){
                  page_left = left
               }

               var bottom =  blockItemList[i]['newPoly'][2]['y']
               if(bottom > page_bottom){
                   page_bottom = bottom
               }

               var right =  blockItemList[i]['newPoly'][2]['x']
               if(right > page_right){
                   page_right = right
               }

        }


        var page_margin ={'top':0, 'bottom':1, 'left':0, 'right':'1'}
        page_margin['top'] = page_top;
        page_margin['bottom'] = page_bottom;
        page_margin['left'] = page_left;
        page_margin['right'] = page_right;
        page_margin['height'] = page_bottom - page_top;


        page_margin['height_rate'] = 1.0/(page_bottom - page_top);
        page_margin['width_rate'] =  1.0/(page_right - page_left)  ;

//        console.log("page_margin",  JSON.stringify(page_margin))
        return page_margin;

}

/**
把现有元素等比例放大， 占满空间
*/
function zoom_layout_block(blockItem, document_zoom_out_height){

    var polyArray  = blockItem['newPoly']
    for (var i=0; i<polyArray.length; i++){
        var poly = polyArray[i];
        poly['x'] = parseInt(poly['x']  * page_width)
        poly['y'] = parseInt(poly['y']  * page_height)
    }
    blockItem['width'] = parseInt(polyArray[1]['x'] - polyArray[0]['x'])
    blockItem['height'] = parseInt(polyArray[3]['y'] - polyArray[0]['y'])
    blockItem['left'] = polyArray[0]['x']
    blockItem['top'] = polyArray[0]['y']
    blockItem['right'] = polyArray[1]['x']
    blockItem['bottom'] = polyArray[2]['y']
    blockItem['x'] = parseInt((polyArray[2]['x'] + polyArray[0]['x']) / 2.0)
    blockItem['y'] = parseInt((polyArray[2]['y'] + polyArray[0]['y']) / 2.0)
}
/**
显示错误消息
*/
function show_message(message){
    $("#myModalContent").html(message)
    $('#myModal').modal('show')
}

