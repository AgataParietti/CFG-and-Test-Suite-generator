import java.util.ArrayList;
import java.util.HashMap;

public class Graph {
    public ArrayList<String> indip_path = new ArrayList<>();
    public ArrayList<String>lines;
    public ArrayList<Node> nodes = new ArrayList<>();
    public static char nodeName = 'A';
    public static int lineNo = 0;

    public Graph(ArrayList<String>lines) {
        this.lines = lines;
    }

    private void visitTree(Node root, ArrayList<Node>visited, HashMap<Character,
            ArrayList<Character>> table) {
        visited.add(root);
        ArrayList<Node>childs = root.childs;
        Node current;

        table.put(root.nodeName, printChild(root));
        for(int i=0;i<childs.size();i++){
            current = childs.get(i);
            if(!visited.contains(current)) visitTree(current,visited,table);
        }

    }

    public ArrayList<Character> printChild(Node node){
        ArrayList<Character> nodes = new ArrayList<>();
        System.out.print(node.nodeName+"-->(");
        for(int i=0;i<node.childs.size();i++){
            System.out.print(node.childs.get(i).nodeName);
            if(node.childs.size() - i >1)System.out.print(",");
            nodes.add(node.childs.get(i).nodeName);
        }
        System.out.println(")");
        System.out.print(node.nodeName);
        System.out.println(node.lines);
        return nodes;
    }

    public void buildGraph(){
        Node root = new Node(Graph.nodeName,0);
        Graph.nodeName++;
        buildChild(root, true);

        ArrayList<Node>visited = new ArrayList<>();
        HashMap<Character, ArrayList<Character>> table = new HashMap<>();
        visitTree(root,visited,table);
        //int cy = 0;
        System.out.println();
        System.out.println("Independent paths:");
        findPaths(root,"",0);

        for (String node : indip_path) {
            System.out.println(node);
        }
    }

    private int findPaths(Node root, String string, int cyclomaticComplexity) {
        boolean isLoop = false;

        if(string.contains(String.valueOf(root.nodeName)))
            isLoop = true;

        string = string.concat(String.valueOf(root.nodeName));

        if(root.childs.isEmpty()){
            indip_path.add(string);
            cyclomaticComplexity++;
        }
        else string = string.concat("-->");
        ArrayList<Node> childs = root.childs;
        Node current;

        for(int i=0;i<childs.size();i++){
            current = childs.get(i);
            if(!string.contains(String.valueOf( current.nodeName)) || !isLoop)
                cyclomaticComplexity = Math.max(cyclomaticComplexity, findPaths(current,string,cyclomaticComplexity));
        }
        return cyclomaticComplexity;
    }


    private Node buildChild(Node parent, boolean isMultipleLine) {
        Node statement_node = null ;
        ArrayList<Node> lastNodesInBranch = new ArrayList<>();
        lineNo++;

        while(true){
            if(lineNo>=lines.size())return null;

            if(this.lines.get(lineNo).contains("else if")){
                Node newNode = new Node(nodeName,lineNo);
                nodes.add(newNode);
                nodeName+=1;
                parent.childs.add(newNode);
                newNode = buildChild(newNode, isBlock(newNode));
                if(newNode == null) return null;
                lastNodesInBranch.add(newNode);
            }

            else if(this.lines.get(lineNo).contains("if")){
                Node newNode = new Node(nodeName, lineNo);
                nodes.add(newNode);
                nodeName+=1;

                if(!lastNodesInBranch.isEmpty()){
                    addParents(newNode, lastNodesInBranch);
                    lastNodesInBranch.clear();
                }
                else{
                    parent.childs.add(newNode);
                }
                parent = newNode;
                newNode = buildChild(newNode, isBlock(newNode));
                if(newNode == null) return null;
                lastNodesInBranch.add(newNode);
                statement_node = null;

            }

            else if(this.lines.get(lineNo).contains("else")){
                Node newNode = new Node(nodeName,lineNo);
                nodes.add(newNode);
                nodeName+=1;
                parent.childs.add(newNode);
                newNode = buildChild(newNode, isBlock(newNode));
                if(newNode == null) return null;
                lastNodesInBranch.add(newNode);
            }

            else if(this.lines.get(lineNo).contains("for") ||
                    ( this.lines.get(lineNo).contains("while") && !this.lines.get(lineNo).contains("}while") &&
                            !this.lines.get(lineNo).contains("} while"))){
                Node newNode = new Node(nodeName,lineNo);
                nodes.add(newNode);
                nodeName+=1;
                if(!lastNodesInBranch.isEmpty()){
                    addParents(newNode, lastNodesInBranch);
                    lastNodesInBranch.clear();
                }
                else{
                    parent.childs.add(newNode);
                }
                parent = newNode;
                lastNodesInBranch.add(parent);
                newNode = buildChild(newNode, isBlock(newNode));
                if(newNode == null) return null;
                newNode.childs.add(parent);
                /*lastNodesInBranch.add(newNode);*/
                parent = newNode;
                statement_node = null;

            }

            else if(this.lines.get(lineNo).contains("do")){
                Node newNode = new Node(nodeName,lineNo);
                nodes.add(newNode);
                nodeName+=1;
                if(!lastNodesInBranch.isEmpty()){
                    addParents(newNode, lastNodesInBranch);
                    lastNodesInBranch.clear();
                }
                else{
                    parent.childs.add(newNode);

                }
                parent = newNode;
                newNode = buildChild(newNode, isBlock(newNode));
                if(newNode == null) return null;
                newNode.childs.add(parent);
                parent = newNode;
                statement_node = null;

            }
            else{
                if(lines.get(lineNo).trim().isEmpty()){
                    lineNo++; continue;
                }
                if(statement_node == null){
                    statement_node = new Node(Graph.nodeName, lineNo);
                    nodes.add(statement_node);
                    Graph.nodeName +=1;
                    if(!lastNodesInBranch.isEmpty()){
                        addParents(statement_node, lastNodesInBranch);
                        lastNodesInBranch.clear();
                    }
                    else parent.childs.add(statement_node);
                    parent = statement_node;

                }
                else{
                    statement_node.lines.add(lineNo);
                }
                if(lines.get(lineNo).contains("}") || ! isMultipleLine) return statement_node;
                if(lines.get(lineNo).contains("}while") || lines.get(lineNo).contains("} while")) {
                    addParents(statement_node, lastNodesInBranch);
                }
            }
            lineNo++;
        }
    }

    private void addParents(Node newNode , ArrayList<Node> lastNodesInBranch) {
        for (Node nodesInBranch : lastNodesInBranch) {
            nodesInBranch.childs.add(newNode);
        }

    }

    private boolean isBlock(Node newNde){
        if(this.lines.get(lineNo).endsWith("{")) return true;
        lineNo++;
        while(true){
            if(this.lines.get(lineNo).endsWith("{")) return true;
            else if(this.lines.get(lineNo).endsWith(";")){
                lineNo--;
                return false;
            }
            else newNde.lines.add(lineNo);
            lineNo++;

        }
    }


}
