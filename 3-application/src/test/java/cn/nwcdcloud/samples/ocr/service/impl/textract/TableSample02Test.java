package cn.nwcdcloud.samples.ocr.service.impl.textract;

import cn.nwcdcloud.samples.ocr.parse.BlockItemUtils;
import cn.nwcdcloud.samples.ocr.parse.FileUtils;
import cn.nwcdcloud.samples.ocr.parse.ParseFactory;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.List;


public class TableSample02Test {
    private static final Logger logger = LoggerFactory.getLogger(TableSample02Test.class);

    @Resource
    CommonServiceImpl commonServiceImpl;

    private static final String  SAMPLE_JSON_OBJECT_FILE_1 =  "/sample/table_sample02.json";
    private static final String  CONFIG_FILE_PATH =  "config/table_sample02.yaml" ;
    private final static int PAGE_WIDTH = 1200;
    private final static int PAGE_HEIGHT = 2000;
    @Test
    public void parseId001() {


        String jsonObjectPath= this.getClass().getResource(SAMPLE_JSON_OBJECT_FILE_1).getFile().toString();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
        List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject, PAGE_WIDTH, PAGE_HEIGHT);
        ParseFactory parseJsonUtil = new ParseFactory(PAGE_WIDTH, PAGE_HEIGHT, CONFIG_FILE_PATH);
        JSONObject resultObject = parseJsonUtil.extractValue(blockItemList);
        JSONArray  resultArray =  resultObject.getJSONArray("keyValueList");
        JSONArray  tableArray =  resultObject.getJSONArray("tableList");
        logger.info("   {} ", resultObject.toJSONString());


    }





}