module com.example.absdict {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires org.slf4j;

    opens com.example.absdict to javafx.fxml;
    exports com.example.absdict;
}