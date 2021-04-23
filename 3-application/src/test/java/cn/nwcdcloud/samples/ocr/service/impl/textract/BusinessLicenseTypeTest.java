package cn.nwcdcloud.samples.ocr.service.impl.textract;

import java.util.List;

import cn.nwcdcloud.samples.ocr.parse.ConfigConstants;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.nwcdcloud.samples.ocr.parse.BlockItemUtils;
import cn.nwcdcloud.samples.ocr.parse.FileUtils;
import cn.nwcdcloud.samples.ocr.parse.ParseFactory;


public class BusinessLicenseTypeTest {
    private static final Logger logger = LoggerFactory.getLogger(BusinessLicenseTypeTest.class);
    private static final String  SAMPLE_JSON_OBJECT_FILE_1 =  "/sample/business_license001.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_2 =  "/sample/business_license002.json";



    @Test
    public void parse001() {

        String jsonObjectPath=this.getClass().getResource(SAMPLE_JSON_OBJECT_FILE_1).getFile().toString();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
        List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject, ConfigConstants.PAGE_WIDTH, ConfigConstants.PAGE_HEIGHT);
        ParseFactory parseJsonUtil = new ParseFactory(ConfigConstants.PAGE_WIDTH, ConfigConstants.PAGE_HEIGHT, "business_license");
        JSONObject resultObject = parseJsonUtil.extractValue(blockItemList);
        JSONArray  resultArray =  resultObject.getJSONArray("keyValueList");
        logger.info(resultObject.toJSONString());
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "名称", "河北晟途电器有限公司");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "类型", "有限责任公司（自然人投资或控股）");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "住所", "河北省廊坊市文安县大围河回族满族乡南辛庄村");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "统一社会信用代码", "911310263362986212");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "法定代表人", "张建柱");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "注册资本", "伍仟壹佰捌拾万元整");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "成立日期", "2015年05月28日");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "营业期限", "2015年05月28日至2035年05月");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "经营范围", "电缆桥架、配电箱、母线槽制造销售。");

    }

    @Test
    public void parse002() {

        String jsonObjectPath=this.getClass().getResource(SAMPLE_JSON_OBJECT_FILE_2).getFile().toString();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
        List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject, ConfigConstants.PAGE_WIDTH, ConfigConstants.PAGE_HEIGHT);
        ParseFactory parseJsonUtil = new ParseFactory(ConfigConstants.PAGE_WIDTH, ConfigConstants.PAGE_HEIGHT, "business_license");
        JSONObject resultObject = parseJsonUtil.extractValue(blockItemList);
        JSONArray  resultArray =  resultObject.getJSONArray("keyValueList");
        logger.info(resultArray.toJSONString());
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "注册号", "61040361");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "名称", "杨凌中港西餐厅");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "类型", "个体");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "经营场所", "陕西省杨凌示范区康乐路名都酒店二楼");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "组成形式", "个人经营");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "成立日期", "2016年10月10日");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "经营范围", "餐饮、西餐、预包装食品：服务兼零售。（依法须经批准的理经相关部门批准后方可开展经营活动）");

    }







}