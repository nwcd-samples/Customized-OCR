package cn.nwcdcloud.samples.ocr.service.impl.textract;

import cn.nwcdcloud.samples.ocr.commons.util.BlockItemUtils;
import cn.nwcdcloud.samples.ocr.commons.util.FileUtils;
import cn.nwcdcloud.samples.ocr.commons.util.ParseJsonWorker;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.List;


public class TableSampleTest {
    private static final Logger logger = LoggerFactory.getLogger(TableSampleTest.class);

    @Resource
    CommonServiceImpl commonServiceImpl;

    private static final String  SAMPLE_JSON_OBJECT_FILE_1 =  "/sample/table_sample.json";
//    private static final String  SAMPLE_JSON_OBJECT_FILE_2 =  "/sample/cost_table002.json";



    @Test
    public void parseId001() {


        String jsonObjectPath= this.getClass().getResource(SAMPLE_JSON_OBJECT_FILE_1).getFile().toString();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
        List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject, 1200, 2000);
        ParseJsonWorker parseJsonUtil = new ParseJsonWorker(1200, 2000, blockItemList, "config/table_sample.yaml");
        JSONObject resultObject = parseJsonUtil.extractValue(blockItemList);
        JSONArray  resultArray =  resultObject.getJSONArray("keyValueList");
//        logger.info("   {} ", resultArray.size());
        logger.info(resultObject.toJSONString());

    }





}