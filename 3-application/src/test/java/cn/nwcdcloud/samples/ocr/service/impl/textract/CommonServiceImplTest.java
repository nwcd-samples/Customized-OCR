package cn.nwcdcloud.samples.ocr.service.impl.textract;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.nwcdcloud.samples.ocr.commons.util.BlockItemUtils;
import cn.nwcdcloud.samples.ocr.commons.util.FileUtils;
import cn.nwcdcloud.samples.ocr.commons.util.ParseJsonUtils;


public class CommonServiceImplTest {
    private static final Logger logger = LoggerFactory.getLogger(CommonServiceImplTest.class);

    @Resource
    CommonServiceImpl commonServiceImpl;

    private static final String  ID_SAMPLE_JSON_OBJECT_FILE =  "/sample/id002.json";
    private static final String  ID_SAMPLE_JSON_ARRAY_FILE =  "/sample/id001_array.json";

    @Test
    public void parse() {
        String jsonArrayPath=this.getClass().getResource(ID_SAMPLE_JSON_ARRAY_FILE).getFile().toString();
        JSONArray jsonArray = FileUtils.readJsonArray(jsonArrayPath);//前面两行是读取文件
//        commonServiceImpl.parse(jsonArray);

        String jsonObjectPath=this.getClass().getResource(ID_SAMPLE_JSON_OBJECT_FILE).getFile().toString();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
        List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject, 1124, 800);
        ParseJsonUtils parseJsonUtil = new ParseJsonUtils(1124, 800, blockItemList);
        JSONArray  resultArray =  parseJsonUtil.extractValue(blockItemList);

        logger.info(resultArray.toJSONString());


    }
}