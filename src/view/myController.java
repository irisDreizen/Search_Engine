package view;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Pair;
import model.myModel;
import model.termData;
import viewModel.viewModel;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.util.*;


public class myController implements Observer {
    private viewModel myViewModel;
    private Stage myStage;
    private String pathToRead="";
    private String pathToWrite="";
    private String pathForQuery="";
    private boolean toStem=false;
    private boolean toUseSemantic=false;
    HashMap<String, termData> dictionary;

    @FXML
    public javafx.scene.control.TextField txtfld_pathToRead;
    public javafx.scene.control.TextField txtfld_pathToWrite;
    public javafx.scene.control.TextField txtfld_singleQuery;
    public javafx.scene.control.TextField txtfld_pathForQuery;
    public javafx.scene.control.CheckBox check_stem;
    public javafx.scene.control.CheckBox check_semantic;
    public javafx.scene.control.Button btn_dictionaryOn;
    public javafx.scene.control.Button btn_clearData;
    public javafx.scene.control.Button btn_showDictionary;
    public javafx.scene.control.Button btn_loadDictionary;


    @Override
    public void update(Observable o, Object arg) {
        if (o == myViewModel) {
            dictionary=myViewModel.getDictionary();
        }
    }
    public void setViewModel(viewModel viewModel) {
        this.myViewModel = viewModel;

    }

    public void setStage(Stage primaryStage) {
        myStage=primaryStage;
    }

    public void callSearchOneQuery() throws Exception {
        setToUseSemantics();
        setToStem();
        myViewModel.setPahtForQueries(txtfld_pathToWrite.getText());
        this.myViewModel.callSearchOneQuery("IC", txtfld_singleQuery.getText());
        this.myViewModel.writeQueryToDisk();
        showQueries();
    }

    public void callSearchManyQuery() throws Exception {
        setToUseSemantics();
        String pathOfQueries = txtfld_pathForQuery.getText();
        myViewModel.setPahtForQueries(pathOfQueries);
        myViewModel.callSearchManyQuery(pathOfQueries);
        this.myViewModel.writeQueryToDisk();
        showQueries();
    }

    public void loadQueryPath(){
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setDialogTitle("Choose a directory to read: ");
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int returnValue = jfc.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            if (jfc.getSelectedFile().isDirectory()) {
                txtfld_pathForQuery.setText(jfc.getSelectedFile().getPath());
                pathForQuery=jfc.getSelectedFile().getPath();
                myViewModel.setPahtForQueries(pathForQuery);
                //  System.out.println("You selected the directory: " + jfc.getSelectedFile());
            }
        }
    }

    public void loadPathToRead(){
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setDialogTitle("Choose a directory to read: ");
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int returnValue = jfc.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            if (jfc.getSelectedFile().isDirectory()) {
                txtfld_pathToRead.setText(jfc.getSelectedFile().getPath());
                pathToRead=jfc.getSelectedFile().getPath();
                myViewModel.setPahToRead(pathToRead);
              //  System.out.println("You selected the directory: " + jfc.getSelectedFile());
            }
        }

    }

    public void loadPathToWrite(){
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setDialogTitle("Choose a directory to Write: ");
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int returnValue = jfc.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            if (jfc.getSelectedFile().isDirectory()) {
                txtfld_pathToWrite.setText(jfc.getSelectedFile().getPath());
                pathToWrite=jfc.getSelectedFile().getPath();
                myViewModel.setPathToWrite(pathToWrite);
                //  System.out.println("You selected the directory: " + jfc.getSelectedFile());
            }
        }

    }

    public void setPathToRead() {
        this.pathToRead = txtfld_pathToRead.getText();
        myViewModel.setPahToRead(pathToRead);
    }

    public void setPathToWrite() {
        this.pathToWrite = txtfld_pathToWrite.getText();
        myViewModel.setPathToWrite(pathToWrite);
    }

    public void setToStem(){
        if(check_stem.isSelected()){
            toStem=true;
        }
        else{
            toStem=false;
        }
        myViewModel.setToStem(toStem);
    }


    public void setToUseSemantics() {
        if(check_semantic.isSelected()){
            toUseSemantic=true;
        }
        else{
            toUseSemantic=false;
        }
        myViewModel.setToUseSemantics(toUseSemantic);
    }
    public void setDictionary() throws IOException {
        File f1 = new File(txtfld_pathToRead.getText());
        if(!f1.exists()){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"pathToRead is not a valid path");
            Optional<ButtonType> result = alert.showAndWait();
            return;
        }
        if(!f1.isDirectory()){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"pathToRead is not a directory path");
            Optional<ButtonType> result = alert.showAndWait();
            return;
        }
        File f = new File(txtfld_pathToWrite.getText());
        if(!f.exists()){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"pathToWrite is not a valid path");
            Optional<ButtonType> result = alert.showAndWait();
            return;
        }
        if(!f.isDirectory()){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"pathToWrite is not a directory path");
            Optional<ButtonType> result = alert.showAndWait();
            return;
        }
        this.pathToRead = txtfld_pathToRead.getText();
        myViewModel.setPahToRead(pathToRead);
        this.pathToWrite = txtfld_pathToWrite.getText();
        myViewModel.setPathToWrite(pathToWrite);
        myViewModel.setToStem(toStem);
        myViewModel.setDictionary();
        btn_clearData.setDisable(false);
        btn_loadDictionary.setDisable(false);
        btn_showDictionary.setDisable(false);
    }
    public void deleteData(){
        myViewModel.clearData();
    }

    public void showDictionary() {
        myViewModel.showDictionary();
    }
    public void loadDictionary() throws IOException {
        this.pathToRead = txtfld_pathToWrite.getText();
        myViewModel.setPahToRead(pathToRead);
        this.pathToWrite = txtfld_pathToWrite.getText();
        myViewModel.setPathToWrite(pathToWrite);
        myViewModel.setToStem(toStem);
        myViewModel.loadDictionary(pathToRead,pathToWrite);
    }



    public void showQueries(){
        Stage stage = new Stage();
        stage.setTitle("Display Queries");

        Map<String,Map<String,Double>> rankingMap = myViewModel.getRankingMap();

        TableColumn<Map.Entry<String,Map<String,Double>>, String> column1 = new TableColumn<>("Q ID");
        column1.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<String,Map<String,Double>>, String>, ObservableValue<String>>() {

            @Override
            public ObservableValue<String > call(TableColumn.CellDataFeatures<Map.Entry<String,Map<String,Double>>, String> p) {
                // this callback returns property for just one cell, you can't use a loop here
                // for first column we use key
                return new SimpleObjectProperty<String>(p.getValue().getKey());
            }
        });

        TableColumn<Map.Entry<String,Map<String,Double>>, Void> colBtn = new TableColumn("Button Column");

        Callback<TableColumn<Map.Entry<String,Map<String,Double>>, Void>, TableCell<Map.Entry<String,Map<String,Double>>, Void>> cellFactory = new Callback<TableColumn<Map.Entry<String,Map<String,Double>>, Void>, TableCell<Map.Entry<String,Map<String,Double>>, Void>>() {
            @Override
            public TableCell<Map.Entry<String,Map<String,Double>>, Void> call(final TableColumn<Map.Entry<String,Map<String,Double>>, Void> param) {
                final TableCell<Map.Entry<String,Map<String,Double>>, Void> cell = new TableCell<Map.Entry<String,Map<String,Double>>, Void>() {

                    private final Button btn = new Button("show rating");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Map.Entry<String,Map<String,Double>> data = getTableView().getItems().get(getIndex());
                            selectDisplayRankQueries(data.getValue());
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };

        colBtn.setCellFactory(cellFactory);

        TableView<Map.Entry<String,Map<String,Double>>> dictionary = new TableView<>();
        ObservableList<Map.Entry<String, Map<String,Double>>> items = FXCollections.observableArrayList(rankingMap.entrySet());

        dictionary.setItems(items);
        dictionary.getColumns().setAll(column1,colBtn);

        Scene scene = new Scene(dictionary);
        stage.setScene(scene);
        stage.show();
    }

    private void selectDisplayRankQueries(Map<String,Double> rankingMap){
        Stage stage = new Stage();
        stage.setTitle("Q Rank");


        TableColumn<Map.Entry<String, Double>, String> column1 = new TableColumn<>("Doc Number");
        column1.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<String, Double>, String>, ObservableValue<String>>() {

            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<String, Double>, String> p) {
                // this callback returns property for just one cell, you can't use a loop here
                // for first column we use key
                return new SimpleObjectProperty<String>(p.getValue().getKey());
            }
        });

        TableColumn<Map.Entry<String, Double>,Double> column2 = new TableColumn<>("Rank");
        column2.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<String, Double>, Double>, ObservableValue<Double>>() {

            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<Map.Entry<String, Double>, Double> p) {
                // for second column we use value
                return new SimpleObjectProperty<Double>(p.getValue().getValue());
            }
        });

        TableColumn<Map.Entry<String, Double>, Void> colBtn = new TableColumn("Button Column");
        Callback<TableColumn<Map.Entry<String, Double>, Void>, TableCell<Map.Entry<String, Double>, Void>> cellFactory = new Callback<TableColumn<Map.Entry<String, Double>, Void>, TableCell<Map.Entry<String, Double>, Void>>() {
            @Override
            public TableCell<Map.Entry<String, Double>, Void> call(final TableColumn<Map.Entry<String, Double>, Void> param) {
                final TableCell<Map.Entry<String, Double>, Void> cell = new TableCell<Map.Entry<String, Double>, Void>() {

                    private final Button btn = new Button("Entities");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Map.Entry<String, Double> data = getTableView().getItems().get(getIndex());
                            showEntities(myViewModel.getEntitiesMap(data.getKey()));
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };
        colBtn.setCellFactory(cellFactory);

        ObservableList<Map.Entry<String, Double>> items = FXCollections.observableArrayList(rankingMap.entrySet());

        TableView<Map.Entry<String,Double>> dictionary = new TableView<>();

        dictionary.setItems(items);
        dictionary.getColumns().setAll(column1,column2,colBtn);
        dictionary.getSortOrder().add(column2);

        Scene scene = new Scene(dictionary);
        stage.setScene(scene);
        stage.show();
    }

    private void showEntities(Map<String,Integer> specificRankingMap){
        Stage stage = new Stage();
        stage.setTitle("Entities");


        TableColumn<Map.Entry<String, Integer>, String> column1 = new TableColumn<>("Entity");
        column1.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<String, Integer>, String>, ObservableValue<String>>() {

            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<String, Integer>, String> p) {
                // this callback returns property for just one cell, you can't use a loop here
                // for first column we use key
                return new SimpleObjectProperty<String>(p.getValue().getKey());
            }
        });

        TableColumn<Map.Entry<String, Integer>,Integer> column2 = new TableColumn<>("Rank");
        column2.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<String, Integer>, Integer>, ObservableValue<Integer>>() {

            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Map.Entry<String, Integer>, Integer> p) {
                // for second column we use value
                return new SimpleObjectProperty<Integer>(p.getValue().getValue());
            }
        });

        ObservableList<Map.Entry<String, Integer>> items = FXCollections.observableArrayList(specificRankingMap.entrySet());

        TableView<Map.Entry<String,Integer>> dictionary = new TableView<>();

        dictionary.setItems(items);
        dictionary.getColumns().setAll(column1,column2);

        Scene scene = new Scene(dictionary);
        stage.setScene(scene);
        stage.show();
    }

}
