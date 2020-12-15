package cn.nwcdcloud.commons.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TextractUtil {
    private static final Logger logger = LoggerFactory.getLogger(TextractUtil.class);
    public static void parseData(JSONObject jsonObject){

        logger.info(jsonObject.toJSONString());
        logger.info(jsonObject.getJSONObject("DocumentMetadata").toJSONString());
        Integer pageCount = jsonObject.getJSONObject("DocumentMetadata").getInteger("Pages");
        logger.info("" + pageCount);

        if(pageCount<=0){
            logger.warn("该文档 没有内容");
            return;
        }


        float margin_document_top = 0.0f;      //累计文档开始高度
        List blockItemList =  new ArrayList();    //保存所有元素的列表
        float document_page_height = 0.0f;      //文档的累计总高度

        //TODO:  先处理单页的情况，
        blockItemList = parseDataByPageCount(1, jsonObject.getJSONArray("Blocks"));


    }

    private static List parseDataByPageCount(int pageCount, JSONArray jsonArray){
        List<JSONObject> blockList = new ArrayList<JSONObject>();
        logger.info(" 共有Blocks {} 个元素 ", jsonArray.size());

        for(int i=0; i<jsonArray.size(); i++){
            JSONObject blockItem = jsonArray.getJSONObject(i);
            String blockType = blockItem.getString("BlockType");
            if(blockItem.getInteger("Page") == pageCount && ("WORD".equals(blockType) || "LINE".equals(blockType))){
                blockList.add(blockItem);
                logger.info(" {} {} {} ", blockItem.getString("Page"), blockItem.getString("BlockType"),
                        blockItem.getString("Text"));
            }

        }

        if(blockList.size() == 0){
            return blockList;
        }
        // 取出最长的元素， 找到旋转角度， 让它保持水平。
        JSONObject maxWidthBlockItem = findMaxWidthBlockItem(blockList);

        List<Double> matrixList = computeDegree(maxWidthBlockItem);

        List<JSONObject> newBlockItemList = new ArrayList<JSONObject>();

        for(int i=0; i<blockList.size(); i++){

            createBlockItem(matrixList, blockList.get(i));

        }

        return null;
    }


    /**
     找到最宽的元素， 用它来进行页面的旋转
     */

    private static JSONObject findMaxWidthBlockItem(List<JSONObject> blockList){
        JSONObject resultItem = null;
        double max_width = 0.0;
        for(int i=0; i< blockList.size(); i++){
            JSONObject item = blockList.get(i);

            BigDecimal width = item.getJSONObject("Geometry").getJSONObject("BoundingBox").getBigDecimal("Width");
            if( width.doubleValue() > max_width){
                max_width = width.doubleValue();
                resultItem = item;
            }

        }
        if(resultItem !=null){
            logger.info("findMaxWidthBlockItem {}", resultItem.toJSONString());
        }else {
            logger.warn("findMaxWidthBlockItem  没有找到结果");
        }

        return resultItem;


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
     计算所有元素经过旋转以后的新坐标
     */
    private static void createBlockItem(List<Double> matrixList, JSONObject rawBlockItem){

        JSONArray pointArray = rawBlockItem.getJSONObject("Geometry").getJSONArray("Polygon");
        logger.info("createBlockItem ---------------   {} ", rawBlockItem.getString("Text"));

        JSONArray newPolyArray = new JSONArray();
        for(int i=0; i<pointArray.size(); i++){

            JSONObject ploy = new JSONObject();
            ploy.put("x", pointArray.getJSONObject(i).getDouble("X") -0.5) ;
            ploy.put("y", pointArray.getJSONObject(i).getDouble("Y") -0.5) ;
            JSONObject newPloy = matrixRotate(matrixList, ploy);
            newPloy.put("x", newPloy.getDouble("x")+ 0.5);
            newPloy.put("y", newPloy.getDouble("y")+ 0.5);
            newPolyArray.add(newPloy);

            logger.info(" x: {} y: {} ", newPloy.getString("x"), newPloy.getString("y"));
//            logger.info(" x: {} y: {} ", pointArray.getJSONObject(i).getDouble("X"), pointArray.getJSONObject(i).getDouble("Y"));
        }




//        //对坐标按照原点进行旋转
//        for(j=0; j<polyList.length; j++){
//            //围绕中心点旋转
//            ploy = {}
//            ploy['x'] = polyList[j]['X'] - 0.5
//            ploy['y'] = polyList[j]['Y'] - 0.5
//            newPloy = matrix_rotate(matrix, ploy)
//            newPloy['x'] =   newPloy['x']+ 0.5
//            newPloy['y'] =   newPloy['y']+ 0.5
//            polyArray.push(newPloy)
//        }
//
//
////    封装block 元素， 供页面显示
//        var blockItem = {
//                id:block['Id'],
//                raw_block_type: block['BlockType'],   // LINE or WORD
//                newPoly:polyArray,
//                selected:0,  // 是否选中
//                blockType:0, //0 默认;  1 表头; 2 表格中的值
//                text:block['Text']
//        };
//
//        // 如果是行元素， 保存它的孩子元素 WORD
//        if(blockItem['raw_block_type'] == 'LINE' ){
//            blockItem['child_list'] = block['Relationships'][0]['Ids']
//            blockItem['child_count'] = block['Relationships'][0]['Ids'].length
//            blockItem['is_split'] = false  // 是否做拆分
//        }


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

//    function matrix_rotate(m, point){
//
//        var a = point['x']
//        var b = point['y']
//        var X = m[0]*a + m[1]*b
//        var Y = m[2]*a + m[3]*b
//        var new_point = {}
//        new_point['x']=X
//        new_point['y']=Y
////    console.log('point: %f, %f , new point: %f  %f',a, b , X, Y)
//        return new_point
//    }


}
