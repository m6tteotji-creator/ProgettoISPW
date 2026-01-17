module com.example.onetour {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires java.sql;
    requires com.opencsv;

    opens com.example.onetour.graphicController to javafx.fxml;

    exports com.example.onetour;
}
