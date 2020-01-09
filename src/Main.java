import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.myModel;
import view.myController;
import viewModel.viewModel;

import java.io.IOException;
import java.util.Optional;


public class Main extends Application {

    public static void main(String[] args) throws IOException {


        launch(args);
//        ///////checking runing time
//        long startTime = System.nanoTime();
//        ///////checking runing time
//
//
//        model.Indexer index=new model.Indexer("C:\\Users\\iris dreizenshtok\\Desktop\\programming\\testCorpus", "C:\\Users\\iris dreizenshtok\\Desktop\\programming\\testCorpus");
//        index.index();
//
//
//        ///////checking runing time
//        long endTime   = System.nanoTime();
//        long totalTime = endTime - startTime;
//        System.out.println(totalTime);
//        ///////checking runing time




        /*
//        System.out.println("Hello World!");
//        System.out.println("Hello World!");
//        //URL path = ClassLoader.getSystemResource("test.htm");
//        String path1 = "C:\\Python27\\FB396001";
//        File input = new File(path1);
//        Document document = Jsoup.parse(input, "UTF-8");
//        Elements docNumber = document.getElementsByTag("DOCNO");
//        Elements text = document.getElementsByTag("TEXT");
//        Elements DATE = document.getElementsByTag("DATE1");
//        System.out.println(DATE.get(0).text());

        model.ReadFile r = new model.ReadFile();
        r.model.ReadFile("C:\\Users\\Chen\\Downloads\\corpus1\\");
        model.Parse p = new model.Parse("C:\\Users\\Chen\\Downloads\\stop_words.txt");
        p.buildDictionary(r);
        for (Map.Entry entry : p.getDictionary().entrySet())
        {
            System.out.println("key: " + entry.getKey());
        }

        */


    }


    @Override
    public void start(Stage primaryStage) throws Exception {

        myModel model = new myModel();
        viewModel viewModel =new viewModel(model);
        ;
        //--------------
        primaryStage.setTitle("My Application!");
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("MyView2.fxml").openStream());
        Scene scene = new Scene(root, 800, 600);
//        scene.getStylesheets().add(getClass().getResource("ViewStyle.css").toExternalForm());
        primaryStage.setScene(scene);
        //--------------
        myController view = fxmlLoader.getController();
       // view.btn_showDictionary.setDisable(true);
        //view.btn_loadDictionary.setDisable(true);
        view.btn_clearData.setDisable(true);
        view.setStage(primaryStage);
       // view.setResizeEvent(scene);
        view.setViewModel(viewModel);
        viewModel.addObserver(view);

        //--------------
        SetStageCloseEvent(primaryStage,view);
        init();
        primaryStage.show();
    }


    private void SetStageCloseEvent(Stage primaryStage, myController view) {
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent windowEvent) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Are you sure you want to exit?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    // ... user chose OK
                    System.exit(0);
                } else {
                    // ... user chose CANCEL or closed the dialog
                    windowEvent.consume();
                }
            }
        });


    }
}
