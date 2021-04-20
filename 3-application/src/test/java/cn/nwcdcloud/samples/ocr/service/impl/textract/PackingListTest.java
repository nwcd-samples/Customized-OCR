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


public class PackingListTest {
    private static final Logger logger = LoggerFactory.getLogger(PackingListTest.class);

    @Resource
    CommonServiceImpl commonServiceImpl;

    private static final String  SAMPLE_JSON_OBJECT_FILE_1 =  "/sample/packing_list.json";
//    private static final String  SAMPLE_JSON_OBJECT_FILE_2 =  "/sample/cost_table002.json";



    @Test
    public void parseId001() {

        String jsonObjectPath=this.getClass().getResource(SAMPLE_JSON_OBJECT_FILE_1).getFile().toString();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);

        List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject, 1200, 2000);
        ParseFactory parseJsonUtil = new ParseFactory(1200, 2000, blockItemList, "config/packing_list.yaml");
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