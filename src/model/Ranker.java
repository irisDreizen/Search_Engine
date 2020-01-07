package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.TreeMap;

public class Ranker {

    public Ranker() {
    }




    public HashMap<String,TreeMap<String, Double>> collectLinesQuery(String query, String nameQuery, Indexer index,String pathToRead,HashMap<String,TreeMap<String,Double>> relevantDoc, double docAvg) throws IOException {

        String[] splitedQuery=query.split(" ");
        int  numOfDOC=index.getP().getDocInfo().size();
        int sumTotalIdf=0;

        File file3= new File(pathToRead+"\\"+index.getPostingFileName_NoStem()+".txt");
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
                        BN25(nameQuery,investigate,relevantDoc,numOfDOC,index.getP().getDocInfo(),docAvg);

                    }

                }
                st=posting.readLine();
            }
        return relevantDoc;
        }




    public void BN25(String nameQuery,String postingLine, HashMap<String, TreeMap<String,Double>> relevantDoc,int numOfDoc,HashMap<String, DocDetails> DocInfo,double avg){
        String []st=postingLine.split("@");
        String []docAppear=st[2].split(" ");
        for(int i=0; i<postingLine.length(); i=i+2){
            String docName=docAppear[i];
            int numOfAppear=Integer.parseInt(docAppear[i+1]);
            //compute tf
            double tf=numOfAppear/DocInfo.get(docName).getMax_tf();

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





    public void semantic(String query){
        
    }
}
