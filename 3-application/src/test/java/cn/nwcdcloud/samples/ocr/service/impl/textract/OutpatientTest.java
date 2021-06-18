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


public class OutpatientTest {
    private static final Logger logger = LoggerFactory.getLogger(OutpatientTest.class);

    private static final String  SAMPLE_YAML_OBJECT_FILE_1 =  "outpatient";

    private static final String  SAMPLE_JSON_OBJECT_FILE_1 =  "/sample/outpatient/001.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_2 =  "/sample/outpatient/002.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_3 =  "/sample/outpatient/003.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_4 =  "/sample/outpatient/004.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_5 =  "/sample/outpatient/005.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_6 =  "/sample/outpatient/006.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_7 =  "/sample/outpatient/007.json";



    private static final String  SAMPLE_JSON_OBJECT_FILE_101 =  "/sample/outpatient/101.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_102 =  "/sample/outpatient/102.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_103 =  "/sample/outpatient/103.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_104 =  "/sample/outpatient/104.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_105 =  "/sample/outpatient/105.json";


    private static final String  SAMPLE_JSON_OBJECT_FILE_106 =  "/sample/outpatient/106.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_107 =  "/sample/outpatient/107.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_108 =  "/sample/outpatient/108.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_109 =  "/sample/outpatient/109.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_110 =  "/sample/outpatient/110.json";


    private static final String  SAMPLE_JSON_OBJECT_FILE_111 =  "/sample/outpatient/111.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_112 =  "/sample/outpatient/112.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_113 =  "/sample/outpatient/113.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_114 =  "/sample/outpatient/114.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_115 =  "/sample/outpatient/115.json";


    private static final String  SAMPLE_JSON_OBJECT_FILE_116 =  "/sample/outpatient/116.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_117 =  "/sample/outpatient/117.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_118 =  "/sample/outpatient/118.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_119 =  "/sample/outpatient/119.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_120 =  "/sample/outpatient/120.json";


    private static final String  SAMPLE_JSON_OBJECT_FILE_121 =  "/sample/outpatient/121.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_122 =  "/sample/outpatient/122.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_123 =  "/sample/outpatient/123.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_124 =  "/sample/outpatient/124.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_125 =  "/sample/outpatient/125.json";


    private static final String  SAMPLE_JSON_OBJECT_FILE_126 =  "/sample/outpatient/126.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_127 =  "/sample/outpatient/127.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_128 =  "/sample/outpatient/128.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_129 =  "/sample/outpatient/129.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_130 =  "/sample/outpatient/130.json";

    private static final String  SAMPLE_JSON_OBJECT_FILE_131 =  "/sample/outpatient/131.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_132 =  "/sample/outpatient/132.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_133 =  "/sample/outpatient/133.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_134 =  "/sample/outpatient/134.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_135 =  "/sample/outpatient/135.json";

    private static final String  SAMPLE_JSON_OBJECT_FILE_136 =  "/sample/outpatient/136.json";





    @Test
    public void parseId001() {

        String jsonObjectPath= this.getClass().getResource(SAMPLE_JSON_OBJECT_FILE_1).getFile();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
        List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject);
        ParseFactory parseJsonUtil = new ParseFactory(SAMPLE_YAML_OBJECT_FILE_1);
        JSONObject resultObject = parseJsonUtil.extractValue(blockItemList);
//        logger.info(resultObject.toJSONString());
        JSONArray keyValueList = resultObject.getJSONArray("keyValueList");
        logger.info(keyValueList.toJSONString());
        for(int i=0; i< keyValueList.size() ; i++){
            JSONObject item = (JSONObject) keyValueList.get(i);
            logger.info("name: [{}]  value: [{}]", item.getString("name"), item.getString("value"));
        }

        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "交易流水号", "081100140Z191031007412");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "业务流水号", "11785403");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "医疗机构类型", "非营利性医疗机构");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "姓名", "李初");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "性别", "男");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "医保类型", "城多居民");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "社会保障卡号", "126776699001");

        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "NO", "0006475067");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "合计（大写）", "伍拾陆元整");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "个人支付金额", "28.00");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "自付一", "28.00");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "自付二", "0.00");

        JSONArray  tableArray =  resultObject.getJSONArray("tableList");
        logger.info("   {} ", tableArray.toJSONString());


        assert tableArray.size() == 2;
        JSONObject table1 = (JSONObject) tableArray.get(0);
        assert table1 !=null;
        JSONArray tableRowList =  table1.getJSONArray("rowList");
        assert tableRowList.size() ==3;

        JSONArray cell1List = (JSONArray) tableRowList.get(0);
        assert  cell1List.size() == 5;
        logger.info(cell1List.getJSONObject(0).getString("text"));
        assert cell1List.getJSONObject(0).getString("text").equals("化验费");
        assert ((JSONArray) tableRowList.get(0)).getJSONObject(3).getString("text").equals("50.00");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(3).getString("text").equals("30.0000");
        assert ((JSONArray) tableRowList.get(2)).getJSONObject(3).getString("text").equals("6.0000");

        JSONObject table2 = (JSONObject) tableArray.get(1);
        JSONArray table2RowList =  table2.getJSONArray("rowList");

        assert ((JSONArray) table2RowList.get(0)).getJSONObject(3).getString("text").equals("6.00");
        assert ((JSONArray) table2RowList.get(1)).getJSONObject(3).getString("text").equals("20.0000");


        JSONArray cell2List = (JSONArray) tableRowList.get(1);
        logger.info(cell2List.getJSONObject(0).getString("text"));
        assert cell2List.getJSONObject(0).getString("text").equals("C-反应蛋白（CRP）测定/次");

        assert ((JSONArray) tableRowList.get(0)).getJSONObject(3).getString("text").equals("50.00");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(3).getString("text").equals("30.0000");

        for(int i=0; i<tableArray.size(); i++ ){
            logger.info("------");
            JSONObject object = (JSONObject) tableArray.get(i);
            JSONArray array = (JSONArray) object.getJSONArray("rowList").get(0);
            logger.info("  size {} ", object.getJSONArray("rowList").size());
            JSONObject o1 = (JSONObject)array.get(0);
            JSONObject o2 = (JSONObject) array.get(1);
            logger.info(o1.toJSONString());
            logger.info(o2.toJSONString());
        }
     }

    @Test
    public void parseId002() {

        String jsonObjectPath= this.getClass().getResource(SAMPLE_JSON_OBJECT_FILE_2).getFile();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
        List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject);
        ParseFactory parseJsonUtil = new ParseFactory(SAMPLE_YAML_OBJECT_FILE_1);
        JSONObject resultObject = parseJsonUtil.extractValue(blockItemList);
//        logger.info(resultObject.toJSONString());
        JSONArray keyValueList = resultObject.getJSONArray("keyValueList");
        logger.info(keyValueList.toJSONString());
        for(int i=0; i< keyValueList.size() ; i++){
            JSONObject item = (JSONObject) keyValueList.get(i);
            logger.info("name: [{}]  value: [{}]", item.getString("name"), item.getString("value"));
        }
    }



    @Test
    public void parseId003() {

        String jsonObjectPath= this.getClass().getResource(SAMPLE_JSON_OBJECT_FILE_3).getFile();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
        List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject);
        ParseFactory parseJsonUtil = new ParseFactory(SAMPLE_YAML_OBJECT_FILE_1);
        JSONObject resultObject = parseJsonUtil.extractValue(blockItemList);
//        logger.info(resultObject.toJSONString());
        JSONArray keyValueList = resultObject.getJSONArray("keyValueList");
        logger.info(keyValueList.toJSONString());
        for(int i=0; i< keyValueList.size() ; i++){
            JSONObject item = (JSONObject) keyValueList.get(i);
            logger.info("name: [{}]  value: [{}]", item.getString("name"), item.getString("value"));
        }

        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "业务流水号", "50011971609168");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "医疗机构类型", "综合医院");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "姓名", "文雨年");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "性别", "男");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "社会保障卡号", "103961136014");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "合计（大写）", "陆佰登拾壹元壹角肆分");

        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "门诊大额支付", "427.80");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "自付一", "183.34");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "自付二", "000");



    }


    @Test
    public void parseId004() {

        String jsonObjectPath= this.getClass().getResource(SAMPLE_JSON_OBJECT_FILE_4).getFile();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
        List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject);
        ParseFactory parseJsonUtil = new ParseFactory(SAMPLE_YAML_OBJECT_FILE_1);
        JSONObject resultObject = parseJsonUtil.extractValue(blockItemList);
//        logger.info(resultObject.toJSONString());
        JSONArray keyValueList = resultObject.getJSONArray("keyValueList");
        logger.info(keyValueList.toJSONString());
        for(int i=0; i< keyValueList.size() ; i++){
            JSONObject item = (JSONObject) keyValueList.get(i);
            logger.info("name: [{}]  value: [{}]", item.getString("name"), item.getString("value"));
        }




    }


    @Test
    public void parseId005() {

        String jsonObjectPath= this.getClass().getResource(SAMPLE_JSON_OBJECT_FILE_5).getFile();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
        List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject);
        ParseFactory parseJsonUtil = new ParseFactory(SAMPLE_YAML_OBJECT_FILE_1);
        JSONObject resultObject = parseJsonUtil.extractValue(blockItemList);
//        logger.info(resultObject.toJSONString());
        JSONArray keyValueList = resultObject.getJSONArray("keyValueList");
        logger.info(keyValueList.toJSONString());
        for(int i=0; i< keyValueList.size() ; i++){
            JSONObject item = (JSONObject) keyValueList.get(i);
            logger.info("name: [{}]  value: [{}]", item.getString("name"), item.getString("value"));
        }

        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "交易流水号", "121100010A201209007260");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "业务流水号", "12110001201209004669");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "医疗机构类型", "综合医院");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "姓名", "兰杭杭");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "性别", "医");

        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "医保类型", "城镇职工");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "社会保障卡号", "116910487001");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "NO", "116910487001");





    }




    @Test
    public void parseId006() {

        String jsonObjectPath= this.getClass().getResource(SAMPLE_JSON_OBJECT_FILE_6).getFile();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
        List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject);
        ParseFactory parseJsonUtil = new ParseFactory(SAMPLE_YAML_OBJECT_FILE_1);
        JSONObject resultObject = parseJsonUtil.extractValue(blockItemList);
//        logger.info(resultObject.toJSONString());
        JSONArray keyValueList = resultObject.getJSONArray("keyValueList");
        logger.info(keyValueList.toJSONString());
        for(int i=0; i< keyValueList.size() ; i++){
            JSONObject item = (JSONObject) keyValueList.get(i);
            logger.info("name: [{}]  value: [{}]", item.getString("name"), item.getString("value"));
        }

        JSONArray  tableArray =  resultObject.getJSONArray("tableList");
        logger.info("   {} ", tableArray.toJSONString());
        assert tableArray.size() == 2;



        JSONObject table1 = (JSONObject) tableArray.get(0);
        assert table1 !=null;
        JSONArray tableRowList =  table1.getJSONArray("rowList");
        assert tableRowList.size() ==5;

        logger.info(((JSONArray) tableRowList.get(0)).getJSONObject(3).getString("text"));
        assert ((JSONArray) tableRowList.get(0)).getJSONObject(3).getString("text").equals("20.00");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(3).getString("text").equals("63.10");
        assert ((JSONArray) tableRowList.get(2)).getJSONObject(3).getString("text").equals("20.0000");
        assert ((JSONArray) tableRowList.get(3)).getJSONObject(3).getString("text").equals("33.3000");
        assert ((JSONArray) tableRowList.get(4)).getJSONObject(3).getString("text").equals("5.1300");



        JSONObject table2 = (JSONObject) tableArray.get(1);
        assert table2 !=null;
        JSONArray table2RowList =  table2.getJSONArray("rowList");
        assert table2RowList.size() ==5;

        logger.info(((JSONArray) table2RowList.get(0)).getJSONObject(3).getString("text"));
        assert ((JSONArray) table2RowList.get(0)).getJSONObject(3).getString("text").equals("7.00");
        assert ((JSONArray) table2RowList.get(1)).getJSONObject(3).getString("text").equals("12.93");
        assert ((JSONArray) table2RowList.get(2)).getJSONObject(3).getString("text").equals("7.0000");
        assert ((JSONArray) table2RowList.get(3)).getJSONObject(3).getString("text").equals("29.8000");
        assert ((JSONArray) table2RowList.get(4)).getJSONObject(3).getString("text").equals("7.8000");


    }

    private JSONObject getResultObject(String dataFile, String templateFile){
        String jsonObjectPath= this.getClass().getResource(dataFile).getFile();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
        List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject);
        ParseFactory parseJsonUtil = new ParseFactory(templateFile);
        return  parseJsonUtil.extractValue(blockItemList);
    }



    @Test
    public void parseId007() {

        JSONObject resultObject = getResultObject(SAMPLE_JSON_OBJECT_FILE_7, SAMPLE_YAML_OBJECT_FILE_1);
        JSONArray keyValueList = resultObject.getJSONArray("keyValueList");
        logger.info(keyValueList.toJSONString());
        for(int i=0; i< keyValueList.size() ; i++){
            JSONObject item = (JSONObject) keyValueList.get(i);
            logger.info("name: [{}]  value: [{}]", item.getString("name"), item.getString("value"));
        }

        // 表格个数
        JSONArray  tableArray =  resultObject.getJSONArray("tableList");
        logger.info("   {} ", tableArray.toJSONString());
        assert tableArray.size() == 2;

        //第一个表格
        JSONObject table1 = (JSONObject) tableArray.get(0);
        assert table1 !=null;
        JSONArray tableRowList =  table1.getJSONArray("rowList");
        assert tableRowList.size() ==10; //第一个表格行数

        logger.info(((JSONArray) tableRowList.get(0)).getJSONObject(3).getString("text"));
        assert ((JSONArray) tableRowList.get(0)).getJSONObject(3).getString("text").equals("180.00");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(3).getString("text").equals("6.00");
        assert ((JSONArray) tableRowList.get(2)).getJSONObject(3).getString("text").equals("150.0000");
        assert ((JSONArray) tableRowList.get(3)).getJSONObject(3).getString("text").equals("50.0000");
        assert ((JSONArray) tableRowList.get(4)).getJSONObject(3).getString("text").equals("27.0000");
        assert ((JSONArray) tableRowList.get(5)).getJSONObject(3).getString("text").equals("35.0000");
        assert ((JSONArray) tableRowList.get(6)).getJSONObject(3).getString("text").equals("45.0000");
        assert ((JSONArray) tableRowList.get(7)).getJSONObject(3).getString("text").equals("40.0000");
        assert ((JSONArray) tableRowList.get(8)).getJSONObject(3).getString("text").equals("45.0000");
        assert ((JSONArray) tableRowList.get(9)).getJSONObject(3).getString("text").equals("3.0600");



        //第二个表格
        JSONObject table2 = (JSONObject) tableArray.get(1);
        assert table2 !=null;
        JSONArray table2RowList =  table2.getJSONArray("rowList");
        assert table2RowList.size() ==9;

        logger.info(((JSONArray) table2RowList.get(0)).getJSONObject(3).getString("text"));
        assert ((JSONArray) table2RowList.get(0)).getJSONObject(3).getString("text").equals("426.00");
        assert ((JSONArray) table2RowList.get(1)).getJSONObject(3).getString("text").equals("3.06");
        assert ((JSONArray) table2RowList.get(2)).getJSONObject(3).getString("text").equals("30.0000");
        assert ((JSONArray) table2RowList.get(3)).getJSONObject(3).getString("text").equals("27.0000");
        assert ((JSONArray) table2RowList.get(4)).getJSONObject(3).getString("text").equals("32.0000");
        assert ((JSONArray) table2RowList.get(5)).getJSONObject(3).getString("text").equals("45.0000");
        assert ((JSONArray) table2RowList.get(6)).getJSONObject(3).getString("text").equals("35.0000");
        assert ((JSONArray) table2RowList.get(7)).getJSONObject(3).getString("text").equals("45.0000");
        assert ((JSONArray) table2RowList.get(8)).getJSONObject(3).getString("text").equals("6.0000");


    }


    @Test
    public void parseId101() {

        JSONObject resultObject = getResultObject(SAMPLE_JSON_OBJECT_FILE_101, SAMPLE_YAML_OBJECT_FILE_1);
        JSONArray keyValueList = resultObject.getJSONArray("keyValueList");
        logger.info(keyValueList.toJSONString());
        for(int i=0; i< keyValueList.size() ; i++){
            JSONObject item = (JSONObject) keyValueList.get(i);
            logger.info("name: [{}]  value: [{}]", item.getString("name"), item.getString("value"));
        }

        // 表格个数
        JSONArray  tableArray =  resultObject.getJSONArray("tableList");
        logger.info("   {} ", tableArray.toJSONString());
        assert tableArray.size() == 2;

        //第一个表格
        JSONObject table1 = (JSONObject) tableArray.get(0);
        assert table1 !=null;
        JSONArray tableRowList =  table1.getJSONArray("rowList");
        assert tableRowList.size() == 5; //第一个表格行数

        assert ((JSONArray) tableRowList.get(0)).getJSONObject(3).getString("text").equals("675.00");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(3).getString("text").equals("150.0000");
        assert ((JSONArray) tableRowList.get(2)).getJSONObject(3).getString("text").equals("120.0000");
        assert ((JSONArray) tableRowList.get(3)).getJSONObject(3).getString("text").equals("15.0000");
        assert ((JSONArray) tableRowList.get(4)).getJSONObject(3).getString("text").equals("16.5000");



        //第二个表格
        JSONObject table2 = (JSONObject) tableArray.get(1);
        assert table2 !=null;
        JSONArray table2RowList =  table2.getJSONArray("rowList");
        assert table2RowList.size() ==4;

        logger.info("[{}]", ((JSONArray) table2RowList.get(0)).getJSONObject(3).getString("text"));
        assert ((JSONArray) table2RowList.get(0)).getJSONObject(3).getString("text").equals("43.23");
        assert ((JSONArray) table2RowList.get(1)).getJSONObject(3).getString("text").equals("300.0000");
        assert ((JSONArray) table2RowList.get(2)).getJSONObject(3).getString("text").equals("90.0000");
        assert ((JSONArray) table2RowList.get(3)).getJSONObject(3).getString("text").equals("26.7300");


    }


    @Test
    public void parseId102() {

        JSONObject resultObject = getResultObject(SAMPLE_JSON_OBJECT_FILE_102, SAMPLE_YAML_OBJECT_FILE_1);
        JSONArray keyValueList = resultObject.getJSONArray("keyValueList");
        logger.info(keyValueList.toJSONString());
        for(int i=0; i< keyValueList.size() ; i++){
            JSONObject item = (JSONObject) keyValueList.get(i);
            logger.info("name: [{}]  value: [{}]", item.getString("name"), item.getString("value"));
        }

        // 表格个数
        JSONArray  tableArray =  resultObject.getJSONArray("tableList");
        logger.info("   {} ", tableArray.toJSONString());
        assert tableArray.size() == 2;

        //第一个表格
        JSONObject table1 = (JSONObject) tableArray.get(0);
        assert table1 !=null;
        JSONArray tableRowList =  table1.getJSONArray("rowList");
        assert tableRowList.size() == 4; //第一个表格行数

        assert ((JSONArray) tableRowList.get(0)).getJSONObject(3).getString("text").equals("240.00");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(3).getString("text").equals("90.0000");
        assert ((JSONArray) tableRowList.get(2)).getJSONObject(3).getString("text").equals("90.0000");
        assert ((JSONArray) tableRowList.get(3)).getJSONObject(3).getString("text").equals("12.0000");



        //第二个表格
        JSONObject table2 = (JSONObject) tableArray.get(1);
        assert table2 !=null;
        JSONArray table2RowList =  table2.getJSONArray("rowList");
        assert table2RowList.size() ==4;

        logger.info("[{}]", ((JSONArray) table2RowList.get(0)).getJSONObject(3).getString("text"));
        assert ((JSONArray) table2RowList.get(0)).getJSONObject(3).getString("text").equals("24.0");
        assert ((JSONArray) table2RowList.get(1)).getJSONObject(3).getString("text").equals("0000.00");
        assert ((JSONArray) table2RowList.get(2)).getJSONObject(3).getString("text").equals("30.0000");
        assert ((JSONArray) table2RowList.get(3)).getJSONObject(3).getString("text").equals("12.0000");


    }


    @Test
    public void parseId103() {

        JSONObject resultObject = getResultObject(SAMPLE_JSON_OBJECT_FILE_103, SAMPLE_YAML_OBJECT_FILE_1);
        JSONArray keyValueList = resultObject.getJSONArray("keyValueList");
        logger.info(keyValueList.toJSONString());
        for(int i=0; i< keyValueList.size() ; i++){
            JSONObject item = (JSONObject) keyValueList.get(i);
            logger.info("name: [{}]  value: [{}]", item.getString("name"), item.getString("value"));
        }

        // 表格个数
        JSONArray  tableArray =  resultObject.getJSONArray("tableList");
        logger.info("   {} ", tableArray.toJSONString());
        assert tableArray.size() == 2;

        //第一个表格
        JSONObject table1 = (JSONObject) tableArray.get(0);
        assert table1 !=null;
        JSONArray tableRowList =  table1.getJSONArray("rowList");
        assert tableRowList.size() == 2; //第一个表格行数

        assert ((JSONArray) tableRowList.get(0)).getJSONObject(3).getString("text").equals("31.39");



    }




    @Test
    public void parseId104() {

        JSONObject resultObject = getResultObject(SAMPLE_JSON_OBJECT_FILE_104, SAMPLE_YAML_OBJECT_FILE_1);
        JSONArray keyValueList = resultObject.getJSONArray("keyValueList");
        logger.info(keyValueList.toJSONString());
        for(int i=0; i< keyValueList.size() ; i++){
            JSONObject item = (JSONObject) keyValueList.get(i);
            logger.info("name: [{}]  value: [{}]", item.getString("name"), item.getString("value"));
        }

        // 表格个数
        JSONArray  tableArray =  resultObject.getJSONArray("tableList");
        logger.info("   {} ", tableArray.toJSONString());
        assert tableArray.size() == 2;

        //第一个表格
        JSONObject table1 = (JSONObject) tableArray.get(0);
        assert table1 !=null;
        JSONArray tableRowList =  table1.getJSONArray("rowList");
        assert tableRowList.size() == 3; //第一个表格行数

        assert ((JSONArray) tableRowList.get(0)).getJSONObject(3).getString("text").equals("170.00");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(3).getString("text").equals("20.0000");
        assert ((JSONArray) tableRowList.get(2)).getJSONObject(3).getString("text").equals("120.0000");



        //第二个表格
        JSONObject table2 = (JSONObject) tableArray.get(1);
        assert table2 !=null;
        JSONArray table2RowList =  table2.getJSONArray("rowList");
        assert table2RowList.size() ==3;

        logger.info("[{}]", ((JSONArray) table2RowList.get(0)).getJSONObject(3).getString("text"));
        assert ((JSONArray) table2RowList.get(0)).getJSONObject(0).getString("text").equals("治疗费");
        assert ((JSONArray) table2RowList.get(1)).getJSONObject(0).getString("text").equals("C-反应蛋白（CRP）测定／");
        assert ((JSONArray) table2RowList.get(2)).getJSONObject(0).getString("text").equals("末梢血采集｛六岁以下儿／");


        assert ((JSONArray) table2RowList.get(0)).getJSONObject(1).getString("text").equals("6.00");
        assert ((JSONArray) table2RowList.get(1)).getJSONObject(1).getString("text").equals("30.0000");
        assert ((JSONArray) table2RowList.get(2)).getJSONObject(1).getString("text").equals("6.0000");

    }


    @Test
    public void parseId105() {

        JSONObject resultObject = getResultObject(SAMPLE_JSON_OBJECT_FILE_105, SAMPLE_YAML_OBJECT_FILE_1);
        JSONArray keyValueList = resultObject.getJSONArray("keyValueList");
        logger.info(keyValueList.toJSONString());
        for(int i=0; i< keyValueList.size() ; i++){
            JSONObject item = (JSONObject) keyValueList.get(i);
            logger.info("name: [{}]  value: [{}]", item.getString("name"), item.getString("value"));
        }

        // 表格个数
        JSONArray  tableArray =  resultObject.getJSONArray("tableList");
        logger.info("   {} ", tableArray.toJSONString());
        assert tableArray.size() == 2;

        //第一个表格
        JSONObject table1 = (JSONObject) tableArray.get(0);
        assert table1 !=null;
        JSONArray tableRowList =  table1.getJSONArray("rowList");
        assert tableRowList.size() == 2; //第一个表格行数

        assert ((JSONArray) tableRowList.get(0)).getJSONObject(3).getString("text").equals("131.67");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(3).getString("text").equals("131.6700");



    }



    @Test
    public void parseId106() {

        JSONObject resultObject = getResultObject(SAMPLE_JSON_OBJECT_FILE_106, SAMPLE_YAML_OBJECT_FILE_1);
        JSONArray keyValueList = resultObject.getJSONArray("keyValueList");
        logger.info(keyValueList.toJSONString());
        for(int i=0; i< keyValueList.size() ; i++){
            JSONObject item = (JSONObject) keyValueList.get(i);
            logger.info("name: [{}]  value: [{}]", item.getString("name"), item.getString("value"));
        }

        // 表格个数
        JSONArray  tableArray =  resultObject.getJSONArray("tableList");
        logger.info("   {} ", tableArray.toJSONString());
        assert tableArray.size() == 2;

        //第一个表格
        JSONObject table1 = (JSONObject) tableArray.get(0);
        assert table1 !=null;
        JSONArray tableRowList =  table1.getJSONArray("rowList");
        assert tableRowList.size() == 3; //第一个表格行数

        assert ((JSONArray) tableRowList.get(0)).getJSONObject(0).getString("text").equals("西药费");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(0).getString("text").equals("氨溴特罗口服溶液／100l");
        assert ((JSONArray) tableRowList.get(2)).getJSONObject(0).getString("text").equals("小儿消积止咳口服／10mlx1；66.6000");

        assert ((JSONArray) tableRowList.get(0)).getJSONObject(3).getString("text").equals("20.47");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(3).getString("text").equals("20.4700");
        assert ((JSONArray) tableRowList.get(2)).getJSONObject(3).getString("text").equals("133.2000");



        //第二个表格
        JSONObject table2 = (JSONObject) tableArray.get(1);
        assert table2 !=null;
        JSONArray table2RowList =  table2.getJSONArray("rowList");
        assert table2RowList.size() ==2;

        logger.info("[{}]", ((JSONArray) table2RowList.get(0)).getJSONObject(3).getString("text"));
        assert ((JSONArray) table2RowList.get(0)).getJSONObject(0).getString("text").equals("中成药费");
        assert ((JSONArray) table2RowList.get(1)).getJSONObject(0).getString("text").equals("小儿豉翘清热颗粒／2gx6袋");


        assert ((JSONArray) table2RowList.get(0)).getJSONObject(3).getString("text").equals("264.87");
        assert ((JSONArray) table2RowList.get(1)).getJSONObject(3).getString("text").equals("131.6700");

    }


    @Test
    public void parseId107() {

        JSONObject resultObject = getResultObject(SAMPLE_JSON_OBJECT_FILE_107, SAMPLE_YAML_OBJECT_FILE_1);
        JSONArray keyValueList = resultObject.getJSONArray("keyValueList");
        logger.info(keyValueList.toJSONString());
        for(int i=0; i< keyValueList.size() ; i++){
            JSONObject item = (JSONObject) keyValueList.get(i);
            logger.info("name: [{}]  value: [{}]", item.getString("name"), item.getString("value"));
        }

        // 表格个数
        JSONArray  tableArray =  resultObject.getJSONArray("tableList");
        logger.info("   {} ", tableArray.toJSONString());
        assert tableArray.size() == 2;

        //第一个表格
        JSONObject table1 = (JSONObject) tableArray.get(0);
        assert table1 !=null;
        JSONArray tableRowList =  table1.getJSONArray("rowList");
        assert tableRowList.size() == 4; //第一个表格行数

        assert ((JSONArray) tableRowList.get(0)).getJSONObject(0).getString("text").equals("化验费");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(0).getString("text").equals("全血细胞计数＋5分类检测／");
        assert ((JSONArray) tableRowList.get(2)).getJSONObject(0).getString("text").equals("肺炎支原体免疫学试验／");
        assert ((JSONArray) tableRowList.get(3)).getJSONObject(0).getString("text").equals("甲型流感病毒抗原检测／");

        assert ((JSONArray) tableRowList.get(0)).getJSONObject(3).getString("text").equals("219.00");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(3).getString("text").equals("20.0000");
        assert ((JSONArray) tableRowList.get(2)).getJSONObject(3).getString("text").equals("50.0000");
        assert ((JSONArray) tableRowList.get(3)).getJSONObject(3).getString("text").equals("59.0000");



        //第二个表格
        JSONObject table2 = (JSONObject) tableArray.get(1);
        assert table2 !=null;
        JSONArray table2RowList =  table2.getJSONArray("rowList");
        assert table2RowList.size() ==4;

        logger.info("[{}]", ((JSONArray) table2RowList.get(0)).getJSONObject(3).getString("text"));
        assert ((JSONArray) table2RowList.get(0)).getJSONObject(0).getString("text").equals("治疗费");
        assert ((JSONArray) table2RowList.get(1)).getJSONObject(0).getString("text").equals("C-反应蛋白（CRP）测定／");
        assert ((JSONArray) table2RowList.get(2)).getJSONObject(0).getString("text").equals("呼吸道病原检测（8种病／");
        assert ((JSONArray) table2RowList.get(3)).getJSONObject(0).getString("text").equals("未梢血采条大饮下元");


        assert ((JSONArray) table2RowList.get(0)).getJSONObject(3).getString("text").equals("6.00");
        assert ((JSONArray) table2RowList.get(1)).getJSONObject(3).getString("text").equals("30.0000");
        assert ((JSONArray) table2RowList.get(2)).getJSONObject(3).getString("text").equals("60.0000");
        assert ((JSONArray) table2RowList.get(3)).getJSONObject(3).getString("text").equals("6.0000");

    }



}