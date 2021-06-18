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
    //FIXME: 有两类不同样式的表单， 以后可以考虑分开， 识别效果更好， 自动完成模板匹配。
    private static final String  SAMPLE_YAML_OBJECT_FILE_02 =  "outpatient02";

    private static final String  SAMPLE_JSON_OBJECT_FILE_1 =  "/samples/outpatient/001.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_2 =  "/samples/outpatient/002.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_3 =  "/samples/outpatient/003.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_4 =  "/samples/outpatient/004.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_5 =  "/samples/outpatient/005.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_6 =  "/samples/outpatient/006.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_7 =  "/samples/outpatient/007.json";



    private static final String  SAMPLE_JSON_OBJECT_FILE_101 =  "/samples/outpatient/101.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_102 =  "/samples/outpatient/102.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_103 =  "/samples/outpatient/103.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_104 =  "/samples/outpatient/104.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_105 =  "/samples/outpatient/105.json";


    private static final String  SAMPLE_JSON_OBJECT_FILE_106 =  "/samples/outpatient/106.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_107 =  "/samples/outpatient/107.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_108 =  "/samples/outpatient/108.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_109 =  "/samples/outpatient/109.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_110 =  "/samples/outpatient/110.json";


    private static final String  SAMPLE_JSON_OBJECT_FILE_111 =  "/samples/outpatient/111.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_112 =  "/samples/outpatient/112.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_113 =  "/samples/outpatient/113.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_114 =  "/samples/outpatient/114.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_115 =  "/samples/outpatient/115.json";


    private static final String  SAMPLE_JSON_OBJECT_FILE_116 =  "/samples/outpatient/116.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_117 =  "/samples/outpatient/117.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_118 =  "/samples/outpatient/118.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_119 =  "/samples/outpatient/119.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_120 =  "/samples/outpatient/120.json";


    private static final String  SAMPLE_JSON_OBJECT_FILE_121 =  "/samples/outpatient/121.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_122 =  "/samples/outpatient/122.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_123 =  "/samples/outpatient/123.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_124 =  "/samples/outpatient/124.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_125 =  "/samples/outpatient/125.json";


    private static final String  SAMPLE_JSON_OBJECT_FILE_126 =  "/samples/outpatient/126.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_127 =  "/samples/outpatient/127.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_128 =  "/samples/outpatient/128.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_129 =  "/samples/outpatient/129.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_130 =  "/samples/outpatient/130.json";

    private static final String  SAMPLE_JSON_OBJECT_FILE_131 =  "/samples/outpatient/131.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_132 =  "/samples/outpatient/132.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_133 =  "/samples/outpatient/133.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_134 =  "/samples/outpatient/134.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_135 =  "/samples/outpatient/135.json";

    private static final String  SAMPLE_JSON_OBJECT_FILE_136 =  "/samples/outpatient/136.json";





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
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(3).getString("text").equals("31.3900");



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


        assert ((JSONArray) table2RowList.get(0)).getJSONObject(1).getString("text").equals("");
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


    @Test
    public void parseId108() {

        JSONObject resultObject = getResultObject(SAMPLE_JSON_OBJECT_FILE_108, SAMPLE_YAML_OBJECT_FILE_1);
        JSONArray keyValueList = resultObject.getJSONArray("keyValueList");

        // 表格个数
        JSONArray  tableArray =  resultObject.getJSONArray("tableList");
        assert tableArray.size() == 2;

        //第一个表格
        JSONObject table1 = (JSONObject) tableArray.get(0);
        assert table1 !=null;
        JSONArray tableRowList =  table1.getJSONArray("rowList");
        assert tableRowList.size() == 2; //第一个表格行数

        assert ((JSONArray) tableRowList.get(0)).getJSONObject(0).getString("text").equals("诊察费");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(0).getString("text").equals("医事服务费副主任医师（／");

        assert ((JSONArray) tableRowList.get(0)).getJSONObject(3).getString("text").equals("00.09");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(3).getString("text").equals("60.0000");



        //第二个表格
        JSONObject table2 = (JSONObject) tableArray.get(1);
        assert table2 !=null;
        JSONArray table2RowList =  table2.getJSONArray("rowList");
        assert table2RowList.size() ==0;


    }


    @Test
    public void parseId109() {

        JSONObject resultObject = getResultObject(SAMPLE_JSON_OBJECT_FILE_109, SAMPLE_YAML_OBJECT_FILE_1);
        JSONArray keyValueList = resultObject.getJSONArray("keyValueList");

        // 表格个数
        JSONArray  tableArray =  resultObject.getJSONArray("tableList");
        assert tableArray.size() == 2;

        //第一个表格
        JSONObject table1 = (JSONObject) tableArray.get(0);
        assert table1 !=null;
        JSONArray tableRowList =  table1.getJSONArray("rowList");
        assert tableRowList.size() == 2; //第一个表格行数

        assert ((JSONArray) tableRowList.get(0)).getJSONObject(0).getString("text").equals("诊察费");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(0).getString("text").equals("医事服务费普通门诊（三／");

        assert ((JSONArray) tableRowList.get(0)).getJSONObject(3).getString("text").equals("50.00");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(3).getString("text").equals("50.0000");



        //第二个表格
        JSONObject table2 = (JSONObject) tableArray.get(1);
        assert table2 !=null;
        JSONArray table2RowList =  table2.getJSONArray("rowList");
        assert table2RowList.size() ==0;


    }


    @Test
    public void parseId110() {

        JSONObject resultObject = getResultObject(SAMPLE_JSON_OBJECT_FILE_110, SAMPLE_YAML_OBJECT_FILE_1);
        JSONArray keyValueList = resultObject.getJSONArray("keyValueList");

        // 表格个数
        JSONArray  tableArray =  resultObject.getJSONArray("tableList");
        assert tableArray.size() == 2;

        //第一个表格
        JSONObject table1 = (JSONObject) tableArray.get(0);
        assert table1 !=null;
        JSONArray tableRowList =  table1.getJSONArray("rowList");
        assert tableRowList.size() == 2; //第一个表格行数

        assert ((JSONArray) tableRowList.get(0)).getJSONObject(0).getString("text").equals("诊察费");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(0).getString("text").equals("医事服务费普通门诊（三／");

        assert ((JSONArray) tableRowList.get(0)).getJSONObject(3).getString("text").equals("50.00");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(3).getString("text").equals("50.0000");



        //第二个表格
        JSONObject table2 = (JSONObject) tableArray.get(1);
        assert table2 !=null;
        JSONArray table2RowList =  table2.getJSONArray("rowList");
        assert table2RowList.size() ==0;


    }


    @Test
    public void parseId111() {

        JSONObject resultObject = getResultObject(SAMPLE_JSON_OBJECT_FILE_111, SAMPLE_YAML_OBJECT_FILE_1);
        // 表格个数
        JSONArray  tableArray =  resultObject.getJSONArray("tableList");
        assert tableArray.size() == 2;

        //第一个表格
        JSONObject table1 = (JSONObject) tableArray.get(0);
        assert table1 !=null;
        JSONArray tableRowList =  table1.getJSONArray("rowList");
        assert tableRowList.size() == 2; //第一个表格行数

        assert ((JSONArray) tableRowList.get(0)).getJSONObject(0).getString("text").equals("西药费");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(0).getString("text").equals("曲安奈德益康唑乳／15g．0");

        assert ((JSONArray) tableRowList.get(1)).getJSONObject(1).getString("text").equals("14.7900");

        assert ((JSONArray) tableRowList.get(0)).getJSONObject(3).getString("text").equals("40.38");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(3).getString("text").equals("14.7900");

        assert ((JSONArray) tableRowList.get(1)).getJSONObject(4).getString("text").equals("无自付");



        //第二个表格
        JSONObject table2 = (JSONObject) tableArray.get(1);
        assert table2 !=null;
        JSONArray table2RowList =  table2.getJSONArray("rowList");
        assert table2RowList.size() ==1;

        assert ((JSONArray) table2RowList.get(0)).getJSONObject(0).getString("text").equals("多磺酸粘多糖乳膏／14g");
        assert ((JSONArray) table2RowList.get(0)).getJSONObject(1).getString("text").equals("25.5900");
        assert ((JSONArray) table2RowList.get(0)).getJSONObject(2).getString("text").equals("25.5900 1／支");
        assert ((JSONArray) table2RowList.get(0)).getJSONObject(3).getString("text").equals("25.5900");
        assert ((JSONArray) table2RowList.get(0)).getJSONObject(4).getString("text").equals("全自付");


    }




    @Test
    public void parseId112() {

        JSONObject resultObject = getResultObject(SAMPLE_JSON_OBJECT_FILE_112, SAMPLE_YAML_OBJECT_FILE_1);
        JSONArray keyValueList = resultObject.getJSONArray("keyValueList");

        // 表格个数
        JSONArray  tableArray =  resultObject.getJSONArray("tableList");
        assert tableArray.size() == 2;

        //第一个表格
        JSONObject table1 = (JSONObject) tableArray.get(0);
        assert table1 !=null;
        JSONArray tableRowList =  table1.getJSONArray("rowList");
        assert tableRowList.size() == 2; //第一个表格行数

        assert ((JSONArray) tableRowList.get(0)).getJSONObject(0).getString("text").equals("西药费");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(0).getString("text").equals("桉柠蒎肠溶软胶囊／0.12gx");

        assert ((JSONArray) tableRowList.get(0)).getJSONObject(3).getString("text").equals("56.32");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(3).getString("text").equals("56.3200");

        assert ((JSONArray) tableRowList.get(1)).getJSONObject(4).getString("text").equals("无自付");



        //第二个表格
        JSONObject table2 = (JSONObject) tableArray.get(1);
        assert table2 !=null;
        JSONArray table2RowList =  table2.getJSONArray("rowList");
        assert table2RowList.size() ==0;


    }



    @Test
    public void parseId113() {

        JSONObject resultObject = getResultObject(SAMPLE_JSON_OBJECT_FILE_113, SAMPLE_YAML_OBJECT_FILE_1);
        // 表格个数
        JSONArray  tableArray =  resultObject.getJSONArray("tableList");
        assert tableArray.size() == 2;

        //第一个表格
        JSONObject table1 = (JSONObject) tableArray.get(0);
        assert table1 !=null;
        JSONArray tableRowList =  table1.getJSONArray("rowList");
        assert tableRowList.size() == 6; //第一个表格行数

        assert ((JSONArray) tableRowList.get(0)).getJSONObject(0).getString("text").equals("");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(0).getString("text").equals("西药费");
        assert ((JSONArray) tableRowList.get(2)).getJSONObject(0).getString("text").equals("电耳镜检查／");
        assert ((JSONArray) tableRowList.get(3)).getJSONObject(0).getString("text").equals("盐酸氨卓斯汀鼻端／10ml：1s5.4700");
        assert ((JSONArray) tableRowList.get(4)).getJSONObject(0).getString("text").equals("核柠测肠溶软胶囊／0.12g＊");
        assert ((JSONArray) tableRowList.get(5)).getJSONObject(0).getString("text").equals("糠酸英米松鼻晴雾／0.0540");



        assert ((JSONArray) tableRowList.get(0)).getJSONObject(3).getString("text").equals("10.00");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(3).getString("text").equals("231.08");
        assert ((JSONArray) tableRowList.get(2)).getJSONObject(3).getString("text").equals("10.0000");
        assert ((JSONArray) tableRowList.get(3)).getJSONObject(3).getString("text").equals("45.4700");
        assert ((JSONArray) tableRowList.get(4)).getJSONObject(3).getString("text").equals("56.3800");
        assert ((JSONArray) tableRowList.get(5)).getJSONObject(3).getString("text").equals("0010");

        assert ((JSONArray) tableRowList.get(5)).getJSONObject(4).getString("text").equals("有自付");



        //第二个表格
        JSONObject table2 = (JSONObject) tableArray.get(1);
        assert table2 !=null;
        JSONArray table2RowList =  table2.getJSONArray("rowList");
        assert table2RowList.size() ==4;

        assert ((JSONArray) table2RowList.get(1)).getJSONObject(0).getString("text").equals("无自付");
        assert ((JSONArray) table2RowList.get(2)).getJSONObject(0).getString("text").equals("无自付");
        assert ((JSONArray) table2RowList.get(3)).getJSONObject(0).getString("text").equals("无自付三酸轻甲叶醇喷查／10x");


        assert ((JSONArray) table2RowList.get(0)).getJSONObject(3).getString("text").equals("4.00");
        assert ((JSONArray) table2RowList.get(1)).getJSONObject(3).getString("text").equals("4.0000");
        assert ((JSONArray) table2RowList.get(2)).getJSONObject(3).getString("text").equals("32.5800");
        assert ((JSONArray) table2RowList.get(3)).getJSONObject(3).getString("text").equals("55.8000");

        assert ((JSONArray) table2RowList.get(1)).getJSONObject(4).getString("text").equals("无自付");


    }



    @Test
    public void parseId114() {

        JSONObject resultObject = getResultObject(SAMPLE_JSON_OBJECT_FILE_114, SAMPLE_YAML_OBJECT_FILE_1);
        // 表格个数
        JSONArray  tableArray =  resultObject.getJSONArray("tableList");
        assert tableArray.size() == 2;

        //第一个表格
        JSONObject table1 = (JSONObject) tableArray.get(0);
        assert table1 !=null;
        JSONArray tableRowList =  table1.getJSONArray("rowList");
        assert tableRowList.size() == 2; //第一个表格行数

        assert ((JSONArray) tableRowList.get(0)).getJSONObject(0).getString("text").equals("检查费");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(0).getString("text").equals("数字化牙片／");

        assert ((JSONArray) tableRowList.get(0)).getJSONObject(2).getString("text").equals("25.00");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(2).getString("text").equals("25.0000");

        assert ((JSONArray) tableRowList.get(1)).getJSONObject(3).getString("text").equals("无自付");



        //第二个表格
        JSONObject table2 = (JSONObject) tableArray.get(1);
        assert table2 !=null;
        JSONArray table2RowList =  table2.getJSONArray("rowList");
        assert table2RowList.size() ==0;


    }



    @Test
    public void parseId115() {

        JSONObject resultObject = getResultObject(SAMPLE_JSON_OBJECT_FILE_115, SAMPLE_YAML_OBJECT_FILE_1);
        // 表格个数
        JSONArray  tableArray =  resultObject.getJSONArray("tableList");
        assert tableArray.size() == 2;

        //第一个表格
        JSONObject table1 = (JSONObject) tableArray.get(0);
        assert table1 !=null;
        JSONArray tableRowList =  table1.getJSONArray("rowList");
        assert tableRowList.size() == 4; //第一个表格行数

        assert ((JSONArray) tableRowList.get(0)).getJSONObject(0).getString("text").equals("治疗费");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(0).getString("text").equals("中成药费");
        assert ((JSONArray) tableRowList.get(2)).getJSONObject(0).getString("text").equals("艾条灸治疗／");
        assert ((JSONArray) tableRowList.get(3)).getJSONObject(0).getString("text").equals("四妙丸／6g＊24袋");

        assert ((JSONArray) tableRowList.get(2)).getJSONObject(1).getString("text").equals("6／次");
        assert ((JSONArray) tableRowList.get(3)).getJSONObject(1).getString("text").equals("1／盒");

        assert ((JSONArray) tableRowList.get(0)).getJSONObject(2).getString("text").equals("180.00");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(2).getString("text").equals("179.88");
        assert ((JSONArray) tableRowList.get(2)).getJSONObject(2).getString("text").equals("180.0000");
        assert ((JSONArray) tableRowList.get(3)).getJSONObject(2).getString("text").equals("94.8000");


        assert ((JSONArray) tableRowList.get(2)).getJSONObject(3).getString("text").equals("无自付");
        assert ((JSONArray) tableRowList.get(3)).getJSONObject(3).getString("text").equals("无自付");



        //第二个表格
        JSONObject table2 = (JSONObject) tableArray.get(1);
        assert table2 !=null;
        JSONArray table2RowList =  table2.getJSONArray("rowList");
        assert table2RowList.size() ==3;

        assert ((JSONArray) table2RowList.get(0)).getJSONObject(0).getString("text").equals("");
        assert ((JSONArray) table2RowList.get(1)).getJSONObject(0).getString("text").equals("克霉唑阴道片／500mg＊1片／");
        assert ((JSONArray) table2RowList.get(2)).getJSONObject(0).getString("text").equals("皮肤康洗液／50ml");


        assert ((JSONArray) table2RowList.get(0)).getJSONObject(2).getString("text").equals("185.90");
        assert ((JSONArray) table2RowList.get(1)).getJSONObject(2).getString("text").equals("185.9000");
        assert ((JSONArray) table2RowList.get(2)).getJSONObject(2).getString("text").equals("85.0800");


        assert ((JSONArray) table2RowList.get(1)).getJSONObject(3).getString("text").equals("无自付");
        assert ((JSONArray) table2RowList.get(2)).getJSONObject(3).getString("text").equals("有自付");
    }




    @Test
    public void parseId116() {

        JSONObject resultObject = getResultObject(SAMPLE_JSON_OBJECT_FILE_116, SAMPLE_YAML_OBJECT_FILE_1);
        // 表格个数
        JSONArray  tableArray =  resultObject.getJSONArray("tableList");
        assert tableArray.size() == 2;

        //第一个表格
        JSONObject table1 = (JSONObject) tableArray.get(0);
        assert table1 !=null;
        JSONArray tableRowList =  table1.getJSONArray("rowList");
        assert tableRowList.size() == 4; //第一个表格行数

        assert ((JSONArray) tableRowList.get(0)).getJSONObject(0).getString("text").equals("西药费");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(0).getString("text").equals("头孢地尼分散片／0.1g＊12片");
        assert ((JSONArray) tableRowList.get(2)).getJSONObject(0).getString("text").equals("金花清感颗粒／5g＊6袋");
        assert ((JSONArray) tableRowList.get(3)).getJSONObject(0).getString("text").equals("荆花胃康胶丸／80mg＊12粒");

        assert ((JSONArray) tableRowList.get(0)).getJSONObject(1).getString("text").equals("11.63");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(1).getString("text").equals("11.6300");
        assert ((JSONArray) tableRowList.get(2)).getJSONObject(1).getString("text").equals("55.5600");
        assert ((JSONArray) tableRowList.get(3)).getJSONObject(1).getString("text").equals("18.0500");

        assert ((JSONArray) tableRowList.get(0)).getJSONObject(3).getString("text").equals("11.63");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(3).getString("text").equals("11.6300");
        assert ((JSONArray) tableRowList.get(2)).getJSONObject(3).getString("text").equals("166.6800");
        assert ((JSONArray) tableRowList.get(3)).getJSONObject(3).getString("text").equals("36.1000");


        assert ((JSONArray) tableRowList.get(0)).getJSONObject(4).getString("text").equals("");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(4).getString("text").equals("无自付");
        assert ((JSONArray) tableRowList.get(2)).getJSONObject(4).getString("text").equals("无自付");
        assert ((JSONArray) tableRowList.get(3)).getJSONObject(4).getString("text").equals("无自付");



        //第二个表格
        JSONObject table2 = (JSONObject) tableArray.get(1);
        assert table2 !=null;
        JSONArray table2RowList =  table2.getJSONArray("rowList");
        assert table2RowList.size() ==3;

        assert ((JSONArray) table2RowList.get(0)).getJSONObject(0).getString("text").equals("中成药费");
        assert ((JSONArray) table2RowList.get(1)).getJSONObject(0).getString("text").equals("黄连上清胶囊／0.4g＊30粒");
        assert ((JSONArray) table2RowList.get(2)).getJSONObject(0).getString("text").equals("蛇胆川贝液／10ml＊6支");

        assert ((JSONArray) table2RowList.get(0)).getJSONObject(1).getString("text").equals("");
        assert ((JSONArray) table2RowList.get(1)).getJSONObject(1).getString("text").equals("25.0000");
        assert ((JSONArray) table2RowList.get(2)).getJSONObject(1).getString("text").equals("66.0000");

        assert ((JSONArray) table2RowList.get(0)).getJSONObject(3).getString("text").equals("359.78");
        assert ((JSONArray) table2RowList.get(1)).getJSONObject(3).getString("text").equals("25.0000");
        assert ((JSONArray) table2RowList.get(2)).getJSONObject(3).getString("text").equals("132.0000");


        assert ((JSONArray) table2RowList.get(1)).getJSONObject(4).getString("text").equals("无自付");
        assert ((JSONArray) table2RowList.get(2)).getJSONObject(4).getString("text").equals("无自付");
    }


    @Test
    public void parseId117() {

        JSONObject resultObject = getResultObject(SAMPLE_JSON_OBJECT_FILE_117, SAMPLE_YAML_OBJECT_FILE_1);
        // 表格个数
        JSONArray  tableArray =  resultObject.getJSONArray("tableList");
        assert tableArray.size() == 2;

        //第一个表格
        JSONObject table1 = (JSONObject) tableArray.get(0);
        assert table1 !=null;
        JSONArray tableRowList =  table1.getJSONArray("rowList");
        assert tableRowList.size() == 5; //第一个表格行数

        assert ((JSONArray) tableRowList.get(0)).getJSONObject(0).getString("text").equals("西药费");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(0).getString("text").equals("雷贝拉唑钠肠溶胶／10mg＊1");
        assert ((JSONArray) tableRowList.get(2)).getJSONObject(0).getString("text").equals("玻璃酸钠滴眼液／5mg：5ml");
        assert ((JSONArray) tableRowList.get(3)).getJSONObject(0).getString("text").equals("连花清瘟颗粒／6g＊10袋");
        assert ((JSONArray) tableRowList.get(4)).getJSONObject(0).getString("text").equals("双黄连颗粒（无蔗糖／5g＊6袋");



        assert ((JSONArray) tableRowList.get(0)).getJSONObject(4).getString("text").equals("");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(4).getString("text").equals("有自付");
        assert ((JSONArray) tableRowList.get(2)).getJSONObject(4).getString("text").equals("无自付");
        assert ((JSONArray) tableRowList.get(3)).getJSONObject(4).getString("text").equals("无自付");
        assert ((JSONArray) tableRowList.get(4)).getJSONObject(4).getString("text").equals("无自付");



        //第二个表格
        JSONObject table2 = (JSONObject) tableArray.get(1);
        assert table2 !=null;
        JSONArray table2RowList =  table2.getJSONArray("rowList");
        assert table2RowList.size() ==5;

        assert ((JSONArray) table2RowList.get(0)).getJSONObject(0).getString("text").equals("中成药费");
        assert ((JSONArray) table2RowList.get(1)).getJSONObject(0).getString("text").equals("阿莫西林克拉维酸／0.1875,22.4000");
        assert ((JSONArray) table2RowList.get(2)).getJSONObject(0).getString("text").equals("清肺消炎丸／5g＊6袋");
        assert ((JSONArray) table2RowList.get(3)).getJSONObject(0).getString("text").equals("穿心莲内酯滴丸／0.6g＊15袋20.0300");
        assert ((JSONArray) table2RowList.get(4)).getJSONObject(0).getString("text").equals("苏黄止咳胶囊／0.45g＊24粒");



        assert ((JSONArray) table2RowList.get(0)).getJSONObject(3).getString("text").equals("808.43");
        assert ((JSONArray) table2RowList.get(1)).getJSONObject(3).getString("text").equals("44.8000");
        assert ((JSONArray) table2RowList.get(2)).getJSONObject(3).getString("text").equals("304.3800");
        assert ((JSONArray) table2RowList.get(3)).getJSONObject(3).getString("text").equals("23.0300");
        assert ((JSONArray) table2RowList.get(4)).getJSONObject(3).getString("text").equals("230.3100");


        assert ((JSONArray) table2RowList.get(1)).getJSONObject(4).getString("text").equals("无自付");
        assert ((JSONArray) table2RowList.get(2)).getJSONObject(4).getString("text").equals("有自付");
        assert ((JSONArray) table2RowList.get(3)).getJSONObject(4).getString("text").equals("有自付");
        assert ((JSONArray) table2RowList.get(4)).getJSONObject(4).getString("text").equals("有自付");
    }


    @Test
    public void parseId123() {

        JSONObject resultObject = getResultObject(SAMPLE_JSON_OBJECT_FILE_123, SAMPLE_YAML_OBJECT_FILE_1);
        // 表格个数
        JSONArray  tableArray =  resultObject.getJSONArray("tableList");
        assert tableArray.size() == 2;

        //第一个表格
        JSONObject table1 = (JSONObject) tableArray.get(0);
        assert table1 !=null;
        JSONArray tableRowList =  table1.getJSONArray("rowList");
        assert tableRowList.size() == 2; //第一个表格行数

        assert ((JSONArray) tableRowList.get(0)).getJSONObject(0).getString("text").equals("西药费");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(0).getString("text").equals("盐酸氨基葡萄糖胶／（普力得");

        assert ((JSONArray) tableRowList.get(1)).getJSONObject(1).getString("text").equals("219.000");



        assert ((JSONArray) tableRowList.get(0)).getJSONObject(3).getString("text").equals("366.00");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(3).getString("text").equals("219.0000");

        assert ((JSONArray) tableRowList.get(1)).getJSONObject(4).getString("text").equals("无自付");



        //第二个表格
        JSONObject table2 = (JSONObject) tableArray.get(1);
        assert table2 !=null;
        JSONArray table2RowList =  table2.getJSONArray("rowList");
        assert table2RowList.size() ==1;

        assert ((JSONArray) table2RowList.get(0)).getJSONObject(0).getString("text").equals("艾瑞昔布片／（恒扬）（0.1GM：49.0000");
    }


    @Test
    public void parseId135() {

        JSONObject resultObject = getResultObject(SAMPLE_JSON_OBJECT_FILE_135, SAMPLE_YAML_OBJECT_FILE_02);
        // 表格个数
        JSONArray  tableArray =  resultObject.getJSONArray("tableList");
        assert tableArray.size() == 2;

        //第一个表格
        JSONObject table1 = (JSONObject) tableArray.get(0);
        assert table1 !=null;
        JSONArray tableRowList =  table1.getJSONArray("rowList");
        assert tableRowList.size() == 2; //第一个表格行数

        assert ((JSONArray) tableRowList.get(0)).getJSONObject(0).getString("text").equals("检查费");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(0).getString("text").equals("卫生材料费");

        assert ((JSONArray) tableRowList.get(0)).getJSONObject(2).getString("text").equals("11.00");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(2).getString("text").equals("1.85");


        //第二个表格
        JSONObject table2 = (JSONObject) tableArray.get(1);
        assert table2 !=null;
        JSONArray table2RowList =  table2.getJSONArray("rowList");
        assert table2RowList.size() ==2;

        assert ((JSONArray) table2RowList.get(0)).getJSONObject(0).getString("text").equals("化验费");
        assert ((JSONArray) table2RowList.get(1)).getJSONObject(0).getString("text").equals("中草药费");


        assert ((JSONArray) table2RowList.get(0)).getJSONObject(2).getString("text").equals("105.00");
        assert ((JSONArray) table2RowList.get(1)).getJSONObject(2).getString("text").equals("341.95");
    }


    @Test
    public void parseId136() {

        JSONObject resultObject = getResultObject(SAMPLE_JSON_OBJECT_FILE_136, SAMPLE_YAML_OBJECT_FILE_02);
        // 表格个数
        JSONArray  tableArray =  resultObject.getJSONArray("tableList");
        assert tableArray.size() == 2;

        //第一个表格
        JSONObject table1 = (JSONObject) tableArray.get(0);
        assert table1 !=null;
        JSONArray tableRowList =  table1.getJSONArray("rowList");
        assert tableRowList.size() == 5; //第一个表格行数

        assert ((JSONArray) tableRowList.get(0)).getJSONObject(0).getString("text").equals("检查费");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(0).getString("text").equals("卫生材料费");
        assert ((JSONArray) tableRowList.get(2)).getJSONObject(0).getString("text").equals("阴道检查／");
        assert ((JSONArray) tableRowList.get(3)).getJSONObject(0).getString("text").equals("细菌性阴道病唾液酸酶测／");
        assert ((JSONArray) tableRowList.get(4)).getJSONObject(0).getString("text").equals("一次性使用无菌阴／北京米时路");

        assert ((JSONArray) tableRowList.get(0)).getJSONObject(2).getString("text").equals("11.00");
        assert ((JSONArray) tableRowList.get(1)).getJSONObject(2).getString("text").equals("1.85");
        assert ((JSONArray) tableRowList.get(2)).getJSONObject(2).getString("text").equals("5.0000");
        assert ((JSONArray) tableRowList.get(3)).getJSONObject(2).getString("text").equals("100.0000");
        assert ((JSONArray) tableRowList.get(4)).getJSONObject(2).getString("text").equals("1.8500");




        assert ((JSONArray) tableRowList.get(2)).getJSONObject(3).getString("text").equals("无自付");
        assert ((JSONArray) tableRowList.get(3)).getJSONObject(3).getString("text").equals("无自付");
        assert ((JSONArray) tableRowList.get(4)).getJSONObject(3).getString("text").equals("无自付");



        //第二个表格
        JSONObject table2 = (JSONObject) tableArray.get(1);
        assert table2 !=null;
        JSONArray table2RowList =  table2.getJSONArray("rowList");
        assert table2RowList.size() ==3;

        assert ((JSONArray) table2RowList.get(0)).getJSONObject(0).getString("text").equals("化验费");
        assert ((JSONArray) table2RowList.get(1)).getJSONObject(0).getString("text").equals("骨盆内诊／");
        assert ((JSONArray) table2RowList.get(2)).getJSONObject(0).getString("text").equals("阴道分泌物检查／人工业微镜检潢");


        assert ((JSONArray) table2RowList.get(0)).getJSONObject(2).getString("text").equals("105.00");
        assert ((JSONArray) table2RowList.get(1)).getJSONObject(2).getString("text").equals("6.0000");
        assert ((JSONArray) table2RowList.get(2)).getJSONObject(2).getString("text").equals("5.0000");
    }
}