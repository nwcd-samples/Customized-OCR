package cn.nwcdcloud.samples.ocr.service.impl.textract;

import java.util.List;

import cn.nwcdcloud.samples.ocr.parse.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.springframework.util.StringUtils;


public class RangeTest {
    private static final Logger logger = LoggerFactory.getLogger(RangeTest.class);

    private static final String  SAMPLE_JSON_OBJECT_FILE_1 =  "/samples/range.json";


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
    public void testNumericalValue(){


//        String str = "0.80 本次医保";

        int direction = 0;
        assert "800".equals(ParseUtils.processBlockValue("number", "800 ", direction));
        assert "800".equals(ParseUtils.processBlockValue("number", " 800", direction));
        assert "800".equals(ParseUtils.processBlockValue("number", "800 本次医保", direction));
        assert "800".equals(ParseUtils.processBlockValue("number", "本次医保 800", direction));


        assert "8.00".equals(ParseUtils.processBlockValue("number", "8.00 ", direction));
        assert "80.0".equals(ParseUtils.processBlockValue("number", " 80.0", direction));
        assert "-80.0".equals(ParseUtils.processBlockValue("number", "-80.0 本次医保", direction));
        assert "-8.00".equals(ParseUtils.processBlockValue("number", "本次医保 -8.00", direction));



        assert "8.00".equals(ParseUtils.processBlockValue("number", "8。00 ", direction));
        assert "80.0".equals(ParseUtils.processBlockValue("number", " 80.0", direction));
        assert "-80.0".equals(ParseUtils.processBlockValue("number", "-80。0 本次医保", direction));
        assert "-8.00".equals(ParseUtils.processBlockValue("number", "本次医保 -8。00", direction));

        assert "-8.00".equals(ParseUtils.processBlockValue("number", "本次医保-8。00", direction));
        assert "80.0".equals(ParseUtils.processBlockValue("number", "本次医保80.0", direction));
        assert "0.002".equals(ParseUtils.processBlockValue("number", "0.002ABCD", direction));
        assert "0.012".equals(ParseUtils.processBlockValue("number", "ABCD0.012", direction));




    }


    @Test
    public void testRetainFixedLength(){
        int direction = ConfigConstants.PARSE_TABLE_CELL_VALUE_DIRECTION_FROM_LEFT;
        assert "5.0000".equals(ParseUtils.processBlockValue("number", "5.00001", direction));
        assert "12.325.0000".equals(ParseUtils.processBlockValue("number", "12.325.0000123", direction));

    }


    @Test
    public void testStringValue(){
        int direction = ConfigConstants.PARSE_TABLE_CELL_VALUE_DIRECTION_FROM_LEFT;
        assert "你好".equals(ParseUtils.processBlockValue("word", "你好 5.00001", direction));
        assert "你好".equals(ParseUtils.processBlockValue("word", "你好 5.00001  我们", direction));
        assert "你好".equals(ParseUtils.processBlockValue("word", "你好5.00001", direction));
    }

    @Test
    public void testCheckParseCellValueDirection(){
        int direction = ConfigConstants.PARSE_TABLE_CELL_VALUE_DIRECTION_FROM_RIGHT;
        assert "我们".equals(ParseUtils.processBlockValue("word", "你好 5.00001 我们", direction));
        assert "我们".equals(ParseUtils.processBlockValue("word", "你好 5.00001  我们", direction));
        assert "你好".equals(ParseUtils.processBlockValue("word", "你好5.00001", direction));


        assert "你好5.00001".equals(ParseUtils.processBlockValue("string", "你好5.00001", direction));

        assert "5.0000".equals(ParseUtils.processBlockValue("number", "你好 5.00001", direction));
        assert "5.0000".equals(ParseUtils.processBlockValue("number", "你好 5.00001我们", direction));
    }

    @Test
    public void testRemainNumberString(){

        assert "9.10".equals(ParseUtils.remainNumberString("23 hello 9.10", ConfigConstants.PARSE_TABLE_CELL_VALUE_DIRECTION_FROM_RIGHT));
        assert ".".equals(ParseUtils.remainNumberString("abcd.ab", ConfigConstants.PARSE_TABLE_CELL_VALUE_DIRECTION_FROM_RIGHT));
        assert "239.10".equals(ParseUtils.remainNumberString("239.10", ConfigConstants.PARSE_TABLE_CELL_VALUE_DIRECTION_FROM_RIGHT));
        assert "9.10".equals(ParseUtils.remainNumberString("234.22hello 9.10 abc", ConfigConstants.PARSE_TABLE_CELL_VALUE_DIRECTION_FROM_RIGHT));


        assert "234.22".equals(ParseUtils.remainNumberString("234.22hello 9.10 abc", ConfigConstants.PARSE_TABLE_CELL_VALUE_DIRECTION_FROM_LEFT));
        assert "234.22".equals(ParseUtils.remainNumberString("234.22h", ConfigConstants.PARSE_TABLE_CELL_VALUE_DIRECTION_FROM_LEFT));
        assert "234.22".equals(ParseUtils.remainNumberString("abc234.22", ConfigConstants.PARSE_TABLE_CELL_VALUE_DIRECTION_FROM_LEFT));
        assert "234.22".equals(ParseUtils.remainNumberString("abc 234.22", ConfigConstants.PARSE_TABLE_CELL_VALUE_DIRECTION_FROM_LEFT));

        assert "234".equals(ParseUtils.remainNumberString("abc 234bb 23.23", ConfigConstants.PARSE_TABLE_CELL_VALUE_DIRECTION_FROM_LEFT));
        assert "234.0001".equals(ParseUtils.remainNumberString("abc 234.0001bb 23.23", ConfigConstants.PARSE_TABLE_CELL_VALUE_DIRECTION_FROM_LEFT));
        assert "23.23".equals(ParseUtils.remainNumberString("abc 234.0001bb 23.23", ConfigConstants.PARSE_TABLE_CELL_VALUE_DIRECTION_FROM_RIGHT));

        assert "234.90".equals(ParseUtils.remainNumberString("234.90", ConfigConstants.PARSE_TABLE_CELL_VALUE_DIRECTION_FROM_LEFT));

        assert "234.90".equals(ParseUtils.remainNumberString("a234.90", ConfigConstants.PARSE_TABLE_CELL_VALUE_DIRECTION_FROM_RIGHT));
        assert "234.90".equals(ParseUtils.remainNumberString("a234.90", ConfigConstants.PARSE_TABLE_CELL_VALUE_DIRECTION_FROM_LEFT));

        assert "234.90".equals(ParseUtils.remainNumberString("234.90baa", ConfigConstants.PARSE_TABLE_CELL_VALUE_DIRECTION_FROM_RIGHT));
        assert "234.90".equals(ParseUtils.remainNumberString("234.90baa", ConfigConstants.PARSE_TABLE_CELL_VALUE_DIRECTION_FROM_LEFT));

    }

//
}