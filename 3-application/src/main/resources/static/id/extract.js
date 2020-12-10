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

        }else if(item['recognition_type'] == 'horizontal-all'){
            result = doHorizontalAll(item, blockItemList)
            if (result == null){
                continue
            }
            result_item = result[0]
            result_list.push(result_item)

        }else if(item['recognition_type'] == 'horizontal-multi-line'){


            result = doHorizontalMultiLine(item, blockItemList)
            if (result == null){
                continue
            }
            result_item = result[0]
            result_list.push(result_item)

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
//        console.log('\t---关键字 ',i, '  ',  key_word)


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
            console.log('\t-----------------' , key_word, blockItem['text'])
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



    index =  blockItemList[j]['text'].indexOf(key_word)
    console.log("current_text==================== ", current_text, 'index : ', index, 'key_word ', key_word, item)
    console.log("start: ", index + result_item['target_name'].length,  'end: ', index + result_item['target_name'].length+ item['max_length'])
    value = current_text.substr(index + result_item['target_name'].length, item['max_length'] )
    value = value.trim()
    console.log("=======+++++++++++++++++++[%s]", value )
    if(value.length>=1){
        result_item['value'] = value
        return [result_item, blockItem]
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


function find_target_block(item, blockItemList){

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
                    console.log('\t-----------------' , key_word, blockItem['text'])
                    break
                }
         }
         return blockItem


}

/**
查找一整行的元素
*/
function doHorizontalAll(item, blockItemList){
    console.log('查找目标名称： ', item['target_value_name'], item)

        var blockItem = find_target_block(item, blockItemList)
        if(blockItem ==null){
            return null
        }

        var result_list = new Array()
        for(var i=0; i<blockItemList.length; i++){
            var curItem = blockItemList[i]
            if(blockItem['text'] == curItem['text']){
                continue
            }
            console.log('\t-----------------curItem ' , curItem['text'], curItem['top'])
            if(curItem['top'] > blockItem['top']- blockItem['height']
                && curItem['bottom'] < blockItem['bottom'] + blockItem['height']){
                console.log('\t-----------------curItem ' , curItem['text'])
                result_list.push(curItem)
            }

        }

        var result_value = ''

        result_list.sort(sort_block_by_x)

        for(var i=0; i< result_list.length; i++ ){
            result_value += result_list[i]['text']
        }
        var result_item = {}
        result_item['target_name'] = item['target_value_name']
        result_item['value'] = result_value

        console.log("------------------------- 890", result_value)
        if(result_value != null && result_value != ''){
            return [result_item, blockItem]
        }

        return doHorizontal(item, blockItemList)

}


/**
查找一整行的元素
*/
function doHorizontalMultiLine(item, blockItemList){
    console.log('doHorizontalMultiLine 查找目标名称： ', item['target_value_name'])

        var blockItem = find_target_block(item, blockItemList)
        if(blockItem ==null){
            return null
        }

        var result_list = new Array()
        for(var i=0; i<blockItemList.length; i++){
            var curItem = blockItemList[i]
            if(blockItem['text'] == curItem['text']){
                continue
            }
//            console.log('\t-----------------curItem ' , curItem['text'], curItem['top'])
            if(curItem['top'] > blockItem['top']- blockItem['height']
                && curItem['bottom'] < blockItem['bottom'] + 3 * blockItem['height']){
                console.log('\t-----------------curItem ' , curItem['text'])
                result_list.push(curItem)
            }

        }

        var result_value = ''

        result_list.sort(sort_block_by_y)

        for(var i=0; i< result_list.length; i++ ){
            result_value += result_list[i]['text']
        }
        var result_item = {}
        result_item['target_name'] = item['target_value_name']
        result_item['value'] = result_value
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


        if(tempBlockItem['left'] > blockItem['x']
            && Math.abs(tempBlockItem['y'] - blockItem['y']) < 2 * blockItem['height']
            ){

            temp_vertical_distance = Math.abs(blockItem['y'] - tempBlockItem['y']) + Math.abs(blockItem['right'] - tempBlockItem['left'])

            if(temp_vertical_distance < min_vertical_distance){
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
