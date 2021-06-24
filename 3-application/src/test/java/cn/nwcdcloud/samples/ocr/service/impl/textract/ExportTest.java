package cn.nwcdcloud.samples.ocr.service.impl.textract;

import cn.nwcdcloud.samples.ocr.parse.BlockItemUtils;
import cn.nwcdcloud.samples.ocr.parse.FileUtils;
import cn.nwcdcloud.samples.ocr.parse.ParseFactory;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class ExportTest {
    private static final Logger logger = LoggerFactory.getLogger(ExportTest.class);

    private static final String  SAMPLE_JSON_OBJECT_FILE_1 =  "/samples/export.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_02 =  "/samples/export_02.json";


    @Test
    public void parseId001() {

        String jsonObjectPath= this.getClass().getResource(SAMPLE_JSON_OBJECT_FILE_1).getFile();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
        List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject);
        ParseFactory parseJsonUtil = new ParseFactory("export");
        JSONObject resultObject = parseJsonUtil.extractValue(blockItemList);
//        logger.info(resultObject.toJSONString());
        JSONArray keyValueList = resultObject.getJSONArray("keyValueList");
        logger.info(keyValueList.toJSONString());
        for(int i=0; i< keyValueList.size() ; i++){
            JSONObject item = (JSONObject) keyValueList.get(i);
            logger.info("name: [{}]  value: [{}]", item.getString("name"), item.getString("value"));
        }

        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "编号", "184111");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "订舱日期", "2021.05.06");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "货代名称", "深圳美林联合国际货运代理有限公司");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "联系人", "Lisa");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "TEL", "86 755-2586 2305/2264(8038)");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "FAX", "755-2586 2885");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "E-Mail", "sales17@wells-shipping.com");

        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "地址", "深圳市罗湖区地王大厦2501");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "托运人", "浙江泰普森实业集团有限公司");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "储运", "范璐青｜0571-28028027｜0p06＠hengfeng-china.com");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "单证", "吴秋菊｜0571-28906150｜Doc03＠hengfeng-china.com");


        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "发票抬头", "ATVK LTD 108811,Moscow,Moscowskiy city St.Georgievskay,11,VII Contract N9003CH/2018 from 27.02.2018");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "产地抬头", "无");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "传真", "0571-28887270");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "寄单地址", "杭州市通益路68号（原杭印路68号）");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "托运人", "浙江泰普森实业集团有限公司");

        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "SHIPPRER（承运人）", "ZHEJIANG HENGFENG TOP LEISURE CO.,LTD BEIHU STREET MOGANSHAN ECONOMICAL DEVELOPING ZONE DEQING ZHEJIANG,P.R.CHINA");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "CONSIGNEE（收货人）", "ATVK LTD 108811,Moscow,Moscowskiy city St.Georgievskay,11,VII");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "NOTIFYPARTY（通知人）", "无");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "客户订单号或合同号", "20-09-025M/68626/62238/18-05-617M");


        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "付款条件", "T/T");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "价格条款", "FOB SHANGHAI,CHINA");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "起运港", "SHANGHAI");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "目的港", "MOSCOW");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "目的地", "RUSSIA");

        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "国别地区", "RUSSIA");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "出货日期", "2021.05.12");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "运费", "FREIGHT COLLECT");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "箱量", "1x40'H");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "唛头", "N/M");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "备注：", "1、贵司承运我司货物，在目的港务必凭正本提单放货。如在未收到我司正本书面确认的情况下擅自无单放货，贵司将承担由此造成的全部经济损失和法律责任。2、我司不接受任何形式的FCR、SEAWAY B／L、EXPRESS CARGO B／L等无货权提单。我司只接受船公司提单或在交通部备案的无船承运人提单。如贵司无法提供以上两种法定提单，请在第一时间与我司联系，告知情况。3、贵司的各项费用标准请与配舱回单一同传至我司确认，谢谢！");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "合计-件数", "638");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "合计-毛重", "4,214.90");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "合计-体积", "58.362");



    }


    @Test
    public void parseId002() {

        String jsonObjectPath= this.getClass().getResource(SAMPLE_JSON_OBJECT_FILE_02).getFile();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
        List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject);
        ParseFactory parseJsonUtil = new ParseFactory("export");
        JSONObject resultObject = parseJsonUtil.extractValue(blockItemList);
//        logger.info(resultObject.toJSONString());
        JSONArray keyValueList = resultObject.getJSONArray("keyValueList");
        logger.info(keyValueList.toJSONString());
        for(int i=0; i< keyValueList.size() ; i++){
            JSONObject item = (JSONObject) keyValueList.get(i);
            logger.info("name: [{}]  value: [{}]", item.getString("name"), item.getString("value"));
        }

        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "编号", "184111");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "订舱日期", "2021.05.06");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "货代名称", "深圳美林联合国际货运代理有限公司");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "联系人", "Lisa");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "TEL", "86755-2586 2305/2264(8038)");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "FAX", "755-2586 2885");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "E-Mail", "sales17@wells-shipping.com");

        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "地址", "深圳市罗湖区地王大厦2501");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "托运人", "浙江泰普森实业集团有限公司");
//        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "储运", "范璐青｜0571-28028027｜0p06＠hengfeng-china.com");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "储运", "范璐青｜0571-28028027｜Op06＠hengfeng-china.com");

        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "单证", "吴秋菊｜0571-28906150｜Doc03＠hengfeng-china.com");


        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "发票抬头", "ATVK LTD 108811,Moscow,Moscowskiy city St.Georgievskay,11,VII Contract N9003CH/2018 from 27.02.2018");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "产地抬头", "无");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "传真", "0571-28887270");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "寄单地址", "杭州市通益路68号（原杭印路68号）");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "托运人", "浙江泰普森实业集团有限公司");

//        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "SHIPPRER（承运人）", "ZHEJIANG HENGFENG TOP LEISURE CO.,LTD BEIHU STREET MOGANSHAN ECONOMICAL DEVELOPING ZONE DEQING ZHEJIANG,P.R.CHINA");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "SHIPPRER（承运人）", "ZHEJIANG HENGFENG TOP LEISURE CO.,LTD BEIHU STREET MOGANSHAN ECONOMICAL DEVELOPING ZONE DEQING ZHEJIANG.P.R.CHINA");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "CONSIGNEE（收货人）", "ATVK LTD 108811,Moscow,Moscowskiy city St.Georgievskay,11,VII");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "NOTIFYPARTY（通知人）", "无");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "客户订单号或合同号", "20-09-025M/68626/62238/18-05-617M");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "客户订单号或合同号", "20-09-025M/68626/62238/18-05-617M");


//        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "付款条件", "T/T");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "付款条件", "т/Т");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "价格条款", "FOB SHANGHAI,CHINA");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "起运港", "SHANGHAI");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "目的港", "MOSCOW");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "目的地", "RUSSIA");

        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "国别地区", "RUSSIA");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "出货日期", "2021.05.12");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "运费", "FREIGHT COLLECT");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "箱量", "1x40'H");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "唛头", "N/M");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "备注：", "1、贵司承运我司货物，在目的港务必凭正本提单放货。如在未收到我司正本书面确认的情况下擅自无单放货，贵司将承担由此造成的全部经济损失和法律责任。2、我司不接受任何形式的FCR、SEAWAY B／L、EXPRESS CARGO B／L等无货权提单。我司只接受船公司提单或在交通部备案的无船承运人提单。如贵司无法提供以上两种法定提单，请在第一时间与我司联系，告知情况。3、贵司的各项费用标准请与配舱回单一同传至我司确认，谢谢！");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "合计-件数", "638");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "合计-毛重", "4,214.90");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "合计-体积", "58.362");



    }
}