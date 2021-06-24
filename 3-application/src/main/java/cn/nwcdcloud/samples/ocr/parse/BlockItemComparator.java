package cn.nwcdcloud.samples.ocr.parse;

import com.alibaba.fastjson.JSONObject;

import java.util.Comparator;

public class BlockItemComparator implements Comparator<JSONObject> {

    // 优先比较 x 坐标的 高度范围， Y值比较接近的时候， 比较x的值。
    private double compareHeightRate ;
    public BlockItemComparator(double compareHeightRate) {
        this.compareHeightRate = compareHeightRate;
    }

    @Override
    public int compare(JSONObject j1, JSONObject j2) {
        //上下高度差距在 1/3 分之一以内， 比较X 坐标， 剩余比较Y 坐标
        double j1YMin = j1.getDouble("yMin");
        double j2YMin = j2.getDouble("yMin");
        double height = j1.getDouble("heightRate");

        if(Math.abs(j1YMin - j2YMin) < height/compareHeightRate){
            return (int) ((j1.getDouble("xMin") - j2.getDouble("xMin")) * 10000);
        }else{
            return (int) ((j1.getDouble("yMin") - j2.getDouble("yMin")) * 10000);
        }

//
    }
}
