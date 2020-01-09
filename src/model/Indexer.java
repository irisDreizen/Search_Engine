package model;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.util.Pair;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import org.tartarus.snowball.ext.PorterStemmer;

public class Indexer {

    private Parse p;
    private ReadFile r;
    private int counterWriter=0;
    private int posIndex=0;
    private String pathToWrite;
    private String pathToRead;
    private boolean toStem;
    PorterStemmer stemmer;






    public Indexer(String pathToRead, String pathToWrite,Boolean toStemUpdate) throws IOException {
        stemmer = new PorterStemmer();
        String newPathOfStopWords = pathToRead+"\\stop_words.txt";
        this.p = new Parse(newPathOfStopWords) ;
        this.r = new ReadFile(pathToRead+"\\corpus");
        this.pathToWrite=pathToWrite;
        this.pathToRead=pathToRead;
        this.toStem=toStemUpdate;
    }

    public Parse getP() {
        if(p!=null){
            return p;
        }
        else{
            return null;
        }

    }

  public String getPostingFileName_NoStem(){
        String postingName="";
      File dir = new File(pathToWrite);

      File[] matches = dir.listFiles(new FilenameFilter()
      {
          public boolean accept(File dir, String name)
          {
              return name.startsWith("p") && name.endsWith(".txt");
          }
      });

      if(matches.length!=0 && matches[0]!=null){
          postingName=matches[0].getName();
      }
      return  postingName;
  }

    public String getPostingFileName_WithStem(){
        String postingName="";
        File dir = new File(pathToWrite);

        File[] matches = dir.listFiles(new FilenameFilter()
        {
            public boolean accept(File dir, String name)
            {
                return name.startsWith("s") && name.endsWith(".txt");
            }
        });

        if(matches.length!=0 && matches[0]!=null){
            postingName=matches[0].getName();
        }
        return  postingName;
    }

    public HashMap<String, DocDetails> loadDocDetails(String pathToWrite) throws IOException {
        HashMap<String, DocDetails> DocDetailsMap = new HashMap<>();
        File f;
        f=new File(pathToWrite+"\\" + "DocInfo" + ".txt");
        if(!f.exists()){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"this is a wrong path");
            Optional<ButtonType> result = alert.showAndWait();
            return null;
        }
        BufferedReader bf= new BufferedReader(new FileReader(f));
        String st1=bf.readLine();
        while (st1!= null) {
            String [] splitedText = st1.split("@");
            String docName = splitedText[0];
            String DocSize = splitedText[1];
            String max_tf = splitedText[2];
            String unique = splitedText[3];
            String yeshut = splitedText[4];
            DocDetails docDetails = new DocDetails(Integer.parseInt(max_tf),Integer.parseInt(unique),Integer.parseInt(DocSize),Integer.parseInt(yeshut));
            DocDetailsMap.put(docName,docDetails);
            st1=bf.readLine();
        }
        return DocDetailsMap;

    }

    public void loadDictionary() throws IOException {
        int counter=0;
        File f;
        if(toStem){
            f=new File(pathToWrite+"\\"+getPostingFileName_WithStem());
            if(!f.exists()){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"you didn't build your dictionary with stemming, you can load it only without stemming option");
                Optional<ButtonType> result = alert.showAndWait();
                return;
            }
        }
        else{
            f=new File(pathToWrite+"\\"+getPostingFileName_NoStem());
            if(!f.exists()){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"please build a dictionary first");
                Optional<ButtonType> result = alert.showAndWait();
                return;
            }
        }
        BufferedReader bf= new BufferedReader(new FileReader(f));
       String st1=bf.readLine();
        while (st1!= null) {
            String [] splitedTest = st1.split("@");
            String term = splitedTest[0];
            int totalAppearnce = (int)Double.parseDouble(splitedTest[1]);
            String info = splitedTest[2];
            int docNum = (int)Double.parseDouble(splitedTest[3]);
            termData t = new termData(docNum,counter,term,totalAppearnce);
            p.getDictionary().put(term,t);
            st1=bf.readLine();
            counter++;
        }
        bf.close();
        p.setDocInfo(loadDocDetails(pathToWrite));
    }


    public void index() throws IOException {
        int start=0;
        int endIndex=8;

        while (start<r.getSizeOfFolder()){

            TreeMap<String, String> local_dictionary =
                    new TreeMap<String, String>(new Comparator<String>() {
                        public int compare(String o1, String o2) {
                            return o1.toLowerCase().compareTo(o2.toLowerCase());
                        }
                    });

            p.buildDictionary(r.ReadFile(start,endIndex), local_dictionary,toStem);
            posting(local_dictionary,pathToWrite);
            //add here posting file function
            start=endIndex;
            endIndex=endIndex+8;
        }

        //adding all the suspected words(Yeshut)
        HashMap<String, Pair<String,Integer>> globalYeshut = p.getYeshutGlobalMap();
        TreeMap<String,String> local_dictionary2 =  new TreeMap<String, String>(new Comparator<String>() {
            public int compare(String o1, String o2) {
                return o1.toLowerCase().compareTo(o2.toLowerCase());
            }
        });
        for(Map.Entry<String, Pair<String,Integer>> term : globalYeshut.entrySet()){
            String [] splitedYeshut = term.getKey().split(" ");
            String termDetails=term.getValue().getKey()+" "+ term.getValue().getValue();
            int appearDoc=term.getValue().getValue();
            String seperatedWordUpper = "";
            String seperatedWordLower="";
            for(int i=0; i<=splitedYeshut.length-1; i++){
                seperatedWordUpper=splitedYeshut[i].toUpperCase();
                seperatedWordLower=splitedYeshut[i].toLowerCase();
                if(p.getDictionary().containsKey(seperatedWordLower) ){
                    int newNumOfDoc= p.getDictionary().get(seperatedWordLower).getNumOfDoc()+1;//not sure
                    p.getDictionary().get(seperatedWordLower).setNumOfDoc(newNumOfDoc);
                    int newTotalApear=p.getDictionary().get(seperatedWordLower).getTotalApearance()+appearDoc;
                    p.getDictionary().get(seperatedWordLower).setTotalApearance(newTotalApear);
                    local_dictionary2.put(seperatedWordLower,termDetails);
                  //  System.out.println("i'm adding1:"+seperatedWord.toLowerCase());

                }
                else if(p.getDictionary().containsKey( seperatedWordUpper)){
                    int newNumOfDoc= p.getDictionary().get( seperatedWordUpper).getNumOfDoc()+1;
                    p.getDictionary().get( seperatedWordUpper).setNumOfDoc(newNumOfDoc);
                    int newTotalApear=p.getDictionary().get( seperatedWordUpper).getTotalApearance()+appearDoc;
                    p.getDictionary().get( seperatedWordUpper).setTotalApearance(newTotalApear);
                    local_dictionary2.put( seperatedWordUpper,termDetails);
                //   System.out.println("i'm adding2:"+seperatedWord.toUpperCase());

                }
                else{
                    if(seperatedWordUpper.equals("AND")){
                        System.out.println("i'm AND and i'm adding myself because i'm Yeshut");
                    }
                    if(seperatedWordUpper.equals("and")){
                        System.out.println("i'm and and i'm adding myself because i'm Yeshut");
                    }
                    termData t=new termData(1,1, seperatedWordUpper,1);
                    p.getDictionary().put(seperatedWordUpper,t);
                    local_dictionary2.put(seperatedWordUpper,termDetails);
                 //   System.out.println("i'm adding3:"+seperatedWord.toUpperCase());

                }

            }


        }
        posting(local_dictionary2,pathToWrite);

         MergePostingFiles(pathToWrite);
         writeDocInfoFile(pathToWrite);
         copyStopWords(pathToRead,pathToWrite);
        System.out.println(p.getDictionary().size());


    }

    public void writeDocInfoFile(String pathToWrite) throws IOException {
        File file = new File(pathToWrite+"\\" + "DocInfo" + ".txt");
        FileWriter fw= new FileWriter(file);
        BufferedWriter bw= new BufferedWriter(fw);
        for(Map.Entry<String,DocDetails> entry : p.getDocInfo().entrySet()){
            bw.write(entry.getKey()+"@"+entry.getValue().getDocSize()+"@"+entry.getValue().getMax_tf()+"@"+entry.getValue().getUniqeWords()+"@"+entry.getValue().getYeshutNumber());
            bw.newLine();
        }

    }

    public void posting(TreeMap<String,String> localDictionary,String pathToWrite) throws IOException {
        File file = new File(pathToWrite+"\\" +"IC"+ counterWriter + ".txt");
        FileWriter fw= new FileWriter(file);
        BufferedWriter bw= new BufferedWriter(fw);
        counterWriter++;
        for (Map.Entry<String, String> term : localDictionary.entrySet()) {
            //System.out.println("i'm in the local dictionary and my term is:"+term.getKey());
            String termKey = term.getKey();
            if(!p.getDictionary().containsKey(termKey)){
                termKey=termKey.toLowerCase();
                if(!p.getDictionary().containsKey(termKey)){
                    System.out.println("problem!!!!");
                }
            }
        //    System.out.println("i'm in the local dictionary and my term is:"+term.getKey());

            String info = term.getValue();
            String [] splitedInfo=info.split(" ");
            int totalTimesOccures=0;
            for(int i=1; i<splitedInfo.length; i=i+2){
                double tempNum = Double.parseDouble(splitedInfo[i]);
                totalTimesOccures=totalTimesOccures+(int)tempNum;
            }
            int numOfDoc=p.getDictionary().get(termKey).getNumOfDoc();

            bw.write(termKey + "@" +totalTimesOccures+"@"+ info+"@"+numOfDoc);
          //  System.out.println("post"+termKey);
            bw.newLine();
        }

        bw.close();


    }

    public void MergePostingFiles(String pathToWrite) throws IOException {
        Queue<String>filesQueue = new LinkedList<String>();
        int index=0;
        while(index < counterWriter-1){//insert all posting files to the queue
            filesQueue.add(pathToWrite+"\\"+"IC"+index+".txt");
            index++;

        }
        String st1="";
        String st2="";
      while(!filesQueue.isEmpty()&& filesQueue.size()!=1){
            String file1=filesQueue.poll();
            String file2=filesQueue.poll();
            File fileFirst=new File(file1);
            File fileSecond=new File(file2);
            File file3;
            if(toStem){
                file3= new File(pathToWrite+"\\"+"s"+posIndex+".txt");
            }
            else{
                file3= new File(pathToWrite+"\\"+"p"+posIndex+".txt");
            }



            BufferedWriter mergePostingTwoFiles=new BufferedWriter(new FileWriter(file3));


            BufferedReader bfFirst= new BufferedReader(new FileReader(fileFirst));
            BufferedReader bfSecond= new BufferedReader(new FileReader(fileSecond));
            st1=bfFirst.readLine();
            st2=bfSecond.readLine();

            while (st1!= null && st2!= null) {
                String[] st1Array = st1.split("@");
                String term1 = st1Array[0];
                String numOfAppear1 = st1Array[1];
                String info1 = st1Array[2];
                String numOfDoc1=st1Array[3];

                String[] st2Array = st2.split("@");
                String term2 = st2Array[0];
                String numOfAppear2 = st2Array[1];
                String info2 = st2Array[2];
                String numOfDoc2=st2Array[3];

                String totNumDoc="";

                if(((int)Double.parseDouble(numOfDoc1))>((int)Double.parseDouble(numOfDoc2))){
                    totNumDoc=numOfDoc1;
                }
                else {
                    totNumDoc=numOfDoc2;
                }




                String mergeTerms = "";

                if (term1.equals(term2)) {
                    mergeTerms = term1 + "@" + ((int) (Double.parseDouble(numOfAppear1)) + ((int) (Double.parseDouble(numOfAppear2)))) + "@" + info1 + " " + info2 +"@"+ totNumDoc ;
                    mergePostingTwoFiles.write(mergeTerms);
                    mergePostingTwoFiles.newLine();
                    st1 = bfFirst.readLine();
                    st2 = bfSecond.readLine();

                } else if ((term1.toLowerCase()).equals(term2.toLowerCase())) {

                    mergeTerms = term1.toLowerCase() + "@" + ((int) (Double.parseDouble(numOfAppear1)) + ((int) (Double.parseDouble(numOfAppear2)))) + "@" + info1 + " " + info2+"@"+ totNumDoc;
                    mergePostingTwoFiles.write(mergeTerms);
                    mergePostingTwoFiles.newLine();
                    st1 = bfFirst.readLine();
                    st2 = bfSecond.readLine();
                } else {
                    if ((term1.toLowerCase()).compareTo(term2.toLowerCase()) > 0 ) {
                        mergeTerms = st2;
                        mergePostingTwoFiles.write(mergeTerms);
                        mergePostingTwoFiles.newLine();
                        st2 = bfSecond.readLine();
                    } else {

                        mergeTerms = st1;
                        mergePostingTwoFiles.write(mergeTerms);
                        mergePostingTwoFiles.newLine();
                        st1 = bfFirst.readLine();

                    }

                }
            }


            while (st1!=null && st2==null){
                mergePostingTwoFiles.write(st1);
                mergePostingTwoFiles.newLine();
                st1=bfFirst.readLine();
            }

            while (st1==null && st2!=null){
                mergePostingTwoFiles.write(st2);
                mergePostingTwoFiles.newLine();
                st2=bfSecond.readLine();
            }


            if(toStem){
                filesQueue.add(pathToWrite+"\\"+"s"+posIndex+".txt");
            }
            else{
                filesQueue.add(pathToWrite+"\\"+"p"+posIndex+".txt");
            }

            posIndex++;
            mergePostingTwoFiles.close();
            bfFirst.close();
            bfSecond.close();
            fileFirst.delete();
            fileSecond.delete();
            /*
            File fil1Delete= new File(file1);
            fil1Delete.delete();
            File fil2Delete= new File(file2);
            fil2Delete.delete();

             */

        }
      //mrege yeshut and total posting file
      filesQueue.add(pathToWrite+"\\"+"IC"+(counterWriter-1)+".txt");


        while(!filesQueue.isEmpty()&& filesQueue.size()!=1){
            String totalPost=filesQueue.poll();//the total post
            String yeshutPost=filesQueue.poll();//yeshut post
            File fileFirst=new File(totalPost);
            File fileSecond=new File(yeshutPost);

            File file3;
            if(toStem){
                file3= new File(pathToWrite+"\\"+"s"+posIndex+".txt");
            }
            else{
                file3= new File(pathToWrite+"\\"+"p"+posIndex+".txt");
            }

            BufferedWriter mergePostingTwoFiles=new BufferedWriter(new FileWriter(file3));


            BufferedReader bfFirst= new BufferedReader(new FileReader(fileFirst));
            BufferedReader bfSecond= new BufferedReader(new FileReader(fileSecond));
            st1=bfFirst.readLine();
            st2=bfSecond.readLine();//yeshut

            while (st1!= null && st2!= null) {
                String[] st1Array = st1.split("@");
                String term1 = st1Array[0];
                String numOfAppear1 = st1Array[1];
                String info1 = st1Array[2];
                String numOfDoc1=st1Array[3];

                String[] st2Array = st2.split("@");
                String term2 = st2Array[0];
                String numOfAppear2 = st2Array[1];
                String info2 = st2Array[2];
                String numOfDoc2=st2Array[3];

                String [] docOfYeshut=info2.split(" ");
                String docNameOfYeshut=docOfYeshut[0];

                String mergeTerms = "";

                if (term1.equals(term2)) {
                    if(info1.contains(docNameOfYeshut)){
                        mergeTerms = term1 + "@" + ((int) (Double.parseDouble(numOfAppear1)) + ((int) (Double.parseDouble(numOfAppear2)))) + "@" + info1 + " " + info2 +"@"+ ((int) (Double.parseDouble(numOfDoc1))) ;

                    }
                    else{
                        mergeTerms = term1 + "@" + ((int) (Double.parseDouble(numOfAppear1)) + ((int) (Double.parseDouble(numOfAppear2)))) + "@" + info1 + " " + info2 +"@"+ (((int) (Double.parseDouble(numOfDoc1)))+1) ;

                    }

                    mergePostingTwoFiles.write(mergeTerms);
                    mergePostingTwoFiles.newLine();
                    st1 = bfFirst.readLine();
                    st2 = bfSecond.readLine();

                } else if ((term1.toLowerCase()).equals(term2.toLowerCase())) {


                    if(info1.contains(docNameOfYeshut)){
                        mergeTerms = term1 + "@" + ((int) (Double.parseDouble(numOfAppear1)) + ((int) (Double.parseDouble(numOfAppear2)))) + "@" + info1 + " " + info2 +"@"+ ((int) (Double.parseDouble(numOfDoc1))) ;

                    }
                    else{
                        mergeTerms = term1 + "@" + ((int) (Double.parseDouble(numOfAppear1)) + ((int) (Double.parseDouble(numOfAppear2)))) + "@" + info1 + " " + info2 +"@"+ (((int) (Double.parseDouble(numOfDoc1)))+1) ;

                    }

                    mergePostingTwoFiles.write(mergeTerms);
                    mergePostingTwoFiles.newLine();
                    st1 = bfFirst.readLine();
                    st2 = bfSecond.readLine();
                } else {
                    if ((term1.toLowerCase()).compareTo(term2.toLowerCase()) > 0 ) {
                        mergeTerms = term2+"@"+((int) (Double.parseDouble(numOfAppear2)))+"@"+info2+"@"+"1";
                        mergePostingTwoFiles.write(mergeTerms);
                        mergePostingTwoFiles.newLine();
                        st2 = bfSecond.readLine();
                    } else {

                        mergeTerms = st1;
                        mergePostingTwoFiles.write(mergeTerms);
                        mergePostingTwoFiles.newLine();
                        st1 = bfFirst.readLine();

                    }

                }
            }


            while (st1!=null && st2==null){
                mergePostingTwoFiles.write(st1);
                mergePostingTwoFiles.newLine();
                st1=bfFirst.readLine();
            }





            while (st1==null && st2!=null){
                String[] st2Array = st2.split("@");
                String term2 = st2Array[0];
                String numOfAppear2 = st2Array[1];
                String info2 = st2Array[2];
                mergePostingTwoFiles.write(term2+"@"+((int) (Double.parseDouble(numOfAppear2)))+"@"+info2+"@"+"1");
                mergePostingTwoFiles.newLine();
                st2=bfSecond.readLine();
            }



            if(toStem){
                filesQueue.add(pathToWrite+"\\"+"s"+posIndex+".txt");
            }
            else{
                filesQueue.add(pathToWrite+"\\"+"p"+posIndex+".txt");
            }
            posIndex++;
            mergePostingTwoFiles.close();
            bfFirst.close();
            bfSecond.close();
            fileFirst.delete();
            fileSecond.delete();
            /*
            File fil1Delete= new File(file1);
            fil1Delete.delete();
            File fil2Delete= new File(file2);
            fil2Delete.delete();

             */

        }





        //connect the pointer to the dictionary
        int countPointer=0;
        String lastPosting =filesQueue.poll();
        File file1= new File(lastPosting);
       // File file2=new File(pathToWrite+"\\posting.txt");
        //file1.renameTo(file2);
        BufferedReader bf= new BufferedReader(new FileReader(file1));
        String st="";
        while ((st=bf.readLine())!= null){
            String[] st1Array = st.split("@");
            String term1 = st1Array[0];
            int totAppear=(int)Double.parseDouble(st1Array[1]);
            termData temp=(p.getDictionary()).get(term1);
            if(temp==null){
                System.out.println(st);

                continue;
            }

            int numOfDoc=temp.getNumOfDoc();
            int totalApearence = temp.getTotalApearance();
            int pointer=countPointer;
            p.getDictionary().replace(term1,new termData(numOfDoc,pointer,term1,totalApearence));
            countPointer++;
        }

        bf.close();
    }


    private static void copyStopWords(String pathToRead, String pathToWrite) {

        String sourcePath = pathToRead+"\\stop_words.txt";
        String destinationPath = pathToWrite+"stop_words.txt";
        File source = new File(sourcePath);
        File dest = new File(destinationPath);
        try {
            Files.copy(source.toPath(), dest.toPath());
        } catch (IOException e) {

        }
    }

}

