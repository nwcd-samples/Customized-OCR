package cn.nwcdcloud.samples.ocr.commons.util;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ParseTablesWorker {

    private final Logger logger = LoggerFactory.getLogger(ParseTablesWorker.class);

    /**
     * 处理表格元素
     */
    public List<JSONObject> parse(HashMap rootMap, List<JSONObject> blockItemList) {

        String name = rootMap.get("Name").toString();
        System.out.println("*******  "+ name);


        List columnList = (ArrayList) rootMap.get("Columns");
        for (Object item : columnList) {
            System.out.println(item);

        }

        return null;
    }


}
