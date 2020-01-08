package model;

import javafx.util.Pair;
import org.tartarus.snowball.ext.PorterStemmer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class QueryPraser {
    private HashMap<String, termData> dictionary;
    private HashSet<String> stopWords;
    private boolean NotToCheckWord2;
    private boolean NotToCheckWord3=false;
    private boolean NotToCheckWord4=false;
    private HashMap<String,String> monthList;
    private int pointerLines=1;
    private HashMap<String, DocDetails> DocInfo;
    private HashMap<String, Pair<String,Integer>> YeshutGlobalMap;
    private boolean toStem;
    PorterStemmer stemmer;

    public QueryPraser(String stopWordsPath) throws IOException {
        stemmer = new PorterStemmer();
        toStem=false;
        dictionary = new HashMap<String, termData>();
        stopWords = new HashSet<>();
        NotToCheckWord2=false;
        monthList = new HashMap<>();
        initializeMonthList(monthList);
        insertStopWords(stopWordsPath);
        DocInfo=new HashMap<>();
        YeshutGlobalMap=new HashMap<>();
    }


    public HashMap<String, DocDetails> getDocInfo() {
        return DocInfo;
    }


    public void initializeMonthList(HashMap<String, String> monthList) {
        monthList.put("January", "01");
        monthList.put("February", "02");
        monthList.put("March", "03");
        monthList.put("April", "04");
        monthList.put("May", "05");
        monthList.put("June", "06");
        monthList.put("July", "07");
        monthList.put("August", "08");
        monthList.put("September", "09");
        monthList.put("October", "10");
        monthList.put("November", "11");
        monthList.put("December", "12");

        monthList.put("JANUARY", "01");
        monthList.put("FEBRUARY", "02");
        monthList.put("MARCH", "03");
        monthList.put("APRIL", "04");
        monthList.put("MAY", "05");
        monthList.put("JUNE", "06");
        monthList.put("JULY", "07");
        monthList.put("AUGUST", "08");
        monthList.put("SEPTEMBER", "09");
        monthList.put("OCTOBER", "10");
        monthList.put("NOVEMBER", "11");
        monthList.put("DECEMBER", "12");

        monthList.put("Jan", "01");
        monthList.put("Feb", "02");
        monthList.put("Mar", "03");
        monthList.put("Apr", "04");
        monthList.put("Jun", "06");
        monthList.put("Jul", "07");
        monthList.put("Aug", "08");
        monthList.put("Sep", "09");
        monthList.put("Oct", "10");
        monthList.put("Nov", "11");
        monthList.put("Dec", "12");
    }

    public void insertStopWords(String stopWordFile) throws IOException {

        File file = new File(stopWordFile);

        BufferedReader br = new BufferedReader(new FileReader(file));

        String st;
        while ((st = br.readLine()) != null) {
            stopWords.add(st);
            if(st.charAt(0)>='a'&&st.charAt(0)<='z'){
                st=st.substring(0, 1).toUpperCase() + st.substring(1);
            }
            stopWords.add(st);
            stopWords.add(st.toUpperCase());
        }
    }


    public HashMap<String, termData> getDictionary() {
        return dictionary;
    }

    public void addMonthNumbersFirst(String word1, String word2, HashMap<String,Integer> docmap){
        String wordToDictionary="";
        if(word1.length()==2){
            wordToDictionary=monthList.get(word2)+"-"+word1;
        }
        else{
            wordToDictionary=monthList.get(word2)+"-"+"0"+word1;
        }
        addToDictionary(wordToDictionary,docmap);
        NotToCheckWord2=true;
    }

    public void addMonth_MonthFirst(String word1, String word2, HashMap<String,Integer> docmap){
        String wordToDictionary="";
        if(word2.length()<=2){//in case of month+date
            if(word2.length()==2){//in case of month+date with 2 characters
                wordToDictionary=monthList.get(word1)+"-"+word2;
            }
            else{//in case of date with 1 character
                wordToDictionary=monthList.get(word1)+"-"+"0"+word2;
            }

        }
        else if(word2.length()==4){//in case of month+year
            wordToDictionary=word2+"-"+monthList.get(word1);
        }
        else{
            addToDictionary(word1.toUpperCase(),docmap);
            addToDictionary(word2.toUpperCase(),docmap);
            NotToCheckWord2 = true;
            return;
        }

        addToDictionary(wordToDictionary, docmap);
        NotToCheckWord2 = true;
    }

    public void addToDictionary(String newWord, HashMap<String,Integer> docmap) {
        if(newWord.length()==0){
            return;
        }

        if (!dictionary.containsKey(newWord)) {
            dictionary.put(newWord, new termData(1, pointerLines,newWord,1));
            pointerLines++;
            if (!docmap.containsKey(newWord)) {
                docmap.put(newWord, 1);
            }

        } else {
            int currentAppear = dictionary.get(newWord).getTotalApearance();
            dictionary.get(newWord).setTotalApearance(currentAppear+1);
            if (!docmap.containsKey(newWord)) {
                docmap.put(newWord, 1);
                int newCounter = dictionary.get(newWord).getNumOfDoc();
                newCounter++;
                int pointer = dictionary.get(newWord).getPointerLine();
                dictionary.replace(newWord, new termData(newCounter, pointer,newWord,currentAppear+1));
            } else {
                int newCounter = docmap.get(newWord);
                newCounter++;
                docmap.replace(newWord, newCounter);

            }
        }

        //System.out.println(newWord);

    }


    public boolean checkIfFriction(String s){
        for(int i=0; i<s.length()-1; i++){
            if(s.charAt(i)=='/'){
                return true;
            }
            if(!(s.charAt(i)>='0'&&s.charAt(i)<='9')){
                return false;
            }
        }
        return false;
    }

    public boolean checkIfPercent(String word2){
        if(word2.equals("percent") || word2.equals("percentage")){
            NotToCheckWord2=true;
            return true;
        }
        return false;
    }


    public void addNumberToDictionary(String word1, String word2, HashMap<String, Integer> docmap) {
        String wordToDictionary = "";
        boolean withPercent = false;
        if (checkIfFriction(word1)) {
            addToDictionary(word1, docmap);
            return;
        }
        if (word1.charAt(word1.length() - 1) == '%') {
            withPercent = true;
            word1 = word1.substring(0, word1.length() - 1);
        }


        String[] splitByDot = word1.split("\\.");
        String[] splitByPsik;
        if (splitByDot.length == 0) {//in case there is no dot
            splitByDot = new String[1];
            splitByDot[0] = word1;
        }
        //in case there is a dot
        splitByPsik = splitByDot[0].split("\\,");


        String newNumber = "";
        if (splitByPsik.length == 0) {//if there is no psik
            newNumber = splitByDot[0];
            splitByPsik = new String[1];
            splitByPsik[0] = splitByDot[0];
        } else {
            for (int j = 0; j < splitByPsik.length; j++) {//making new number witout Psik
                newNumber = newNumber + splitByPsik[j];
            }
        }





        double newNumberDouble;
        try{
            newNumberDouble = Double.parseDouble(newNumber);

            if (newNumberDouble >= 1000 && newNumberDouble < 1000000) {
                newNumberDouble = newNumberDouble / 1000;
                wordToDictionary = newNumberDouble +" K";

            } else if (newNumberDouble >= 1000000 && newNumberDouble < 1000000000) {
                newNumberDouble = newNumberDouble / 1000000;
                String tempMilion = newNumberDouble + "";
                String[] tempMilionSplit = tempMilion.split("\\.");
                if (tempMilionSplit.length == 0) {
                    tempMilionSplit = new String[2];
                    tempMilionSplit[0] = tempMilion;
                    tempMilionSplit[1] = "";
                }
                if (tempMilionSplit[1].length() <= 3) {
                    wordToDictionary = wordToDictionary + tempMilionSplit[0] + "." + tempMilionSplit[1] + " M";
                } else {
                    wordToDictionary = wordToDictionary + tempMilionSplit[0] + "." + tempMilionSplit[1].substring(0, 3) + " M";
                }

            } else if (newNumberDouble >= 1000000000) {
                newNumberDouble = newNumberDouble / 1000000000;
                String tempMilion = newNumberDouble + "";
                String[] tempMilionSplit = tempMilion.split("\\.");
                if (tempMilionSplit.length == 0) {
                    tempMilionSplit = new String[2];
                    tempMilionSplit[0] = tempMilion;
                    tempMilionSplit[1] = "";
                }
                if (tempMilionSplit[1].length() <= 3) {
                    wordToDictionary = wordToDictionary + tempMilionSplit[0] + "." + tempMilionSplit[1] + " B";
                } else {
                    wordToDictionary = wordToDictionary + tempMilionSplit[0] + "." + tempMilionSplit[1].substring(0, 3) + " B";
                }

            } else {// its a number smaller than 1000
                if (splitByDot.length > 1) {//there is something after the dot
                    if (splitByDot[1].length() <= 3) {
                        wordToDictionary = wordToDictionary + splitByDot[0] + "." + splitByDot[1];
                    } else {
                        wordToDictionary = wordToDictionary + splitByDot[0] + "." + splitByDot[1].substring(0, 3);
                    }
                } else {
                    wordToDictionary = splitByDot[0];
                }

                if (word2.equals("Thousand") || word2.equals("thousand")) {
                    wordToDictionary = wordToDictionary + " K";
                    NotToCheckWord2 = true;
                } else if (word2.equals("Million") || word2.equals("million") || word2.equals("m")) {
                    wordToDictionary = wordToDictionary + " M";
                    NotToCheckWord2 = true;

                } else if (word2.equals("Billion") || word2.equals("billion") || word2.equals("bn")) {
                    wordToDictionary = wordToDictionary + " B";
                    NotToCheckWord2 = true;

                }

            }
            if (checkIfFriction(word2)) {
                wordToDictionary = wordToDictionary + " " + word2;
                NotToCheckWord2 = true;
            }
            if (checkIfPercent(word2) || withPercent) {
                wordToDictionary = wordToDictionary + "%";
            }

            addToDictionary(wordToDictionary, docmap);
        }
        catch (NumberFormatException e){
            addToDictionary(word1.toUpperCase(),docmap);
        }

    }

    public String wordWithoutPsik(String s) {
        String[] splitByPsik = s.split("\\,");//in case there is dot
        String newNumberWithoutPsik = "";
        if (splitByPsik.length == 0) {//in case there is no dot and no psik;
            splitByPsik = new String[1];
            splitByPsik[0] = "";
            newNumberWithoutPsik = s;
        } else {
            for (int j = 0; j < splitByPsik.length; j++) {//making new number witout Psik
                newNumberWithoutPsik = newNumberWithoutPsik + splitByPsik[j];
            }
        }
        return newNumberWithoutPsik;
    }

    public String wordWithoutPsikANDDot(String s) {
        String[] splitByDot = s.split("\\.");
        String[] splitByPsik;
        if (splitByDot.length == 0) {//in case there is no dot
            splitByDot = new String[1];
            splitByDot[0] = s;
        }
        splitByPsik = splitByDot[0].split("\\,");//in case there is dot
        String newNumberWithoutPsikDot = "";
        if (splitByPsik.length == 0) {//in case there is no dot and no psik;
            splitByPsik = new String[1];
            splitByPsik[0] = "";
            newNumberWithoutPsikDot = splitByDot[0];
        } else {
            for (int j = 0; j < splitByPsik.length; j++) {//making new number witout Psik
                newNumberWithoutPsikDot = newNumberWithoutPsikDot + splitByPsik[j];
            }
        }
        return newNumberWithoutPsikDot;

    }

    public void addPrice(String word1, String word2, String word3, String word4, HashMap<String, Integer> docmap) {
        String wordToDictionary = "";

        if (word1.charAt(0) == '$') {
            word1 = word1.substring(1);
        }


        String newNumberWithoutPsikDot = wordWithoutPsikANDDot(word1);
        String numberWithoutPsikONLY = wordWithoutPsik(word1);

        try {


            double doubleWithoutPsikDot = Double.parseDouble(newNumberWithoutPsikDot);
            double doubleWithoutPsikONLY = Double.parseDouble(numberWithoutPsikONLY);

            if (doubleWithoutPsikDot >= 1000000) {
                doubleWithoutPsikDot = doubleWithoutPsikDot / 1000000;
                String tempMilion = doubleWithoutPsikDot + "";
                String[] tempMilionSplit = tempMilion.split("\\.");
                if (tempMilionSplit[1].length() <= 3) {
                    wordToDictionary = wordToDictionary + tempMilionSplit[0] + "." + tempMilionSplit[1];
                } else {
                    wordToDictionary = wordToDictionary + tempMilionSplit[0] + "." + tempMilionSplit[1].substring(0, 3);
                }
                wordToDictionary = wordToDictionary + " M Dollars";
            } else {//in case the number is smaller then million
                if (word2.equals("bn") || word2.equals("billion")) {
                    doubleWithoutPsikONLY = doubleWithoutPsikONLY * 1000;
                    int temp = (int) doubleWithoutPsikONLY / 1000;
                    wordToDictionary = wordToDictionary + temp + "000" + " M Dollars";
                } else if (word2.equals("trillion")) {
                    doubleWithoutPsikONLY = doubleWithoutPsikONLY * 1000000;
                    int temp = (int) doubleWithoutPsikONLY / 1000000;
                    //doubleWithoutPsikONLY=(int)doubleWithoutPsikONLY/1000000;
                    wordToDictionary = wordToDictionary + temp + "000000" + " M Dollars";
                } else if (word2.equals("m") || word2.equals("million")) {
                    wordToDictionary = wordToDictionary + numberWithoutPsikONLY + " M Dollars";


                } else if (checkIfFriction(word2)) {//the number is smaller the million and there is no other word that make it bigger(for example "million")
                    wordToDictionary = newNumberWithoutPsikDot + " " + word2 + " Dollars";
                } else {
                    wordToDictionary = numberWithoutPsikONLY + " Dollars";
                }

            }

            addToDictionary(wordToDictionary, docmap);
            HashSet<String> word2Set = new HashSet<>();
            word2Set.add("Dollars");
            word2Set.add("million");
            word2Set.add("billion");
            word2Set.add("m");
            word2Set.add("bn");
            word2Set.add("trillion");

            HashSet<String> word3Set = new HashSet<>();
            word3Set.add("Dollars");
            word3Set.add("U.S");

            HashSet<String> word4Set = new HashSet<>();
            word4Set.add("dollars");
            word4Set.add("Dollars");


            if (word2Set.contains(word2) || checkIfFriction(word2)) {
                if (word3Set.contains(word3)) {
                    if (word4Set.contains(word4)) {
                        NotToCheckWord4 = true;
                    } else {
                        NotToCheckWord3 = true;
                    }
                } else {
                    NotToCheckWord2 = true;
                }
            }



            addToDictionary(wordToDictionary, docmap);




        }
        catch (NumberFormatException e){
            addToDictionary(word1.toUpperCase(), docmap);

        }
    }


    public void addUpperLowerLetter(String word1, HashMap<String, Integer> docmap) {
        if(word1.equals("and")){
            System.out.println("i'm and - and i am in upperLowerLetter");
        }
        if (!dictionary.containsKey(word1)) {
            if (word1.charAt(0) >= 'A' && word1.charAt(0) <= 'Z') {

                String temp =word1.toLowerCase();
                if(!dictionary.containsKey(temp)){
                    addToDictionary(word1.toUpperCase(),docmap);
                    if(word1.toUpperCase().equals("AND")){
                        System.out.println("i'm and - and i am in upperLowerLetter - changing to upper");
                    }
                }
                else{
                    addToDictionary(temp,docmap);
                }
            }
            else{
                String tempToUpper = word1.toUpperCase();
                String tempToLower = word1.toLowerCase();

                if(dictionary.containsKey(tempToUpper)){
                    termData toSave=dictionary.get(tempToUpper);
                    dictionary.remove(tempToUpper);
                    dictionary.put(tempToLower,toSave);
                    //change in the dictionary+local hashmap to the word with the small letter
                    if(docmap.containsKey(tempToUpper)){
                        int counter=docmap.get(tempToUpper);
                        docmap.remove(tempToUpper);
                        docmap.put(tempToLower,counter);

                    }
                    addToDictionary(tempToLower,docmap);


                }
                else{
                    addToDictionary(tempToLower,docmap);                }
            }
        }
        else{
            addToDictionary(word1,docmap);
            if(word1.equals("and")){
                System.out.println("i'm and - and i am in dictionary already");
            }
            if(word1.equals("AND")){
                System.out.println("i'm AND - and i am in dictionary already");
            }
        }
    }

    public String checkDotPsikHyphenInTheEnd(String wordToCheck){
        String wordToReturn = wordToCheck;
        if(wordToCheck.length()!=0) {
            if (wordToCheck.charAt(wordToCheck.length() - 1) == ',' || wordToCheck.charAt(wordToCheck.length() - 1) == '.' || wordToCheck.charAt(wordToCheck.length() - 1) == '-') {
                wordToReturn = wordToCheck.substring(0, wordToCheck.length() - 1);
            }
        }
        return wordToReturn;

    }

    public boolean checkTermWithBETWEENandAdd(String word1,String word2,String word3,String word4,HashMap<String,Integer> docmap){
        boolean isItwithBetween = false;
        if(word1.equals("between")){
            if(word2.length()!=0 &&word2.charAt(0)>='0' && word2.charAt(0)<='9'){
                if(word3.length()!=0 && word3.equals("and")){
                    if(word4.length()!=0 &&word4.charAt(0)>='0' && word4.charAt(0)<='9'){
                        String wordToDictionary = word1+ " "+ word2+" "+word3+" "+ word4;
                        addToDictionary(wordToDictionary,docmap);
                        isItwithBetween=true;
                    }
                }
            }
        }
        else{
            isItwithBetween=false;
        }
        return isItwithBetween;
    }

    public String  removeUnccessaryChar(String word){

        while (word.length() >= 2 && (word.charAt(0) == '.' || word.charAt(0) == ',' || word.charAt(0) == '/' || word.charAt(0) == '%' || word.charAt(0) == '+' || word.charAt(0) == '|')) {

            word = word.substring(1);
        }

        if (word.length() == 1 && (word.charAt(0) == '.' || word.charAt(0) == ',' || word.charAt(0) == '/' || word.charAt(0) == '%' || word.charAt(0) == '+' || word.charAt(0) == '|')) {
            word = "";
        }
        return word;

    }

    public void buildDictionary(String query) {
            HashMap<String, Integer> suspectedLocalMapYeshut=new HashMap<>();
            HashMap<String,Integer> docMap= new HashMap<>(); //hashmap for every doc
            String text = query;
            String[] splitedText = text.split(" |\\;|\\?|\\!|\\:|\\_|\\(|\\)|\"|\t|\\]|\\[|\"|\\*|\\#|\"|\\&|\\@|\\'");
            for (int i = 0; i < splitedText.length; i++) {
                String word1 = splitedText[i];
                String word2 = "";
                String word3 = "";
                String word4 = "";
                String wordToDictionary = "";

                if (i <= splitedText.length - 2) {//set the second word
                    word2 = checkDotPsikHyphenInTheEnd(splitedText[i + 1]);
                    word2=removeUnccessaryChar(word2);
                }
                if(i<= splitedText.length -3 ){
                    word3 = checkDotPsikHyphenInTheEnd(splitedText[i+2]);
                    word3=removeUnccessaryChar(word3);

                }
                if(i<= splitedText.length -4 ){
                    word4 =checkDotPsikHyphenInTheEnd(splitedText[i+3]);
                    word4=removeUnccessaryChar(word4);

                }
                if(toStem){
                    stemmer.setCurrent(word1); //set string you need to stem
                    stemmer.stem();  //stem the word
                    word1= stemmer.getCurrent();//get the stemmed word

                    stemmer.setCurrent(word2); //set string you need to stem
                    stemmer.stem();  //stem the word
                    word2= stemmer.getCurrent();//get the stemmed word

                    stemmer.setCurrent(word3); //set string you need to stem
                    stemmer.stem();  //stem the word
                    word3= stemmer.getCurrent();//get the stemmed word

                    stemmer.setCurrent(word4); //set string you need to stem
                    stemmer.stem();  //stem the word
                    word4= stemmer.getCurrent();//get the stemmed word
                }




                word1=checkDotPsikHyphenInTheEnd(word1);
                word1=removeUnccessaryChar(word1);


                if(stopWords.contains(word1)){
                    continue;
                }





                if(word1.equals(" ")|| word1.length()==0 || word1.equals("%")){
                    continue;
                }


                int numOfSuspected =howMuchSuspectedWords(word1,word2,word3,word4);
                if(numOfSuspected>1){
                    if(numOfSuspected==2){
                        word1=word1+" "+word2;
                        i++;
                    }
                    else if(numOfSuspected==3){
                        word1=word1+" "+word2+" "+word3;
                        i=i+2;
                    }
                    else if(numOfSuspected==4){
                        word1=word1+" "+word2+" "+word3+" "+word4;
                        i=i+3;
                    }


                    String tempWord1= word1.toUpperCase();
                    if(suspectedLocalMapYeshut.containsKey(tempWord1)){
                        suspectedLocalMapYeshut.replace(tempWord1,suspectedLocalMapYeshut.get(tempWord1)+1);
                    }
                    else{
                        suspectedLocalMapYeshut.put(tempWord1,1);
                    }
                    continue;
                }

                if(checkTermWithBETWEENandAdd(word1,word2,word3,word4,docMap)){//this function must be here because the word between is a stop word
                    i++;
                    i++;
                    i++;
                    continue;
                }




                if((word1.charAt(0)>='0'&& word1.charAt(0)<='9')){//it's a number
                    if(word1.contains("-")){
                        word1=word1.toUpperCase();
                        addToDictionary(word1,docMap);
                        continue;
                    }

                    //try{
                    if(word1.charAt(word1.length()-1)=='f' ||word1.charAt(word1.length()-1)=='F'|| word1.charAt(word1.length()-1)=='d'|| word1.charAt(word1.length()-1)=='D'){
                        word1=word1.toUpperCase();
                        addToDictionary(word1,docMap);

                        continue;
                    }
                    // Double.parseDouble(word1);
                    //fixing a bug happens only in this case: we seperate the word but the original string wasn't update, so it's skeeps on word
                    //because of NotToCheckWord3 parameter
                    //}
//                    catch (NumberFormatException e){
//                        word1=word1.toUpperCase();
//                        addToDictionary(word1,docMap);
//
//
//                        continue;
//                    }



                    if((word2.length()!=0&&(word2.equals("Dollars")||word2.equals("dollars"))) || (word3.length()!=0&&(word3.equals("Dollars")||word3.equals("dollars")))) {
                        addPrice(word1,word2,word3,word4,docMap);
                    }
                    else if(((word3.length()!=0 &&word4.length()!=0) &&((word4.equals("Dollars")||word4.equals("dollars"))&&word3.equals("U.S")))){
                        addPrice(word1,word2,word3,word4,docMap);
                    }
                    else if(monthList.containsKey(word2)){
                        addMonthNumbersFirst(word1,word2,docMap);
                    }
                    else {
                        boolean toContinue=false;
                        for(int j=0; j<word1.length() && !toContinue; j++){
                            if(((word1.charAt(j)>='a') && (word1.charAt(j)<='z'))||((word1.charAt(j)>='A') && (word1.charAt(j)<='Z'))){
                                addToDictionary(word1.toUpperCase(),docMap);
                                toContinue=true;

                            }
                        }
                        if(toContinue==false){
                            addNumberToDictionary(word1,word2,docMap);

                        }
                    }
                }
                else if(word1.charAt(0)=='$'){                         // another if to check if first char is $
                    addPrice(word1,word2,word3,word4,docMap);
                }
                else if(word1.charAt(0)=='-'){//in case that the word is --
                    continue;
                }
                else{
                    if(monthList.containsKey(word1)&& word2.length()!=0 && word2.charAt(0)<='9'&& word2.charAt(0)>='0'){
                        addMonth_MonthFirst(word1,word2,docMap);
                    }
                    else{
                        addUpperLowerLetter(word1,docMap);
                    }

                }
                if(NotToCheckWord2){
                    NotToCheckWord2=false;
                    i++;
                }
                if(NotToCheckWord3){
                    NotToCheckWord3=false;
                    i++;
                    i++;
                }
                if(NotToCheckWord4){
                    NotToCheckWord4=false;
                    i++;
                    i++;
                    i++;
                }



            }//finish parser doc words

    }

    public void YeshutAddToDictionary(HashMap<String, Pair<String,Integer>> termGlobalMap, HashMap<String,Integer> termLocalMap,TreeMap<String,String> localDictionary,String DocName,String termName){
        String termNameUpper = termName.toUpperCase();
        String newDetails = termGlobalMap.get(termNameUpper).getKey()+" "+ termGlobalMap.get(termNameUpper).getValue()+ " "+ DocName+ " "+ termLocalMap.get(termName);
        int setTotalAppear=termGlobalMap.get(termNameUpper).getValue()+termLocalMap.get(termName);//take counter of global and local
        dictionary.put(termNameUpper,new termData(2,1,termNameUpper,setTotalAppear));
        localDictionary.put(termNameUpper,newDetails);
        termGlobalMap.remove(termName);
    }

    public HashMap<String, Pair<String, Integer>> getYeshutGlobalMap() {
        return YeshutGlobalMap;
    }

    public int howMuchSuspectedWords(String word1, String word2, String word3, String word4){
        int numOfSuspected = 0;
        if(word1.charAt(0)>='A'&& word1.charAt(0)<='Z'){
            numOfSuspected=1;
            if(word2.length()!=0 && word2.charAt(0)>='A'&& word2.charAt(0)<='Z'){
                numOfSuspected=2;
                if(word3.length()!=0 && word3.charAt(0)>='A'&& word3.charAt(0)<='Z'){
                    numOfSuspected=3;
                    if(word4.length()!=0 && word4.charAt(0)>='A'&& word4.charAt(0)<='Z'){
                        numOfSuspected=4;

                    }
                }
            }
        }
        return numOfSuspected;
    }
}


