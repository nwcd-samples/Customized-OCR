package cn.nwcdcloud.samples.ocr.service.impl.textract;

public class ClientCallTest {

	public static void main(String[] args) {
		int count = 4;
		for (int i = 0; i < count; i++) {
			ClientRunTest test = new ClientRunTest();
			test.start();
		}
	}
}
