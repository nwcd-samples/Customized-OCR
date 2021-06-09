package cn.nwcdcloud.samples.ocr.parse;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static cn.nwcdcloud.samples.ocr.parse.ConfigConstants.DEBUG_PARSE_FIXED_POSITION;
import static cn.nwcdcloud.samples.ocr.parse.ConfigConstants.DEBUG_PARSE_KEY_VALUE;

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
        if(DEBUG_PARSE_FIXED_POSITION){
            logger.debug("\n【Fixed Position 查找】 【{}】config配置: {}", configMap.get("Name"), configMap);
        }
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
        itemList.sort(new BlockItemComparator(ConfigConstants.COMPARE_HEIGHT_RATE));

        if(DEBUG_PARSE_FIXED_POSITION) {
            for (int i = 0; i < itemList.size(); i++) {
                logger.debug("【1.{} 候选Item】 [{}]", i, BlockItemUtils.generateBlockItemString(itemList.get(i)));
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        for(JSONObject item : itemList){
            stringBuilder.append(item.getString("text")).append(" ");
        }

        if(StringUtils.hasLength(stringBuilder.toString())){
            resultItem.put("value", stringBuilder.toString().trim());
            resultItem.put("confidence", confidence);
            if(DEBUG_PARSE_FIXED_POSITION){
                logger.debug("【2. END 找到元素】  {} ", resultItem.toJSONString());
            }
            return resultItem;

        }else {
            if(DEBUG_PARSE_FIXED_POSITION){
                logger.debug("【2. END 未到元素】  ------ ");
            }
            return null;
        }

    }
}
