package cn.nwcdcloud.samples.ocr.service.impl.textract;

import java.io.FileNotFoundException;

import cn.nwcdcloud.samples.ocr.parse.FileUtils;
import cn.nwcdcloud.samples.ocr.parse.BlockItemUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdServiceImplTest {

    private static final Logger logger = LoggerFactory.getLogger(IdServiceImplTest.class);

    private static final String  ID_SAMPLE_JSON_OBJECT_FILE =  "/sample/id001.json";
    private static final String  ID_SAMPLE_JSON_ARRAY_FILE =  "/sample/id001_array.json";

    @Before
    public void setUp() throws Exception {

        logger.info("-------------- setUp");
        String jsonArrayPath=this.getClass().getResource(ID_SAMPLE_JSON_ARRAY_FILE).getFile().toString();
        JSONArray jsonArray = FileUtils.readJsonArray(jsonArrayPath);//前面两行是读取文件
        logger.info("-------------- jsonArray size: " + jsonArray.size());
        assert (jsonArray.size()>=1);




        String jsonObjectPath=this.getClass().getResource(ID_SAMPLE_JSON_OBJECT_FILE).getFile().toString();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);

        assert (jsonObject != null);



    }

    @Test
    public void parse() {

//
        String jsonObjectPath=this.getClass().getResource(ID_SAMPLE_JSON_OBJECT_FILE).getFile().toString();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
        BlockItemUtils.getBlockItemList(jsonObject, 1200, 2000);
    }

    @Test
    public void readConfig() throws FileNotFoundException {
        logger.info("-------------- readConfig");


    }
}