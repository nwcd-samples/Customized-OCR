package cn.nwcdcloud.samples.ocr.commons.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 解析Textract返回的json数据， 生成元素列表， 供后续进行结构化提取
 */
public class BlockItemUtils {
    private static final Logger logger = LoggerFactory.getLogger(BlockItemUtils.class);

    public static List<JSONObject> getBlockItemList(JSONObject jsonObject, int pageWidth, int pageHeight){

//        logger.info(jsonObject.toJSONString());
//        logger.info(jsonObject.getJSONObject("DocumentMetadata").toJSONString());
        Integer pageCount = jsonObject.getJSONObject("DocumentMetadata").getInteger("Pages");
        logger.info("页数： {} ",  pageCount);

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
        logger.info(" 共有Blocks {} 个元素 ", jsonArray.size());

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

        logger.info(blockItem.getJSONObject("Geometry").getJSONArray("Polygon").toJSONString());

        JSONArray pointArray = blockItem.getJSONObject("Geometry").getJSONArray("Polygon");

        JSONObject pointA = pointArray.getJSONObject(0);
        JSONObject pointB = pointArray.getJSONObject(1);

        double tan = (pointB.getDouble("Y")  - pointA.getDouble("Y") )/
                (pointB.getDouble("X")  - pointA.getDouble("X"));

        logger.info("倾斜角度 {} ", tan);
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
//        blockItem.put("id", rawBlockItem.getString("Id"));
        blockItem.put("newPoly", newPolyArray);
        blockItem.put("text", deleteUnnecessaryChar(rawBlockItem.getString("Text")));
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

        logger.info("page margin    {} ", pageMargin.toJSONString());
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

//        logger.info("-------- {} ",blockItem.toJSONString());

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

}
