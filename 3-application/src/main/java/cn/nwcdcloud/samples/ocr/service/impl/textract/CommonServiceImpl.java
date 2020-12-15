package cn.nwcdcloud.samples.ocr.service.impl.textract;

import cn.nwcdcloud.commons.lang.Result;
import cn.nwcdcloud.samples.ocr.service.InferenceService;
import cn.nwcdcloud.samples.ocr.service.TextractService;
import com.alibaba.fastjson.JSONArray;
import org.ho.yaml.Yaml;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

@Service
public class CommonServiceImpl implements TextractService {
    @Autowired
    private InferenceService inferenceService;

    private static final String  ID_SAMPLE_CONFIG_FILE =  "/config/id.yaml";

    @Override
    public Result parse(JSONArray jsonArray) {
        Result result = new Result();
        String data = "[{\"姓名\":\"测试\"}]";
        result.setData(data);
        return result;
    }

    @Override
    @PostConstruct
    public void init() {
        inferenceService.addTextractService("id", this);
    }




}
