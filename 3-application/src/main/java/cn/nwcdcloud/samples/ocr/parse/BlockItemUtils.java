package cn.nwcdcloud.samples.ocr.parse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 解析Textract返回的json数据， 生成元素列表， 供后续进行结构化提取
 */
public class BlockItemUtils {
    private static final Logger logger = LoggerFactory.getLogger(BlockItemUtils.class);

    public static List<JSONObject> getBlockItemList(JSONObject jsonObject){
        int pageWidth = ConfigConstants.PAGE_WIDTH;
        int pageHeight  = ConfigConstants.PAGE_HEIGHT;
//        logger.info(jsonObject.toJSONString());
//        logger.info(jsonObject.getJSONObject("DocumentMetadata").toJSONString());
        Integer pageCount = jsonObject.getJSONObject("DocumentMetadata").getInteger("Pages");
//        logger.debug("页数： {} ",  pageCount);

        if(pageCount<=0){
            logger.warn("该文档 没有内容");
            return null;
        }

        //TODO:  先处理单页的情况, 或者元素坐标， 进行转正 去空白等操作。
        return parseDataByPageCount(1, jsonObject.getJSONArray("Blocks"), pageWidth, pageHeight);

    }

    /**
     * 返回每页的元素列表
     * @param pageCount
     * @param jsonArray
     * @return
     */
    private static List<JSONObject> parseDataByPageCount(int pageCount, JSONArray jsonArray,  int pageWidth, int pageHeight){
        List<JSONObject> blockList = new ArrayList<JSONObject>();
        logger.debug(" 共有Block元素{}个", jsonArray.size());

        for(int i=0; i<jsonArray.size(); i++){
            JSONObject blockItem = jsonArray.getJSONObject(i);
            String blockType = blockItem.getString("BlockType");
            if(blockItem.getInteger("Page") == pageCount
                    && ("WORD".equals(blockType) || "LINE".equals(blockType))){
                blockList.add(blockItem);
//                logger.info(" {} {} {} ", blockItem.getString("Page"), blockItem.getString("BlockType"),
//                        blockItem.getString("Text"));
            }

        }

        if(blockList.size() == 0){
            return blockList;
        }
        // 取出最长的元素， 找到旋转角度， 让它保持水平。
        JSONObject maxWidthBlockItem = findMaxWidthBlockItem(blockList);

        // 计算旋转矩阵， 用于对元素进行旋转
        List<Double> matrixList = computeDegree(maxWidthBlockItem);


        List<JSONObject> newBlockItemList = new ArrayList<JSONObject>();

        for(int i=0; i<blockList.size(); i++){
            newBlockItemList.add(createBlockItem(matrixList, blockList.get(i)));
        }
        //去除左右空白， 将多页合并到一起， （目前是一页）
        JSONObject pageMargin = initPageMargin(newBlockItemList);
        for(int i=0; i<newBlockItemList.size(); i++){
            reArrangePositionBlockItem(newBlockItemList.get(i), pageMargin);
        }

        double documentPageHeight = pageMargin.getDouble("bottom") - pageMargin.getDouble("top");
        double documentZoomOutHeight = pageHeight * documentPageHeight;

        // 让元素占满整个空间， 在Y轴方向进行拉伸
        for(int i=0; i<newBlockItemList.size(); i++){
            zoomLayoutBlockItem(newBlockItemList.get(i), documentZoomOutHeight, pageWidth, pageHeight);
        }

        return newBlockItemList;
    }



    /**
     * 计算用于界面旋转的矩阵
     * @param blockItem
     * @return
     */
    private static List<Double> computeDegree(JSONObject blockItem){

//        logger.debug(blockItem.getJSONObject("Geometry").getJSONArray("Polygon").toJSONString());

        JSONArray pointArray = blockItem.getJSONObject("Geometry").getJSONArray("Polygon");

        JSONObject pointA = pointArray.getJSONObject(0);
        JSONObject pointB = pointArray.getJSONObject(1);

        double tan = (pointB.getDouble("Y")  - pointA.getDouble("Y") )/
                (pointB.getDouble("X")  - pointA.getDouble("X"));

//        logger.debug("倾斜角度 {} ", tan);
        double theta = Math.atan(tan);
        List<Double> matrix =new ArrayList<Double>();
        matrix.add(Math.cos(theta));
        matrix.add(Math.sin(theta));
        matrix.add(-1 * Math.sin(theta));
        matrix.add(Math.cos(theta));
//        [cos(theta), Math.sin(theta), -1 * Math.sin(theta), Math.cos(theta)]
        return matrix;

    }

    /**
     * 计算所有元素经过旋转以后的新坐标 ， 生成新blockItem
     * @param matrixList
     * @param rawBlockItem
     * @return
     */
    private static JSONObject createBlockItem(List<Double> matrixList, JSONObject rawBlockItem){

        JSONArray pointArray = rawBlockItem.getJSONObject("Geometry").getJSONArray("Polygon");


        JSONArray newPolyArray = new JSONArray();
        for(int i=0; i<pointArray.size(); i++){

            JSONObject ploy = new JSONObject();
            ploy.put("x", pointArray.getJSONObject(i).getDouble("X") -0.5) ;
            ploy.put("y", pointArray.getJSONObject(i).getDouble("Y") -0.5) ;
            JSONObject newPloy = matrixRotate(matrixList, ploy);
            newPloy.put("x", newPloy.getDouble("x")+ 0.5);
            newPloy.put("y", newPloy.getDouble("y")+ 0.5);
            newPolyArray.add(newPloy);

//            logger.info(" x: {} y: {} ", newPloy.getString("x"), newPloy.getString("y"));
//            logger.info(" x: {} y: {} ", pointArray.getJSONObject(i).getDouble("X"), pointArray.getJSONObject(i).getDouble("Y"));
        }


        JSONObject blockItem = new JSONObject();
        blockItem.put("id", rawBlockItem.getString("Id"));
        blockItem.put("newPoly", newPolyArray);
//        blockItem.put("boundingBox", rawBlockItem.getJSONObject("Geometry").getJSONObject("BoundingBox"));
        blockItem.put("text", deleteUnnecessaryChar(rawBlockItem.getString("Text")));
        blockItem.put("Confidence", rawBlockItem.getString("Confidence"));
//        blockItem.put("raw_block_type", rawBlockItem.getString("BlockType"));

//        logger.info("    {} ", blockItem.toJSONString());
        return blockItem;

    }

    /**
     对单个点进行矩阵乘法， 围绕原点旋转。
     point{'x':1, 'y':2}
     matrix[[0, 1], [2, 3]]
     */

    private static JSONObject matrixRotate(List<Double> matrixList, JSONObject point){

        double x = point.getDouble("x");
        double y = point.getDouble("y");
        double X = matrixList.get(0)* x + matrixList.get(1)*y;
        double Y = matrixList.get(2)* x + matrixList.get(3)*y;

        JSONObject newPoint = new JSONObject();
        newPoint.put("x", X);
        newPoint.put("y", Y);
        return newPoint;
    }


    /**
     * 找到每一页空白的地方， 去除掉， 防止有偏移
     * @param blockItemList
     * @return
     */
    private static JSONObject initPageMargin(List<JSONObject> blockItemList){

        double pageTop = 1;
        double pageBottom = 0.0;
        double pageLeft = 1;
        double pageRight = 0.0;

        for(int i=0; i< blockItemList.size(); i++){
            JSONObject item = blockItemList.get(i);
            JSONArray polyArray =  item.getJSONArray("newPoly");
            double top = polyArray.getJSONObject(0).getDouble("y");
            double left = polyArray.getJSONObject(0).getDouble("x");
            double bottom = polyArray.getJSONObject(2).getDouble("y");
            double right = polyArray.getJSONObject(2).getDouble("x");

            if(top < pageTop){
                pageTop = top;
            }
            if(left < pageLeft){
                pageLeft = left;
            }
            if(bottom > pageBottom){
                pageBottom = bottom;
            }
            if(right > pageRight){
                pageRight = right;
            }

        }
        JSONObject pageMargin = new JSONObject();
        pageMargin.put("top", pageTop);
        pageMargin.put("bottom", pageBottom);
        pageMargin.put("left", pageLeft);
        pageMargin.put("right", pageRight);
        pageMargin.put("height", pageBottom - pageTop);

        pageMargin.put("height_rate", 1.0/(pageBottom - pageTop));
        pageMargin.put("width_rate", 1.0/(pageRight - pageLeft));

//        logger.debug("page margin    {} ", pageMargin.toJSONString());
        return pageMargin;

    }

    /**
     * 删除空白区域以后，加上前面所有页的高度， 将所有页面合并到一起
     * @param blockItem
     * @param pageMargin
     */
    private static void reArrangePositionBlockItem(JSONObject blockItem, JSONObject pageMargin){

        double pageTop = pageMargin.getDouble("top");
        double pageLeft = pageMargin.getDouble(("left"));
//        logger.info("\n{}----------------- reArrangePositionBlockItem  {} ",blockItem.getString("text"), blockItem.toJSONString());
        JSONArray polyArray = blockItem.getJSONArray("newPoly");

        for(int i=0; i< polyArray.size(); i++ ){
            JSONObject poly = polyArray.getJSONObject(i);
//            logger.info("---------------^^^  {}  {} {} ", poly.getDouble("x"), pageLeft, pageMargin.getDouble("width_rate"));
            poly.put("x" , ((poly.getDouble("x") - pageLeft) * pageMargin.getDouble("width_rate")));
            poly.put("y" , (poly.getDouble("y") - pageTop) );
        }
    }

    /**
     * 把现有元素等比例放大， 占满空间
     * @return
     */
    private static void zoomLayoutBlockItem(JSONObject blockItem, double documentZoomOutHeight,
                                            int pageWidth, int pageHeight){
        JSONArray polyArray = blockItem.getJSONArray("newPoly");

        for(int i=0; i< polyArray.size(); i++ ){
            JSONObject poly = polyArray.getJSONObject(i);
            poly.put("x", (int) (poly.getDouble("x")* pageWidth));
            poly.put("y", (int) (poly.getDouble("y")* pageHeight));
        }

        blockItem.put("width", (int)((polyArray.getJSONObject(1).getDouble("x") - polyArray.getJSONObject(0).getDouble("x"))));
        blockItem.put("height", (int)((polyArray.getJSONObject(3).getDouble("y") - polyArray.getJSONObject(0).getDouble("y"))));
        blockItem.put("left", (int)(polyArray.getJSONObject(0).getDouble("x").doubleValue()));
        blockItem.put("top", (int)(polyArray.getJSONObject(0).getDouble("y")).doubleValue());

        blockItem.put("right", (int)(polyArray.getJSONObject(1).getDouble("x")).doubleValue());
        blockItem.put("bottom", (int)(polyArray.getJSONObject(2).getDouble("y")).doubleValue());

        blockItem.put("x", (int)((polyArray.getJSONObject(2).getDouble("x") + polyArray.getJSONObject(0).getDouble("x"))/2.0));
        blockItem.put("y", (int)((polyArray.getJSONObject(2).getDouble("y") + polyArray.getJSONObject(0).getDouble("y"))/2.0));

        blockItem.put("xMin",(polyArray.getJSONObject(0).getDouble("x").doubleValue() /pageWidth ));
        blockItem.put("xMax",(polyArray.getJSONObject(1).getDouble("x").doubleValue() /pageWidth ));

        //Page height 做了缩放
        blockItem.put("yMin",(polyArray.getJSONObject(0).getDouble("y").doubleValue() /documentZoomOutHeight ));
        blockItem.put("yMax",(polyArray.getJSONObject(2).getDouble("y").doubleValue() /documentZoomOutHeight));

        blockItem.put("widthRate",blockItem.getDouble("xMax") - blockItem.getDouble("xMin"));
        blockItem.put("heightRate",blockItem.getDouble("yMax") - blockItem.getDouble("yMin"));


//        logger.info("{}-------- {} ",blockItem.getString("text"), blockItem.toJSONString());

    }

    /**
     * 找到最宽的元素， 用它来进行页面的旋转
     * @param blockList
     * @return
     */
    private static JSONObject findMaxWidthBlockItem(List<JSONObject> blockList){
        JSONObject resultItem = blockList.get(0);
        double max_width = 0.0;
        for(int i=0; i< blockList.size(); i++){
            JSONObject item = blockList.get(i);
            double width = item.getJSONObject("Geometry").getJSONObject("BoundingBox").getDouble("Width");
            if( width > max_width){
                max_width = width;
                resultItem = item;
            }
        }
        return resultItem;
    }

    /**
     * 去掉字符串中 多余的字符
     */
    private static String deleteUnnecessaryChar(String oldString){

        if (oldString == null || "".equals(oldString)){
            return "";
        }
        return oldString.replaceAll(" ", "");
    }

    /**
     * 找到最右边元素的 右边坐标
     * @param blockItemList
     * @return
     */
    public static double findRightMostPoz(List<JSONObject> blockItemList){
        double rightMost = 0;
        for (JSONObject item : blockItemList){
            if(item.getDouble("xMax")> rightMost){
                rightMost = item.getDouble("xMax");
            }
        }
        return rightMost;

    }

    /**
     * 检测目标元素 坐标范围 是否符合配置文件的要求
     *
     * @param configMap
     * @param blockItem
     * @return
     */
    public static boolean isValidRange(DefaultValueConfig mDefaultConfig, HashMap configMap, JSONObject blockItem ) {

        double xRangeMin = Double.valueOf(mDefaultConfig.getKeyValue(configMap, "XRangeMin", ConfigConstants.PAGE_RANGE_X_MIN).toString());
        double xRangeMax = Double.valueOf(mDefaultConfig.getKeyValue(configMap, "XRangeMax", ConfigConstants.PAGE_RANGE_X_MAX).toString());
        double yRangeMin = Double.valueOf(mDefaultConfig.getKeyValue(configMap, "YRangeMin", ConfigConstants.PAGE_RANGE_Y_MIN).toString());
        double yRangeMax = Double.valueOf(mDefaultConfig.getKeyValue(configMap, "YRangeMax", ConfigConstants.PAGE_RANGE_Y_MAX).toString());



//        JSONObject boundingBox = blockItem.getJSONObject("boundingBox");
        double itemLeft = blockItem.getDouble("xMin") ;
        double itemRight = blockItem.getDouble("xMax") ;
        double itemTop = blockItem.getDouble("yMin")  ;
        double itemBottom = blockItem.getDouble("yMax") ;

//        logger.debug("isFixedPositionValidRange range   x: [{}, {}]  y: [{}, {}]   {}", xRangeMin, xRangeMax, yRangeMin, yRangeMax, blockItem.getString("text"));
//        logger.debug("isFixedPositionValidRange block   x: [{}, {}]  y: [{}, {}]   {}",
//                new java.text.DecimalFormat("#0.000").format(itemLeft),
//                new java.text.DecimalFormat("#0.000").format(itemRight),
//                new java.text.DecimalFormat("#0.000").format(itemTop),
//                new java.text.DecimalFormat("#0.000").format(itemBottom),
//                blockItem.getString("text"));

        if(  itemLeft >= xRangeMin && itemRight <= xRangeMax && itemTop >= yRangeMin && itemBottom <= yRangeMax) {
            return true;
        }
        return false;
    }



    public static boolean checkKeyValueMap(JSONArray array, String name, String value){
        for(int i=0; i< array.size(); i++){
            String tempValue = array.getJSONObject(i).getString("value");
            String tempName = array.getJSONObject(i).getString("name");
            if(value.equals(tempValue)  &&  name.equals(tempName)){
                return true;
            }
        }
        return false;

    }

    public static String generateBlockItemString(JSONObject blockItem){

        StringBuilder stringBuilder = new StringBuilder();

        if(blockItem == null){
            return "Block Item is null. ";
        }
        DecimalFormat df=new DecimalFormat("#0.000");
        stringBuilder.append("[yMin="+ df.format(blockItem.getDouble("yMin")) +", ");
        stringBuilder.append("yMax="+ df.format(blockItem.getDouble("yMax")) +", ");
        stringBuilder.append("xMin="+ df.format(blockItem.getDouble("xMin")) +", ");
        stringBuilder.append("xMax="+ df.format(blockItem.getDouble("xMax")) +"]");
        stringBuilder.append("【"+ blockItem.getString("text") +"】  id="+ blockItem.getString("id").substring(0, 5));


        return stringBuilder.toString();

    }


    /**
     * 判断一个元素 是否在指定的范围内。
     * @param valueBlockItem
     * @param rangeObject
     * @return
     */
    public static boolean checkBlockItemRangeValidation(JSONObject valueBlockItem, JSONObject rangeObject){
        double rangeXMin = rangeObject.getDouble("xMin");
		double rangeXMax = rangeObject.getDouble("xMax");
		double rangeYMin = rangeObject.getDouble("yMin");
		double rangeYMax = rangeObject.getDouble("yMax");

//		logger.debug("Range: {}, {}    {}, {} ", rangeXMin , rangeXMax, rangeYMin,rangeYMax);

        return valueBlockItem.getDouble("xMin") >= rangeXMin
                &&  valueBlockItem.getDouble("xMax") <= rangeXMax
                &&  valueBlockItem.getDouble("yMin") >= rangeYMin
                &&  valueBlockItem.getDouble("yMax") <= rangeYMax;
    }

    /**
     * 通过Key的配置信息和 key的坐标， 计算Value 元素所在元素的坐标
     *
     *     ValueXRangeMin: 0.10
     *     ValueXRangeMax: 0.60
     *     TopOffsetRadio: -0.9
     *     BottomOffsetRadio: 3.1
     *     LeftOffsetRadio: 0
     *     RightOffsetRadio: 2.0
     *
     * @param mDefaultConfig
     * @param configMap
     * @param blockItem
     * @return
     */

    public static JSONObject findValueRange(DefaultValueConfig mDefaultConfig, HashMap configMap, JSONObject blockItem){

//		logger.info("findValueRange:  {} ", configMap.toString());
//		logger.info("blockItem : xMin: {} xMax: {}  yMin:{} yMax:{}", blockItem.getDouble("xMin"), blockItem.getDouble("xMax"),
//				blockItem.getDouble("yMin"), blockItem.getDouble("yMax"));

        DecimalFormat df=new DecimalFormat("#0.000");
        double topRadio = Double.parseDouble(mDefaultConfig.getKeyValue(configMap, "TopOffsetRadio", ConfigConstants.ITEM_OFFSET_TOP_RADIO).toString());
        double bottomRadio = Double.parseDouble(mDefaultConfig.getKeyValue(configMap, "BottomOffsetRadio", ConfigConstants.ITEM_OFFSET_BOTTOM_RADIO).toString());
        double leftRadio = Double.parseDouble(mDefaultConfig.getKeyValue(configMap, "LeftOffsetRadio", ConfigConstants.ITEM_OFFSET_LEFT_RADIO).toString());
        double rightRadio = Double.parseDouble(mDefaultConfig.getKeyValue(configMap, "RightOffsetRadio", ConfigConstants.ITEM_OFFSET_RIGHT_RADIO).toString());

        double valueXRangeMin = Double.parseDouble(mDefaultConfig.getKeyValue(configMap, "ValueXRangeMin", ConfigConstants.ITEM_VALUE_X_RANGE_MIN).toString());
        double valueXRangeMax = Double.parseDouble(mDefaultConfig.getKeyValue(configMap, "ValueXRangeMax", ConfigConstants.ITEM_VALUE_X_RANGE_MAX).toString());

//        logger.debug("【Radio】top: {}  bottom: {} left: {} right: {}  ---- valueXRangeMin {}, valueXRangeMax {}",
//                df.format(topRadio), df.format(bottomRadio), df.format(leftRadio), df.format(rightRadio),
//                df.format(valueXRangeMin), df.format(valueXRangeMax));

        //step1. 找到top 范围

//        logger.debug("[key range]   xMin={}, xMax={}, yMin={}, yMax={}",
//                df.format(blockItem.getDouble("xMin")),
//                df.format(blockItem.getDouble("xMax")),
//                df.format(blockItem.getDouble("yMin")),
//                df.format(blockItem.getDouble("yMax")));


        double xMin = blockItem.getDouble("xMin") + blockItem.getDouble("widthRate") * leftRadio;
        double xMax = blockItem.getDouble("xMax") + blockItem.getDouble("widthRate") * rightRadio;
        double yMin = blockItem.getDouble("yMin") + blockItem.getDouble("heightRate") * topRadio;
        double yMax = blockItem.getDouble("yMax") + blockItem.getDouble("heightRate") * bottomRadio;
        //同时设计了 *OffsetRadio 和 ValueXRangeMin， 按照交集进行计算， 两者都要满足
        //如果用户没有设置 ValueXRangeMin ValueXRangeMax, 都按照默认的 *OffsetRadio的范围
        if( valueXRangeMin> 0 && valueXRangeMin > xMin){
            xMin = valueXRangeMin;
        }

        if( valueXRangeMax<1.0 && valueXRangeMax < xMax){
            xMax = valueXRangeMax;
        }

//        logger.info("[value range] widthRate={}   heightRate={}  \t xMin={} xMax={} yMin={}  yMax={} "
//                ,  df.format(blockItem.getDouble("widthRate")),
//                df.format(blockItem.getDouble("heightRate")),
//                df.format(xMin), df.format(xMax), df.format(yMin), df.format(yMax));


        // step 2. 如果没有设置ValueXRangeMax， 默认只识别最近的单元格, 左边的范围
        if(valueXRangeMax > ConfigConstants.DOUBLE_ONE_VALUE){
            xMin = blockItem.getDouble("xMin") + blockItem.getDouble("widthRate") * leftRadio;
        }



        JSONObject result = new JSONObject();
        result.put("xMin", df.format(xMin -0.001) );
        result.put("xMax", df.format(xMax+ 0.001));
        result.put("yMin", df.format(yMin -0.001));
        result.put("yMax", df.format(yMax+0.001));
        return result;

    }


    /**
     * 删除起始的 冒号
     * @param value
     * @return
     */
    public static String removeInvalidChar(String value){
        if(!StringUtils.hasLength(value)){
            return value;
        }
        if(value.startsWith(":") || value.startsWith("：") || value.endsWith("：") || value.endsWith(":")){
            value = value.replaceAll("[:：]", "");
        }
        return value;
    }

    /**
     * 比较字符串， 忽略特殊符号
     * @param keyString
     * @param itemString
     * @return
     */
    public static boolean compareString(String keyString, String itemString){

        if(keyString == null || itemString == null){
            return false;
        }
        keyString = keyString.replaceAll("[/／:：.。 ]", "");
        itemString = itemString.replaceAll("[/／:：.。 ]", "");
        return keyString.equals(itemString);

    }


    private static boolean isDoubleOrFloat(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
        return pattern.matcher(str).matches();
    }
    /**
     * 或者Item value 里面数字的值，
     * Step 1.  根据空格进行拆分， 找到第一组是数字的字符串，并且返回。
     * Step 2.   过滤掉不是数字的文字， 然后返回。
     * @param value
     * @return
     */
    public static String getItemNumericalValue(String value){
        if(!StringUtils.hasLength(value)){
            return value;
        }
        value = value.replaceAll("[。*,，]", ".");
        String  [] splitArray = value.split(" ");

        for(String tempStr: splitArray){
            if(tempStr.length()>0 && BlockItemUtils.isDoubleOrFloat(tempStr)){
//                System.out.println("----- "+tempStr);
                return  tempStr;
            }
        }

        return value.replaceAll("[^0-9.-]", "");
    }


}
