package cn.nwcdcloud.samples.ocr.service.impl.textract;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.nwcdcloud.samples.ocr.commons.util.BlockItemUtils;
import cn.nwcdcloud.samples.ocr.commons.util.FileUtils;
import cn.nwcdcloud.samples.ocr.commons.util.ParseJsonWorker;


public class InvoiceTypeTest {
    private static final Logger logger = LoggerFactory.getLogger(InvoiceTypeTest.class);

    @Resource
    CommonServiceImpl commonServiceImpl;

    private static final String  ID_SAMPLE_JSON_OBJECT_FILE_1 =  "/sample/invoice01.json";
    private static final String  ID_SAMPLE_JSON_OBJECT_FILE_2 =  "/sample/invoice02.json";



    @Test
    public void parseId001() {

        String jsonObjectPath=this.getClass().getResource(ID_SAMPLE_JSON_OBJECT_FILE_1).getFile().toString();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
        List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject, 1200, 900);
        ParseJsonWorker parseJsonUtil = new ParseJsonWorker(1200, 900, blockItemList, "config/invoice.yaml");
        JSONArray  resultArray =  parseJsonUtil.extractValue(blockItemList);
        logger.info(resultArray.toJSONString());
        assert  checkKeyValueMap(resultArray, "购买方-名称", "北京西云数据科技有限公司");
        assert  checkKeyValueMap(resultArray, "购买方-纳税人识别号", "91110105MA01M3778H");
        assert  checkKeyValueMap(resultArray, "购买方-开户行及账号", "中国工商银行股份有限公司北京东四支行0200004109200060094");

        assert  checkKeyValueMap(resultArray, "销售方-名称", "北京滴滴出行科技有限公司");
        assert  checkKeyValueMap(resultArray, "销售方-纳税人识别号", "91110108MA01GOFB09");
        assert  checkKeyValueMap(resultArray, "销售方-开户行及账号", "招商银行股份有限公司北京东三环支行110936504210806");

        assert  checkKeyValueMap(resultArray, "价税合计", "肆佰柒拾肆圆柒角玖分");
        assert  checkKeyValueMap(resultArray, "价税合计-小写", "￥474.79");
        assert  checkKeyValueMap(resultArray, "发票代码", "011002000311");

        assert  checkKeyValueMap(resultArray, "发票号码", "96079560");
        assert  checkKeyValueMap(resultArray, "开票日期", "2020年09月25日");
        assert  checkKeyValueMap(resultArray, "校验码", "16372973982892550584");


    }


    private boolean checkKeyValueMap(JSONArray array, String name, String value){
        for(int i=0; i< array.size(); i++){
            String tempValue = array.getJSONObject(i).getString("value");
            String tempName = array.getJSONObject(i).getString("name");
//            logger.info("{}        {}    {} ", key, value, tempValue);
            if(value.equals(tempValue)  &&  name.equals(tempName)){
                return true;
            }
        }
        return false;

    }



}