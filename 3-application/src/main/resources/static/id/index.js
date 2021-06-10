var page_width=900;  //默认页面宽度
var page_height=1200;  //默认页面高度 会根据元素内容自动跳转
var canvas_width = page_width
var canvas_height = page_height

/**
解析ajax 返回的数据
**/
function parse_data(data){
    pageCount = parseInt(data['DocumentMetadata']['Pages'])
    vue.data = data   //保存数据

    if(pageCount<=0){
        show_message("该文档 没有内容")
        return;
    }

    var margin_document_top = 0.0       //累计文档开始高度
    var blockItemList =  new Array()    //保存所有元素的列表
    var document_page_height = 0.0      //文档的累计总高度

    //按照页数解析所有页面元素， 并且把它们拼接到一起
    for (count=0 ; count< pageCount; count++){
        result = parse_data_by_page(count+1 , margin_document_top)

        if(result == null){
            continue
        }

        _blockItemList = result['blockItemList']

        page_margin = result['page_margin']
        margin_document_top += page_margin['bottom'] - page_margin['top']
        document_page_height += page_margin['bottom'] - page_margin['top']

        console.log('Page : %d \t Item count: %d \t margin_document_top %f \t document_page_height %f',
         (count + 1),  _blockItemList.length, margin_document_top, document_page_height)


        blockItemList.push.apply(blockItemList,_blockItemList)
        console.log('Total: Item count: %d ', blockItemList.length)

    }


    var document_zoom_out_height = page_height * document_page_height
    canvas_width = page_width;
    canvas_height = document_zoom_out_height;
    reset_canvas(page_width , document_zoom_out_height)

    console.log('Canvas size=[%f , %f]  document height %f ', page_width, document_zoom_out_height,  document_page_height)

    vue.blockItemList = blockItemList



    console.log("Block item count:   ", vue.blockItemList.length)

     for(i =0 ; i<blockItemList.length; i++){
            var _blockItem = blockItemList[i]
            zoom_layout_block(_blockItem, document_zoom_out_height)
     }
    // 绘制元素

    vue.blockItemList.sort(sort_block_by_left_top)

    redraw_canvas()


    // 提取页面里面的元素
//    item_list = init_config_map()   // 初始化配置文件 ， TODO: 修改成从数据库读取
//    vue.result_list = extract_value(vue.blockItemList, item_list)
}
/**
解析单页的数据
**/
function parse_data_by_page(page, margin_document_top){
    var data = vue.data
    var blockList = new Array()
    var index = 0

    // 将所有'行'的元素取出来
    for (i =0 ; i< data['Blocks'].length ; i++){
        if(data['Blocks'][i]['Page'] == page  &&
            (data['Blocks'][i]['BlockType']=='LINE' || data['Blocks'][i]['BlockType']=='WORD' )){
            blockList[index] = data['Blocks'][i]
            index++
        }
    }

    if(blockList.length == 0){

        return null
    }

    // 取出最长的元素， 找到旋转角度， 让它保持水平。
     var max_width_block = find_max_width_block(blockList)
     pointA = max_width_block['Geometry']['Polygon'][0]
     pointB = max_width_block['Geometry']['Polygon'][1]

    tan = (pointB['Y'] - pointA['Y'])/((pointB['X'] - pointA['X']))
    var theta = Math.atan(tan)
    console.log("PageCount=%d,   tan = %f,  theta =   %f   ", page , tan, theta)

    //反方向旋转Theta
    matrix = [Math.cos(theta), Math.sin(theta), -1 * Math.sin(theta), Math.cos(theta)]


    var block_item_list = new Array()

    //计算旋转后坐标
    var blockCount = blockList.length
    for(i =0 ; i<blockCount; i++){
       blockItem = create_block(blockList[i])
       block_item_list.push(blockItem)
    }

    var page_margin = init_page_margin_block(block_item_list)

    // 添加Margin Top， 把所有页面合并到一页
    for(i =0 ; i<block_item_list.length ; i++){
        var blockItem = block_item_list[i]
        re_arrange_position_block(blockItem, page_margin, margin_document_top)
    }

    return {'blockItemList': block_item_list, 'page_margin':page_margin }

}

/**
计算所有元素经过旋转以后的新坐标
*/
function create_block(block){

    var polyList = block['Geometry']['Polygon']
    var polyArray = new Array()

    //对坐标按照原点进行旋转
    for(j=0; j<polyList.length; j++){
        //围绕中心点旋转
        ploy = {}
        ploy['x'] = polyList[j]['X'] - 0.5
        ploy['y'] = polyList[j]['Y'] - 0.5
        newPloy = matrix_rotate(matrix, ploy)
        newPloy['x'] =   newPloy['x']+ 0.5
        newPloy['y'] =   newPloy['y']+ 0.5
        polyArray.push(newPloy)
    }


//    封装block 元素， 供页面显示
    var blockItem = {
        id:block['Id'],
        confidence: block['Confidence'],
        raw_block_type: block['BlockType'],   // LINE or WORD
        newPoly:polyArray,
        selected:0,  // 是否选中
        blockType:0, //0 默认;  1 表头; 2 表格中的值
        text:block['Text']
        };

     // 如果是行元素， 保存它的孩子元素 WORD
     if(blockItem['raw_block_type'] == 'LINE' ){
        blockItem['child_list'] = block['Relationships'][0]['Ids']
        blockItem['child_count'] = block['Relationships'][0]['Ids'].length
        blockItem['is_split'] = false  // 是否做拆分
     }


    return blockItem
}


/**
删除空白区域以后，加上前面所有页的高度， 将所有页面合并到一起
*/
function re_arrange_position_block(blockItem , page_margin, margin_document_top){
    var page_top = page_margin['top']
    var page_left = page_margin['left']
    var polyArray  = blockItem['newPoly']
    for (var i=0; i<polyArray.length; i++){
        var poly = polyArray[i];
        poly['x'] = (poly['x'] -  page_left )*  page_margin['width_rate']
        poly['y'] = poly['y'] -  page_top +  margin_document_top
    }


}


/**
判断一个元素是否显示
*/
function is_display_block(blockItem){
    //LINE
    if (blockItem['raw_block_type'] == 'LINE'){
        return false
    }
    return true
}




/**
绘制block
*/
function draw_block_inside(blockItem){

    if( is_display_block(blockItem) == false ){
        return
    }

     var strokeStyle = 'blue'
     if(blockItem['blockType'] ==1){  //1 定位元素
        strokeStyle="red";
     }

    $('#myCanvas').drawRect({
      layer: true,
      strokeStyle: strokeStyle,
      strokeWidth: 1,
      x: blockItem['x'], y: blockItem['y'],
      width: blockItem['width'],
      height: blockItem['height']-5,
      cornerRadius: 1,
      autosave: true,
      click: function() {
            click_item(blockItem)
        }
    });

        var textStrokeStyle = '#36c'

        if(parseFloat(blockItem['confidence']) < parseFloat(vue.confidence_threshold) ){
            textStrokeStyle="red";
        }



   $('#myCanvas').drawText({
     layer: true,
     fillStyle: textStrokeStyle,
     fontSize: '11pt',
     text: blockItem['text'],
     autosave: true,
     x: blockItem['x'] - $('#myCanvas').measureText('myText').width / 2, y: blockItem['y'],
     align: 'left',
   });

}

/**
重新绘制所有元素
*/
function redraw_canvas(){
    $('#myCanvas').remove(); // this is my <canvas> element
    $('#myCanvasParent').append('<canvas id="myCanvas" height="'+canvas_height+'" width="'+canvas_width+'" style="border:1px solid #000000;"></canvas>');
//    console.log("---------------------------redraw_canvas  canvas_height ", canvas_height)
    for(i =0 ; i<vue.blockItemList.length; i++){
        draw_block_inside(vue.blockItemList[i] )
    }

    initClickEvent()

}

function click_item(blockItem){
    print_block_item("click_item", blockItem)
}
