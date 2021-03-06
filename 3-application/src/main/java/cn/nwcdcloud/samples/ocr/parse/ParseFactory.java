package cn.nwcdcloud.samples.ocr.parse;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.ehcache.Cache;
import org.ho.yaml.Yaml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.nwcdcloud.commons.util.ApplicationUtils;
import cn.nwcdcloud.samples.ocr.constant.ImageType;
import cn.nwcdcloud.samples.ocr.service.S3Service;
import software.amazon.awssdk.core.SdkBytes;

public class ParseFactory {
	private final Logger logger = LoggerFactory.getLogger(ParseFactory.class);

	private String configType;
	private String templateDir;

	public ParseFactory(String configType) {
		this.configType = configType;
	}

	public ParseFactory(String configType, String templateDir) {
		this.configType = configType;
		this.templateDir = templateDir;
	}

	public JSONObject extractValue(List<JSONObject> blockItemList) {
		return extractValue(blockItemList, 0, null);
	}

	public JSONObject extractValue(List<JSONObject> blockItemList, int imageType, String imageContent) {
		Map<String, ?> configMap = readConfig(this.configType, this.templateDir);
		if (configMap == null) {
			return null;
		}
		ParseKeyValueWorker horizontalWorker = new ParseKeyValueWorker(configMap);
		ParseTablesWorker tablesWorker = new ParseTablesWorker(configMap);
		ParseFixedPosition fixedPositionWorker = new ParseFixedPosition(configMap);
		ParseQRCodeWorker qrcodeWorker = new ParseQRCodeWorker(configMap);
		JSONArray keyValueArray = new JSONArray();
		JSONArray tableArray = new JSONArray();
		JSONObject jsonResult = new JSONObject();
		BufferedImage image = null;

		@SuppressWarnings("unchecked")
		List<HashMap<String, Object>> targetList = (List<HashMap<String, Object>>) configMap.get("Targets");
		for (HashMap<String, Object> newItem : targetList) {
			if (!newItem.containsKey("Name")) {
				throw new IllegalArgumentException(" 配置文件必须包含  'Name' 选项 ");
			}

			String recognitionType = newItem.getOrDefault("RecognitionType", "Default").toString();
			if ("Default".equalsIgnoreCase(recognitionType) || "KeyValue".equalsIgnoreCase(recognitionType)) {
				// 识别单个key-value元素
				JSONObject resultItem = horizontalWorker.parse(newItem, blockItemList);
				if (resultItem != null) {
					keyValueArray.add(resultItem);
				}
			} else if ("Table".equalsIgnoreCase(recognitionType)) {
				JSONObject result = tablesWorker.parse(newItem, blockItemList);
				if (result != null) {
					tableArray.add(result);
				}
			} else if ("FixedPosition".equalsIgnoreCase(recognitionType)) {
				JSONObject result = fixedPositionWorker.parse(newItem, blockItemList);
				if (result != null) {
					keyValueArray.add(result);
				}

			} else if ("Qrcode".equalsIgnoreCase(recognitionType)) {
				if (image == null) {
					image = getImage(imageType, imageContent);
				}
				JSONObject resultItem = qrcodeWorker.parse(newItem, image);
				if (resultItem != null) {
					keyValueArray.add(resultItem);
				}
			} else {
				throw new IllegalArgumentException(" 没有  '" + recognitionType + "' 的识别类型， 请检查 RecognitionType 配置 ");
			}
		}
		jsonResult.put("keyValueList", keyValueArray);
		jsonResult.put("tableList", tableArray);
		return jsonResult;
	}

	private BufferedImage getImage(int imageType, String imageContent) {
		if (imageType == ImageType.Byte.getId()) {
			Cache<String, byte[]> cacheImageByte = ApplicationUtils.getBean("cacheImageByte");
			try {
				byte[] imageByte = cacheImageByte.get(imageContent);
				if (imageByte == null) {
					return null;
				}
				return ImageIO.read(SdkBytes.fromByteArray(imageByte).asInputStream());
			} catch (Exception e) {
				logger.warn("载入图片信息出错", e);
				return null;
			}
		}
		if (imageType == ImageType.JSON.getId()) {
			S3Service s3Service = ApplicationUtils.getBean(S3Service.class);
			try {
				String content = imageContent;
				if (!StringUtils.hasLength(content)) {
					return null;
				}
				logger.debug("获取图片内容:{}", content);
				JSONObject jsonContent = JSON.parseObject(content);
				String bucket = jsonContent.getString("bucket");
				String keyName = jsonContent.getString("image_uri");
				byte[] imageByte = s3Service.getContent(null, bucket, keyName);
				return ImageIO.read(SdkBytes.fromByteArray(imageByte).asInputStream());
			} catch (Exception e) {
				logger.warn("载入图片信息出错", e);
				return null;
			}
		}
		return null;
	}

	/**
	 * 读取配置文件
	 *
	 * @param configType
	 * @param templateDir
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, ?> readConfig(String configType, String templateDir) {
		InputStream is = null;
		if (StringUtils.hasLength(templateDir)) {
			String configExternal = templateDir + configType + ".yaml";
			File file = new File(configExternal);
			if (file.exists()) {
				try {
					is = new FileInputStream(file);
				} catch (FileNotFoundException e) {
					is = null;
				}
			}
		}
		if (is == null) {
			String configInternal = "config/" + configType + ".yaml";
			is = this.getClass().getClassLoader().getResourceAsStream(configInternal);
		}
		if (is == null) {
			logger.error("未找到外部配置文件：{}，外部配置文件目录：{}", configType, templateDir);
			return null;
		}
		Map<String, ?> rootMap = null;
		try {
			rootMap = Yaml.loadType(is, HashMap.class);
		} catch (Exception e) {
			logger.error("读取配置文件出错:" + configType, e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					logger.warn("关闭配置文件出错{}", configType);
				}
			}
		}
		return rootMap;
	}

	/**
	 * Cell类 封装页面使用
	 */
	public static class Cell {
		public Cell() {
			text = "";
			confidence = 1.0f;
		}

		public String text;
		public float confidence;
	}
}