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


public class FixedPositionTest {
    private static final Logger logger = LoggerFactory.getLogger(FixedPositionTest.class);

    private static final String  SAMPLE_JSON_OBJECT_FILE_1 =  "/samples/fixed_position_01.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_2 =  "/samples/fixed_position_02.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_3 =  "/samples/fixed_position_03.json";


    @Test
    public void parse01() {

        String jsonObjectPath= this.getClass().getResource(SAMPLE_JSON_OBJECT_FILE_1).getFile().toString();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
        List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject);
        ParseFactory parseJsonUtil = new ParseFactory("fixed_position");
        JSONObject resultObject = parseJsonUtil.extractValue(blockItemList);
//        logger.info(resultObject.toJSONString());
        JSONArray keyValueList = resultObject.getJSONArray("keyValueList");
        logger.info(keyValueList.toJSONString());

        for(int i=0; i< keyValueList.size(); i++){
            logger.info(keyValueList.get(i).toString());
        }

        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "始发站", "北京南站");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "终点站", "上海站");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "车次", "G5");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "开车时间", "2021年04月25日07:00开");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "价格", "￥604.0元");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "座位号", "10车15F号");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "身份证&姓名", "5111241982****3413张岭惠");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "单号", "10010301040427D000534北京南");

    }


    @Test
    public void parse02() {

        String jsonObjectPath= this.getClass().getResource(SAMPLE_JSON_OBJECT_FILE_2).getFile().toString();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
        List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject);
        ParseFactory parseJsonUtil = new ParseFactory("fixed_position");
        JSONObject resultObject = parseJsonUtil.extractValue(blockItemList);
//        logger.info(resultObject.toJSONString());
        JSONArray keyValueList = resultObject.getJSONArray("keyValueList");
        logger.info(keyValueList.toJSONString());

        for(int i=0; i< keyValueList.size(); i++){
            logger.info(keyValueList.get(i).toString());
        }

        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "始发站", "扬州东站");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "终点站", "北京南站");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "车次", "G880");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "开车时间", "2021年04月29日14：54开");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "价格", "￥449.0元");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "座位号", "05车020号");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "身份证&姓名", "1404241981****001X张良");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "单号", "17334300290430A029342扬州东售");


    }


    @Test
    public void parse03() {

        String jsonObjectPath= this.getClass().getResource(SAMPLE_JSON_OBJECT_FILE_3).getFile().toString();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
        List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject);
        ParseFactory parseJsonUtil = new ParseFactory( "fixed_position");
        JSONObject resultObject = parseJsonUtil.extractValue(blockItemList);
//        logger.info(resultObject.toJSONString());
        JSONArray keyValueList = resultObject.getJSONArray("keyValueList");
        logger.info(keyValueList.toJSONString());

        for(int i=0; i< keyValueList.size(); i++){
            logger.info(keyValueList.get(i).toString());
        }

        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "始发站", "北京南站");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "终点站", "杭州东站");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "车次", "G165");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "开车时间", "2021年05月18日08:30开");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "价格", "￥581.5元");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "座位号", "15车10B号");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "身份证&姓名", "1101051984****8614刘禹");
        assert  BlockItemUtils.checkKeyValueMap(keyValueList, "单号", "10010310680519T064144 北京南");


    }


}