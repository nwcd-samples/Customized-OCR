/**
显示错误消息
*/

function init_config_map(){

   item_list = new Array()


    item_split_name = {}
    item_split_name['target_value_name'] = '名称'              // 目标识别字段
    item_split_name['recognition_type'] = 'two-space'             // 类型 horizontal; vertical; all; table
    item_split_name['key_word_list'] = ['名','称']       // 可能出现的关键字
    item_split_name['min_length'] = 8                              // 目标识别字段 最小长度
    item_split_name['max_length'] = 20                             // 目标识别字段 最大长度
    item_split_name['max_line_count'] = 3                             // 最多行数
    item_split_name['target_value_type'] = 'digit'                  // 目标识别字段 类型   char:字符;  digit 数字;
    item_list.push(item_split_name)


    item_split_type = {}
    item_split_type['target_value_name'] = '类型'              // 目标识别字段
    item_split_type['recognition_type'] = 'two-space'             // 类型 horizontal; vertical; all; table
    item_split_type['key_word_list'] = ['类','型']       // 可能出现的关键字
    item_split_type['min_length'] = 8                              // 目标识别字段 最小长度
    item_split_type['max_length'] = 20                             // 目标识别字段 最大长度
    item_split_type['max_line_count'] = 3                             // 最多行数
    item_split_type['target_value_type'] = 'digit'                  // 目标识别字段 类型   char:字符;  digit 数字;
    item_list.push(item_split_type)


    item_split_address = {}
    item_split_address['target_value_name'] = '住所'              // 目标识别字段
    item_split_address['recognition_type'] = 'two-space'             // 类型 horizontal; vertical; all; table
    item_split_address['key_word_list'] = ['住','所']       // 可能出现的关键字
    item_split_address['min_length'] = 8                              // 目标识别字段 最小长度
    item_split_address['max_length'] = 20                             // 目标识别字段 最大长度
    item_split_address['max_line_count'] = 3                             // 最多行数
    item_split_address['target_value_type'] = 'digit'                  // 目标识别字段 类型   char:字符;  digit 数字;
    item_list.push(item_split_address)



    item_address = {}
    item_address['target_value_name'] = '统一社会信用代码'              // 目标识别字段
    item_address['recognition_type'] = 'horizontal'             // 类型 horizontal; vertical; all; table
    item_address['key_word_list'] = ['统一社会信用代码']       // 可能出现的关键字
    item_address['min_length'] = 8                              // 目标识别字段 最小长度
    item_address['max_length'] = 20                             // 目标识别字段 最大长度
    item_address['max_line_count'] = 3                             // 最多行数
    item_address['target_value_type'] = 'digit'                  // 目标识别字段 类型   char:字符;  digit 数字;
    item_list.push(item_address)




    item_insurance_name = {}
    item_insurance_name['target_value_name'] = '法定代表人'              // 目标识别字段
    item_insurance_name['recognition_type'] = 'horizontal'             // 类型 horizontal; vertical; all; table
    item_insurance_name['key_word_list'] = ['法定代表人']       // 可能出现的关键字
    item_insurance_name['min_length'] = 8                              // 目标识别字段 最小长度
    item_insurance_name['max_length'] = 20                             // 目标识别字段 最大长度
    item_insurance_name['target_value_type'] = 'char'                  // 目标识别字段 类型   char:字符;  digit 数字;
    item_list.push(item_insurance_name)


    item_contract_number = {}
    item_contract_number['target_value_name'] = '注册资本'              // 目标识别字段
    item_contract_number['recognition_type'] = 'horizontal'             // 类型 horizontal; vertical; all; table
    item_contract_number['key_word_list'] = ['注册资本', '注册录本']       // 可能出现的关键字
    item_contract_number['min_length'] = 8                              // 目标识别字段 最小长度
    item_contract_number['max_length'] = 20                             // 目标识别字段 最大长度
    item_contract_number['target_value_type'] = 'digit'                  // 目标识别字段 类型   char:字符;  digit 数字;
    item_list.push(item_contract_number)



    item_race = {}
    item_race['target_value_name'] = '成立日期'              // 目标识别字段
    item_race['recognition_type'] = 'horizontal'             // 类型 horizontal; vertical; all; table
    item_race['key_word_list'] = ['成立日期']       // 可能出现的关键字
    item_race['min_length'] = 8                              // 目标识别字段 最小长度
    item_race['max_length'] = 20                             // 目标识别字段 最大长度
    item_race['target_value_type'] = 'digit'                  // 目标识别字段 类型   char:字符;  digit 数字;
    item_list.push(item_race)



    item_birth = {}
    item_birth['target_value_name'] = '营业期限'              // 目标识别字段
    item_birth['recognition_type'] = 'horizontal-all'             // 类型 horizontal; vertical; all; table
    item_birth['key_word_list'] = ['营业期限']       // 可能出现的关键字
    item_birth['min_length'] = 8                              // 目标识别字段 最小长度
    item_birth['max_length'] = 20                             // 目标识别字段 最大长度
    item_birth['target_value_type'] = 'digit'                  // 目标识别字段 类型   char:字符;  digit 数字;
    item_list.push(item_birth)




    item_id = {}
    item_id['target_value_name'] = '经营范围'              // 目标识别字段
    item_id['recognition_type'] = 'horizontal-multi-line'             // 类型 horizontal; vertical; all; table
    item_id['key_word_list'] = ['经营范围']       // 可能出现的关键字
    item_id['min_length'] = 8                              // 目标识别字段 最小长度
    item_id['max_length'] = 20                             // 目标识别字段 最大长度
    item_id['target_value_type'] = 'digit'                  // 目标识别字段 类型   char:字符;  digit 数字;
    item_list.push(item_id)



//   console.log(JSON.stringify(item_list))

   return item_list
}