package cn.nwcdcloud.samples.ocr.parse;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParseFixedPosition {

    private final Logger logger = LoggerFactory.getLogger(ParseFixedPosition.class);

    private int pageWidth;
    private int pageHeight;
    // 默认值配置类
    DefaultValueConfig mDefaultConfig ;

    public ParseFixedPosition(Map<String, ?> rootConfig, int pageWidth, int pageHeight) {
        this.pageWidth = pageWidth;
        this.pageHeight = pageHeight;
        logger.info( "***    pageWidth: {}   pageHeight:{} ", pageWidth, pageHeight  );
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

        logger.info(configMap.get("Name").toString());

        for(JSONObject blockItem : blockItemList){
            logger.info(blockItem.getString("text"));
            if(BlockItemUtils.isValidRange(mDefaultConfig, configMap, blockItem)){
                logger.info("____________________", blockItem.toJSONString());
            }
        }

        return null;
    }
}
