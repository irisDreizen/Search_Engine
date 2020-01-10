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
    private HashMap<String,Map<String,Integer>> enteties=new HashMap<>();

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


    public void callSearchOneQuery(String nameQuery, String notParsedquery) throws Exception {
        String query = parseQuery(pathToWrite,notParsedquery);
        searche.RankDocs(nameQuery,query,index,pathToWrite,relevantDoc,getDocAvg());//this should be changed, the input is the parsed query
    }

    public void callSearchManyQuery(String pathOfQueries) throws Exception {
        HashMap<String,Map<String,Double>>fix=new HashMap<>();
        HashMap<String, Query> queries = TagsAndParseQuery_AndRank(pathOfQueries);
        for (Map.Entry<String, Query> entry : queries.entrySet()) {
            String idQ = entry.getKey();//id
            String desc = entry.getValue().getDescription();
            String q = entry.getValue().getTitle() + " " + desc;
            callSearchOneQuery(idQ, q);
        }

        for (Map.Entry<String, Map<String, Double>> entry : relevantDoc.entrySet()) {
            Map<String, Double> temp = entry.getValue();
            List<Map.Entry<String, Double>> list =
                    new LinkedList<Map.Entry<String, Double>>(temp.entrySet());

            // Sort the list
            Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
                public int compare(Map.Entry<String, Double> o1,
                                   Map.Entry<String, Double> o2) {
                    return (o1.getValue()).compareTo(o2.getValue());
                }
            });

            // put data from sorted list to hashmap
            Map<String, Double> temp2 = new HashMap<>();
            if (list.size() > 50) {
                for (int i = list.size() - 1; i >= list.size() - 50; i--) {
                    temp2.put(list.get(i).getKey(), list.get(i).getValue());
                }
            } else {
                for (int j = list.size() - 1; j >= 0; j--) {
                    temp2.put(list.get(j).getKey(), list.get(j).getValue());
                }

            }
            fix.put(entry.getKey(), temp2);


        }
        relevantDoc = fix;
    }
    public Map<String, Integer> getEntitiesMap(String DocName){
        return enteties.get(DocName);
    }

    public void writeQueryToDisk() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(pathForQueries+"\\"+"results.txt"));
        LinkedHashMap<String, Map<String, Double>> sortedDictionary = new LinkedHashMap<>();
        relevantDoc.entrySet().stream().sorted(new Comparator<Map.Entry<String, Map<String, Double>>>() {
            @Override
            public int compare(Map.Entry<String, Map<String, Double>> o1, Map.Entry<String, Map<String, Double>> o2) {
                return o1.getKey().compareToIgnoreCase(o2.getKey());
            }


        }).forEachOrdered(x->sortedDictionary.put(x.getKey(),x.getValue()));

        for(Map.Entry<String, Map<String, Double>> queryId: sortedDictionary.entrySet()){
            for(Map.Entry<String, Double> DocsAndScore: queryId.getValue().entrySet()){
                writer.write(queryId.getKey()+" 0 "+DocsAndScore.getKey()+" 1 42.38 mt");
                writer.newLine();
            }
        }
        writer.close();
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
         loadEnteties();
        index=new model.Indexer(pathToRead1,pathToWrite1,toStem1);
        index.loadDictionary();
        dictionary=index.getP().getDictionary();

    }

    public void loadEnteties() throws IOException {
        File f;
        f=new File(pathToWrite+"\\" + "enteties" + ".txt");
        if(!f.exists()){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"this is a wrong path");
            Optional<ButtonType> result = alert.showAndWait();
        }
        BufferedReader bf= new BufferedReader(new FileReader(f));
        String st1=bf.readLine();
        while (st1!= null) {
            String[] splitedText = st1.split("@");
            String entetyName = splitedText[0];
            String [] docAppear = (splitedText[1]).split(" ");
            for(int i=0; i<docAppear.length; i=i+2) {
                String docName = docAppear[i];
                int numOfAppear = Integer.parseInt(docAppear[i + 1]);
                if(enteties.containsKey(docName)){
                    enteties.get(docName).put(entetyName,numOfAppear);
                }
                else{
                    HashMap<String, Integer> toAdd = new HashMap<>();
                    toAdd.put(entetyName,numOfAppear);
                    enteties.put(docName,toAdd);
                }
            }
            st1=bf.readLine();
        }
        bf.close();

        HashMap<String,Map<String,Integer>> fix = new HashMap<>();
        for (Map.Entry<String, Map<String, Integer>> entry : enteties.entrySet()) {
            Map<String, Integer> temp = entry.getValue();
            List<Map.Entry<String, Integer>> list =
                    new LinkedList<Map.Entry<String, Integer>>(temp.entrySet());

            // Sort the list
            Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
                public int compare(Map.Entry<String, Integer> o1,
                                   Map.Entry<String, Integer> o2) {
                    return (o1.getValue()).compareTo(o2.getValue());
                }
            });

            // put data from sorted list to hashmap
            Map<String, Integer> temp2 = new HashMap<>();
            if (list.size() > 5) {
                for (int i = list.size() - 1; i >= list.size() - 5; i--) {
                    temp2.put(list.get(i).getKey(), list.get(i).getValue());
                }
            } else {
                for (int j = list.size() - 1; j >= 0; j--) {
                    temp2.put(list.get(j).getKey(), list.get(j).getValue());
                }

            }
            fix.put(entry.getKey(), temp2);


        }
        enteties = fix;
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
