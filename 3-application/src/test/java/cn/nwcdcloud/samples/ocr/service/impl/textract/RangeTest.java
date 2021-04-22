package cn.nwcdcloud.samples.ocr.service.impl.textract;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.nwcdcloud.samples.ocr.parse.BlockItemUtils;
import cn.nwcdcloud.samples.ocr.parse.FileUtils;
import cn.nwcdcloud.samples.ocr.parse.ParseFactory;


public class RangeTest {
    private static final Logger logger = LoggerFactory.getLogger(RangeTest.class);

    private static final String  SAMPLE_JSON_OBJECT_FILE_1 =  "/sample/range.json";
//    private static final String  SAMPLE_JSON_OBJECT_FILE_2 =  "/sample/cost_table002.json";



    @Test
    public void parseId001() {


        String jsonObjectPath= this.getClass().getResource(SAMPLE_JSON_OBJECT_FILE_1).getFile().toString();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
        List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject, 1200, 2000);
        ParseFactory parseJsonUtil = new ParseFactory(1200, 2000, "range");
        JSONObject resultObject = parseJsonUtil.extractValue(blockItemList);
        logger.info(resultObject.toJSONString());
        JSONArray keyValueList = resultObject.getJSONArray("keyValueList");
        logger.info(keyValueList.toJSONString());
        assert keyValueList.size()>0;

        JSONObject object = (JSONObject) keyValueList.get(0);
        logger.info(object.toJSONString());
        assert "180.00".equals(object.getString("value"));
    }

}