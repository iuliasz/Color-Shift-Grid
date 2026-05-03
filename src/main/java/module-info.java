module com.colorshiftgrid.colorshiftgrid {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.colorshiftgrid.colorshiftgrid to javafx.fxml;
    exports com.colorshiftgrid.colorshiftgrid;
}