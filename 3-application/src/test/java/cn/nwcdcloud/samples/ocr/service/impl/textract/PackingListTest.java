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


public class PackingListTest {
    private static final Logger logger = LoggerFactory.getLogger(PackingListTest.class);

    private static final String  SAMPLE_JSON_OBJECT_FILE_1 =  "/sample/packing_list.json";
//    private static final String  SAMPLE_JSON_OBJECT_FILE_2 =  "/sample/cost_table002.json";



    @Test
    public void parseId001() {

        String jsonObjectPath=this.getClass().getResource(SAMPLE_JSON_OBJECT_FILE_1).getFile();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);

        List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject);
        ParseFactory parseJsonUtil = new ParseFactory("packing_list");
        JSONObject  resultObject =  parseJsonUtil.extractValue(blockItemList);
//        {"tableList":[],"keyValueList":[{"confidence":"0.9940133","name":"境内收货人","value":"（914401013210409001）"}]}
        logger.info(resultObject.toJSONString());
        JSONArray  resultArray =  resultObject.getJSONArray("keyValueList");
        JSONArray  tableArray =  resultObject.getJSONArray("tableList");
        logger.info(resultObject.toJSONString());

        for(JSONObject item : resultArray.toJavaList(JSONObject.class)){
            logger.info("{}   {} ", item.getString("name"), item.getString("value"));
        }

        assert  BlockItemUtils.checkKeyValueMap(resultArray, "境内收货人", "（914401013210409001）");

    }

}