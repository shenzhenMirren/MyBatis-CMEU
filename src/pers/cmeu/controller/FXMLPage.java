package pers.cmeu.controller;

public enum FXMLPage {
    CONNECTION("pers/resource/FXML/Connection.fxml"),
    UPDATE_CONNECTION("pers/resource/FXML/UpdateConnection.fxml"),
    HISTORY_CONFIG("pers/resource/FXML/HistoryConfig.fxml"),
    ATTRIBUTE_SET("pers/resource/FXML/AttributeSet.fxml"),
    SET_ATTRIBUTE("pers/resource/FXML/SetAttribute.fxml"),
    ADD_SON_ATTRIBUTE("pers/resource/FXML/AddSonAttribute.fxml"),
	ADD_GRAND_ATTRIBUTE("pers/resource/FXML/AddGrandAttribute.fxml");

    private String fxml;

    FXMLPage(String fxml) {
        this.fxml = fxml;
    }

    public String getFxml() {
        return this.fxml;
    }


}
