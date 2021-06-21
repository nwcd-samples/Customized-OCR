package cn.nwcdcloud.samples.ocr.parse;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.regex.Pattern;

public class ParseUtils {

    private static final Logger logger = LoggerFactory.getLogger(ParseUtils.class);

    /**
     *
     * @param valueType
     * @param value
     * @param direction 取文字的方向， 从左往右， 从右往左， 考虑到column 取值时， 多个value 元素连在一起，
     * @return
     */
    public static String processBlockValue(String valueType, String value, int direction ){
        //FIXME:  实现  direction 功能
        if(!StringUtils.hasLength(valueType) || "string".equalsIgnoreCase(valueType) || "default".equalsIgnoreCase(valueType)){
            return value;
        }
        if("number".equalsIgnoreCase(valueType)){
            return getItemNumericalValue(value, direction);
        }
        if("word".equalsIgnoreCase(valueType)){
            return getItemWordValue(value, direction);
        }
        return value;
    }


    /**
     * 或者Item value 里面数字的值，
     * Step 1.  根据空格进行拆分， 找到第一组是数字的字符串，并且返回。
     * Step 2.   过滤掉不是数字的文字， 然后返回。
     * @param value
     * @return
     */
    private static String getItemNumericalValue(String value, int direction){
        if(!StringUtils.hasLength(value)){
            return value;
        }
        value = value.replaceAll("[。]", ".");
        String  [] splitArray = value.split(" ");

        if(direction == ConfigConstants.PARSE_TABLE_CELL_VALUE_DIRECTION_FROM_LEFT) {
            for (String tempStr : splitArray) {
                if (tempStr.length() > 0 && isDoubleOrFloat(tempStr)) {
                    return retainFixedLength(tempStr);
                }
            }
        }else {
            for(int i=splitArray.length-1; i>=0;i--){
                String tempStr = splitArray[i];
                if (tempStr.length() > 0 && isDoubleOrFloat(tempStr)) {
                    return retainFixedLength(tempStr);
                }
            }
        }


//        value = value.replaceAll("[^0-9.-]", "");
        value = remainNumberString(value, direction);
        return retainFixedLength(value);
    }

    private static String getItemWordValue(String value, int direction){
        if(!StringUtils.hasLength(value)){
            return value;
        }
        String  [] splitArray = value.split(" ");
        if(splitArray.length>1) {
            if(direction == ConfigConstants.PARSE_TABLE_CELL_VALUE_DIRECTION_FROM_LEFT) {
                for (String tempStr : splitArray) {
                    if (tempStr.length() > 0 && !isDoubleOrFloat(tempStr)) {
                        return tempStr;
                    }
                }
            }else {
                for(int i=splitArray.length-1; i>=0;i--){
                    String tempStr = splitArray[i];
                    if (tempStr.length() > 0 && !isDoubleOrFloat(tempStr)) {
                        return tempStr;
                    }
                }


            }
        }
        value = value.replaceAll("[0-9.]", "");
        return value;


    }


    private static String retainFixedLength(String value){

        //FIXME: 最多保留4位小数， 以后可以从配置文件进行设置
        if(!StringUtils.hasLength(value) ){
            return value;
        }

        int fixedLength = 4;
        if(value.length() >  value.lastIndexOf('.') + fixedLength +1  ){
            value = value.substring(0, value.lastIndexOf('.')+ fixedLength + 1);
        }
        return value;

    }




    private static boolean isDoubleOrFloat(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
        return pattern.matcher(str).matches();
    }

    /**
     * 判断一个 元素是否在主列的范围内
     * @param item
     * @param columnItem
     * @return
     */
    public static boolean isContainItemInRow(JSONObject item, JSONObject columnItem){
        double xMinBorder = columnItem.getDouble("xMinBorder");
        double xMaxBorder = columnItem.getDouble("xMaxBorder");

        // 边界内部包含候选元素
        boolean insideContainFlag = item.getDouble("xMin")>= xMinBorder
                && item.getDouble("xMax")<= xMaxBorder ;

        // 候选元素 包含列元素
        double middleX = (columnItem.getDouble("xMin") + columnItem.getDouble("xMax"))/2;

        boolean outsideContainFlag = item.getDouble("xMin") <= middleX
                && item.getDouble("xMax")>= middleX && item.getDouble("widthRate") >= columnItem.getDouble("widthRate") ;

        if(item.getString("text").equals("10.00") && columnItem.getString("text").equals("金额")){

        }

        if(columnItem.getDouble("widthRate") < 0.06){
            outsideContainFlag = item.getDouble("xMin") <= columnItem.getDouble("xMin")
                    && item.getDouble("xMax")>= columnItem.getDouble("xMax") && item.getDouble("widthRate") >= columnItem.getDouble("widthRate") ;

        }
//        if(item.getString("text").equals("10.00") && columnItem.getString("text").equals("金额")) {
//            logger.warn("--------------  insideContainFlag={}   outsideContainFlag={} ", insideContainFlag, outsideContainFlag);
//        }
        return insideContainFlag || outsideContainFlag;
    }


    /**
     * 判断 table 里面 单元元素 取值方向， 元素过长， 通常是识别表格时候连在了一起，可以分别从左右取第一个空格分隔的元素。
     * @param cellList
     * @param columnItem
     * @return
     */
    public static int checkParseCellValueDirection (List<JSONObject> cellList, JSONObject columnItem){

        JSONObject configMap = columnItem.getJSONObject("config");
        Integer directionValue = configMap.getInteger("Direction");

        if(directionValue != null){
            if(directionValue == ConfigConstants.PARSE_TABLE_CELL_VALUE_DIRECTION_FROM_LEFT){
                return ConfigConstants.PARSE_TABLE_CELL_VALUE_DIRECTION_FROM_LEFT;
            }else {
                return ConfigConstants.PARSE_TABLE_CELL_VALUE_DIRECTION_FROM_RIGHT;
            }
        }

        double xMin = 2.0;
        double xMax = -1.0;
        if(cellList == null || cellList.size() == 0 || cellList.size()>=2){
            return ConfigConstants.PARSE_TABLE_CELL_VALUE_DIRECTION_FROM_LEFT;
        }

        JSONObject item = cellList.get(0);
        xMin = item.getDouble("xMin");
        xMax = item.getDouble("xMax");

        double middleX = (xMin + xMax)/2;
        // cell 元素更靠左， 从右边开始取元素
        if(middleX < columnItem.getDouble("xMin")){
            return ConfigConstants.PARSE_TABLE_CELL_VALUE_DIRECTION_FROM_RIGHT;
        }


        return ConfigConstants.PARSE_TABLE_CELL_VALUE_DIRECTION_FROM_LEFT;
    }

    /**
     * 寻找用来定位主列元素， 主列是用来进行行划分的， 可能会出现一个单元格里面有多行文字的情况。
     * 主列一般都是不为空， 单行， 长度固定， 可以用来定义一行的元素。
     * @param columnBlockItemList
     * @return
     */
    public static int findMainColumnIndex( List<JSONObject> columnBlockItemList){


        for(int i=0; i< columnBlockItemList.size(); i++){
            JSONObject blockItem = columnBlockItemList.get(i);
            boolean isMainColumn = blockItem.getBoolean("isMainColumn");
            if(isMainColumn){
                return i;
            }
        }
        return 0;
    }

    /***
     * 只保留数字字符， 从两个方向查找数字， 找到数字的开始和结束位置， 返回只含有数字的字符串。
     * @param value
     * @param direction
     * @return
     */
    public static String remainNumberString(String value, int direction){
        if(!StringUtils.hasLength(value)){
            return value;
        }

        boolean startFlag = false;
        int endIndex = value.length();
        int startIndex = 0;

        if(direction == ConfigConstants.PARSE_TABLE_CELL_VALUE_DIRECTION_FROM_RIGHT){
            for(int i=value.length()-1; i>=0; i--){
                Character c = value.charAt(i);
                if(c == '.' || c=='-' || c==',' || (c>='0' && c<='9')){
                    if(startFlag == false){
                        startFlag = true;
                        endIndex = i+1;
                    }
                }else {
                    if(startFlag){
                        startIndex = i+1;
                        break;
                    }
                }
            }
            String result = value.substring(startIndex, endIndex);
//            logger.warn("value = [{}] length=[{}]  start=[{}] end=[{}] result=[{}]", value, value.length(), startIndex, endIndex, result);
            return result;
        }else {

            for(int i=0; i< value.length(); i++){
                Character c = value.charAt(i);

                if(c == '.' || c=='-' || c==',' || (c>='0' && c<='9')){
                    if(startFlag == false){
                        startFlag = true;
                        startIndex = i;
                    }
                }else {
                    if(startFlag){
                        endIndex = i;
                        break;
                    }
                }
            }
            String result = value.substring(startIndex, endIndex);
//            logger.warn("value = [{}] length=[{}]  start=[{}] end=[{}] result=[{}]", value, value.length(), startIndex, endIndex, result);
            return result;
        }


    }

}
