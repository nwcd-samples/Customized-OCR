package cn.nwcdcloud.samples.ocr.service.impl.textract;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.nwcdcloud.samples.ocr.parse.BlockItemUtils;
import cn.nwcdcloud.samples.ocr.parse.ConfigConstants;
import cn.nwcdcloud.samples.ocr.parse.FileUtils;

public class IdServiceImplTest {

    private static final Logger logger = LoggerFactory.getLogger(IdServiceImplTest.class);

    private static final String  ID_SAMPLE_JSON_OBJECT_FILE =  "/samples/id001.json";
    private static final String  ID_SAMPLE_JSON_ARRAY_FILE =  "/samples/id001_array.json";

    @Test
    public void setUp() throws Exception {

        String jsonArrayPath=this.getClass().getResource(ID_SAMPLE_JSON_ARRAY_FILE).getFile().toString();
        JSONArray jsonArray = FileUtils.readJsonArray(jsonArrayPath);//前面两行是读取文件
        assert (jsonArray.size()>=1);

        String jsonObjectPath=this.getClass().getResource(ID_SAMPLE_JSON_OBJECT_FILE).getFile().toString();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);

        assert (jsonObject != null);
    }

    @Test
    public void parse() {

        String jsonObjectPath=this.getClass().getResource(ID_SAMPLE_JSON_OBJECT_FILE).getFile().toString();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
        BlockItemUtils.getBlockItemList(jsonObject);
    }
}