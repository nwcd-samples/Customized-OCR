function extract_table(blockItemList, item_list){

    // 遍历所有需要识别的元素， 元素的定义 在config map 中
        for(var i= 0; i< item_list.length; i++){
            item = item_list[i]
            if( item['recognition_type'] != 'table'){
                continue
            }
//            console.log("===== ", item)
            var find_flag = false
            for (var a=0; a<item['key_word_1_list'].length; a++){
                for(var b=0; b<item['key_word_2_list'].length; b++){
                   result_item_list =  find_table_by_two_keys(item['key_word_1_list'][a], item['key_word_2_list'][b], blockItemList)
                   if(result_item_list != null){
                        return result_item_list
                   }
                }
            }
        }
        return new Array()
}



function find_table_by_two_keys(key1, key2, blockItemList){
    console.log('根据两个关键字进行表格的定位  ',  "key1: ", key1, "key2: ", key2)

    key1_block_item = null;
    key2_block_item = null;

    for(var i=0; i<blockItemList.length; i++){
        var tempBlockItem = blockItemList[i]
        if(tempBlockItem['text'].trim() == key1){
            key1_block_item = tempBlockItem
        }
        if(delete_unnecessary_char(tempBlockItem['text'].trim()) == key2){
            key2_block_item = tempBlockItem
        }
    }

    if(key1_block_item == null || key2_block_item == null ){
        return null
    }
//    console.log("找到了进行表格定位的关键字:  1. 【%s】   2. 【%s】 " , key1_block_item['text'], key2_block_item['text'])


    if(Math.abs(key1_block_item['y'] - key2_block_item['y']) >  key1_block_item['height']){
        console.log("找到了进行表格定位的关键字:  1. 【%s】   2. 【%s】 , 两个关键字的高度差太多， 不是表格元素" , key1_block_item['text'], key2_block_item['text'])
        return null
    }

    console.log("找到了进行表格定位的关键字:  1. [x=%d, y=%d]   2. [x=%d, y=%d] " ,
       key1_block_item['x'], key1_block_item['y'], key2_block_item['x'], key2_block_item['y'])

    column_block_item_list_1 = find_column_block_item(key1_block_item, blockItemList, false)
    column_block_item_list_2 = find_column_block_item(key2_block_item, blockItemList, true)

    return merge_column_list(column_block_item_list_1, column_block_item_list_2)

}

/*根据表头元素， 找到下面一列的元素列表*/
/**
@param hasDigit 表示单元格内是否有数字
*/
function find_column_block_item(blockItem, blockItemList, hasDigit){

    var column_block_item_list = new Array()
    for(var i=0; i<blockItemList.length; i++){
        var tempBlockItem = blockItemList[i]
        if(tempBlockItem['y']> blockItem['y'] &&
           tempBlockItem['right'] > blockItem['left'] &&
           tempBlockItem['left'] < blockItem['right']){

            console.log("find ",  tempBlockItem['text'], 'y: ', tempBlockItem['y'],
            containsNumber(tempBlockItem['text']) , calculateNumberCount(tempBlockItem['text']))

            if(!hasDigit){
                column_block_item_list.push(tempBlockItem)
            }else if(calculateNumberCount(tempBlockItem['text']) *2 > tempBlockItem['text'].length ) {
                column_block_item_list.push(tempBlockItem)
            }else {
                console.log("未找到含有字符串的单元格")
            }

        }
    }
    return column_block_item_list

}

function containsNumber(str) {
    return !!str.match(/\d/g);
}

function calculateNumberCount(str) {
    if(str ==null || str =='' || str.length ==0 ){
        return 0
    }
    var count = 0
    for(j=0; j<str.length; j++){
        for(var i=0;i<10;i++){
            if(str[j] == ''+i){
                count +=1
            }
        }
    }
    return count;
}

function merge_column_list(column_list_1, column_list_2){


    console.log(column_block_item_list_1)
    console.log(column_block_item_list_2)


    if(column_block_item_list_1.length == 0 || column_block_item_list_2.length ==0 ){
        console.log("未找到匹配的记录")
        return null
    }

    var result_item_list = new Array()

    var added_item_list = new Array()  // 已经添加到列表里面的blockItem

    for(var i=0; i< column_block_item_list_2.length; i++ ){
        item_2 = column_block_item_list_2[i]
        var min_distance = 1000000
        var min_distance_item = null
        for(var j=0; j<column_block_item_list_1.length; j++){
            item_1 = column_block_item_list_1[j]

            // 如果已经添加的， 就跳过
            var find_added_flag = false
            for(var k=0; k< added_item_list.length; k++ ){
                if(item_1['text'] == added_item_list[k]['text']){
                    find_added_flag = true
                }
            }
            if(find_added_flag){
                continue
            }

            //找到垂直方向最近的一个， 并且y值相差不超过高度
            distance = Math.abs(item_2['y'] - item_1['y'])
            if( distance < min_distance  && Math.abs(item_2['y'] -item_1['y']) < item_2['height'] ){
                min_distance = distance
                min_distance_item = item_1
            }
        }
        if(min_distance_item != null){
            console.log("[%s] ===== [%s]", min_distance_item['text'], item_2['text'])
            added_item_list.push(min_distance_item)
//            console.log(added_item_list)
            result_item = {}
            result_item['name'] = min_distance_item['text']
            result_item['value'] = item_2['text']
            result_item_list.push(result_item)
        }

    }//end for

    return result_item_list

}