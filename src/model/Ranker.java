package model;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.util.Pair;

import java.io.IOException;
import java.util.ArrayList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.TreeMap;

public class Ranker {

    private HashMap<String, Double> weights;
    private final String USER_AGENT = "Mozilla/5.0";
    public Ranker() {
        weights=new HashMap<>();
    }




    public HashMap<String,Map<String, Double>> collectLinesQuery(String nameQuery, String query, Indexer index,String pathToRead,HashMap<String,Map<String,Double>> relevantDoc, double docAvg) throws IOException {

        String[] splitedQuery=query.split(" ");
        int  numOfDOC=index.getP().getDocInfo().size();
        int sumTotalIdf=0;

        File file3= new File(pathToRead+"\\"+index.getPostingFileName_NoStem());
        String st="";
        String [] splitedPosting;


        BufferedReader posting= new BufferedReader(new FileReader(file3));

        String investigate="";

        int sumIdFwordQuery=0;
        int countDoc=0;
        st=posting.readLine();
        while (st!=null) {
                for (int i = 0; i < splitedQuery.length; i++) {
                    splitedPosting = st.split("@");
                    if (splitedPosting[0].toLowerCase().equals(splitedQuery[i].toLowerCase())) {
                        investigate=investigate+st;
                        BN25(nameQuery,investigate,relevantDoc,numOfDOC,index.getP().getDocInfo(),docAvg);

                    }
                    investigate="";

                }
                st=posting.readLine();
            }
        return relevantDoc;
        }




    public void BN25(String nameQuery,String postingLine, HashMap<String, Map<String,Double>> relevantDoc,int numOfDoc,HashMap<String, DocDetails> DocInfo,double avg){
        String []st=postingLine.split("@");
        String []docAppear=st[2].split(" ");
        for(int i=0; i<docAppear.length; i=i+2){
            String docName=docAppear[i];
            int numOfAppear=Integer.parseInt(docAppear[i+1]);
            //compute tf
            double tf=(double) numOfAppear/(DocInfo.get(docName).getMax_tf());

            double numOfDocCurrenQuery=docAppear.length/2;

            //conpute idf
            double idf=Math.log((numOfDoc-numOfDocCurrenQuery+0.5)/(numOfDocCurrenQuery+0.5));

            //compute score for qi
            double partBScore= (tf*(1.3+1))/((1-0.75+0.75*(DocInfo.get(docName).getDocSize()/avg))+tf);

            double score=idf*partBScore;///////////////
            ///////////////////
            ////////////////to add weight
            if(relevantDoc.containsKey(nameQuery)){
               if(relevantDoc.get(nameQuery).containsKey(docName)){
                  double scoreToAdd= relevantDoc.get(nameQuery).get(docName)+score;
                   relevantDoc.get(nameQuery).replace(docName,scoreToAdd);
               }
               else{
                   relevantDoc.get(nameQuery).put(docName,score);
               }

            }
            else{
                TreeMap<String,Double> toAdd=new TreeMap<>();
                toAdd.put(docName,score);
                relevantDoc.put(nameQuery,toAdd);
            }
        }





    }
    public String semantic(String query) throws Exception {
        String newQuery = "";
        String [] splitedQuery = query.split(" ");
        for(String word:splitedQuery){
            weights.put(word,(double)1);
            searchSynonym(word);
        }

        return newQuery;
    }


    public void searchSynonym(String wordToSearch) throws Exception {
        String url = "https://api.datamuse.com/words?rel_syn=" + wordToSearch;

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);

        // ordering the response
        StringBuilder response;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }

        ObjectMapper mapper = new ObjectMapper();

        try {
            // converting JSON array to ArrayList of words
            ArrayList<Word> words = mapper.readValue(
                    response.toString(),
                    mapper.getTypeFactory().constructCollectionType(ArrayList.class, Word.class)
            );

            System.out.println("Synonym word of '" + wordToSearch + "':");
            if(words.size() > 0) {
                for(Word word : words) {
                    if(word.getScore()>5000){
                        Double weight = Double.valueOf((1/2));
                        weights.put(word.getWord(), weight);
                    }
                    else if(word.getScore()>1000){
                        Double weight = Double.valueOf((1/8));
                        weights.put(word.getWord(), weight);
                    }

                    System.out.println((words.indexOf(word) + 1) + ". " + word.getWord() + ", the score is:"+ word.getScore()+"");
                }
            }
            else {
                System.out.println("none synonym word!");
            }
        }
        catch (IOException e) {
            e.getMessage();
        }
    }

    // word and score attributes are from DataMuse API
    static class Word {
        private String word;
        private int score;

        public String getWord() {
            return this.word;
        }

        public int getScore() {
            return this.score;
        }


    }
}
