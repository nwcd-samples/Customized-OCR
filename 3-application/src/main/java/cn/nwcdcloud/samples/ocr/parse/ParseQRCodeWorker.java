package cn.nwcdcloud.samples.ocr.parse;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.google.zxing.Binarizer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

public class ParseQRCodeWorker {
	private final Logger logger = LoggerFactory.getLogger(ParseQRCodeWorker.class);

	private Map<String, ?> mRootConfig ;
	public ParseQRCodeWorker(Map<String, ?> rootConfig) {
		mRootConfig = rootConfig;
	}

	/**
	 * 处理单个水平元素
	 * 
	 * @param configMap 配置文件Map
	 * @return 解析后的json 文件
	 */
	public JSONObject parse(Map<String, Object> configMap, BufferedImage image) {
		if (image == null) {
			return null;
		}
		String data = decode(configMap, image);
		if (data == null) {
			return null;
		}
		JSONObject resultItem = new JSONObject();
		resultItem.put("name", configMap.get("Name"));
		resultItem.put("confidence", "1.0");
		resultItem.put("value", data);
		return resultItem;
	}

	private String decode(Map<String, Object> configMap, BufferedImage image) {
		String content = null;
		try {
			BufferedImage imageOut = getImage(configMap, image);
			LuminanceSource source = new BufferedImageLuminanceSource(imageOut);
			Binarizer binarizer = new HybridBinarizer(source);
			BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
			Map<DecodeHintType, Object> hints = new HashMap<DecodeHintType, Object>();
			hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
			Result result = new MultiFormatReader().decode(binaryBitmap, hints);// 解码
			content = result.getText();
		} catch (NotFoundException e) {
			logger.warn("识别二维码出错", e);
			return null;
		}
		return content;
	}

	private BufferedImage getImage(Map<String, Object> configMap, BufferedImage image) {
		float xMin = Float.valueOf(configMap.getOrDefault("XRangeMin", 0).toString());
		float xMax = Float.valueOf(configMap.getOrDefault("XRangeMax", 1).toString());
		float yMin = Float.valueOf(configMap.getOrDefault("YRangeMin", 0).toString());
		float yMax = Float.valueOf(configMap.getOrDefault("YRangeMax", 1).toString());
		if (xMin == 0.0f && xMax == 1.0f && yMin == 0.0f && yMax == 1.0f) {
			return image;
		}

		int chunkWidth = (int) (image.getWidth() * (xMax - xMin));
		int chunkHeight = (int) (image.getHeight() * (yMax - yMin));
		BufferedImage imageOut = new BufferedImage(chunkWidth, chunkHeight, image.getType());
		Graphics2D gr = imageOut.createGraphics();
		gr.drawImage(image, 0, 0, chunkWidth, chunkHeight, (int) (image.getWidth() * xMin),
				(int) (image.getHeight() * yMin), (int) (image.getWidth() * xMax), (int) (image.getHeight() * yMax),
				null);
		gr.dispose();
		return imageOut;
	}
}
