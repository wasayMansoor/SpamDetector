package Assignment1;

public class TestFile {
    private String FileName;
    private String ActualClass;
    private String SpamProbability;

    public TestFile(String fn, String sp, String ac) {
        this.FileName = FileName;
        this.SpamProbability = sp;
        this.ActualClass = ActualClass;
    }

    public String getFileName(){
        return this.FileName;
    }

    public String getSpamProbability(){
        return this.SpamProbability;
    }

    public String getActualClass(){
        return this.ActualClass;
    }

    public void setFilename(String FName){
        this.FileName = FName;
    }

    public void setSpamProbability(String SProb){
        this.SpamProbability = SProb;
    }
    public void setActualClass(String AClass){
        this.ActualClass = AClass;
    }
}
