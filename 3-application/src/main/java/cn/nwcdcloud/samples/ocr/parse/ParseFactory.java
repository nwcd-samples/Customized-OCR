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

	private int pageWidth;
	private int pageHeight;
	private String configType;
	private String templateDir;

	public ParseFactory(int pageWidth, int pageHeight, String configType) {
		this.pageWidth = pageWidth;
		this.pageHeight = pageHeight;
		this.configType = configType;
	}

	public ParseFactory(int pageWidth, int pageHeight, String configType, String templateDir) {
		this.pageWidth = pageWidth;
		this.pageHeight = pageHeight;
		this.configType = configType;
		this.templateDir = templateDir;
	}

	public JSONObject extractValue(List<JSONObject> blockItemList) {
		return extractValue(blockItemList, 0, null);
	}

	public JSONObject extractValue(List<JSONObject> blockItemList, int imageType, String imageContent) {
		Map<String, ?> configMap = readConfig(this.configType, this.templateDir);
		ParseHorizontalWorker horizontalWorker = new ParseHorizontalWorker(configMap, pageWidth, pageHeight);
		ParseTablesWorker tablesWorker = new ParseTablesWorker(configMap, pageWidth, pageHeight);
		ParseQRCodeWorker qrcodeWorker = new ParseQRCodeWorker(configMap);
		JSONArray keyValueArray = new JSONArray();
		JSONArray tableArray = new JSONArray();
		JSONObject jsonResult = new JSONObject();
		BufferedImage image = null;

		@SuppressWarnings("unchecked")
		List<HashMap<String, Object>> targetList = (List<HashMap<String, Object>>) configMap.get("Targets");
		for (HashMap<String, Object> newItem : targetList) {
			String recognitionType = newItem.getOrDefault("RecognitionType", "default").toString();
			if ("default".equals(recognitionType)) {
				// 识别单个key-value元素
				JSONObject resultItem = horizontalWorker.parse(newItem, blockItemList);
				if (resultItem != null) {
					keyValueArray.add(resultItem);
				}
			} else if ("table".equals(recognitionType)) {
				JSONObject result = tablesWorker.parse(newItem, blockItemList);
				if (result != null) {
					tableArray.add(result);
				}
			} else if ("qrcode".equals(recognitionType)) {
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
		String configPath;
		if (StringUtils.hasLength(templateDir)) {
			configPath = templateDir + configType + ".yaml";
			File file = new File(configPath);
			if (file.exists()) {
				try {
					is = new FileInputStream(file);
				} catch (FileNotFoundException e) {
					is = null;
				}
			} else {
				logger.info("未找到配置文件{}", configPath);
			}
		}
		if (is == null) {
			configPath = "config/" + configType + ".yaml";
			is = this.getClass().getClassLoader().getResourceAsStream(configPath);
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