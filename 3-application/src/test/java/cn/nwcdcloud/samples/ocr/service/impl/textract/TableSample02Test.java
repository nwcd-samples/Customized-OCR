package cn.nwcdcloud.samples.ocr.service.impl.textract;

import java.util.List;

import org.junit.jupiter.api.Test;
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
    private static final String  SAMPLE_JSON_OBJECT_FILE_4 =  "/sample/table_sample04.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_5 =  "/sample/table_sample05.json";

    private static final String  SAMPLE_JSON_OBJECT_FILE_MULTI =  "/sample/table_multi.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_MULTI_02 =  "/sample/table_multi02.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_MULTI_03 =  "/sample/table_multi03.json";

    private static final String  CONFIG_FILE_PATH =  "table_sample02" ;
    private static final String  CONFIG_FILE_PATH_3 =  "table_sample03" ;
    private static final String  CONFIG_FILE_PATH_4 =  "table_sample04" ;
    private static final String  CONFIG_FILE_PATH_5 =  "table_sample05" ;
    private static final String  CONFIG_FILE_PATH_MULTI =  "table_multi" ;
    private static final String  CONFIG_FILE_PATH_MULTI_02 =  "table_multi02" ;
    private static final String  CONFIG_FILE_PATH_MULTI_03 =  "table_multi03" ;
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
        logger.info(item3_4.getString("text"));
        assert "银联手续费".equals(item3_4.getString("text"));

    }


    @Test
    public void parseId004() {


        String jsonObjectPath= this.getClass().getResource(SAMPLE_JSON_OBJECT_FILE_4).getFile().toString();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
        List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject, PAGE_WIDTH, PAGE_HEIGHT);
        ParseFactory parseJsonUtil = new ParseFactory(PAGE_WIDTH, PAGE_HEIGHT, CONFIG_FILE_PATH_4);
        JSONObject resultObject = parseJsonUtil.extractValue(blockItemList);
        JSONArray  resultArray =  resultObject.getJSONArray("keyValueList");
        JSONArray  tableArray =  resultObject.getJSONArray("tableList");
        logger.info("   {} ", tableArray.toJSONString());


    }

    @Test
    public void parseId005() {


        String jsonObjectPath= this.getClass().getResource(SAMPLE_JSON_OBJECT_FILE_5).getFile().toString();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
        List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject, PAGE_WIDTH, PAGE_HEIGHT);
        ParseFactory parseJsonUtil = new ParseFactory(PAGE_WIDTH, PAGE_HEIGHT, CONFIG_FILE_PATH_5);
        JSONObject resultObject = parseJsonUtil.extractValue(blockItemList);
        JSONArray  resultArray =  resultObject.getJSONArray("keyValueList");
        JSONArray  tableArray =  resultObject.getJSONArray("tableList");
        logger.info("   {} ", tableArray.toJSONString());

        assert  tableArray.size() == 1;

        JSONObject table = tableArray.getJSONObject(0);
        assert table != null;

        logger.info(table.getInteger("rowCount").toString());
        logger.info(table.getInteger("columnCount").toString());
        assert table.getInteger("rowCount") == 8;
        assert table.getInteger("columnCount") == 9;

        JSONArray rowList = table.getJSONArray("rowList");

        assert rowList.size() == 8;

        logger.info(rowList.get(3).toString());
        JSONArray row3 = rowList.getJSONArray(3);

        assert row3.size() == 9;
        JSONObject item3_4 = row3.getJSONObject(4);
        logger.info(item3_4.getString("text"));
        assert "2.000*1250.00".equals(item3_4.getString("text"));


    }


    @Test
    public void parseIdMultiTable() {
        String jsonObjectPath= this.getClass().getResource(SAMPLE_JSON_OBJECT_FILE_MULTI).getFile().toString();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
        List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject, PAGE_WIDTH, PAGE_HEIGHT);
        ParseFactory parseJsonUtil = new ParseFactory(PAGE_WIDTH, PAGE_HEIGHT, CONFIG_FILE_PATH_MULTI);
        JSONObject resultObject = parseJsonUtil.extractValue(blockItemList);
        JSONArray  resultArray =  resultObject.getJSONArray("keyValueList");
        JSONArray  tableArray =  resultObject.getJSONArray("tableList");

        logger.info(" {}  ", tableArray.size());
        assert tableArray !=null;
        assert tableArray.size() == 2;

        JSONObject table1 = tableArray.getJSONObject(0);

        logger.info(table1.toJSONString());
        assert table1.getInteger("rowCount") == 4;
        assert table1.getInteger("columnCount") == 3;


        JSONArray rowList1 = table1.getJSONArray("rowList");

        assert rowList1.size() == 4;

        assert "西药费".equals(rowList1.getJSONArray(0).getJSONObject(0).getString("text"));
        assert "752.56".equals(rowList1.getJSONArray(0).getJSONObject(1).getString("text"));
        assert "752、55".equals(rowList1.getJSONArray(0).getJSONObject(2).getString("text"));



        assert "化验费".equals(rowList1.getJSONArray(1).getJSONObject(0).getString("text"));
        assert "1742.00".equals(rowList1.getJSONArray(1).getJSONObject(1).getString("text"));
        assert "1742.00".equals(rowList1.getJSONArray(1).getJSONObject(2).getString("text"));
//        assert rowList1.getJSONObject(0).getString("name").equals("西药费");

        logger.info(" {} ", rowList1.getJSONArray(2));

        assert "".equals(rowList1.getJSONArray(2).getJSONObject(0).getString("text"));
        assert "500.00".equals(rowList1.getJSONArray(2).getJSONObject(1).getString("text"));
        assert "500.00".equals(rowList1.getJSONArray(2).getJSONObject(2).getString("text"));



        assert "床位费".equals(rowList1.getJSONArray(3).getJSONObject(0).getString("text"));
        assert "350".equals(rowList1.getJSONArray(3).getJSONObject(1).getString("text"));
        assert "".equals(rowList1.getJSONArray(3).getJSONObject(2).getString("text"));



        JSONObject table2 = tableArray.getJSONObject(1);
        logger.info(table2.toJSONString());
        assert table2.getInteger("rowCount") == 4;
        assert table2.getInteger("columnCount") == 3;


        JSONArray rowList2 = table2.getJSONArray("rowList");
        assert rowList2.size() == 4;

        logger.info(" {} ", rowList2.getJSONArray(0));
        logger.info(" {} ", rowList2.getJSONArray(1));
        logger.info(" {} ", rowList2.getJSONArray(2));
        logger.info(" {} ", rowList2.getJSONArray(3));

        assert "检查费".equals(rowList2.getJSONArray(0).getJSONObject(0).getString("text"));
        assert "471.00".equals(rowList2.getJSONArray(0).getJSONObject(1).getString("text"));
        assert "471.00".equals(rowList2.getJSONArray(0).getJSONObject(2).getString("text"));

        assert "护理费".equals(rowList2.getJSONArray(1).getJSONObject(0).getString("text"));
        assert "12948".equals(rowList2.getJSONArray(1).getJSONObject(1).getString("text"));
        assert "129.48".equals(rowList2.getJSONArray(1).getJSONObject(2).getString("text"));

        assert "治疗费".equals(rowList2.getJSONArray(2).getJSONObject(0).getString("text"));
        assert "198.05".equals(rowList2.getJSONArray(2).getJSONObject(1).getString("text"));
        assert "".equals(rowList2.getJSONArray(2).getJSONObject(2).getString("text"));

        assert "转他".equals(rowList2.getJSONArray(3).getJSONObject(0).getString("text"));
        assert "45.00".equals(rowList2.getJSONArray(3).getJSONObject(1).getString("text"));
        assert "".equals(rowList2.getJSONArray(3).getJSONObject(2).getString("text"));
    }

    @Test
    public void parseIdMultiTable02() {
        String jsonObjectPath= this.getClass().getResource(SAMPLE_JSON_OBJECT_FILE_MULTI_02).getFile().toString();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
        List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject, PAGE_WIDTH, PAGE_HEIGHT);
        ParseFactory parseJsonUtil = new ParseFactory(PAGE_WIDTH, PAGE_HEIGHT, CONFIG_FILE_PATH_MULTI_02);
        JSONObject resultObject = parseJsonUtil.extractValue(blockItemList);
        JSONArray  resultArray =  resultObject.getJSONArray("keyValueList");
        JSONArray  tableArray =  resultObject.getJSONArray("tableList");

        assert tableArray.size() == 3;

        JSONObject table0 = tableArray.getJSONObject(0);
        logger.info(table0.toJSONString());
        assert table0.getInteger("rowCount") == 4;
        assert table0.getInteger("columnCount") == 2;


        JSONObject table1 = tableArray.getJSONObject(1);
        logger.info(table1.toJSONString());
        assert table1.getInteger("rowCount") == 4;
        assert table1.getInteger("columnCount") == 2;



        JSONObject table2 = tableArray.getJSONObject(2);
        logger.info(table2.toJSONString());
        assert table2.getInteger("rowCount") == 3;
        assert table2.getInteger("columnCount") == 2;
    }



    @Test
    public void parseIdMultiTable03() {
        String jsonObjectPath= this.getClass().getResource(SAMPLE_JSON_OBJECT_FILE_MULTI_03).getFile().toString();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
        List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject, PAGE_WIDTH, PAGE_HEIGHT);
        ParseFactory parseJsonUtil = new ParseFactory(PAGE_WIDTH, PAGE_HEIGHT, CONFIG_FILE_PATH_MULTI_03);
        JSONObject resultObject = parseJsonUtil.extractValue(blockItemList);
        JSONArray  resultArray =  resultObject.getJSONArray("keyValueList");
        JSONArray  tableArray =  resultObject.getJSONArray("tableList");
        logger.info(resultArray.toJSONString());

        for(int i=0; i< resultArray.size(); i++){
            JSONObject object = resultArray.getJSONObject(i);
            logger.info(object.toJSONString());

        }


    }


}