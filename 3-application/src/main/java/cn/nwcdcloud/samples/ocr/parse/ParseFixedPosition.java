package cn.nwcdcloud.samples.ocr.parse;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParseFixedPosition {

    private final Logger logger = LoggerFactory.getLogger(ParseFixedPosition.class);

    // 默认值配置类
    DefaultValueConfig mDefaultConfig ;

    public ParseFixedPosition(Map<String, ?> rootConfig) {
        mDefaultConfig = new DefaultValueConfig((Map<String, ?>)rootConfig.get("DefaultValue"));

    }

    /**
     * 处理单个水平元素
     * @param configMap 配置文件Map
     * @param blockItemList  元素列表
     * @return  解析后的json 文件
     */
    public JSONObject parse(HashMap configMap, List<JSONObject> blockItemList) {


        //step 0. 通过关键字进行 key 元素的定位
        if(!configMap.containsKey("Name")){
            throw new IllegalArgumentException(" 配置文件必须包含  'Name' 选项 ");
        }

//        logger.info(configMap.get("Name").toString());

        JSONObject resultItem = new JSONObject();

        resultItem.put("name", configMap.get("Name"));


        double confidence = 1.0;

        List<JSONObject> itemList = new ArrayList<>();
        for(JSONObject blockItem : blockItemList){
            if(BlockItemUtils.isValidRange(mDefaultConfig, configMap, blockItem)){

                itemList.add(blockItem);
                if(blockItem.getDouble("Confidence") < confidence){
                    confidence = blockItem.getDouble("Confidence");
                }
            }
        }
        itemList.sort(new BlockItemComparator());
        StringBuilder stringBuilder = new StringBuilder();
        for(JSONObject item : itemList){
            stringBuilder.append(item.getString("text")).append(" ");
        }

        if(StringUtils.hasLength(stringBuilder.toString())){
            resultItem.put("value", stringBuilder.toString().trim());
            resultItem.put("confidence", confidence);
            return resultItem;

        }else {
            return null;
        }

    }
}
