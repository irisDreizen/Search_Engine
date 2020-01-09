package model;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public interface IModel {
    public void setDictionary(String pathToRead1, String pathToWrite1, boolean toStem1) throws IOException;
    public void clearData();
    public void setPahtToRead(String pathToRead);
    public void setPathToWrite(String pathToWrite);
    public void setToStem(boolean toStem);
    public HashMap<String, termData> getDictionary();
    public void showDictionary();
    public void loadDictionary(String pathToRead1, String pathToWrite1, boolean toStem1) throws IOException;
    public void callSearchOneQuery(String nameQuery, String query) throws IOException;
    public void callSearchManyQuery(String pathOfQueries) throws IOException;
    public void setPathForQueries(String pathForQueries);
    public HashMap<String, Map<String,Double>> getRankingMap();
}