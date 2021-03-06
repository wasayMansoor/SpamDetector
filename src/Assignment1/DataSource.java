package Assignment1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DataSource {
    public static ObservableList<TestFile> getTestFiles(){
        ObservableList<Object> list = FXCollections.observableArrayList();
        return list;
    }
}
