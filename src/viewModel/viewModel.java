package viewModel;

import model.IModel;
import model.myModel;
import model.termData;

import java.io.IOException;
import java.util.*;

public class viewModel extends Observable implements Observer {
    private IModel model;
    private String pahToRead="";
    private String pathToWrite="";
    private boolean toStem=false;
    HashMap<String, termData> dictionary;

    public HashMap<String, termData> getDictionary() {
        return dictionary;
    }
    public HashMap<String, Map<String,Double>> getRankingMap(){
        return model.getRankingMap();
    }

    public viewModel(IModel model) {
        this.model = model;
    }
    public void showDictionary(){
        model.showDictionary();
    }
    public void setPahToRead(String path){
        pahToRead=path;
        model.setPahtToRead(pahToRead);
    }
    public void setPathToWrite(String path){
        pathToWrite=path;
        model.setPathToWrite(pathToWrite);
    }
    public void setPahtForQueries(String pathForQueries) {
        model.setPathForQueries(pathForQueries);
    }

    public void setToStem(boolean toStemUpdate){
        toStem=toStemUpdate;
        model.setToStem(toStem);
    }
    @Override
    public void update(Observable o, Object arg) {
        if (o==model){
            this.dictionary=model.getDictionary();
            setChanged();
            notifyObservers();
        }
    }
    public void setDictionary() throws IOException {
        model.setDictionary(pahToRead,pathToWrite,toStem);
    }
    public void clearData(){
        model.clearData();
    }

    public void loadDictionary() throws IOException {
        model.loadDictionary(pahToRead,pathToWrite,toStem);
    }
    public void callSearchOneQuery(String nameQuery, String query) throws IOException {
        model.callSearchOneQuery(nameQuery,query);
    }
    public void callSearchManyQuery(String pathOfQueries) throws IOException{
        model.callSearchManyQuery(pathOfQueries);
    }
}
