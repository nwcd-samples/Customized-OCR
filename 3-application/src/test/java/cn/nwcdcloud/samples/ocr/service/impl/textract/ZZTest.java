package cn.nwcdcloud.samples.ocr.service.impl.textract;

import cn.nwcdcloud.samples.ocr.parse.BlockItemUtils;
import cn.nwcdcloud.samples.ocr.parse.ConfigConstants;
import cn.nwcdcloud.samples.ocr.parse.FileUtils;
import cn.nwcdcloud.samples.ocr.parse.ParseFactory;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class ZZTest {
    private static final Logger logger = LoggerFactory.getLogger(ZZTest.class);

    private static final String  SAMPLE_JSON_OBJECT_FILE_1 =  "/sample/zz.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_2 =  "/sample/invoice01.json";


    @Test
    public void parseId001() {

        String jsonObjectPath= this.getClass().getResource(SAMPLE_JSON_OBJECT_FILE_1).getFile().toString();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
        List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject);
        ParseFactory parseJsonUtil = new ParseFactory(ConfigConstants.PAGE_WIDTH, ConfigConstants.PAGE_HEIGHT, "zz");
        JSONObject resultObject = parseJsonUtil.extractValue(blockItemList);
//        logger.info(resultObject.toJSONString());
        JSONArray keyValueList = resultObject.getJSONArray("keyValueList");
        logger.info(keyValueList.toJSONString());

    }


    @Test
    public void parseDefaultValue() {

        String jsonObjectPath= this.getClass().getResource(SAMPLE_JSON_OBJECT_FILE_2).getFile().toString();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
        List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject);
        ParseFactory parseJsonUtil = new ParseFactory(ConfigConstants.PAGE_WIDTH, ConfigConstants.PAGE_HEIGHT, "invoice_default");
        JSONObject resultObject = parseJsonUtil.extractValue(blockItemList);
//        logger.info(resultObject.toJSONString());
        JSONArray keyValueList = resultObject.getJSONArray("keyValueList");
        logger.info(keyValueList.toJSONString());

    }

}