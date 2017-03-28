package pers.cmeu.controller;

public enum FXMLPage {
    CONNECTION("pers/resource/FXML/Connection.fxml"),
    HISTORY_CONFIG("pers/resource/FXML/HistoryConfig.fxml"),
    ATTRIBUTE_SET("pers/resource/FXML/AttributeSet.fxml"),
    ADD_MORE_ATTRIBUTE("pers/resource/FXML/AddMoreAttribute.fxml"),;

    private String fxml;

    FXMLPage(String fxml) {
        this.fxml = fxml;
    }

    public String getFxml() {
        return this.fxml;
    }


}
