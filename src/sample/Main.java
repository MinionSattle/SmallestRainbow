package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import jdk.nashorn.internal.runtime.ConsString;

import java.util.*;
import java.util.List;
import java.awt.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main extends Application {
    Random rng = new Random();

    private int RNGMIN = 10, RNGMAX = 50;
    @Override
    public void start(Stage primaryStage) throws Exception{
        int seed = 51784861;
        System.out.println(seed);
        rng.setSeed(seed);
        Solution firstSolution = initialization();
        System.out.println("RUN");
        List<Integer> emptyList = new ArrayList<>();
        SAThread child =  new SAThread(firstSolution,"0.",(int)firstSolution.size(), emptyList ,0);
        Future<Solution> futureCall = child.findSmaller();
        Solution result = futureCall.get();
        if(result.validSolution()){
            result.printSolution();
            System.out.println(result.getNumColours());
        }
        else{
            System.out.println("Init Solution best solution");
        }
        //minimumSolution =
        //drawGraph(minimumSolution, primaryStage);
        System.out.println("Fin");
    }

    private Solution initialization(){
        Parameters parameters = getParameters();
        List<String> list = parameters.getRaw();
        int nodes = 0;
        int edges = 0;

        if(list.get(0).equals("Random")){
            nodes = rng.nextInt(RNGMAX) + RNGMIN;
            int edgeMax = ((nodes*nodes)-nodes)/2;
            if(edgeMax > 0)
                edges = rng.nextInt(edgeMax);
            else
                edges = 0;

        }else if(list.size() == 2){
            nodes = Integer.parseInt(list.get(0));
            edges = Integer.parseInt(list.get(1))-nodes;
        }
        Solution initSolution;
        if(list.get(0).contains(".csv")){
            initSolution = new Solution(list.get(0));
        }else {
            initSolution = new Solution(nodes,edges);
        }
        initSolution.printSolution();
        System.out.println("\n"+initSolution.getNumColours());
        return initSolution;
    }

    private void drawGraph(Solution solution, Stage primaryStage){
        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        Pane pane = new Pane();
        Canvas canvas = new Canvas(1000, 1000);
        pane.getChildren().add(canvas);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        primaryStage.setTitle("Graphing Colour");
        primaryStage.setScene(new Scene(pane));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
