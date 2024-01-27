module com.example.mokkivaraus {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.mokkivaraus to javafx.fxml;
    exports com.example.mokkivaraus;
}