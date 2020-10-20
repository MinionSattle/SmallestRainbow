package sample;

public class Edge {

    private Node[] nodes;

    public Edge(Node n0, Node n1) {
        nodes = new Node[2];
        nodes[0] = n0;
        nodes[1] = n1;
    }

    public boolean hasLink(int n0, int n1){
        if((nodes[0].getId() == n0 || nodes[1].getId() == n0) && (nodes[0].getId() == n1 || nodes[1].getId() == n1)){
            return true;
        }
        return false;
    }

    public boolean areTheColoursTheSame(){
        return nodes[0].getColor() == nodes[1].getColor();
    }

    public int getNeighbourID(int id){
        if(id != nodes[0].getId())
            return nodes[0].getId();
        return nodes[1].getId();
    }

    public int getNeighbourColour(int c){
        if(c != nodes[0].getColor())
                return nodes[0].getColor();
        return nodes[1].getColor();
    }
    //#######################################################
    //#################     PRINT    ########################
    //#######################################################

    public int[] getNodesID() {
        int nodeIDS[] = new int[2];
        for (int i = 0;i < 2;i++)
            nodeIDS[i] = nodes[i].getId();
        return nodeIDS;
    }

    public void printEdge(){
        for (Node node: nodes) {
            node.printNode();
        }
        System.out.print("\n");
    }
}
