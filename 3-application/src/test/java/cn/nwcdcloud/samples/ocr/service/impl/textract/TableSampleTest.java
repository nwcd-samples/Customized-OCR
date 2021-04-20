package cn.nwcdcloud.samples.ocr.service.impl.textract;

import cn.nwcdcloud.samples.ocr.parse.BlockItemUtils;
import cn.nwcdcloud.samples.ocr.parse.FileUtils;
import cn.nwcdcloud.samples.ocr.parse.ParseFactory;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.List;


public class TableSampleTest {
    private static final Logger logger = LoggerFactory.getLogger(TableSampleTest.class);

    @Resource
    CommonServiceImpl commonServiceImpl;

    private static final String  SAMPLE_JSON_OBJECT_FILE_1 =  "/sample/table_sample.json";
//    private static final String  SAMPLE_JSON_OBJECT_FILE_2 =  "/sample/cost_table002.json";



    @Test
    public void parseId001() {


        String jsonObjectPath= this.getClass().getResource(SAMPLE_JSON_OBJECT_FILE_1).getFile().toString();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
        List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject, 1200, 2000);
        ParseFactory parseJsonUtil = new ParseFactory(1200, 2000, blockItemList, "config/table_sample.yaml");
        JSONObject resultObject = parseJsonUtil.extractValue(blockItemList);
        JSONArray  resultArray =  resultObject.getJSONArray("keyValueList");
        JSONArray  tableArray =  resultObject.getJSONArray("tableList");
        logger.info("   {} ", tableArray.toJSONString());

        JSONObject table = (JSONObject) tableArray.get(0);
        assert "扣款明细".equals(table.getString("name"));
        assert 5 == table.getInteger("rowCount");
        assert 4 == table.getInteger("columnCount");
        assert 4 == table.getJSONArray("heads").size();


        logger.info(resultObject.toJSONString());

    }





}