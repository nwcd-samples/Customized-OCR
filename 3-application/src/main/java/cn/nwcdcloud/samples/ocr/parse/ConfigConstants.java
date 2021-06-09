package cn.nwcdcloud.samples.ocr.parse;

public interface ConfigConstants {

    boolean DEBUG_PARSE_KEY_VALUE =  true;
    boolean DEBUG_PARSE_FIXED_POSITION =  true;
    boolean DEBUG_PARSE_TABLE =  true;

//    boolean DEBUG_FLAG = true;
    int PAGE_WIDTH = 1200;
    int PAGE_HEIGHT = 2000;

    //  LengthMax 每个元素最大字符数
    int ITEM_LENGTH_MAX = 40;
    // LineCountMax  Value 元素可以有多行
    int ITEM_LINE_COUNT_MAX = 1;


    //通过key 查找value 元素的范围， 相对key的坐标范围
    double ITEM_OFFSET_TOP_RADIO = -1.0;
    double ITEM_OFFSET_BOTTOM_RADIO = 1.0;
    double ITEM_OFFSET_LEFT_RADIO = -0.5;
    double ITEM_OFFSET_RIGHT_RADIO = 30;

    //Value 的左右边界
    double ITEM_VALUE_X_RANGE_MIN = 0.0;
    double ITEM_VALUE_X_RANGE_MAX = 1.0;

    // 解析表格 定位 cell元素， 上下设置一个误差范围
    int PARSE_CELL_ERROR_RANGE_TOP      = 20;
    int PARSE_CELL_ERROR_RANGE_BOTTOM   = 20;

    //FIXME: 合并下面两个值
    double PARSE_CELL_ERROR_RANGE_MIN      = 0.002;
    int PARSE_CELL_ERROR_RANGE_MIN_INT      = 10;

    //FIXME：  上下元素有误差， 扩大范围 进行兼容， 后面根据每个单据倾斜 密集程度不一样， 可以对误差进行设置。
    int PARSE_CELL_ERROR_RANGE_MAX      = 30;

    // 表格解析的最大的行数
    int TABLE_MAX_ROW_COUNT = 20;
    // 表格用来迭代查找行的比率   nextItem.top <=  item.bottom +  ratio * item.height
    double TABLE_MAX_ROW_HEIGHT_RATIO = 2.0;

    int TABLE_MAIN_COLUMN_INDEX  = 0 ;




    String TABLE_MARGIN_TYPE_NEAR               = "near";
    String TABLE_MARGIN_TYPE_MIDDLE             = "middle";
    String TABLE_MARGIN_TYPE_FAR                = "far";

    float TABLE_DEFAULT_MARGIN_LEFT_RATIO      = 0.0f;
    float TABLE_DEFAULT_MARGIN_RIGHT_RATIO      = 0.0f;


    // 每个元素在页面的坐标范围
    double PAGE_RANGE_X_MIN = 0.0;
    double PAGE_RANGE_X_MAX = 1.0;
    double PAGE_RANGE_Y_MIN = 0.0;
    double PAGE_RANGE_Y_MAX = 1.0;



    double DOUBLE_ONE_VALUE = 0.99999d;


    double COMPARE_HEIGHT_RATE = 3.0d;

}
