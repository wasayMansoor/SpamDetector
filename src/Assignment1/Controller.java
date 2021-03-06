package Assignment1;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class Controller {
    @FXML
    private TableView<TestFile> table;
    @FXML
    private TableColumn<TestFile, String> FileName;
    @FXML
    private TableColumn<TestFile, String> ActualClass;
    @FXML
    private TableColumn<TestFile, Double> SpamProbability;
    @FXML
    private TextField Accuracy;
    @FXML
    private TextField Precision;

}
