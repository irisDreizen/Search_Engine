package view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
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
    private boolean toStem=false;
    HashMap<String, termData> dictionary;

    @FXML
    public javafx.scene.control.TextField txtfld_pathToRead;
    public javafx.scene.control.TextField txtfld_pathToWrite;
    public javafx.scene.control.TextField txtfld_singleQuery;
    public javafx.scene.control.CheckBox check_stem;
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

    public void callSearchOneQuery() throws IOException {
        this.myViewModel.callSearchOneQuery("IC", txtfld_singleQuery.getText());
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
        this.pathToRead = txtfld_pathToRead.getText();
        myViewModel.setPahToRead(pathToRead);
        this.pathToWrite = txtfld_pathToWrite.getText();
        myViewModel.setPathToWrite(pathToWrite);
        myViewModel.setToStem(toStem);
        myViewModel.loadDictionary();
    }
}
