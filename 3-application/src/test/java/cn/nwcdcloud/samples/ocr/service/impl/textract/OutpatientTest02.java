package cn.nwcdcloud.samples.ocr.service.impl.textract;

import cn.nwcdcloud.samples.ocr.parse.BlockItemUtils;
import cn.nwcdcloud.samples.ocr.parse.FileUtils;
import cn.nwcdcloud.samples.ocr.parse.ParseFactory;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class OutpatientTest02 {

    private static final Logger logger = LoggerFactory.getLogger(cn.nwcdcloud.samples.ocr.service.impl.textract.OutpatientTest02.class);

    private static final String  SAMPLE_YAML_OBJECT_FILE_1 =  "outpatient";
    private static final String  SAMPLE_YAML_OBJECT_FILE_02 =  "outpatient02";

    private static final String  SAMPLE_JSON_OBJECT_FILE_305 =  "/samples/outpatient/305.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_310 =  "/samples/outpatient/310.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_320 =  "/samples/outpatient/320.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_319 =  "/samples/outpatient/319.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_117 =  "/samples/outpatient/117.json";



    private JSONObject getResultObject(String dataFile, String templateFile){
        String jsonObjectPath= this.getClass().getResource(dataFile).getFile();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
        List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject);
        ParseFactory parseJsonUtil = new ParseFactory(templateFile);
        return  parseJsonUtil.extractValue(blockItemList);
    }

    @Test
    public void parseId305() {

        JSONObject resultObject = getResultObject(SAMPLE_JSON_OBJECT_FILE_305, SAMPLE_YAML_OBJECT_FILE_1);
        // 表格个数
        JSONArray tableArray =  resultObject.getJSONArray("tableList");
        assert tableArray.size() == 2;

        //第一个表格
        JSONObject table1 = (JSONObject) tableArray.get(0);
        assert table1 !=null;
        JSONArray tableRowList =  table1.getJSONArray("rowList");
        assert tableRowList.size() == 2; //第一个表格行数

        assert ((JSONArray) tableRowList.get(0)).getJSONObject(0).getString("text").equals("西药费");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(0).getString("text").equals("盐酸乐卡地平片／10mg＊7片，29.3500");

        assert ((JSONArray) tableRowList.get(0)).getJSONObject(1).getString("text").equals("");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(1).getString("text").equals("29.3500");


        assert ((JSONArray) tableRowList.get(0)).getJSONObject(3).getString("text").equals("254.60");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(3).getString("text").equals("117.4000");

        assert ((JSONArray) tableRowList.get(0)).getJSONObject(4).getString("text").equals("");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(4).getString("text").equals("无自付");


        //第二个表格
        JSONObject table2 = (JSONObject) tableArray.get(1);
        assert table2 !=null;
        JSONArray table2RowList =  table2.getJSONArray("rowList");
        assert table2RowList.size() ==1;


        assert ((JSONArray) table2RowList.get(0)).getJSONObject(0).getString("text").equals("替米沙坦片／80mg＊7片／盒");
        assert ((JSONArray) table2RowList.get(0)).getJSONObject(1).getString("text").equals("34.3000");
        assert ((JSONArray) table2RowList.get(0)).getJSONObject(2).getString("text").equals("4／BX-7TB");
        assert ((JSONArray) table2RowList.get(0)).getJSONObject(3).getString("text").equals("137.2000");
        assert ((JSONArray) table2RowList.get(0)).getJSONObject(4).getString("text").equals("无自付");

    }

    @Test
    public void parseId310() {

        JSONObject resultObject = getResultObject(SAMPLE_JSON_OBJECT_FILE_310, SAMPLE_YAML_OBJECT_FILE_1);
        // 表格个数
        JSONArray tableArray =  resultObject.getJSONArray("tableList");
        assert tableArray.size() == 2;

        //第一个表格
        JSONObject table1 = (JSONObject) tableArray.get(0);
        assert table1 !=null;
        JSONArray tableRowList =  table1.getJSONArray("rowList");
        assert tableRowList.size() == 3; //第一个表格行数

        assert ((JSONArray) tableRowList.get(0)).getJSONObject(0).getString("text").equals("西药费");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(0).getString("text").equals("硫酸氢氯吡格雷片／75mg＊7,91.7000");
        assert ((JSONArray) tableRowList.get(2)).getJSONObject(0).getString("text").equals("甲钴胺片／500");

        assert ((JSONArray) tableRowList.get(0)).getJSONObject(1).getString("text").equals("");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(1).getString("text").equals("366.8000");
        assert ((JSONArray) tableRowList.get(2)).getJSONObject(1).getString("text").equals("84.6300");

        assert ((JSONArray) tableRowList.get(0)).getJSONObject(2).getString("text").equals("");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(2).getString("text").equals("4／BX-7TB");
        assert ((JSONArray) tableRowList.get(2)).getJSONObject(2).getString("text").equals("3／BX-20T");

        assert ((JSONArray) tableRowList.get(0)).getJSONObject(3).getString("text").equals("622.51");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(3).getString("text").equals("366.8000");
        assert ((JSONArray) tableRowList.get(2)).getJSONObject(3).getString("text").equals("84.6300");

        assert ((JSONArray) tableRowList.get(0)).getJSONObject(4).getString("text").equals("");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(4).getString("text").equals("有自付");
        assert ((JSONArray) tableRowList.get(2)).getJSONObject(4).getString("text").equals("无自付");


        //第二个表格
        JSONObject table2 = (JSONObject) tableArray.get(1);
        assert table2 !=null;
        JSONArray table2RowList =  table2.getJSONArray("rowList");
        assert table2RowList.size() ==3;


        assert ((JSONArray) table2RowList.get(0)).getJSONObject(0).getString("text").equals("中成药费");
        assert ((JSONArray) table2RowList.get(1)).getJSONObject(0).getString("text").equals("阿托伐他汀钙片／20mg＊7片，42.7700");
        assert ((JSONArray) table2RowList.get(2)).getJSONObject(0).getString("text").equals("银杏叶片／9.6mg＊36片");


        assert ((JSONArray) table2RowList.get(0)).getJSONObject(3).getString("text").equals("100.05");
        assert ((JSONArray) table2RowList.get(1)).getJSONObject(3).getString("text").equals("171.0800");
        assert ((JSONArray) table2RowList.get(2)).getJSONObject(3).getString("text").equals("100.0500");


        assert ((JSONArray) table2RowList.get(0)).getJSONObject(4).getString("text").equals("");
        assert ((JSONArray) table2RowList.get(1)).getJSONObject(4).getString("text").equals("有自付");
        assert ((JSONArray) table2RowList.get(2)).getJSONObject(4).getString("text").equals("无自付");

    }
    @Test
    public void parseId136() {

        JSONObject resultObject = getResultObject(SAMPLE_JSON_OBJECT_FILE_319, SAMPLE_YAML_OBJECT_FILE_1);
        // 表格个数
        JSONArray tableArray =  resultObject.getJSONArray("tableList");
        assert tableArray.size() == 2;

        //第一个表格
        JSONObject table1 = (JSONObject) tableArray.get(0);
        assert table1 !=null;
        JSONArray tableRowList =  table1.getJSONArray("rowList");
        assert tableRowList.size() == 3; //第一个表格行数

        assert ((JSONArray) tableRowList.get(0)).getJSONObject(0).getString("text").equals("卫生材料费");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(0).getString("text").equals("医用高分子自粘式伤口敷／");
        assert ((JSONArray) tableRowList.get(2)).getJSONObject(0).getString("text").equals("溃疡油纱（6＊7.6＊15）（北／");

        assert ((JSONArray) tableRowList.get(0)).getJSONObject(1).getString("text").equals("");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(1).getString("text").equals("24.5500");
        assert ((JSONArray) tableRowList.get(2)).getJSONObject(1).getString("text").equals("1.9200");


        assert ((JSONArray) tableRowList.get(0)).getJSONObject(2).getString("text").equals("");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(2).getString("text").equals("2/");
        assert ((JSONArray) tableRowList.get(2)).getJSONObject(2).getString("text").equals("3/");

        assert ((JSONArray) tableRowList.get(0)).getJSONObject(3).getString("text").equals("73.48");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(3).getString("text").equals("49.1000");
        assert ((JSONArray) tableRowList.get(2)).getJSONObject(3).getString("text").equals("5.7600");


        assert ((JSONArray) tableRowList.get(0)).getJSONObject(4).getString("text").equals("");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(4).getString("text").equals("无自付");
        assert ((JSONArray) tableRowList.get(2)).getJSONObject(4).getString("text").equals("无自付");



        //第二个表格
        JSONObject table2 = (JSONObject) tableArray.get(1);
        assert table2 !=null;
        JSONArray table2RowList =  table2.getJSONArray("rowList");
        assert table2RowList.size() ==1;

        assert ((JSONArray) table2RowList.get(0)).getJSONObject(0).getString("text").equals("医用高分子自粘式伤口敷／");
        assert ((JSONArray) table2RowList.get(0)).getJSONObject(1).getString("text").equals("18.6200");
        assert ((JSONArray) table2RowList.get(0)).getJSONObject(2).getString("text").equals("1/");
        assert ((JSONArray) table2RowList.get(0)).getJSONObject(3).getString("text").equals("18.6200");
        assert ((JSONArray) table2RowList.get(0)).getJSONObject(4).getString("text").equals("18.6200，无自付");
    }



    @Test
    public void parseFile() {

        JSONObject resultObject = getResultObject("/samples/outpatient/272.json", SAMPLE_YAML_OBJECT_FILE_1);
    }

}
