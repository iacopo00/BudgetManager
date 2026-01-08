module it.unipi.budgetmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.sql;
    requires java.base;
    requires java.desktop;
    requires xstream;

    opens it.unipi.budgetmanager to javafx.fxml;
    exports it.unipi.budgetmanager;
}
