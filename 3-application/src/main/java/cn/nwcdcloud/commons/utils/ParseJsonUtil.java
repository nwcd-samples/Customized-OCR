package cn.nwcdcloud.commons.utils;

import com.alibaba.fastjson.JSONObject;
import org.ho.yaml.Yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParseJsonUtil {
    private static final String  ID_SAMPLE_CONFIG_FILE =  "/config/id.yaml";

    public void extractValue(List<JSONObject> blockItemList){
        Map configMap =  readConfig(ID_SAMPLE_CONFIG_FILE);


    }


    private Map readConfig(String configPath) {

        String filePath=this.getClass().getResource(configPath).getFile().toString();
        File dumpFile=new File(filePath);

        Map rootMap = null;
        try {
            rootMap = Yaml.loadType(dumpFile, HashMap.class);
            for(Object key:rootMap.keySet()){
                System.out.println(key+":\t"+rootMap.get(key).toString());
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return rootMap;
    }
}
