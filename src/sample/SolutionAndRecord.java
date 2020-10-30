package sample;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class SolutionAndRecord {

    Solution solution;
    List<Record> records = new ArrayList<>();
    public SolutionAndRecord(Solution solution) {
        this.solution = solution;
    }
    public void appendRecords(Record record){
        records.add(record);
    }
    public void appendRecords(List<Record> records){
        this.records.addAll(records);
    }

    public void setSolution(Solution solution) {
        this.solution = solution;
    }

    public Solution getSolution() {
        return solution;
    }

    public List<Record> getRecords() {
        return records;
    }

    public List<String> printRecords(LocalTime lT){
        List<String> sRecords = new ArrayList<>();
        for (Record record: records) {
            sRecords.add(record.print(lT));
        }
        return sRecords;
    }
}
