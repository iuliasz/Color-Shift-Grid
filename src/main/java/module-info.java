module com.colorshiftgrid{
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jetbrains.annotations;


    opens com.colorshiftgrid.colorshiftgrid to javafx.fxml;
    exports com.colorshiftgrid;
}