package cn.nwcdcloud.samples.ocr.service.impl.textract;

import cn.nwcdcloud.samples.ocr.commons.util.BlockItemUtils;
import cn.nwcdcloud.samples.ocr.commons.util.FileUtils;
import cn.nwcdcloud.samples.ocr.commons.util.ParseJsonWorker;
import cn.nwcdcloud.samples.ocr.enums.DocumentTypeEnum;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.List;


public class InvoiceTypeTest {
    private static final Logger logger = LoggerFactory.getLogger(InvoiceTypeTest.class);

    @Resource
    CommonServiceImpl commonServiceImpl;

    private static final String  ID_SAMPLE_JSON_OBJECT_FILE_1 =  "/sample/invoice01.json";
    private static final String  ID_SAMPLE_JSON_OBJECT_FILE_2 =  "/sample/invoice02.json";



    @Test
    public void parseId001() {

        String jsonObjectPath=this.getClass().getResource(ID_SAMPLE_JSON_OBJECT_FILE_1).getFile().toString();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
        List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject, 1200, 900);
        ParseJsonWorker parseJsonUtil = new ParseJsonWorker(1200, 900, blockItemList, DocumentTypeEnum.INVOICE.getPath());
        JSONArray  resultArray =  parseJsonUtil.extractValue(blockItemList);
        logger.info(resultArray.toJSONString());
//        assert  checkKeyValueMap(resultArray, "姓名", "代用名");

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