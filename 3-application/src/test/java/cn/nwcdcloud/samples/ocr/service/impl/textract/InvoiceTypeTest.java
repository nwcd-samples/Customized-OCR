package cn.nwcdcloud.samples.ocr.service.impl.textract;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.nwcdcloud.samples.ocr.parse.BlockItemUtils;
import cn.nwcdcloud.samples.ocr.parse.ConfigConstants;
import cn.nwcdcloud.samples.ocr.parse.FileUtils;
import cn.nwcdcloud.samples.ocr.parse.ParseFactory;


public class InvoiceTypeTest {
    private static final Logger logger = LoggerFactory.getLogger(InvoiceTypeTest.class);

    private static final String  ID_SAMPLE_JSON_OBJECT_FILE_1 =  "/sample/invoice01.json";
    private static final String  ID_SAMPLE_JSON_OBJECT_FILE_2 =  "/sample/invoice02.json";

    @Test
    public void parse001() {

        String jsonObjectPath=this.getClass().getResource(ID_SAMPLE_JSON_OBJECT_FILE_1).getFile().toString();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
        List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject);
        ParseFactory parseJsonUtil = new ParseFactory("invoice");
        JSONObject resultObject = parseJsonUtil.extractValue(blockItemList);
        JSONArray  resultArray =  resultObject.getJSONArray("keyValueList");
        logger.info(resultArray.toJSONString());
        for(int i=0; i< resultArray.size() ; i++){
            JSONObject item = (JSONObject) resultArray.get(i);
            logger.info("name: [{}]  value: [{}]", item.getString("name"), item.getString("value"));
        }
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "购买方-名称", "北京西云数据科技有限公司");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "购买方-纳税人识别号", "91110105MA01M3778H");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "购买方-开户行及账号", "中国工商银行股份有限公司北京东四支行0200004109200060094");
//
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "销售方-名称", "北京滴滴出行科技有限公司");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "销售方-纳税人识别号", "91110108MA01GOFB09");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "销售方-开户行及账号", "招商银行股份有限公司北京东三环支行110936504210806");
//
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "价税合计（大写）", "肆佰柒拾肆圆柒角玖分");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "价税合计-小写", "￥474.79");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "发票代码", "011002000311");
//
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "发票号码", "96079560");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "开票日期", "2020年09月25日");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "校验码", "16372973982892550584");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "机器编号", "499098498420");




    }



    @Test
    public void parse002() {

        String jsonObjectPath=this.getClass().getResource(ID_SAMPLE_JSON_OBJECT_FILE_2).getFile();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
        List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject);
        ParseFactory parseJsonUtil = new ParseFactory("invoice_02");
        JSONObject resultObject = parseJsonUtil.extractValue(blockItemList);
        JSONArray  resultArray =  resultObject.getJSONArray("keyValueList");
        logger.info(resultArray.toJSONString());



        for(int i=0; i< resultArray.size() ; i++){
            JSONObject item = (JSONObject) resultArray.get(i);
            logger.info("name: [{}]  value: [{}]", item.getString("name"), item.getString("value"));
        }

        assert  BlockItemUtils.checkKeyValueMap(resultArray, "购买方-名称", "普华永道商务咨询（上海）有限公司");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "价税合计（大写）", "②壹仟壹佰叁拾参元肆角肆分");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "价税合计（大写）-2", "②");


//        [2021-06-17 15:44:51.055][INFO] c.n.s.o.s.i.t.InvoiceTypeTest - name: [发票代码]  value: [012001700111]
//[2021-06-17 15:44:51.055][INFO] c.n.s.o.s.i.t.InvoiceTypeTest - name: [发票号码]  value: [39541056]
//[2021-06-17 15:44:51.055][INFO] c.n.s.o.s.i.t.InvoiceTypeTest - name: [开票日期]  value: [2017年10月30日]
//[2021-06-17 15:44:51.055][INFO] c.n.s.o.s.i.t.InvoiceTypeTest - name: [校验码]  value: [05038725199196463249]
//[2021-06-17 15:44:51.055][INFO] c.n.s.o.s.i.t.InvoiceTypeTest - name: [机器编号]  value: [499099606306]
//[2021-06-17 15:44:51.055][INFO] c.n.s.o.s.i.t.InvoiceTypeTest - name: [购买方-名称]  value: [普华永道商务咨询（上海）有限公司]
//[2021-06-17 15:44:51.055][INFO] c.n.s.o.s.i.t.InvoiceTypeTest - name: [购买方-纳税人识别号]  value: [91310115787244809E]
//[2021-06-17 15:44:51.055][INFO] c.n.s.o.s.i.t.InvoiceTypeTest - name: [购买方-地址电话]  value: [码]
//[2021-06-17 15:44:51.055][INFO] c.n.s.o.s.i.t.InvoiceTypeTest - name: [购买方-开户行及账号]  value: [区]
//[2021-06-17 15:44:51.055][INFO] c.n.s.o.s.i.t.InvoiceTypeTest - name: [销售方-名称]  value: [滴滴出行科技有限公司]
//[2021-06-17 15:44:51.055][INFO] c.n.s.o.s.i.t.InvoiceTypeTest - name: [销售方-开户行及账号]  value: [招商银行股份有限公司天津自由贸易试验区分行122905939910401]
//[2021-06-17 15:44:51.055][INFO] c.n.s.o.s.i.t.InvoiceTypeTest - name: [价税合计（大写）]  value: [②壹仟壹佰叁拾参元肆角肆分]
//[2021-06-17 15:44:51.056][INFO] c.n.s.o.s.i.t.InvoiceTypeTest - name: [价税合计-小写]  value: [￥1133.44]
//[2021-06-17 15:44:51.056][INFO] c.n.s.o.s.i.t.InvoiceTypeTest - name: [销售方-纳税人识别号]  value: [911201163409833307]

    }


    @Test
    public void parse003() {

        String jsonObjectPath=this.getClass().getResource(ID_SAMPLE_JSON_OBJECT_FILE_2).getFile();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
        List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject);
        ParseFactory parseJsonUtil = new ParseFactory("invoice");
        JSONObject resultObject = parseJsonUtil.extractValue(blockItemList);
        JSONArray  resultArray =  resultObject.getJSONArray("keyValueList");
        logger.info(resultArray.toJSONString());
        for(int i=0; i< resultArray.size() ; i++){
            logger.info(resultArray.get(i).toString());
        }


    }



}