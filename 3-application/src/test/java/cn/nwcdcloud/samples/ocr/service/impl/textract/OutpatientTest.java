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

    private static final String  SAMPLE_JSON_OBJECT_FILE_1 =  "/sample/outpatient001.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_2 =  "/sample/outpatient002.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_3 =  "/sample/outpatient003.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_4 =  "/sample/outpatient004.json";
    private static final String  SAMPLE_YAML_OBJECT_FILE_1 =  "outpatient";
    private static final String  SAMPLE_YAML_OBJECT_FILE_2 =  "hospital";


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
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "社会保障号码", "126776699001");

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
        assert tableRowList.size() ==2;

        JSONArray cell1List = (JSONArray) tableRowList.get(0);
        assert  cell1List.size() == 5;
        logger.info(cell1List.getJSONObject(0).getString("text"));
        assert cell1List.getJSONObject(0).getString("text").equals("化验费");

        JSONArray cell2List = (JSONArray) tableRowList.get(1);
        logger.info(cell2List.getJSONObject(0).getString("text"));
        assert cell2List.getJSONObject(0).getString("text").equals("C-反应蛋白（CRP）测定/次");


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
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "社会保障号码", "103961136014");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "合计（大写）", "陆佰登拾壹元壹角肆分");

        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "门诊大额支付", "427.80本次医保范围内金额");
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



}