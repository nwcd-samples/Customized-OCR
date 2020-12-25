package cn.nwcdcloud.samples.ocr.service.impl.textract;

import java.util.List;

import javax.annotation.Resource;

import cn.nwcdcloud.samples.ocr.enums.DocumentTypeEnum;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.nwcdcloud.samples.ocr.commons.util.BlockItemUtils;
import cn.nwcdcloud.samples.ocr.commons.util.FileUtils;
import cn.nwcdcloud.samples.ocr.commons.util.ParseJsonWorker;


public class CommonServiceImplTest {
    private static final Logger logger = LoggerFactory.getLogger(CommonServiceImplTest.class);

    @Resource
    CommonServiceImpl commonServiceImpl;

    private static final String  ID_SAMPLE_JSON_OBJECT_FILE_1 =  "/sample/id001.json";
    private static final String  ID_SAMPLE_JSON_OBJECT_FILE_2 =  "/sample/id002.json";
    private static final String  ID_SAMPLE_JSON_OBJECT_FILE_3 =  "/sample/id003.json";
    private static final String  ID_SAMPLE_JSON_ARRAY_FILE =  "/sample/id001_array.json";

    @Test
    public void parse() {
//        String jsonArrayPath=this.getClass().getResource(ID_SAMPLE_JSON_ARRAY_FILE).getFile().toString();
//        JSONArray jsonArray = FileUtils.readJsonArray(jsonArrayPath);//前面两行是读取文件
////        commonServiceImpl.parse(jsonArray);
//
//        String jsonObjectPath=this.getClass().getResource(ID_SAMPLE_JSON_OBJECT_FILE_3).getFile().toString();
//        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
//        List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject, 1124, 800);
//        ParseJsonUtils parseJsonUtil = new ParseJsonUtils(1124, 800, blockItemList);
//        JSONArray  resultArray =  parseJsonUtil.extractValue(blockItemList);
//
//        logger.info(resultArray.toJSONString());
//

    }

    @Test
    public void parseId001() {

        String jsonObjectPath=this.getClass().getResource(ID_SAMPLE_JSON_OBJECT_FILE_1).getFile().toString();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
        List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject, 1124, 800);
        ParseJsonWorker parseJsonUtil = new ParseJsonWorker(1124, 800, blockItemList, DocumentTypeEnum.IDENTITY_CARD.getPath());
        JSONArray  resultArray =  parseJsonUtil.extractValue(blockItemList);
        logger.info(resultArray.toJSONString());
        assert  checkKeyValueMap(resultArray, "姓名", "代用名");
        assert  checkKeyValueMap(resultArray, "性别", "男");
        assert  checkKeyValueMap(resultArray, "民族", "汉");
        assert  checkKeyValueMap(resultArray, "出生", "2013年05月06日");
        assert  checkKeyValueMap(resultArray, "住址", "湖南省长沙市开福区巡道街幸福小区居民组");
        assert  checkKeyValueMap(resultArray, "公民身份号码", "430512198908131367");

    }


    @Test
    public void parseId002() {

        String jsonObjectPath=this.getClass().getResource(ID_SAMPLE_JSON_OBJECT_FILE_2).getFile().toString();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
        List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject, 1124, 800);
        ParseJsonWorker parseJsonUtil = new ParseJsonWorker(1124, 800, blockItemList, DocumentTypeEnum.IDENTITY_CARD.getPath());
        JSONArray  resultArray =  parseJsonUtil.extractValue(blockItemList);
        logger.info(resultArray.toJSONString());
        assert  checkKeyValueMap(resultArray, "姓名", "李四");
        assert  checkKeyValueMap(resultArray, "性别", "男");
        assert  checkKeyValueMap(resultArray, "民族", "汉");
        assert  checkKeyValueMap(resultArray, "出生", "1992年9月10日");
        assert  checkKeyValueMap(resultArray, "住址", "北京市海淀区银网中心1号楼2202号");
        assert  checkKeyValueMap(resultArray, "公民身份号码", "21112419920910123");

    }


    @Test
    public void parseId003() {

        String jsonObjectPath=this.getClass().getResource(ID_SAMPLE_JSON_OBJECT_FILE_3).getFile().toString();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
        List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject, 1124, 800);
        ParseJsonWorker parseJsonUtil = new ParseJsonWorker(1124, 800, blockItemList, DocumentTypeEnum.IDENTITY_CARD.getPath());
        JSONArray  resultArray =  parseJsonUtil.extractValue(blockItemList);
        logger.info(resultArray.toJSONString());
        assert  checkKeyValueMap(resultArray, "姓名", "韦小宝");
        assert  checkKeyValueMap(resultArray, "性别", "男");
        assert  checkKeyValueMap(resultArray, "民族", "汉");
        assert  checkKeyValueMap(resultArray, "住址", "北京市东城区景山前街4号紫禁城敬事房");

        assert  checkKeyValueMap(resultArray, "出生", "1654年12月20日");
        assert  checkKeyValueMap(resultArray, "公民身份号码", "112044165412202438");

    }

    private boolean checkKeyValueMap(JSONArray array, String name, String value){
        for(int i=0; i< array.size(); i++){
            String tempValue = array.getJSONObject(i).getString("value");
            String tempName = array.getJSONObject(i).getString("name");
//            logger.info("{}        {}    {} ", key, value, tempValue);
            if(value.equals(tempValue)  &&  name.equals(tempName)){
                return true;
            }
        }
        return false;

    }



}