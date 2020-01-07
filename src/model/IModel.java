package model;

import java.io.IOException;
import java.util.HashMap;

public interface IModel {
    public void setDictionary(String pathToRead1, String pathToWrite1, boolean toStem1) throws IOException;
    public void clearData();
    public void setPahtToRead(String pathToRead);
    public void setPathToWrite(String pathToWrite);
    public void setToStem(boolean toStem);
    public HashMap<String, termData> getDictionary();
    public void showDictionary();
    public void loadDictionary(String pathToRead1, String pathToWrite1, boolean toStem1) throws IOException;
}