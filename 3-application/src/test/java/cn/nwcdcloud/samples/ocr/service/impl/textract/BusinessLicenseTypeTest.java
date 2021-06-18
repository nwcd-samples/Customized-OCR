package cn.nwcdcloud.samples.ocr.service.impl.textract;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.nwcdcloud.samples.ocr.parse.BlockItemUtils;
import cn.nwcdcloud.samples.ocr.parse.ConfigConstants;
import cn.nwcdcloud.samples.ocr.parse.FileUtils;
import cn.nwcdcloud.samples.ocr.parse.ParseFactory;


public class BusinessLicenseTypeTest {
    private static final Logger logger = LoggerFactory.getLogger(BusinessLicenseTypeTest.class);
    private static final String  SAMPLE_JSON_OBJECT_FILE_1 =  "/samples/business_license001.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_2 =  "/samples/business_license002.json";
    private static final String  SAMPLE_JSON_OBJECT_FILE_3 =  "/samples/business_license003.json";



    @Test
    public void parse001() {

        String jsonObjectPath=this.getClass().getResource(SAMPLE_JSON_OBJECT_FILE_1).getFile().toString();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
        List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject);
        ParseFactory parseJsonUtil = new ParseFactory("business_license_old");
        JSONObject resultObject = parseJsonUtil.extractValue(blockItemList);
        JSONArray  resultArray =  resultObject.getJSONArray("keyValueList");
        logger.info(resultObject.toJSONString());

        for(int i=0; i< resultArray.size(); i++){
            String tempValue = resultArray.getJSONObject(i).getString("value");
            String tempName = resultArray.getJSONObject(i).getString("name");
            logger.info("[{}] [{}]", tempName, tempValue);
        }
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
        List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject);
        ParseFactory parseJsonUtil = new ParseFactory( "business_license_old");
        JSONObject resultObject = parseJsonUtil.extractValue(blockItemList);
        JSONArray  resultArray =  resultObject.getJSONArray("keyValueList");
        for(int i=0; i< resultArray.size(); i++){
            String tempValue = resultArray.getJSONObject(i).getString("value");
            String tempName = resultArray.getJSONObject(i).getString("name");
            logger.info("[{}] [{}]", tempName, tempValue);
        }
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "注册号", "61040361");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "名称", "杨凌中港西餐厅");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "类型", "个体");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "经营场所", "陕西省杨凌示范区康乐路名都酒店二楼");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "组成形式", "个人经营");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "成立日期", "2016年10月10日");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "经营范围", "餐饮、西餐、预包装食品：服务兼零售。（依法须经批准的理经相关部门批准后方可开展经营活动）");

    }




    @Test
    public void parse003() {

        String jsonObjectPath=this.getClass().getResource(SAMPLE_JSON_OBJECT_FILE_3).getFile().toString();
        JSONObject jsonObject = FileUtils.readJsonObject(jsonObjectPath);
        List<JSONObject> blockItemList = BlockItemUtils.getBlockItemList(jsonObject);
        ParseFactory parseJsonUtil = new ParseFactory("business_license03");
        JSONObject resultObject = parseJsonUtil.extractValue(blockItemList);
        JSONArray  resultArray =  resultObject.getJSONArray("keyValueList");
//        logger.info(resultArray.toJSONString());

        for(int i=0; i< resultArray.size(); i++){
            String tempValue = resultArray.getJSONObject(i).getString("value");
            String tempName = resultArray.getJSONObject(i).getString("name");
            logger.info("[{}]    \t    [{}]",tempName, tempValue );
        }

        assert  BlockItemUtils.checkKeyValueMap(resultArray, "名称", "北京万裕久鑫科技有限公司");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "类型", "有限责任公司（自然人独资）");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "法定代表人", "邓明生");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "经营范围", "技术开发、技术推广、技术转让、技术服务、技术咨询计算机系统服务：基础软件服务：应用软件服务；软件开发：软件咨询：设计、制作、代理、发布广告。（市场主体依法自主选择经营项目，开展经营活动；依法须经批准的项目，经相关部门批准后依批准的内容开展经营活动不得从事国家和本市产业政策禁止和限制类项目的经营活动。）");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "注册资本", "100万元");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "成立日期", "2017年03月31日");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "营业期限", "2017年03月31日至2047年03月30日");
        assert  BlockItemUtils.checkKeyValueMap(resultArray, "住所", "北京市昌平区回龙观东大街338号创客广场A5-026");



    }




}