package cn.nwcdcloud.samples.ocr.commons.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);
    public static JSONObject readJsonObject(String filePath){
        String result = readFile(filePath);
        if(result == null){
            return null;
        }
        // 获取json
        try {
            JSONObject jsonObject = JSONObject.parseObject(result);
            System.out.println(JSON.toJSONString(jsonObject));
            return jsonObject;
        }catch (JSONException e){
            logger.error(e.getMessage());
        }
        return null;

    }

    public static JSONArray readJsonArray(String filePath){
        String result = readFile(filePath);
        if(result == null){
            return null;
        }
        // 获取json
        try {
            JSONArray jsonArray = JSONObject.parseArray(result);
            System.out.println(JSON.toJSONString(jsonArray));
            return jsonArray;
        }catch (JSONException e){
            logger.error(e.getMessage());
        }
        return null;
    }


    private static String readFile(String filePath){
        BufferedReader reader = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            FileInputStream fileInputStream = new FileInputStream(filePath);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
            reader = new BufferedReader(inputStreamReader);
            String tempString = null;
            while ((tempString = reader.readLine()) != null){
                stringBuilder.append(tempString);
            }
            return stringBuilder.toString();
        }catch (IOException e){
            logger.error(e.getMessage());
        }finally {
            if (reader != null){
                try {
                    reader.close();
                }catch (IOException e){
                    logger.error(e.getMessage());
                }
            }
        }
        return null;
    }


}
