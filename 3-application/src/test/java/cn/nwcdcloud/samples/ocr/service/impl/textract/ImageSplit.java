package cn.nwcdcloud.samples.ocr.service.impl.textract;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageSplit {

	public static void main(String[] args) throws IOException {
		split(args[0], Float.valueOf(args[1]), Float.valueOf(args[2]), Float.valueOf(args[3]), Float.valueOf(args[4]));
	}

	public static void split(String originalFileName, float xMin, float xMax, float yMin, float yMax)
			throws IOException {
		File file = new File(originalFileName);
		FileInputStream fis = new FileInputStream(file);
		BufferedImage image = ImageIO.read(fis);

		// 计算每个小图的宽度和高度
		int chunkWidth = (int) (image.getWidth() * (xMax - xMin));
		int chunkHeight = (int) (image.getHeight() * (yMax - yMin));

		// 设置小图的大小和类型
		BufferedImage imgOut = new BufferedImage(chunkWidth, chunkHeight, image.getType());

		// 写入图像内容
		Graphics2D gr = imgOut.createGraphics();
		gr.drawImage(image, 0, 0, chunkWidth, chunkHeight, (int) (image.getWidth() * xMin),
				(int) (image.getHeight() * yMin), (int) (image.getWidth() * xMax), (int) (image.getHeight() * yMax),
				null);
		gr.dispose();

		// 输出小图
		int index = originalFileName.lastIndexOf(".");
		String extName = originalFileName.substring(index + 1);
		String descFileName = originalFileName.substring(0, index) + "_cut" + originalFileName.substring(index);
		ImageIO.write(imgOut, extName, new File(descFileName));
	}
}
