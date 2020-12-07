/**
显示错误消息
*/

function init_config_map(){

   item_list = new Array()

   item_list.push(initApplicant()) // 投险人
   item_list.push(initBeneficiary()) // 被保人

   item_list.push(init_insurance_table()) //保险项目列表



    item_insurance_name = {}
    item_insurance_name['target_value_name'] = '保险名称'              // 目标识别字段
    item_insurance_name['recognition_type'] = 'vertical'             // 类型 horizontal; vertical; all; table
    item_insurance_name['key_word_list'] = ['保险名称', '险种名称']       // 可能出现的关键字
    item_insurance_name['min_length'] = 8                              // 目标识别字段 最小长度
    item_insurance_name['max_length'] = 20                             // 目标识别字段 最大长度
    item_insurance_name['target_value_type'] = 'digit'                  // 目标识别字段 类型   char:字符;  digit 数字;
    item_list.push(item_insurance_name)


    item_contract_number = {}
    item_contract_number['target_value_name'] = '合同号'              // 目标识别字段
    item_contract_number['recognition_type'] = 'horizontal'             // 类型 horizontal; vertical; all; table
    item_contract_number['key_word_list'] = ['保险合同号', '合同号', '保险单号', '保险单号码', '保险合同编号']       // 可能出现的关键字
    item_contract_number['min_length'] = 8                              // 目标识别字段 最小长度
    item_contract_number['max_length'] = 20                             // 目标识别字段 最大长度
    item_contract_number['target_value_type'] = 'digit'                  // 目标识别字段 类型   char:字符;  digit 数字;
    item_list.push(item_contract_number)


    item_contract_valid_date = {}
    item_contract_valid_date['target_value_name'] = '合同生效日'              // 目标识别字段
    item_contract_valid_date['recognition_type'] = 'horizontal'             // 类型 horizontal; vertical; all; table
    item_contract_valid_date['key_word_list'] = ['保险合同生效日', '合同生效日']       // 可能出现的关键字
    item_contract_valid_date['min_length'] = 8                              // 目标识别字段 最小长度
    item_contract_valid_date['max_length'] = 20                             // 目标识别字段 最大长度
    item_contract_valid_date['target_value_type'] = 'char'                  // 目标识别字段 类型   char:字符;  digit 数字;
    item_list.push(item_contract_valid_date)



    item_contract_create_date = {}
    item_contract_create_date['target_value_name'] = '合同成立日'              // 目标识别字段
    item_contract_create_date['recognition_type'] = 'horizontal'             // 类型 horizontal; vertical; all; table
    item_contract_create_date['key_word_list'] = ['合同成立日']       // 可能出现的关键字
    item_contract_create_date['min_length'] = 8                              // 目标识别字段 最小长度
    item_contract_create_date['max_length'] = 20                             // 目标识别字段 最大长度
    item_contract_create_date['target_value_type'] = 'char'                  // 目标识别字段 类型   char:字符;  digit 数字;
    item_list.push(item_contract_create_date)


//   console.log(JSON.stringify(item_list))

   return item_list
}



function initApplicant(){
   item = {}
   item['target_value_name'] = '投保人'              // 目标识别字段
   item['recognition_type'] = 'horizontal'             // 类型 horizontal; vertical; all; table
   item['key_word_list'] = ['投保人姓名', '投保人']       // 可能出现的关键字
   item['min_length'] = 2                              // 目标识别字段 最小长度
   item['max_length'] = 5                              // 目标识别字段 最大长度
   item['target_value_type'] = 'char'                  // 目标识别字段 类型   char:字符;  digit 数字;



        sub_item_list = new Array()


        sub_item_birth = {}
        sub_item_birth['target_value_name'] = '出生日期'
        sub_item_birth['key_word_list'] = ['出生日期',  '生日']               // 可能出现的关键字
        sub_item_birth['default_value_list'] = []
        sub_item_birth['min_length'] = 10                              // 目标识别字段 最小长度
        sub_item_birth['max_length'] = 30                             // 目标识别字段 最大长度
        sub_item_birth['target_value_type'] = 'char'                  // 目标识别字段 类型   char:字符;  digit 数字;

        sub_item_list.push(sub_item_birth)


        sub_item_ID = {}
        sub_item_ID['target_value_name'] = '证件号码'
        sub_item_ID['key_word_list'] = ['证件号码']               // 可能出现的关键字
        sub_item_ID['default_value_list'] = []
        sub_item_ID['min_length'] = 1                              // 目标识别字段 最小长度
        sub_item_ID['max_length'] = 1                              // 目标识别字段 最大长度
        sub_item_ID['target_value_type'] = 'char'                  // 目标识别字段 类型   char:字符;  digit 数字;

        sub_item_list.push(sub_item_ID)


        sub_item_ID_type = {}
        sub_item_ID_type['target_value_name'] = '证件类型'
        sub_item_ID_type['key_word_list'] = ['证件类型']               // 可能出现的关键字
        sub_item_ID_type['default_value_list'] = []
        sub_item_ID_type['min_length'] = 2                              // 目标识别字段 最小长度
        sub_item_ID_type['max_length'] = 5                              // 目标识别字段 最大长度
        sub_item_ID_type['target_value_type'] = 'char'                  // 目标识别字段 类型   char:字符;  digit 数字;

        sub_item_list.push(sub_item_ID_type)


        sub_item_customer_id = {}
        sub_item_customer_id['target_value_name'] = '客户号'
        sub_item_customer_id['key_word_list'] = ['客户号']               // 可能出现的关键字
        sub_item_customer_id['default_value_list'] = []
        sub_item_customer_id['min_length'] = 2                              // 目标识别字段 最小长度
        sub_item_customer_id['max_length'] = 5                              // 目标识别字段 最大长度
        sub_item_customer_id['target_value_type'] = 'char'                  // 目标识别字段 类型   char:字符;  digit 数字;

        sub_item_list.push(sub_item_customer_id)




   item['sub_item_list'] = sub_item_list

    return item
}


function initBeneficiary(){
   item = {}
   item['target_value_name'] = '被保人'              // 目标识别字段
   item['recognition_type'] = 'horizontal'             // 类型 horizontal; vertical; all; table
   item['key_word_list'] = ['被保人姓名', '被保人', '被保险人']       // 可能出现的关键字
   item['min_length'] = 2                              // 目标识别字段 最小长度
   item['max_length'] = 5                              // 目标识别字段 最大长度
   item['target_value_type'] = 'char'                  // 目标识别字段 类型   char:字符;  digit 数字;



    sub_item_list = new Array()


    sub_item_birth = {}
    sub_item_birth['target_value_name'] = '出生日期'
    sub_item_birth['key_word_list'] = ['出生日期', '生日']               // 可能出现的关键字
    sub_item_birth['default_value_list'] = []
    sub_item_birth['min_length'] = 10                              // 目标识别字段 最小长度
    sub_item_birth['max_length'] = 30                             // 目标识别字段 最大长度
    sub_item_birth['target_value_type'] = 'char'                  // 目标识别字段 类型   char:字符;  digit 数字;

    sub_item_list.push(sub_item_birth)


    sub_item_ID = {}
    sub_item_ID['target_value_name'] = '证件号码'
    sub_item_ID['key_word_list'] = ['证件号码']               // 可能出现的关键字
    sub_item_ID['default_value_list'] = []
    sub_item_ID['min_length'] = 1                              // 目标识别字段 最小长度
    sub_item_ID['max_length'] = 1                              // 目标识别字段 最大长度
    sub_item_ID['target_value_type'] = 'char'                  // 目标识别字段 类型   char:字符;  digit 数字;

    sub_item_list.push(sub_item_ID)

    sub_item_ID_type = {}
    sub_item_ID_type['target_value_name'] = '证件类型'
    sub_item_ID_type['key_word_list'] = ['证件类型']               // 可能出现的关键字
    sub_item_ID_type['default_value_list'] = []
    sub_item_ID_type['min_length'] = 2                              // 目标识别字段 最小长度
    sub_item_ID_type['max_length'] = 5                              // 目标识别字段 最大长度
    sub_item_ID_type['target_value_type'] = 'char'                  // 目标识别字段 类型   char:字符;  digit 数字;

    sub_item_list.push(sub_item_ID_type)


    sub_item_customer_id = {}
    sub_item_customer_id['target_value_name'] = '客户号'
    sub_item_customer_id['key_word_list'] = ['客户号']               // 可能出现的关键字
    sub_item_customer_id['default_value_list'] = []
    sub_item_customer_id['min_length'] = 2                              // 目标识别字段 最小长度
    sub_item_customer_id['max_length'] = 5                              // 目标识别字段 最大长度
    sub_item_customer_id['target_value_type'] = 'char'                  // 目标识别字段 类型   char:字符;  digit 数字;

    sub_item_list.push(sub_item_customer_id)


    item['sub_item_list'] = sub_item_list


    return item
}

function init_insurance_table (){

    item_insurance_table = {}
    item_insurance_table['target_value_name'] = '险种列表'              // 险种名称的表格识别
    item_insurance_table['recognition_type'] = 'table'                  // 类型 horizontal; vertical; all; table
    item_insurance_table['key_word_1_list'] = ['险种名称', '保险项目', '险种名称及款式','保险产品']               // 可能出现的关键字
    item_insurance_table['key_word_2_list'] = ['保险费', '保险费转换价值']               // 可能出现的关键字
    item_insurance_table['target_value_type'] = 'char'                  // 目标识别字段 类型   char:字符;  digit 数字;
    return item_insurance_table

}