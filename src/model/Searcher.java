package model;

import java.io.IOException;
import java.util.HashMap;
import java.util.TreeMap;

public class Searcher {

    private Ranker r;


    public HashMap<String,TreeMap<String,Double>>   RankDocs(String query, String nameQuery, Indexer index,String pathToRead,HashMap<String,TreeMap<String,Double>> relevantDoc, double docAvg) throws IOException {
        return  r.collectLinesQuery(query, nameQuery, index, pathToRead, relevantDoc, docAvg);


    }
}
