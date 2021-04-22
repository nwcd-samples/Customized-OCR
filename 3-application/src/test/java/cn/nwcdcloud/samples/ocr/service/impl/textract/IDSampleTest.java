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


public class IDSampleTest {
    private static final Logger logger = LoggerFactory.getLogger(IDSampleTest.class);

    private static final String  SAMPLE_JSON_OBJECT_FILE_1 =  "/sample/id001.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_4 =  "/sample/id004.json";
    private static final String  CONFIG_FILE_PATH_1 =  "id" ;
    private static final String  CONFIG_FILE_PATH_2 =  "id02" ;

    private final static int PAGE_WIDTH = 1200;
    private final static int PAGE_HEIGHT = 2000;
    @Test
    public void parseId001() {


        String jsonObjectPath= this.getClass().getResource(SAMPLE_JSON_OBJECT_FILE_1).getFile().toString();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
        List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject, PAGE_WIDTH, PAGE_HEIGHT);
        ParseFactory parseJsonUtil = new ParseFactory(PAGE_WIDTH, PAGE_HEIGHT, CONFIG_FILE_PATH_1);
        JSONObject resultObject = parseJsonUtil.extractValue(blockItemList);
        JSONArray  resultArray =  resultObject.getJSONArray("keyValueList");
        JSONArray  tableArray =  resultObject.getJSONArray("tableList");
        logger.info(resultObject.toJSONString());

        for(JSONObject item : resultArray.toJavaList(JSONObject.class)){
            logger.info("{}   {} ", item.getString("name"), item.getString("value"));
        }

    }


    @Test
    public void parseId004() {


        String jsonObjectPath= this.getClass().getResource(SAMPLE_JSON_OBJECT_FILE_4).getFile().toString();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
        List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject, PAGE_WIDTH, PAGE_HEIGHT);
        ParseFactory parseJsonUtil = new ParseFactory(PAGE_WIDTH, PAGE_HEIGHT, CONFIG_FILE_PATH_1);
        JSONObject resultObject = parseJsonUtil.extractValue(blockItemList);
        JSONArray  resultArray =  resultObject.getJSONArray("keyValueList");
        JSONArray  tableArray =  resultObject.getJSONArray("tableList");
        logger.info(resultObject.toJSONString());

        for(JSONObject item : resultArray.toJavaList(JSONObject.class)){
            logger.info("{}   {} ", item.getString("name"), item.getString("value"));
        }

        assert  BlockItemUtils.checkKeyValueMap(resultArray, "姓名", "吴亚运");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "性别", "男");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "民族", "汉");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "出生日期", "1990年10月10日");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "住址", "湖北省监利县桥市镇南吴村2-29号");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "公民身份号码", "421023199010105039");


    }




}