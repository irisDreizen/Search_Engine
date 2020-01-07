package viewModel;

import model.IModel;
import model.myModel;
import model.termData;

import java.io.IOException;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public class viewModel extends Observable implements Observer {
    private IModel model;
    private String pahToRead="";
    private String pathToWrite="";
    private boolean toStem=false;
    HashMap<String, termData> dictionary;

    public HashMap<String, termData> getDictionary() {
        return dictionary;
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
}
