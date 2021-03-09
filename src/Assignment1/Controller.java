package Assignment1;

//all imports used
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
    //Import the variables from the fxml class
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
    @FXML
    private TextField CurrentProcess;

    //Used hashmaps to delete any duplicate entries
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

    //when the train button is pushed in the GUI

    public void train(ActionEvent event){
        //used the directory chooser to ensure that the user could pick any directory they would like
        DirectoryChooser dc = new DirectoryChooser();
        dc.setInitialDirectory(new File("."));
        //This displays the window that they can see what directory they are choosing
        File md = dc.showDialog(null);

        if (md != null){
            trainingProcess(md);

            // store P(S|W) in map
            trainSpamGivenWord();
        }else{
            System.out.println("Directory not valid");
        }
    }

    public void trainSpamGivenWord(){
        for (Map.Entry<String,Double> entry: spamFreq.entrySet()){
            if (hamFreq.containsKey(entry.getKey())) {
                //P(S|W) = P(W|S)/(P(W|S) + P(W|H))
                double pSW = entry.getValue() / (entry.getValue() + hamFreq.get(entry.getKey()));
                specificSpamWord.put(entry.getKey(),pSW);
            }
        }
    }

    public void test(ActionEvent event){
        // open dialog for directory chooser
        DirectoryChooser dc = new DirectoryChooser();
        dc.setInitialDirectory(new File("."));
        File md = dc.showDialog(null);  // main directory

        if (md != null){
            testingProcess(md);
            System.out.println(testFilesCount);
            CurrentProcess.setText("Finished Testing Files");
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
    //Only checks training files
    public void trainingProcess (File file){
        if (file.isDirectory()){
            if (file.getName().equals("ham")){
                try {
                    THF(file);
                }catch (IOException e){
                    e.printStackTrace();
                }
                //tried to implement a live search in the GUI
                CurrentProcess.setText("Finished Ham Files");
                System.out.println("Finished ham scan");
            }else if(file.getName().equals("spam")) {
                try {
                    TSF(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                CurrentProcess.setText("Finished Training Files");
                System.out.println("Finished spam scan");
            }else {
                File[] filesInDir = file.listFiles();
                for (int i = 0; i < filesInDir.length; i++){
                    trainingProcess(filesInDir[i]);
                }
            }
        }
    }
    //Only checks test files
    public void testingProcess(File file){
        if (file.isDirectory()){
            File[] filesInDir = file.listFiles();
            //checks through each file via recursion
            for (int i = 0; i < filesInDir.length; i++){
                testingProcess(filesInDir[i]);
            }
        }
        else if (file.exists()){
            double spamProbability = 0.0;
            //determine probability of spam
            try {
                spamProbability = PSF(file);

            } catch (IOException e) {
                e.printStackTrace();
            }
            //Rounds to 5th decimal place as assignment asks for
            DecimalFormat df = new DecimalFormat("0.00000");
            //checks for ham
            if (file.getParent().contains("ham")){
                table.getItems().add(new TestFile(file.getName(), "ham", df.format(spamProbability)));
            }else{
                //checks for spam
                table.getItems().add(new TestFile(file.getName(), "spam",df.format(spamProbability)));
            }
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

    public double PSF(File file) throws FileNotFoundException {
        double pSF;
        double n = 0.0;
        double threshold = 0.5;

        Scanner scanner = new Scanner(file);
        //n = Sum N -> i=1 [ln(1-P(S|W))-ln(P(S|W))]
        //Used while loop to simulate the sum
        while(scanner.hasNext()){
            String word = scanner.next();
            if (findWord(word)) {
                if (specificSpamWord.containsKey(word)){
                    n += Math.log( (1 - specificSpamWord.get(word) - Math.log(specificSpamWord.get(word))));
                }
            }
        }
        //P(S|F) = 1/(1 + e^n)
        pSF = 1/(1 + Math.pow(Math.E,n));

        // finds the numbers to calculate
        if (file.getParent().contains("spam") && pSF > threshold) {
            truePositivesCount++;
        }
        if (file.getParent().contains("ham") && pSF > threshold) {
            falsePositivesCount++;
        }
        if (file.getParent().contains("ham") && pSF < threshold) {
            trueNegativesCount++;
        }

        testFilesCount ++;
        return pSF;
    }
    //Method to train the ham frequency
    public void THF(File file) throws IOException{
        File[] filesInDir = file.listFiles();
        CurrentProcess.setText("Searching Ham Files...");
        System.out.println("Number of ham files to search: " + filesInDir.length);
        System.out.println("Please wait until (Finished) prompt");
        for (int i = 0; i < filesInDir.length; i++){
            HashMap<String, Integer> temp = new HashMap<String, Integer>();

            //Mapping words for ham
            Scanner scanner = new Scanner(filesInDir[i]);
            while(scanner.hasNext()){
                String word = scanner.next();
                if (findWord(word)) {
                    if (!temp.containsKey(word)) {
                        temp.put(word, 1);
                    }
                }
            }

            //Transfer word list from temp to numHamWords count
            for (Map.Entry<String,Integer> entry: temp.entrySet()){
                if (numHamWords.containsKey(entry.getKey())){
                    int oldCount = numHamWords.get(entry.getKey());
                    numHamWords.put(entry.getKey(), oldCount + 1);
                }else{
                    numHamWords.put(entry.getKey(), 1);
                }
            }
            // empty the temp for future use
            temp.clear();
            // P(W|H) = num ham files containing word / num ham files
            for (Map.Entry<String,Integer> entry: numHamWords.entrySet()){
                double pWH = (double)entry.getValue()/(double)filesInDir.length;
                hamFreq.put(entry.getKey(),pWH);
            }
        }
    }
    //Method to train the spam frequency
    public void TSF(File file) throws IOException{

        File[] filesInDir = file.listFiles();
        CurrentProcess.setText("Searching Spam Files...");
        System.out.println("Number of spam files to search: " + filesInDir.length);
        System.out.println("Please wait until (Finished) prompt");
        for (int i = 0; i < filesInDir.length; i++){
            HashMap<String, Integer> temp = new HashMap<String, Integer>();

            //Mapping words for ham
            Scanner scanner = new Scanner(filesInDir[i]);
            while(scanner.hasNext()){
                String word = scanner.next();
                if (findWord(word)) {
                    if (!temp.containsKey(word)) {
                        temp.put(word, 1);
                    }
                }
            }

            //Transfer word list from temp to numSpamWords count
            for (Map.Entry<String,Integer> entry: temp.entrySet()){
                if (numSpamWords.containsKey(entry.getKey())){
                    int oldCount = numSpamWords.get(entry.getKey());
                    numSpamWords.put(entry.getKey(), oldCount + 1);
                }else{
                    //System.out.println(entry.getKey());
                    numSpamWords.put(entry.getKey(), 1);
                }
            }

            // empty the temp for future use
            temp.clear();

            //P(W|S) = (num of files with word / num of spam files)
            for (Map.Entry<String,Integer> entry: numSpamWords.entrySet()){
                double pWS = (double)entry.getValue()/(double)filesInDir.length;
                spamFreq.put(entry.getKey(),pWS);
            }
        }

    }
}

