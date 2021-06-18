package cn.nwcdcloud.samples.ocr.parse;

import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

public class ParseUtils {



    public static String processBlockValue(String valueType, String value ){
        if(!StringUtils.hasLength(valueType) || "string".equalsIgnoreCase(valueType) || "default".equalsIgnoreCase(valueType)){
            return value;
        }
        if("number".equalsIgnoreCase(valueType)){
            return getItemNumericalValue(value);
        }
        if("word".equalsIgnoreCase(valueType)){
            return getItemStringValue(value);
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
    private static String getItemNumericalValue(String value){
        if(!StringUtils.hasLength(value)){
            return value;
        }
        value = value.replaceAll("[。*,，]", ".");
        String  [] splitArray = value.split(" ");

        for(String tempStr: splitArray){
            if(tempStr.length()>0 && isDoubleOrFloat(tempStr)){
                return  retainFixedLength(tempStr);
            }
        }
        value = value.replaceAll("[^0-9.-]", "");
        return retainFixedLength(value);
    }

    private static String getItemStringValue(String value){
        if(!StringUtils.hasLength(value)){
            return value;
        }
        String  [] splitArray = value.split(" ");
        if(splitArray.length>1) {
            for (String tempStr : splitArray) {
                if (tempStr.length() > 0 && !isDoubleOrFloat(tempStr)) {
                    return tempStr;
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

}
