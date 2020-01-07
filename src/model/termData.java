package model;

public class termData {

    private int numOfDoc=0;
    private int pointerLine=0;
    private String termName="";
    private int totalApearance=0;

    public void setTotalApearance(int totalApearance) {
        this.totalApearance = totalApearance;
    }


    public int getTotalApearance() {
        return totalApearance;
    }

    public String getTermName() {
        return termName;
    }

    public void setNumOfDoc(int numOfDoc) {
        this.numOfDoc = numOfDoc;
    }

    public void setPointerLine(int pointerLine) {
        this.pointerLine = pointerLine;
    }

    public int getNumOfDoc() {
        return numOfDoc;
    }

    public int getPointerLine() {
        return pointerLine;
    }

    public termData(int numOfDoc, int pointerLine,String termName, int totalApearance) {
        this.numOfDoc = numOfDoc;
        this.pointerLine=pointerLine;
        this.termName=termName;
        this.totalApearance=totalApearance;
    }


    public termData(int numOfDoc,int pointerLine){
        this.numOfDoc=numOfDoc;
        this.pointerLine=pointerLine;
    }
}
