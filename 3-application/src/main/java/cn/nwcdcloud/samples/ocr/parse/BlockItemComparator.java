package cn.nwcdcloud.samples.ocr.parse;

import com.alibaba.fastjson.JSONObject;

import java.util.Comparator;

public class BlockItemComparator implements Comparator<JSONObject> {
    @Override
    public int compare(JSONObject j1, JSONObject j2) {
        //上下高度差距在 1/3 分之一以内， 比较X 坐标， 剩余比较Y 坐标
        double j1YMin = j1.getDouble("yMin");
        double j2YMin = j2.getDouble("yMin");
        double height = j1.getDouble("heightRate");

        if(Math.abs(j1YMin - j2YMin) < height/3){
            return j1.getInteger("x") - j2.getInteger("x");
        }else{
            return j1.getInteger("y") - j2.getInteger("y");
        }

//
    }
}
