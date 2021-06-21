package cn.nwcdcloud.samples.ocr.parse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface ConfigConstants {

    Logger logger = LoggerFactory.getLogger(ConfigConstants.class);
    boolean DEBUG_PARSE_KEY_VALUE =  false && logger.isDebugEnabled();
    boolean DEBUG_PARSE_FIXED_POSITION =  false && logger.isDebugEnabled();
    boolean DEBUG_PARSE_TABLE =  false && logger.isDebugEnabled();

    //用于计算页面的坐标值， 主要是为了和显示的坐标统一， 目前都用百分比进行判断。
    int PAGE_WIDTH = 1200;
    int PAGE_HEIGHT = 2000;

    //  LengthMax 每个元素最大字符数
    int ITEM_LENGTH_MAX = 40;
    // LineCountMax  Value 元素可以有多行
    int ITEM_LINE_COUNT_MAX = 1;

    //通过key 查找value 元素的范围， 相对key的坐标范围
    double ITEM_OFFSET_TOP_RADIO = -1.0;
    double ITEM_OFFSET_BOTTOM_RADIO = 1.0;
    double ITEM_OFFSET_LEFT_RADIO = -0.3;
    double ITEM_OFFSET_RIGHT_RADIO = 30;

    //Value 的左右边界
    double ITEM_VALUE_X_RANGE_MIN = 0.0;
    double ITEM_VALUE_X_RANGE_MAX = 1.0;

    //FIXME: 合并下面两个值
    double PARSE_CELL_ERROR_RANGE_MIN      = 0.002;



    // 表格解析的最大的行数
    int TABLE_MAX_ROW_COUNT = 20;
    // 表格用来迭代查找行的比率   nextItem.top <=  item.bottom +  ratio * item.height
    double TABLE_MAX_ROW_HEIGHT_RATIO = 2.0;

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


    int PARSE_TABLE_CELL_VALUE_DIRECTION_FROM_LEFT = 0;
    int PARSE_TABLE_CELL_VALUE_DIRECTION_FROM_RIGHT = 1;

}
