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


public class TableSample02Test {
    private static final Logger logger = LoggerFactory.getLogger(TableSample02Test.class);

    private static final String  SAMPLE_JSON_OBJECT_FILE_2 =  "/sample/table_sample02.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_3 =  "/sample/table_sample03.json";
    private static final String  CONFIG_FILE_PATH =  "table_sample02" ;
    private static final String  CONFIG_FILE_PATH_3 =  "table_sample03" ;
    private final static int PAGE_WIDTH = 1200;
    private final static int PAGE_HEIGHT = 2000;

    @Test
    public void parseId002() {


        String jsonObjectPath= this.getClass().getResource(SAMPLE_JSON_OBJECT_FILE_2).getFile().toString();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
        List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject, PAGE_WIDTH, PAGE_HEIGHT);
        ParseFactory parseJsonUtil = new ParseFactory(PAGE_WIDTH, PAGE_HEIGHT, CONFIG_FILE_PATH);
        JSONObject resultObject = parseJsonUtil.extractValue(blockItemList);
        JSONArray  resultArray =  resultObject.getJSONArray("keyValueList");
        JSONArray  tableArray =  resultObject.getJSONArray("tableList");
        logger.info("   {} ", tableArray.toJSONString());

        for(int i=0; i<tableArray.size(); i++ ){
            JSONObject object = (JSONObject) tableArray.get(i);
            JSONArray array = (JSONArray) object.getJSONArray("rowList").get(0);
            JSONObject o1 = (JSONObject)array.get(0);
            JSONObject o2 = (JSONObject) array.get(1);
            logger.info(o1.toJSONString());
            logger.info(o2.toJSONString());
            if (i==0){
                assert o1.getString("text").equals("5600753");
                assert o2.getString("text").equals("2/8/2018");
            }else {
                assert o1.getString("text").equals("4511846139");
                assert o2.getString("text").equals("5600753");

            }
//
        }
    }


    @Test
    public void parseId003() {


        String jsonObjectPath= this.getClass().getResource(SAMPLE_JSON_OBJECT_FILE_3).getFile().toString();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
        List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject, PAGE_WIDTH, PAGE_HEIGHT);
        ParseFactory parseJsonUtil = new ParseFactory(PAGE_WIDTH, PAGE_HEIGHT, CONFIG_FILE_PATH_3);
        JSONObject resultObject = parseJsonUtil.extractValue(blockItemList);
        JSONArray  resultArray =  resultObject.getJSONArray("keyValueList");
        JSONArray  tableArray =  resultObject.getJSONArray("tableList");
        logger.info("   {} ", tableArray.toJSONString());
        assert  tableArray.size() == 1;

        JSONObject table = tableArray.getJSONObject(0);
        assert table != null;

        assert table.getInteger("rowCount") == 8;
        assert table.getInteger("columnCount") == 10;

        JSONArray rowList = table.getJSONArray("rowList");

        assert rowList.size() == 8;

        logger.info(rowList.get(3).toString());
        JSONArray row3 = rowList.getJSONArray(3);

        assert row3.size() == 10;
        JSONObject item3_4 = row3.getJSONObject(4);
        assert "银联手续费".equals(item3_4.getString("text"));

    }



}