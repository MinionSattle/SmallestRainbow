package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import jdk.nashorn.internal.runtime.ConsString;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalTime;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
//Seattle Tupuhi 1286197
//Jesse Whitten 1311972

public class Main extends Application {
    Random rng = new Random();

    private int RNGMIN = 10, RNGMAX = 50;
    Solution bestSolution;
    @Override
    public void start(Stage primaryStage) throws Exception{
        //rng.setSeed(seed);
        for(int c = 0; c < 30;c++) {
            Solution currentSolution = initialization();                //Get First Worst Solution
            bestSolution = new Solution(currentSolution);      //Store Current Solution
            System.out.println("RUN");
            //List<Integer> emptyList = new ArrayList<>();
            //LocalTime start = LocalTime.now();
            //SAThread child =  new SAThread(firstSolution, emptyList ,0); //Create Master Thread

            //Future<Solution> futureCall = child.findSmaller();                                                              //Start Thread
            //SolutionAndRecord result = futureCall.get();

            File file = new File("results.csv");    //This is where the time output will be stored
            if (!file.exists())
                file.createNewFile();

            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
                writer.write(c + " 50\n");             //C stands for which run, the number is the number of child threads
                writer.flush();
                long startTime = System.nanoTime();         //Stores the start time
                writer.write(0 + "," + currentSolution.getNumColours() + "\n"); //Stores the total number of colours at initialisation
                writer.flush();
                for (int i = 0; i < 100; i++) {
                    currentSolution = getNextBest(new Solution(bestSolution));   //Attempt to find a better solution
                    if (currentSolution.validSolution() && currentSolution.getNumColours() < bestSolution.getNumColours()) {    //If New solution is better and if it is valid
                        bestSolution = currentSolution; //Replace
                        writer.write((System.nanoTime() - startTime)/1000000 + "," + currentSolution.getNumColours() + "\n");   //Record time
                        writer.flush();
                        if (bestSolution.getNumColours() <= 3)
                            break;
                        bestSolution.cleanUp(); //Reduces the colour values down
                    }

                }
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            bestSolution.printSolution();
            System.out.println(bestSolution.getNumColours());
            System.gc();
        }
            drawGraph(bestSolution, primaryStage);  //Draw Solution
            System.out.println("Fin");
        this.stop();

    }

    private Solution getNextBest(Solution solution){
        Solution currentSolution = solution;
        SAThread child;
        List<Future<Solution>> listOfExececutions= new ArrayList<>();
        int numChildren = getNumChildren();
        try{
            //For each child, chose a random set of nodes and recolour them, then add child thread to list
            for (int i = 0;i < numChildren;i++){
                List<Integer> nodesToChange = chooseNodes(currentSolution.size());
                int newColour = rng.nextInt((int) currentSolution.size());
                child = new SAThread(currentSolution,currentSolution.getNumColours(), nodesToChange, newColour);
                Future<Solution> futureCall = child.findSmaller();
                listOfExececutions.add(futureCall);
            }

            while(!futuresComplete(listOfExececutions)){                                                    //While the threads have not finished
                for (int f = 0; f < listOfExececutions.size();f++) {                                        //For each thread
                    if(listOfExececutions.get(f).isDone() && !listOfExececutions.get(f).isCancelled()){     //If thread has finished but wasn't canceled
                        Solution result = listOfExececutions.get(f).get();                                  // Get the result
                        int resultNumColours = result.getNumColours();
                        if (resultNumColours < currentSolution.getNumColours() && result.validSolution()) { //If the result is better than the previous and is valid
                            System.out.println("Replacement");
                            currentSolution = result;                                                       //Keep the solution
                            if(currentSolution.getNumColours() <= 3){                                       //If the solution is less than or equal to three
                                System.out.println("Min has been found");
                                cancelAllThreads(listOfExececutions);                                       //Stop all other searches
                                break;
                            }
                        }
                    }
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return currentSolution;
    }
    //Check to see if all the solutions are completed
    private boolean futuresComplete(List<Future<Solution>> threads){
        boolean complete = true;
          for (Future<Solution> thread:threads) {
              if(!thread.isDone()){
                  complete = false;
                  break;
              }
          }
          return complete;
      }
      //Cancel all threads since we have found the minimum
      private void cancelAllThreads(List<Future<Solution>> threads){
        for (Future<Solution> thread:threads) {
            if(!thread.isDone())
                thread.cancel(true);
          }
      }
      //Choose a list of nodes to be changed
    private List<Integer> chooseNodes(int numNodes){
        List<Integer> nodes = new ArrayList<>();
        int node;
        for(int i =0;i<Math.ceil(numNodes/10);i++){
            do {
                node = rng.nextInt(numNodes);
            }while(nodes.contains(node));
            nodes.add(node);
        }
        return nodes;
    }
    private int getNumChildren(){
        int children = 50;
        //Determine how many children
        return children;
    }
    private Solution initialization(){
        Parameters parameters = getParameters();
        List<String> list = parameters.getRaw();
        int nodes = 0;
        int edges = 0;

        if(list.get(0).equals("Random")){
            nodes = rng.nextInt(RNGMAX) + RNGMIN;
            System.out.println(nodes);
            int edgeMax = ((nodes*nodes)-nodes)/4;
            System.out.println(edgeMax);
            if(edgeMax > 0)
                edges = rng.nextInt(edgeMax);
            else
                edges = 0;
            System.out.println(edges);
        }else if(list.size() == 2 || list.size() == 3){
            nodes = Integer.parseInt(list.get(0));
            edges = Integer.parseInt(list.get(1))-nodes;
        } else if(!list.get(0).contains(".csv"))
            System.err.println("Arguments incorrect");
        Solution initSolution;
        if(list.get(0).contains(".csv")){
            initSolution = new Solution(list.get(0));
        }else {
            initSolution = new Solution(nodes,edges);
        }
        if(list.size() == 3)
            initSolution.setSeed(list.get(2));
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

        int numColours = solution.getNumColours();
        int numNodes = (int) solution.size();

        double theta = Math.PI*2/numNodes;
        double thetaTemp = theta;
        double r = 400;
        double midX = canvas.getWidth()/2;
        double midY = canvas.getHeight()/2;
        double nodeSize = r*Math.sqrt(2-2*Math.cos(theta))/2;  // Find an appropriate size of the nodes.

        // Draw and find the nodes.
        for(Node node : solution.getNodes()) {
             // Known variable.  Final length of vector.
            double x; // Unknown variable. x location.
            double y; // Unknown variable. y Location.
            try {
                // Conversion from polar to Cartesian coordinates.
                y = r*Math.cos(theta);
                x = r*Math.sin(theta);

                // Slide over, so it orbits the center node.
                x += midX;
                y += midY;

                node.setxLoc(x);
                node.setyLoc(y);
                //gc.setFill(new Color(node.getColor()));
                gc.setFill(getColour(node.getColor()));
                gc.fillOval(node.getxLoc(), node.getyLoc(), nodeSize, nodeSize);

                //System.out.println(node.getxLoc() + ", " + node.getyLoc());
                //System.out.println(theta + "");
                theta += thetaTemp;

            }catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // Draw and find the edges.
        for(int i = 0; i < solution.getNodes().size(); i++)
        {
            Node node = solution.getNodes().get(i);
            for (Edge edge : node.getEdges())
            {
                int targetNodeId = Math.max(edge.getNodesID()[0], edge.getNodesID()[1]);
                if(targetNodeId <= i) {
                    continue; // We must have already done this edge previously.
                }
                else {

                    Node targetNode = null;
                    for(Node nodeT : solution.getNodes()) {
                        if (nodeT.getId() == targetNodeId) {
                            targetNode = nodeT;
                            break;
                        }
                    }
                    gc.setStroke(Color.BLACK);
                    gc.setLineWidth(2);
                    gc.strokeLine(node.getxLoc() + nodeSize/2, node.getyLoc() + nodeSize/2, targetNode.getxLoc() + nodeSize/2, targetNode.getyLoc() + nodeSize/2);
                }
            }
        }

        primaryStage.setTitle("Graphing Colour");
        primaryStage.setScene(new Scene(pane));
        primaryStage.show();
    }



    public static void main(String[] args) {
        launch(args);
    }

    private Color getColour(int nodeColour){
        switch(nodeColour){
            case(0):return Color.BLUE;
            case(1):return Color.ALICEBLUE;
            case(2):return Color.CORNSILK;
            case(3):return Color.AQUA;
            case(4):return Color.AQUAMARINE;
            case(5):return Color.AZURE;
            case(6):return Color.BISQUE;
            case(7):return Color.BLANCHEDALMOND;
            case(8):return Color.BLUEVIOLET;
            case(9):return Color.BROWN;
            case(10):return Color.BURLYWOOD;
            case(11):return Color.CADETBLUE;
            case(12):return Color.CHARTREUSE;
            case(13):return Color.CHOCOLATE;
            case(14):return Color.CORAL;
            default:return Color.BLACK;
        }
    }
}
