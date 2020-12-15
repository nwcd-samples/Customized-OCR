package cn.nwcdcloud.samples.ocr.service.impl.textract;

import cn.nwcdcloud.commons.utils.BlockItemUtil;
import cn.nwcdcloud.commons.utils.FileUtils;
import cn.nwcdcloud.commons.utils.ParseJsonUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.Assert.*;


public class CommonServiceImplTest {
    private static final Logger logger = LoggerFactory.getLogger(CommonServiceImplTest.class);

    @Resource
    CommonServiceImpl commonServiceImpl;

    private static final String  ID_SAMPLE_JSON_OBJECT_FILE =  "/sample/id001.json";
    private static final String  ID_SAMPLE_JSON_ARRAY_FILE =  "/sample/id001_array.json";

    @Test
    public void parse() {
        String jsonArrayPath=this.getClass().getResource(ID_SAMPLE_JSON_ARRAY_FILE).getFile().toString();
        JSONArray jsonArray = FileUtils.readJsonArray(jsonArrayPath);//前面两行是读取文件
//        commonServiceImpl.parse(jsonArray);

        String jsonObjectPath=this.getClass().getResource(ID_SAMPLE_JSON_OBJECT_FILE).getFile().toString();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
        List<JSONObject> blockItemList = BlockItemUtil.getBlockItemList(jsonObject, 1124, 800);
        ParseJsonUtil parseJsonUtil = new ParseJsonUtil();
        parseJsonUtil.extractValue(blockItemList);



    }
}