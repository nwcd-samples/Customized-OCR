package cn.nwcdcloud.samples.ocr.constant;

public enum ImageType {
	Byte(1), JSON(2);

	private int id;

	private ImageType(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
}
