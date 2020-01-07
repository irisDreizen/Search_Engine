package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;


public class myModel extends Observable implements IModel {
  private   String pathToRead="";
  private   String pathToWrite="";
   private boolean toStem=false;
  private   model.Indexer index;
   private HashMap<String, termData> dictionary;
   private Searcher searche= new Searcher();
    public  HashMap<String,TreeMap<String,Double> >relevantDoc=new HashMap<>();



    public void setToStem(boolean toStem) {
        this.toStem = toStem;
    }

    public double getDocAvg(){
        double sum=0;
        for(Map.Entry<String ,DocDetails> entry : index.getP().getDocInfo().entrySet()){
            sum=sum+entry.getValue().getDocSize();
        }

        double avg=sum/index.getP().getDocInfo().size();
        return  avg;
    }


    public void callSearchOneQuery(String nameQuery, String query, Indexer index, String pathToRead, HashMap<String,TreeMap<String,Double>> relevantDoc, double docAvg) throws IOException {
        searche.RankDocs(nameQuery,query,index,pathToRead,relevantDoc,getDocAvg());//this should be changed, the input is the parsed query
    }

    public void callSearchManyQuery(    String nameQuery, String query, Indexer index,String pathToRead,HashMap<String,TreeMap<String,Double>> relevantDoc, double docAvg) throws IOException {
        //to add parsing option

        //to add tags parsing and then call oneQuery function
        searche.RankDocs(nameQuery,query,index,pathToRead,relevantDoc,getDocAvg());//this should be changed, the input is the parsed query
    }


    public void parseQuery(String pathOfQueries) throws IOException {

        HashMap<String,String > listQuery=new HashMap<>();
        File input = new File(pathOfQueries);
        Document document = Jsoup.parse(input, "UTF-8");
        Elements id = document.getElementsByTag("num");
        Elements query = document.getElementsByTag("title");

        int i=0;
        for(Element e: query){
            listQuery.put(id.get(i).text(),query.text());
            i++;

        }

        for(Map.Entry<String,String> entry : listQuery.entrySet()){
            /////parse for query
            callSearchOneQuery(entry.getKey(),entry.getValue(),index,pathToRead,relevantDoc,getDocAvg());
        }

    }









    public void setDictionary(String pathToRead1, String pathToWrite1, boolean toStem1) throws IOException {
        index=new model.Indexer(pathToRead1,pathToWrite1,toStem1);
        long startTime = System.nanoTime();
        index.index();
        long endTime   = System.nanoTime();
        long totalTime = endTime - startTime;
        double totalTimeSecond=(totalTime)*(1.0E-9);
        dictionary=index.getP().getDictionary();
        int docInfoSize = index.getP().getDocInfoSize();
        int dictionarySize = dictionary.size();
       // System.out.println("i updated model");
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"num of docs:"+docInfoSize+"|||num of words:"+dictionarySize+"|||total time:"+totalTimeSecond+"seconds");
        Optional<ButtonType> result = alert.showAndWait();
        setChanged();
        notifyObservers();

    }

    public void loadDictionary(String pathToRead1, String pathToWrite1, boolean toStem1) throws IOException {
        index=new model.Indexer(pathToRead1,pathToWrite1,toStem1);
        index.loadDictionary();
        dictionary=index.getP().getDictionary();
    }


    public void clearData(){
        if(index!=null){
            if(index.getP()!=null){
                index.getP().clearDictionary();
                index.getP().clearDocInfo();
            }
        }
        if(!pathToWrite.equals("")){
            File readFile = new File(pathToWrite);
            if(readFile.isDirectory()) {
                for(File file : readFile.listFiles()) {
                    if(file.getName().length()>2){
                        if((file.getName().charAt(0)=='I'&& file.getName().charAt(1)=='C')||file.getName().charAt(0)=='p'){
                            file.delete();
                        }
                    }
                }
            } else {
                System.out.println("this is not a directory");
            }
        }


    }

    public void setPahtToRead(String pathToRead) {
        this.pathToRead = pathToRead;
    }

    public void setPathToWrite(String pathToWrite) {
        this.pathToWrite = pathToWrite;
    }
    public HashMap<String, termData> getDictionary(){
        if(index!=null){
            return index.getP().getDictionary();
        }
        else{
            return null;
        }
    }

    public void showDictionary(){
        Stage stage = new Stage();
        stage.setTitle("Dictionary");

        ObservableList<termData> data = FXCollections.observableArrayList();
        data.addAll(dictionary.values());

        Comparator<String> c= new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        };

        TableColumn firstNameCol = new TableColumn();
        firstNameCol.setText("term");
        firstNameCol.setCellValueFactory(new PropertyValueFactory("termName"));
        firstNameCol.setComparator(c);


        TableColumn lastNameCol = new TableColumn();
        lastNameCol.setText("appear");
        lastNameCol.setCellValueFactory(new PropertyValueFactory("totalApearance"));




        TableView tableView = new TableView();
        tableView.setItems(data);
        tableView.getColumns().addAll(firstNameCol, lastNameCol);
        tableView.getSortOrder().add(firstNameCol);


        Scene scene = new Scene(tableView);
        stage.setScene(scene);
        stage.show();



//        TableColumn<, termData> terms = new TableColumn<>("term");
//        terms.setCellValueFactory(new PropertyValueFactory<>("terms"));
//
//        TableColumn<String, termData> timesApear = new TableColumn<>("times appear");
//        timesApear.setCellValueFactory(new PropertyValueFactory<>("timesAppear"));


    }
}
