package sample;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Node {

    private int color;
    private List<Edge> edges;
    private int id;
    private double xLoc;
    private double yLoc;

    public Node(int id,int color) {
        this.color = color;
        this.id = id;
        edges = new ArrayList<>();
    }

    public void addEdge(Edge edge){
        edges.add(edge);
    }
    public int getColor() {
        return color;
    }

    public boolean allReadyConnected(int node){
        //System.out.println("Connection Detection");
        if(id == node){
            //System.out.println("Fail1 " + color + " " + node.color);
            return true;
        }
        if(!edges.isEmpty()){
            for (Edge edge: edges) {
                if(edge.hasLink(id,node)){
                    //System.out.println("Fail2 " + color + " " + node.color);
                    return true;
                }
            }
        }
        //System.out.println("Pass" + id + " " + node);
        return false;
    }
    public int getId(){
        return id;
    }

    public void tryNewColour(int colour){
        for (Edge edge: edges) {
            if(edge.getNeighbourColour(this.color) == colour)
                return;
        }
        color = colour;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int numEdges(){return edges.size();}

    public double getxLoc() {
        return xLoc;
    }

    public void setxLoc(double xLoc) {
        this.xLoc = xLoc;
    }

    public double getyLoc() {
        return yLoc;
    }

    public void setyLoc(double yLoc) {
        this.yLoc = yLoc;
    }

    public List<Edge> getEdges()
    {
        return this.edges;
    }


    //#######################################################
    //#################     PRINT    ########################
    //#######################################################

    public void printNode(){
        //Color printColor = new Color(color);
        System.out.print((char)27+"[38;5;" + color+"m"+id + ",");

        //System.out.println((char)27 + "[33mYELLOW");

    }
}