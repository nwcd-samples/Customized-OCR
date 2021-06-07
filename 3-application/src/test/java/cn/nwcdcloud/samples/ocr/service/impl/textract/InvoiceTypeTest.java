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
        ParseFactory parseJsonUtil = new ParseFactory(ConfigConstants.PAGE_WIDTH, ConfigConstants.PAGE_HEIGHT, "invoice");
        JSONObject resultObject = parseJsonUtil.extractValue(blockItemList);
        JSONArray  resultArray =  resultObject.getJSONArray("keyValueList");
        logger.info(resultArray.toJSONString());
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "购买方-名称", "北京西云数据科技有限公司");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "购买方-纳税人识别号", "91110105MA01M3778H");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "购买方-开户行及账号", "中国工商银行股份有限公司北京东四支行0200004109200060094");

        assert  BlockItemUtils.checkKeyValueMap(resultArray, "销售方-名称", "北京滴滴出行科技有限公司");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "销售方-纳税人识别号", "91110108MA01GOFB09");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "销售方-开户行及账号", "招商银行股份有限公司北京东三环支行110936504210806");

        assert  BlockItemUtils.checkKeyValueMap(resultArray, "价税合计（大写）", "肆佰柒拾肆圆柒角玖分");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "价税合计-小写", "￥474.79");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "发票代码", "011002000311");

        assert  BlockItemUtils.checkKeyValueMap(resultArray, "发票号码", "96079560");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "开票日期", "2020年09月25日");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "校验码", "16372973982892550584");




    }



    @Test
    public void parse002() {

        String jsonObjectPath=this.getClass().getResource(ID_SAMPLE_JSON_OBJECT_FILE_2).getFile().toString();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
        List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject);
        ParseFactory parseJsonUtil = new ParseFactory(ConfigConstants.PAGE_WIDTH, ConfigConstants.PAGE_HEIGHT, "invoice_02");
        JSONObject resultObject = parseJsonUtil.extractValue(blockItemList);
        JSONArray  resultArray =  resultObject.getJSONArray("keyValueList");
        logger.info(resultArray.toJSONString());
        for(int i=0; i< resultArray.size() ; i++){
            logger.info(resultArray.get(i).toString());
        }




    }




}