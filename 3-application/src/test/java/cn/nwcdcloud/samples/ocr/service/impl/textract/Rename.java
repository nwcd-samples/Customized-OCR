package cn.nwcdcloud.samples.ocr.service.impl.textract;

import java.io.File;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Rename {
	private static final Logger logger = LoggerFactory.getLogger(Rename.class);

	public static void main(String[] args) {
		if (args.length != 1) {
			logger.error("请输入需要重命名路径");
			return;
		}
		String dir = args[0];
		File folder = new File(dir);
		int i = 101;
		String[] files = folder.list();
		Arrays.sort(files);
		for (String fileName : files) {
			String extName = getImageExtName(fileName);
			if (extName == null) {
				continue;
			}
			File oldFile = new File(dir + File.separator + fileName);
			String newFileName = String.format("%03d", i);
			File newFile = new File(dir + File.separator + newFileName + "." + extName);
			oldFile.renameTo(newFile);
			i++;
		}
	}

	private static String getImageExtName(String fileName) {
		String[] extNames = { "jpg", "jpeg", "bmp", "png" };
		fileName = fileName.toLowerCase();
		for (String extName : extNames) {
			if (fileName.endsWith(extName)) {
				return extName;
			}
		}
		return null;
	}

}
