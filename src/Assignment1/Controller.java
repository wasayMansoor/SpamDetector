package Assignment1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Controller {
    @FXML
    private TableView<> table;
    @FXML
    private TableColumn<Object, Object> File;
    @FXML
    private TableColumn<Object, Object> ActualClass;
    @FXML
    private TableColumn<Object, Object> SpamProbability;
    @FXML
    private TextField Accuracy;
    @FXML
    private TextField Precision;
    @FXML
    private TextField ;
    @FXML
    private TextField Precision;

    private HashMap<String,Double> hamFreq = new HashMap<String, Double>();
    private HashMap<String,Integer> numHamWords = new HashMap<String,Integer>();
    private HashMap<String,Double> spamFreq = new HashMap<String, Double>();
    private HashMap<String,Integer> numSpamWords = new HashMap<String,Integer>();
    private HashMap<String,Double> specificSpamWord = new HashMap<String,Double>();

    double truePostivesCount = 0;
    double falsePositivesCount = 0;
    double trueNegativesCount = 0;
    double acc;
    double prec;
    double testFilesCount;

    private boolean findWord(String text){
        //Regex check
        String regex = "^[A-Za-z]*$";
        if (text.matches(regex)){
            return true;
        }
        return false;
    }

    public void TrainButtonAction(ActionEvent event){

        DirectoryChooser directory = new DirectoryChooser();
        directory.setInitialDirectory(new File("."));
        File md = directory.showDialog(null);

        if (md != null){
            String path = md.getAbsolutePath();
            trainpathID.setText(path);
            processTrain(md);

            // P(S|W) = P(W|S) / ( P(W|S) + P(W|H) ) and store in map
            trainSpamGivenWord();
        }else{
            System.out.println("Directory not valid");
        }
    }



}
