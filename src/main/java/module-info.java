module com.caesaryh.comp2005fe {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.net.http;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires static lombok;

    opens com.caesaryh.comp2005fe to javafx.fxml;
    exports com.caesaryh.comp2005fe;

    exports com.caesaryh.comp2005fe.utils.models;
}