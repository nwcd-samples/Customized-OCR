package cn.nwcdcloud.samples.ocr.service.impl.textract;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.nwcdcloud.samples.ocr.parse.BlockItemUtils;
import cn.nwcdcloud.samples.ocr.parse.FileUtils;
import cn.nwcdcloud.samples.ocr.parse.ParseFactory;


public class PackingListTest {
    private static final Logger logger = LoggerFactory.getLogger(PackingListTest.class);

    private static final String  SAMPLE_JSON_OBJECT_FILE_1 =  "/sample/packing_list.json";
//    private static final String  SAMPLE_JSON_OBJECT_FILE_2 =  "/sample/cost_table002.json";



    @Test
    public void parseId001() {

        String jsonObjectPath=this.getClass().getResource(SAMPLE_JSON_OBJECT_FILE_1).getFile().toString();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);

        List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject, 1200, 2000);
        ParseFactory parseJsonUtil = new ParseFactory(1200, 2000, "config/packing_list.yaml");
        JSONObject  resultArray =  parseJsonUtil.extractValue(blockItemList);


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