package cn.nwcdcloud.samples.ocr.service.impl.textract;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.nwcdcloud.samples.ocr.parse.BlockItemUtils;
import cn.nwcdcloud.samples.ocr.parse.FileUtils;
import cn.nwcdcloud.samples.ocr.parse.ParseFactory;
import org.springframework.util.StringUtils;


public class RangeTest {
    private static final Logger logger = LoggerFactory.getLogger(RangeTest.class);

    private static final String  SAMPLE_JSON_OBJECT_FILE_1 =  "/sample/range.json";


    @Test
    public void parseId001() {

        String jsonObjectPath= this.getClass().getResource(SAMPLE_JSON_OBJECT_FILE_1).getFile();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
        List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject);
        ParseFactory parseJsonUtil = new ParseFactory( "range");
        JSONObject resultObject = parseJsonUtil.extractValue(blockItemList);
        logger.info(resultObject.toJSONString());
        JSONArray keyValueList = resultObject.getJSONArray("keyValueList");
        logger.info(keyValueList.toJSONString());
        assert keyValueList.size()>0;

        JSONObject object = (JSONObject) keyValueList.get(0);
        logger.info(object.toJSONString());
        assert "180.00".equals(object.getString("value"));


    }


    @Test
    public void testRe(){


//        String str = "0.80 本次医保";

        assert "800".equals(BlockItemUtils.getItemNumericalValue("800 "));
        assert "800".equals(BlockItemUtils.getItemNumericalValue(" 800"));
        assert "800".equals(BlockItemUtils.getItemNumericalValue("800 本次医保"));
        assert "800".equals(BlockItemUtils.getItemNumericalValue("本次医保 800"));


        assert "8.00".equals(BlockItemUtils.getItemNumericalValue("8.00 "));
        assert "80.0".equals(BlockItemUtils.getItemNumericalValue(" 80.0"));
        assert "-80.0".equals(BlockItemUtils.getItemNumericalValue("-80.0 本次医保"));
        assert "-8.00".equals(BlockItemUtils.getItemNumericalValue("本次医保 -8.00"));



        assert "8.00".equals(BlockItemUtils.getItemNumericalValue("8，00 "));
        assert "80.0".equals(BlockItemUtils.getItemNumericalValue(" 80,0"));
        assert "-80.0".equals(BlockItemUtils.getItemNumericalValue("-80。0 本次医保"));
        assert "-8.00".equals(BlockItemUtils.getItemNumericalValue("本次医保 -8。00"));

        assert "-8.00".equals(BlockItemUtils.getItemNumericalValue("本次医保-8。00"));
        assert "80.0".equals(BlockItemUtils.getItemNumericalValue("本次医保80,0"));
        assert "0.002".equals(BlockItemUtils.getItemNumericalValue("0.002ABCD"));
        assert "0.012".equals(BlockItemUtils.getItemNumericalValue("ABCD0.012"));




    }


    @Test
    public void testRetainFixedLength(){
        assert "5.0000".equals(BlockItemUtils.getItemNumericalValue("5.00001"));
        assert "12.325.0000".equals(BlockItemUtils.getItemNumericalValue("12.325.0000123"));

    }

}