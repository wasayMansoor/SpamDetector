package Assignment1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import javafx.stage.DirectoryChooser;


public class Controller {


    //private File mainDir= new File("/D:/OTU/Winter2021/CSCI2020U/Assignment1/data");

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

    private HashMap<String, Double> hamFreq = new HashMap<String, Double>();
    private HashMap<String, Integer> numHamWords = new HashMap<String, Integer>();
    private HashMap<String, Double> spamFreq = new HashMap<String, Double>();
    private HashMap<String, Integer> numSpamWords = new HashMap<String, Integer>();
    private HashMap<String, Double> specificSpamWord = new HashMap<String, Double>();

    double truePositivesCount = 0;
    double falsePositivesCount = 0;
    double trueNegativesCount = 0;
    double acc;
    double prec;
    double testFilesCount;



    public void train(ActionEvent event){

        DirectoryChooser dc = new DirectoryChooser();
        dc.setInitialDirectory(new File("."));
        File md = dc.showDialog(null);

        if (md != null){
            trainingProcess(md);

            // P(S|W) = P(W|S) / ( P(W|S) + P(W|H) ) and store in map
            trainSpamGivenWord();
        }else{
            System.out.println("Directory not valid");
        }
    }

    public void test(ActionEvent event){
        // launch dialog for directory chooser
        DirectoryChooser dc = new DirectoryChooser();
        dc.setInitialDirectory(new File("."));
        File md = dc.showDialog(null);  // main directory

        if (md != null){
            testingProcess(md);
            System.out.println(testFilesCount);
            System.out.println(truePositivesCount + " " + falsePositivesCount + " " + trueNegativesCount);

            // calculate and format accuracy and precision
            DecimalFormat df = new DecimalFormat("0.00000");
            acc = (truePositivesCount + trueNegativesCount)/testFilesCount;
            Accuracy.setText(df.format(acc));

            prec = truePositivesCount/ (falsePositivesCount + trueNegativesCount);
            Precision.setText(df.format(prec));

            // add values to table columns
            FileName.setCellValueFactory(new PropertyValueFactory<TestFile, String>("FileName"));
            ActualClass.setCellValueFactory(new PropertyValueFactory<TestFile, String>("ActualClass"));
            SpamProbability.setCellValueFactory(new PropertyValueFactory<TestFile, Double>("SpamProbability"));
        }else{
            System.out.println("Directory not valid");
        }
    }


    private boolean findWord(String text){
        //Regex check
        String regex = "^[A-Za-z]*$";
        if (text.matches(regex)){
            return true;
        }
        return false;
    }

    public void trainingProcess(File file){
        if (file.isDirectory()){
            if (file.getName().equals("ham")){
                try {
                    trainHamFrequency(file);
                }catch (IOException e){
                    e.printStackTrace();
                }
                System.out.println("DONE Folder: ../ham");
            }else if(file.getName().equals("spam")) {
                try {
                    trainSpamFrequency(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("DONE Folder: ../spam");
            }else {
                File[] filesInDir = file.listFiles();
                for (int i = 0; i < filesInDir.length; i++){
                    trainingProcess(filesInDir[i]);
                }
            }
        }
    }

    public void testingProcess(File file){
        if (file.isDirectory()){

            //process all files recursively
            File[] filesInDir = file.listFiles();
            for (int i = 0; i < filesInDir.length; i++){
                testingProcess(filesInDir[i]);
            }
        }
        else if (file.exists()){
            double spamProbability = 0.0;
            // calculate spam probability of test files
            try {
                spamProbability = testProbability(file);

            } catch (IOException e) {
                e.printStackTrace();
            }

            // format data and add to table data
            DecimalFormat df = new DecimalFormat("0.00000");

            if (file.getParent().contains("ham")){
                table.getItems().add(new TestFile(file.getName(), "ham", df.format(spamProbability)));
            }else{
                table.getItems().add(new TestFile(file.getName(), "spam",df.format(spamProbability)));
            }
        }

    }

    public double testProbability(File file) throws FileNotFoundException {
        double pSF;
        double n = 0.0;
        double threshold = 0.5;

        Scanner scanner = new Scanner(file);
        while(scanner.hasNext()){
            String word = scanner.next();
            if (findWord(word)) {
                if (specificSpamWord.containsKey(word)){
                    n += Math.log( (1 - specificSpamWord.get(word) - Math.log(specificSpamWord.get(word))) );
                }
            }
        }
        pSF = 1/(1 + Math.pow(Math.E,n));

        // accumulate accuracy/precision statistics
        if (file.getParent().contains("spam") && pSF > threshold){
            truePositivesCount ++;
        }
        if (file.getParent().contains("ham") && pSF > threshold){
            falsePositivesCount ++;
        }
        if (file.getParent().contains("ham") && pSF < threshold){
            trueNegativesCount ++;
        }
        testFilesCount ++;
        return pSF;
    }

    public void trainHamFrequency(File file) throws IOException{

        File[] filesInDir = file.listFiles();
        System.out.println("Training...");
        System.out.println("# of Files: " + filesInDir.length);
        for (int i = 0; i < filesInDir.length; i++){
            HashMap<String, Integer> temp = new HashMap<String, Integer>();

            // Gather list of words in specific file and put in temporary Map
            Scanner scanner = new Scanner(filesInDir[i]);
            while(scanner.hasNext()){
                String word = scanner.next();
                if (findWord(word)) {
                    if (!temp.containsKey(word)) {
                        temp.put(word, 1);
                    }
                }
            }

            // iterate through temp and insert word list into WordCount Map
            for (Map.Entry<String,Integer> entry: temp.entrySet()){
                if (numHamWords.containsKey(entry.getKey())){
                    int oldCount = numHamWords.get(entry.getKey());
                    numHamWords.put(entry.getKey(), oldCount + 1);
                }else{
                    numHamWords.put(entry.getKey(), 1);
                }
            }

            // Clear word list so temporary Map can be reused for later files
            temp.clear();

            // Calculate W|Ham Frequency and put in Map
            // # of ham files containing word/# of ham files
            for (Map.Entry<String,Integer> entry: numHamWords.entrySet()){
                double pWH = (double)entry.getValue()/(double)filesInDir.length;
                hamFreq.put(entry.getKey(),pWH);
            }
        }
    }

    public void trainSpamFrequency(File file) throws IOException{

        File[] filesInDir = file.listFiles();
        System.out.println("Training...");
        System.out.println("# of Files: " + filesInDir.length);
        for (int i = 0; i < filesInDir.length; i++){
            HashMap<String, Integer> temp = new HashMap<String, Integer>();

            // Gather list of words in specific file and put in temporary Map
            Scanner scanner = new Scanner(filesInDir[i]);
            while(scanner.hasNext()){
                String word = scanner.next();
                if (findWord(word)) {
                    if (!temp.containsKey(word)) {
                        temp.put(word, 1);
                    }
                }
            }

            // iterate through temp and insert word list into WordCount Map
            for (Map.Entry<String,Integer> entry: temp.entrySet()){
                if (numSpamWords.containsKey(entry.getKey())){
                    int oldCount = numSpamWords.get(entry.getKey());
                    numSpamWords.put(entry.getKey(), oldCount + 1);
                }else{
                    //System.out.println(entry.getKey());
                    numSpamWords.put(entry.getKey(), 1);
                }
            }

            // Clear word list so temporary Map can be reused for later files
            temp.clear();

            // Calculate W|Spam Frequency and put in Map
            // # of spam files containing Word / # of spam files
            for (Map.Entry<String,Integer> entry: numSpamWords.entrySet()){
                double pWS = (double)entry.getValue()/(double)filesInDir.length;
                spamFreq.put(entry.getKey(),pWS);
            }
        }

    }

    // probability Pr(S|W)
    public void trainSpamGivenWord(){
        for (Map.Entry<String,Double> entry: spamFreq.entrySet()){
            if (hamFreq.containsKey(entry.getKey())) {
                double pSW = entry.getValue() / (entry.getValue() + hamFreq.get(entry.getKey()));
                specificSpamWord.put(entry.getKey(),pSW);
            }
        }

    }
}

