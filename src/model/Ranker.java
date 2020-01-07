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

public class Ranker {
    private HashMap<String, Double> weights;
    private final String USER_AGENT = "Mozilla/5.0";
    public Ranker() {
        weights=new HashMap<>();
    }
    public void BN25(String query){

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

        public String getWord() {return this.word;}
        public int getScore() {return this.score;}
    }
}
