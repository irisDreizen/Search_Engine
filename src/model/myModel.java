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
import javafx.util.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;


public class myModel extends Observable implements IModel {
  private   String pathToRead="";
  private   String pathToWrite="";
  private  String pathForQueries="";
   private boolean toStem=false;
  private   model.Indexer index;
   private HashMap<String, termData> dictionary;
   private boolean toUseSemantics = false;
   private Searcher searche;
    public  HashMap<String,Map<String,Double> >relevantDoc=new HashMap<>();

    public HashMap<String,Map<String,Double>> getRankingMap(){
        return relevantDoc;
    }

    public void setToUseSemantics(boolean toUseSemantics) {
        this.toUseSemantics = toUseSemantics;
        searche= new Searcher(toUseSemantics);
    }

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


    public void callSearchOneQuery(String nameQuery, String notParsedquery) throws IOException {
        String query = parseQuery(pathToWrite,notParsedquery);
        searche.RankDocs(nameQuery,query,index,pathToWrite,relevantDoc,getDocAvg());//this should be changed, the input is the parsed query
    }

    public void callSearchManyQuery(String pathOfQueries) throws IOException {
        HashMap<String,Query> queries=TagsAndParseQuery_AndRank(pathOfQueries);
        for(Map.Entry<String,Query> entry : queries.entrySet()){
           String idQ=entry.getKey();//id
            String desc=entry.getValue().getDescription();
            String q=entry.getValue().getTitle()+" "+desc;
            callSearchOneQuery(idQ,q);
        }


    }


    public HashMap<String, Query> TagsAndParseQuery_AndRank(String pathOfQueries) throws IOException {
        HashMap<String, Query> queries = new HashMap<>();
        try {
            Document query = Jsoup.parse(new File(pathOfQueries+"\\quries.txt"), "UTF-8");
            Elements allQueries = query.getElementsByTag("top");
            for (Element currQ : allQueries) {
                //insert ID
                String numQ = currQ.getElementsByTag("num").text();
                //numQ =numQ.substring(9,hara.length()-1);
                numQ = numQ.substring(8);
                String[] t = numQ.split("\\s+");
                numQ = t[0];

                //insert title
                String titleQ = currQ.getElementsByTag("title").text();

                //insert desc
                String descQ = currQ.getElementsByTag("desc").text();

                String[] splitDescQ = descQ.split("\\s+");
                String newDescQ = "";
                for (String s : splitDescQ) {
                    if (s.equals("Narrative:")) {
                        break;
                    }
                    newDescQ = newDescQ + " " + s;
                }
                newDescQ = newDescQ.substring(13);

                Query newQ = new Query(numQ, titleQ, newDescQ);
                queries.put(numQ, newQ);

            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return queries;

    }






public String parseQuery(String pathToWrite, String query) throws IOException {
        QueryPraser queryPraser = new QueryPraser(pathToWrite+"\\stop_words.txt");
        queryPraser.buildDictionary(query);
        HashMap<String, termData> newQuery =  queryPraser.getDictionary();
        String parsedQuery = "";
        for(Map.Entry<String, termData> term : newQuery.entrySet()){
            parsedQuery=parsedQuery+term.getKey()+" ";
        }

        return parsedQuery;

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

    public void setPathForQueries(String pathForQueries) {
        this.pathForQueries = pathForQueries;
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
