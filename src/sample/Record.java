package sample;

import java.time.Duration;
import java.time.LocalTime;

public class Record {
    String id;
    boolean passed;
    LocalTime timeOfSplit, timeOfReconnection;
    public Record(String id) {
        this.id = id;
        passed = false;
    }
    public void closingThread(){
        timeOfReconnection = LocalTime.now();
    }
    public String getId() {
        return id;
    }

    public boolean getPassed(){
        return passed;
    }

    public LocalTime getTimeOfSplit() {
        return timeOfSplit;
    }

    public void foundNewSolution(){
        passed = true;
        timeOfSplit = LocalTime.now();
    }

    public String print(LocalTime startTime){
        String sRecord = "";
        long elapsedTime;
        if(passed)
            elapsedTime = Duration.between(startTime,timeOfSplit).toMillis();
        else
            elapsedTime = Duration.between(startTime,timeOfReconnection).toMillis();
        sRecord += elapsedTime + ","+id+","+passed;
        return sRecord;
    }
}
