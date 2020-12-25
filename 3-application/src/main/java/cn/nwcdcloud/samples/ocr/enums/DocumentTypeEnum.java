package cn.nwcdcloud.samples.ocr.enums;

public enum DocumentTypeEnum {

    IDENTITY_CARD(1,"身份证", "config/identity_card.yaml"),
    INVOICE(2,"标注电子发票", "config/invoice.yaml");

    private int code;
    private String name;


    private String path;
    private DocumentTypeEnum(int code,String name, String path) {
        this.code = code;
        this.name = name;
        this.path = path;
    }

    public int getCode() {
        return code;
    }
    public String getName() {
        return name;
    }
    public void setCode(int code) {
        this.code = code;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return this.getName()+"---"+this.getCode();
    }
}
