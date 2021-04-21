package cn.nwcdcloud.samples.ocr.parse;

public interface ConfigConstants {

    boolean DEBUG_FLAG = true;
    int PAGE_WIDTH = 1200;
    int PAGE_HEIGHT = 1200;

    //  LengthMax 每个元素最大字符数
    int ITEM_LENGTH_MAX = 20;
    // LineCountMax  Value 元素可以有多行
    int ITEM_LINE_COUNT_MAX = 1;


    double ITEM_OFFSET_TOP_RADIO = 1.2;
    double ITEM_OFFSET_BOTTOM_RADIO = 1.2;
    double ITEM_OFFSET_LEFT_RADIO = 0;
    double ITEM_OFFSET_RIGHT_RADIO = 5;

    // 解析表格 定位 cell元素， 上下设置一个误差范围
    int PARSE_CELL_ERROR_RANGE_TOP      = 20;
    int PARSE_CELL_ERROR_RANGE_BOTTOM   = 20;
    int PARSE_CELL_ERROR_RANGE_MIN      = 10;
    //FIXME：  上下元素有误差， 扩大范围 进行兼容， 后面根据每个单据倾斜 密集程度不一样， 可以对误差进行设置。
    int PARSE_CELL_ERROR_RANGE_MAX      = 30;

    // 表格解析的最大的行数
    int TABLE_MAX_ROW_COUNT = 10;
    // 表格用来迭代查找行的比率   nextItem.top <=  item.bottom +  ratio * item.height
    double TABLE_MAX_ROW_HEIGHT_RATIO = 2.0;

    int TABLE_MAIN_COLUMN_INDEX  = 0 ;




    String TABLE_MARGIN_TYPE_NEAR               = "near";
    String TABLE_MARGIN_TYPE_MIDDLE             = "middle";
    String TABLE_MARGIN_TYPE_FAR                = "far";

    float TABLE_DEFAULT_MARGIN_LEFT_RATIO      = 0.0f;
    float TABLE_DEFAULT_MARGIN_RIGHT_RATIO      = 0.0f;


//    int marginLeftType = Integer.parseInt(infoMap.getOrDefault("MarginLeftType", 2).toString());
//    int marginRightType = Integer.parseInt(infoMap.getOrDefault("MarginRightType", 2).toString());
//
//    float moveLeftRatio = Float.parseFloat(infoMap.getOrDefault("MoveLeftRatio", "0.0").toString());
//    float moveRightRatio = Float.parseFloat(infoMap.getOrDefault("MoveRightRatio", "0.0").toString());



    String PARSE_KEY_TYPE_SINGLE = "single" ;
    // 比较极端的情况  例如 【名    称】 两个字符离的比较远， 识别成了两个元素， 用'名' 和'称' 两个字同时去匹配。
    String PARSE_KEY_TYPE_MULTIPLE = "multiple" ;
}
