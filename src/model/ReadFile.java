package model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.HashMap;

public class ReadFile {
    private File file;
    private File[] folders;

    public int getSizeOfFolder(){
        return folders.length;
    }

    public ReadFile(String pathToRead) {
        file = new File(pathToRead);
        folders = file.listFiles();
    }

    public HashMap ReadFile(int start, int end) throws IOException {
         HashMap<String,DocDetails> DocDetailsMap = new HashMap<>();

        for(int i=start;i<end &&i<folders.length; i++){
            File f= folders[i];
            //System.out.println("i'm in folder:" + f.getName());

            if(f.isDirectory()){
                File[] txtFiles = f.listFiles();
                for(File txt: txtFiles){

              //      System.out.println("i'm in file:" + txt.getName());
                    readOneFile(txt.getPath(), DocDetailsMap);
                }
            }
            else{
                readOneFile(f.getPath(), DocDetailsMap);

            }
        }

        return DocDetailsMap;
    }

    public void readOneFile(String textPath,HashMap<String,DocDetails> DocDetailsMap) throws IOException {
        int counter = 0;
        File input = new File(textPath);
        Document document = Jsoup.parse(input, "UTF-8");
        Elements docs = document.getElementsByTag("DOC");
        for(Element e: docs){
            DocDetails docDetailsToAdd = new DocDetails(e.getElementsByTag("DOCNO").text(),e.getElementsByTag("TEXT").text());
            DocDetailsMap.put(e.getElementsByTag("DOCNO").text(), docDetailsToAdd);
        }




    }




}
