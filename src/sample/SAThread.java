package sample;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SAThread extends Thread {
    // Global vars:
    int rateOfDecay,numOfCycles = 100, numColours,maxChanges,numNodesToChange;

    Record record;
    SolutionAndRecord solutionAndRecord;
    String id;
    Solution currentSolution;
    Random rng = new Random();
    private List<Future<SolutionAndRecord>> listOfExececutions= new ArrayList<>();

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    // Contructor
    public SAThread(Solution solution,String id, int numColours_, List<Integer> nodesToRecolour, int newNodeRecolour)
    {
        this.id = id;
        currentSolution = new Solution(solution);
        record = new Record(id);
        solutionAndRecord = new SolutionAndRecord(currentSolution);
        numColours = numColours_;
        for (Integer node: nodesToRecolour) {
            currentSolution.setNodeColour(node,newNodeRecolour);
        }
        numNodesToChange = (int)Math.ceil(currentSolution.size()/10);
        setDecay(numColours);
        // Get copy of graph.

    }

    private void setDecay(int numColours){
        maxChanges = (int)currentSolution.size(); //The more changes it makes, the more difficulty it has finding the optimum
        double max = Math.log(currentSolution.size());
        double cur = Math.log(numColours);
        double coolingFactor = max/cur;
        rateOfDecay = maxChanges / (int)Math.round(coolingFactor);
    }

    @Override
    public void run()
    {

    }

    //@Override
    public Future<SolutionAndRecord> findSmaller() throws Exception {
        return executor.submit(() -> {
            try {
                for(int i = 0; i <numOfCycles;i++) {
                    for (int c = 0; c < rateOfDecay; c++) {
                        currentSolution.changeNode(numColours);
                    }


                    if (currentSolution.validSolution()) {
                        if (currentSolution.getNumColours() < numColours) {
                            record.foundNewSolution();

                            if (currentSolution.getNumColours() == 2)
                                break;
                            int numChildren = currentSolution.getNumColours();
                            System.out.println(id + " found better solution of " + currentSolution.getNumColours() + " from " + numColours + " at cycle " + i + " spawned " + numChildren);
                            currentSolution.cleanUp();
                            numColours = currentSolution.getNumColours();
                            SAThread child;
                            for (int j = 0; j < numChildren; j++) {
                                List<Integer> nodesToChange = chooseNodes(numNodesToChange);
                                int newColour = rng.nextInt((int) currentSolution.size());

                                //ExecutorService executor = Executors.newCachedThreadPool();
                                child = new SAThread(currentSolution, (id + j + "."), numColours, nodesToChange, newColour);
                                Future<SolutionAndRecord> futureCall = child.findSmaller();
                                listOfExececutions.add(futureCall);
                            }

                            int resultNumColours;
                            for (Future<SolutionAndRecord> futureCall : listOfExececutions) {
                                SolutionAndRecord result = futureCall.get(); // Here the thread will be blocked
                                solutionAndRecord.appendRecords(result.getRecords());
                                Solution childSolution = result.getSolution();
                                resultNumColours = childSolution.getNumColours();
                                if (resultNumColours < numColours && childSolution.validSolution()) {
                                    System.out.println("Replacement");
                                    currentSolution = childSolution;
                                    numColours = resultNumColours;
                                    solutionAndRecord.setSolution(currentSolution);
                                }
                            }
                            break;
                        }
                    }
                }
                // Save solution, create children threads with reduced colours.
            }
            catch(Exception ex) {
                ex.printStackTrace();
            }
            record.closingThread();
            solutionAndRecord.appendRecords(record);
            return solutionAndRecord;
        }
        );

      }
      private List<Integer> chooseNodes(int numNodes){
        List<Integer> nodes = new ArrayList<>();
        int node;
        for(int i =0;i<numNodesToChange;i++){
            do {
                node = rng.nextInt((int) currentSolution.size());
            }while(nodes.contains(node));
            nodes.add(node);
        }
        return nodes;
      }
}
