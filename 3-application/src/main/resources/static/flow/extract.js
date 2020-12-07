function extract_value(blockItemList, item_list){

    console.log("---- extract value start ----------------")
    result_list = new Array()       // 返回的结果列表， 供页面显示

    // 遍历所有需要识别的元素， 元素的定义 在config map 中
    for(var i= 0; i< item_list.length; i++){

        item = item_list[i]
        if( item['recognition_type'] == 'horizontal'){
            // 先优先水平方向进行查找， 找不到的情况， 会在垂直方向查找
            result = doHorizontal(item, blockItemList)   // 返回两个结果[ 供页面显示的item , 返回blockItem 便于继续查找其它元素 ]
            result_item = result[0]
            if(result_item['value'] == ''){
                continue
            }
            if(item['target_value_name'] == '投保人' || item['target_value_name'] == '被保人' ){
                result_item['sub_name'] = '姓名'
            }
            result_list.push(result_item)

            //case:  如果是 '投保人'  '被保人' 特殊处理， 查询后面的性别    证件号码等信息

            if(item['target_value_name'] == '投保人' || item['target_value_name'] == '被保人' ){
                 find_beneficiary_additional_info(item, result[1], blockItemList, result_list)
            }// end for
        }
    }

    return result_list
}


/**
水平查找， 先找在一个单元格中的元素， 然后找右边最近的元素
*/
function doHorizontal(item, blockItemList){
    console.log('查找目标名称： ', item['target_value_name'])

//
    var find_flag = false
    var blockItem = null
    var key_word =''
    for(var i=0; i<item['key_word_list'].length; i++){
        key_word = item['key_word_list'][i]
        console.log('\t---关键字 ',i, '  ',  key_word)


        for(var j=0; j<blockItemList.length; j++){
           blockItemList[j]['text'] = delete_unnecessary_char(blockItemList[j]['text'])
           index =  blockItemList[j]['text'].indexOf(key_word)
           if(index>=0){
            find_flag = true
            blockItem = blockItemList[j]
            break
           }
        }

        if(find_flag){
//            console.log('\t-----------------' , key_word, blockItem['text'])
            break
        }


    }

    result_item = {}
    result_item['value'] = ''

    if(find_flag == false){
        return [result_item, blockItem]
    }

    current_text = blockItem['text']
    result_item['target_name'] = item['target_value_name']
    result_item['sub_name'] = '-'
    var value = ''
    //case 1.  英文或者中文引号
    split_poz = current_text.indexOf(':')
    if(split_poz<0){
        split_poz = current_text.indexOf('：')
    }

    if(split_poz>0){
        value = current_text.substr(split_poz+1, current_text.length)
        value = value.trim()
//        console.log("=======+++++++++++++++++++[%s]", value )
        if(value.length>=2){
            result_item['value'] = value
            return [result_item, blockItem]
        }

    }

    //case 2. 在一个单元格里面， 中间有空格， 进行拆分

    if (current_text.length > key_word.length + 3){
        value = current_text.substr(key_word.length , current_text.length)
        result_item['value'] = value
        return [result_item, blockItem]

    }



    //case 3. 值在后面一个单元格里
    value_block = find_next_right_block_item(blockItemList, blockItem)

    if(value_block !=null ){
        value = value_block['text']
        result_item['value'] = value
        return [result_item, blockItem]
    }



    return [result_item, blockItem]

}


/**
去掉字符串中 多余的字符
*/
function delete_unnecessary_char(old_string){

    if(old_string== null || old_string.length<=0){
        return old_string
    }
    return old_string.replace(' ', '').replace(',', '').replace('"', '').replace('/', '').replace('-', '')

}

/**
找到右边一个单元格
*/
function find_next_right_block_item(blockItemList, blockItem){

    var min_vertical_distance  = 10000000
    var min_vertical_distance_block_item = null
    for(var i=0; i<blockItemList.length; i++){

        tempBlockItem = blockItemList[i]


        if(tempBlockItem['left'] > blockItem['left'] -30
            &&  tempBlockItem['left'] < blockItem['right'] + blockItem['width']/3
            && Math.abs(tempBlockItem['y'] - blockItem['y']) < 2 * blockItem['height']
            && tempBlockItem['text'] != blockItem['text']
            ){

            temp_vertical_distance = Math.abs(blockItem['y'] - tempBlockItem['y']) + Math.abs(blockItem['right'] - tempBlockItem['left'])

            if(temp_vertical_distance < min_vertical_distance){
                console.log("----------------------------*************  ", tempBlockItem['text'])
                min_vertical_distance = temp_vertical_distance
                min_vertical_distance_block_item = tempBlockItem
            }

        }
    }
    if(min_vertical_distance_block_item != null){
        console.log("min_vertical_distance_block_item    ", min_vertical_distance_block_item['text'])
    }
    return min_vertical_distance_block_item

}

/*
如果是 '投保人'  '被保人' 特殊处理， 查询后面的性别    证件号码等信息
*/
function find_beneficiary_additional_info(item, blockItem, blockItemList, result_list){

//        console.log("---**find_beneficiary_additional_info---------- ", blockItem['text'])
        result_item = {}
        result_item['target_name'] = item['target_value_name']
        result_item['value'] = ''
        result_item['sub_name'] = '-'


        //case 1. 性别
        var result_sex = find_horizontal_next_block_item_by_keys(['男', '女'], blockItem, blockItemList)
        if(result_sex !=null){
            result_item['sub_name'] = '性别'
            if(result_sex.indexOf('男')>=0){
                result_item['value'] = '男'
            }else {
                result_item['value'] = '女'
            }
        }


        //case 2. 其他字段

        for(var i=0; i<item['sub_item_list'].length; i++){
            sub_item = item['sub_item_list'][i]
            for(var j=0; j<sub_item['key_word_list'].length; j++){
                key_word = sub_item['key_word_list']
                result_text = find_horizontal_next_block_item_by_keys(key_word, blockItem, blockItemList)
                result_text = delete_unnecessary_char(result_text)
                if(result_text !=null){
                    temp_item = {}
                    temp_item['target_name'] = item['target_value_name']
                    temp_item['value'] = result_text
                    temp_item['sub_name'] = sub_item['target_value_name']
                    result_list.push(temp_item)
                    break
                }
            }

        }
        if (result_item['value'] != ''){
            result_list.push(result_item)
        }


        return result_item

}

/**
根据关键字， 找到水平位置下一个元素
*/
function find_horizontal_next_block_item_by_keys(key_list, blockItem, blockItemList){
    var min_distance = 100000
    var min_vertical_distance_block_item = null
    var key_word = ''
    var find_key_word = ''
    var find_block_item = null
    for(var i=0; i<blockItemList.length; i++){

        var tempBlockItem = blockItemList[i]

        for(j=0; j< key_list.length; j++ ){
            key_word = key_list[j]
            if( tempBlockItem['text'].indexOf(key_word)>=0
             && tempBlockItem['left'] > blockItem['right']
             && Math.abs(tempBlockItem['y'] - blockItem['y']) < blockItem['height'] +10     ){

//                console.log("----------------  ",key_word,  tempBlockItem['text'])
                distance = Math.abs(tempBlockItem['y'] - blockItem['y'])
                if(distance < min_distance){
                    min_distance = distance
                    min_vertical_distance_block_item = tempBlockItem
                    find_key_word = key_word

                }
             }
        }
    }

    if (min_vertical_distance_block_item == null){
        return null
    }


    result_text = min_vertical_distance_block_item['text']
//    console.log("#############   ", result_text.length, result_text,   find_key_word.length   , find_key_word)
    if(result_text.length > find_key_word.length +2   ){
        if(result_text.indexOf(":")>0){
            result_text= result_text.substr(result_text.indexOf(":")+1, result_text.length)
        }else {
            result_text= result_text.substr(result_text.indexOf(find_key_word), result_text.length)
        }
    }else if(find_key_word.length<2){

        return result_text
    }else {

        next_item = find_next_right_block_item(blockItemList, min_vertical_distance_block_item)
        if(next_item != null){
//            console.log("*********    ",  min_vertical_distance_block_item['text'], next_item['text'])
            return next_item['text']
        }

        return null
    }

    return result_text

}