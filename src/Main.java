import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        String path = "quickSort.txt";
        Parser p = new Parser(path);
        Graph graph = new Graph(p.lines);
        System.out.println();
        System.out.println("The graph:");
        graph.buildGraph();
        System.out.println();
        System.out.println("The Test Suite:");
        TestSuite ts = new TestSuite(graph);
        ts.generateTS();
    }

}