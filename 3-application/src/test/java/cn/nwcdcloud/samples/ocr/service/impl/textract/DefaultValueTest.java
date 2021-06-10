package cn.nwcdcloud.samples.ocr.service.impl.textract;

import cn.nwcdcloud.samples.ocr.parse.*;
import org.ho.yaml.Yaml;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;


public class DefaultValueTest {
    private static final Logger logger = LoggerFactory.getLogger(DefaultValueTest.class);

    private static final String  CONFIG_OBJECT_FILE =  "/config/invoice_default.yaml";



    @Test
    public void testDefaultValue(){

        String filePath= this.getClass().getResource(CONFIG_OBJECT_FILE).getFile().toString();
        Map<String, ? > rootConfig = readConfig(filePath);
        logger.info(rootConfig.toString());

        DefaultValueConfig mDefaultConfig  = new DefaultValueConfig((Map<String, ?>)rootConfig.get("DefaultValue"));
        logger.info(mDefaultConfig.toString());


        assert mDefaultConfig.getKeyValue(rootConfig, "LengthMax", ConfigConstants.ITEM_LENGTH_MAX).toString().equals("39");
        assert mDefaultConfig.getTableValue(rootConfig, "MaxRowCount", ConfigConstants.TABLE_MAX_ROW_COUNT).toString().equals("21");
        assert mDefaultConfig.getTableColumnValue(rootConfig, "MoveLeftRatio", ConfigConstants.ITEM_OFFSET_LEFT_RADIO).toString().equals("0.1");


    }


    private Map<String, ?> readConfig(String configPath) {
        InputStream is = null;
        File file = new File(configPath);
        if (file.exists()) {
            try {
                is = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                is = null;
            }
        } else {
            logger.info("未找到配置文件{}", configPath);
        }

        Map<String, ?> rootMap = null;
        try {
            rootMap = Yaml.loadType(is, HashMap.class);
        } catch (Exception e) {
            logger.error("读取配置文件出错:", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    logger.warn("关闭配置文件出错");
                }
            }
        }
        return rootMap;
    }

    @Test
    public void compareString(){
        String itemString = "项目／管。理";
        itemString = itemString.replaceAll("[/／:：.。 ]", "");
        logger.info(itemString);
        assert itemString.equals("项目管理");
    }

}