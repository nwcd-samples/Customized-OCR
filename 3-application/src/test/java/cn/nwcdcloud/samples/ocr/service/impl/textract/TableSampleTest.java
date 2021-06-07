package cn.nwcdcloud.samples.ocr.service.impl.textract;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.nwcdcloud.samples.ocr.parse.BlockItemUtils;
import cn.nwcdcloud.samples.ocr.parse.ConfigConstants;
import cn.nwcdcloud.samples.ocr.parse.FileUtils;
import cn.nwcdcloud.samples.ocr.parse.ParseFactory;


public class TableSampleTest {
    private static final Logger logger = LoggerFactory.getLogger(TableSampleTest.class);

    private static final String  SAMPLE_JSON_OBJECT_FILE_1 =  "/sample/table_sample.json";
    private static final String  CONFIG_FILE_PATH =  "table_sample" ;
    @Test
    public void parseId001() {


        String jsonObjectPath= this.getClass().getResource(SAMPLE_JSON_OBJECT_FILE_1).getFile().toString();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
        List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject);
        ParseFactory parseJsonUtil = new ParseFactory(ConfigConstants.PAGE_WIDTH, ConfigConstants.PAGE_HEIGHT, CONFIG_FILE_PATH);
        JSONObject resultObject = parseJsonUtil.extractValue(blockItemList);
        JSONArray  resultArray =  resultObject.getJSONArray("keyValueList");
        JSONArray  tableArray =  resultObject.getJSONArray("tableList");
        logger.info("   {} ", tableArray.toJSONString());

        JSONObject table = (JSONObject) tableArray.get(0);
        assert "结算费用单明细".equals(table.getString("name"));
        assert 5 == table.getInteger("rowCount");
        assert 4 == table.getInteger("columnCount");
        assert 4 == table.getJSONArray("heads").size();

        JSONArray rowList = table.getJSONArray("rowList");
        assert 5 == rowList.size();

        JSONArray cellArray= (JSONArray) rowList.get(1);
        JSONObject cell = (JSONObject) cellArray.get(1);
        logger.info(cell.toJSONString());
        assert "信用卡手续费-内-货扣".equals(cell.getString("text"));


        logger.info(resultObject.toJSONString());

    }





}