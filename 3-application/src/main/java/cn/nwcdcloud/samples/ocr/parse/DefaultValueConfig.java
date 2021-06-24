package cn.nwcdcloud.samples.ocr.parse;

import java.util.Map;

/**
 * 获取配置文件默认值
 */
public class DefaultValueConfig {

    private Map<String, ?> mDefaultRootValueMap;
    private Map<String, ?> mKeyValueMap;
    private Map<String, ?> mTableValueMap;
    private Map<String, ?> mTableColumnMap;
    public DefaultValueConfig(Map<String, ?> defaultRootValueMap) {
        mDefaultRootValueMap = defaultRootValueMap;
        if(mDefaultRootValueMap != null){
            mKeyValueMap = (Map<String, ?>) mDefaultRootValueMap.get("KeyValue");
            mTableValueMap = (Map<String, ?>) mDefaultRootValueMap.get("Table");

            if(mTableValueMap != null){
                mTableColumnMap = (Map<String, ?>) mTableValueMap.get("Columns");
            }
        }
    }

    /**
     * 获取Key value 默认值
     * @param configMap
     * @param key
     * @param defaultValue
     * @return
     */
    public  Object getKeyValue(Map<String, ?> configMap, String key, Object defaultValue){

        // step1. 直接获取配置的值
        if(configMap.get(key) != null && configMap.get(key)!=null){
            return configMap.get(key);
        }

        // step2.  获取模板配置的默认值
        if(mKeyValueMap != null  && mKeyValueMap.get(key) != null){
            return mKeyValueMap.get(key);
        }

        // step3. 或者预置的默认值
        return defaultValue;
    }



    /**
     * 获取Key value 默认值
     * @param configMap
     * @param key
     * @param defaultValue
     * @return
     */
    public  Object getTableValue(Map<String, ?> configMap, String key, Object defaultValue){

        // step1. 直接获取配置的值
        if(configMap.get(key) != null && configMap.get(key)!=null){
            return configMap.get(key);
        }

        // step2.  获取模板配置的默认值
        if(mTableValueMap != null && mTableValueMap.get(key) != null){
            return mTableValueMap.get(key);
        }

        // step3. 或者预置的默认值
        return defaultValue;
    }

    /**
     * 模板table 表格中用到的默认值
     * @param configMap
     * @param key
     * @param defaultValue
     * @return
     */

    public  Object getTableColumnValue(Map<String, ?> configMap, String key, Object defaultValue){

        // step1. 直接获取当前配置的值
        if(configMap.get(key) != null && configMap.get(key)!=null){
            return configMap.get(key);
        }
        // step2.  获取模板配置的默认值
        if(mTableColumnMap != null && mTableColumnMap.get(key) != null){
            return mTableColumnMap.get(key);
        }
        // step3. 或者预置的默认值
        return defaultValue;
    }
}
