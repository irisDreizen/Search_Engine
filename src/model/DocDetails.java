package model;

public class DocDetails {
    private String docono="";
    private String text="";
    private int Max_tf=0;
    private int uniqeWords=0;
    private int docSize=0;
    private int YeshutNumber=0;

    public void setDocSize(int docSize) {
        this.docSize = docSize;
    }

    public int getDocSize() {
        return docSize;
    }

    public void setYeshutNumber(int yeshutNumber) {
        YeshutNumber = yeshutNumber;
    }

    public int getYeshutNumber() {
        return YeshutNumber;
    }

    public DocDetails(int max_tf, int uniqeWords, int docSize, int yeshutNumber) {
        Max_tf = max_tf;
        this.uniqeWords = uniqeWords;
        this.docSize=docSize;
        this.YeshutNumber=yeshutNumber;

    }

    public int getMax_tf() {
        return Max_tf;
    }

    public int getUniqeWords() {
        return uniqeWords;
    }

    public void setMax_tf(int max_tf) {
        Max_tf = max_tf;
    }

    public void setUniqeWords(int uniqeWords) {
        this.uniqeWords = uniqeWords;
    }

    @Override
    public String toString() {
        return "model.DocDetails{" +
                "docono='" + docono + '\'' +
                ", text='" + text + '\'' +
                '}';
    }

    //   private String date1;
  //  private String t1;




    public DocDetails(String docono, String text) {
        this.docono = docono;
        this.text = text;

    }


    public void setDocono(String docono) {
        this.docono = docono;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDocono() {
        return docono;
    }

    public String getText() {
        return text;
    }
}
