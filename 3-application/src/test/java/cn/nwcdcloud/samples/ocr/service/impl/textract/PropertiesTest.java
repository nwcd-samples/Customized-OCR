package cn.nwcdcloud.samples.ocr.service.impl.textract;

import java.util.Set;

public class PropertiesTest {

	public static void main(String[] args) {
		Set<Object> keys = System.getProperties().keySet();

		for (Object objKey : keys) {
			String key = (String) objKey;
			System.out.println(key + "=" + System.getProperty(key));
		}
	}
}
