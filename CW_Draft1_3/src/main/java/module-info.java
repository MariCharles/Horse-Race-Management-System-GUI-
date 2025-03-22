module com.example.cw_draft1_3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;


    opens com.example.cw_draft1_3 to javafx.fxml;
    exports com.example.cw_draft1_3;
}