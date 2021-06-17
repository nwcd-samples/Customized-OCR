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

    private static final String  SAMPLE_JSON_OBJECT_FILE_1 =  "/sample/outpatient/001.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_2 =  "/sample/outpatient/002.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_3 =  "/sample/outpatient/003.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_4 =  "/sample/outpatient/004.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_5 =  "/sample/outpatient/005.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_6 =  "/sample/outpatient/006.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_7 =  "/sample/outpatient/007.json";
    private static final String  SAMPLE_YAML_OBJECT_FILE_1 =  "outpatient";


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


    @Test
    public void parseId007() {

        String jsonObjectPath= this.getClass().getResource(SAMPLE_JSON_OBJECT_FILE_7).getFile();
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
        assert tableRowList.size() ==10;

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


}