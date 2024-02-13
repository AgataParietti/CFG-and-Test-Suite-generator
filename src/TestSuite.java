import java.util.ArrayList;

public class TestSuite {
    public Graph graph;
    public ArrayList<String> ts = new ArrayList<>();
    public TestSuite(Graph graph) {
        this.graph = graph;
    }
    public void generateTS() {
        //some useful variables
        ArrayList<String> c = new ArrayList<>();
        ArrayList<String> v = new ArrayList<>();
        ArrayList<String> indipPath = graph.indip_path;
        for (int r = 0; r < indipPath.size(); r++) {
            String p = indipPath.get(r);
            StringBuilder test = new StringBuilder(r + ". ");
            //split to obtain node names
            String[] nodeNames = p.split("-->");

            for (int i = 0; i < nodeNames.length; i++) {
                //if
                ArrayList<String> ifCase = ifStatement(nodeNames[i]);
                if (!ifCase.isEmpty()) {
                    //Check if it's else if
                    ArrayList<String> elseIf = elseIf(nodeNames[i + 1]);
                    if (!elseIf.isEmpty()) {
                        test.append(elseIf.get(1)).append(" ").append(elseIf.get(0)).append(" ").append(elseIf.get(2)).append("; ");
                        c.add(notCondition(elseIf.get(0)));
                        v.add(elseIf.get(1));
                        v.add(elseIf.get(2));
                        i += 1;
                        int k = 0;
                        for (int j = 0; j < c.size() - 1; j++) {
                            test.append(v.get(k)).append(" ").append(c.get(j)).append(" ").append(v.get(k + 1)).append("; ");
                            k += 2;
                        }
                    } else if ((nodeNames[i + 1].charAt(0) == (nodeNames[i].charAt(0)) + 1)) {
                        test = new StringBuilder(getTestString(c, v, test.toString(), ifCase));

                    }
                }
                //check for else
                ArrayList<String> elIf = elseIf(nodeNames[i]);
                if(checkElse(nodeNames[i]) && elIf.isEmpty()) {
                    int k = 0;
                    for (String s : c) {
                        if (!test.toString().contains(v.get(k) + " " + notCondition(s) + " " + v.get(k + 1))) {
                            test.append(v.get(k)).append(" ").append(s).append(" ").append(v.get(k + 1)).append("; ");
                        }
                        k += 2;
                    }
                }
                //for or while loop
                ArrayList<String> loop = loop(nodeNames[i]);
                if (!loop.isEmpty() && !alreadyTested(nodeNames[i], nodeNames, i)) {
                    if ((nodeNames[i + 1].charAt(0) == (nodeNames[i].charAt(0)) + 1)) {
                        test = new StringBuilder(getTestString(c, v, test.toString(), loop));
                    } else {
                        String cond = notCondition(loop.get(0));
                        test.append(loop.get(1)).append(" ").append(cond).append(" ").append(loop.get(2)).append("; ");
                    }
                }
            }
            ts.add(test.toString());
        }

        for(String t:ts) {
            System.out.println(t);
        }
    }

    private String getTestString(ArrayList<String> c, ArrayList<String> v, String test, ArrayList<String> loop) {
        StringBuilder testBuilder = new StringBuilder(test);
        for (int j = 0; j < loop.size(); j += 3) {
            testBuilder.append(loop.get(j + 1)).append(" ").append(loop.get(j)).append(" ").append(loop.get(j + 2)).append("; ");
            c.add(notCondition(loop.get(j)));
            v.add(loop.get(j + 1));
            v.add(loop.get(j + 2));
        }
        test = testBuilder.toString();
        return test;
    }

    private ArrayList<String> ifStatement(String nodeName) {
        ArrayList<String> r = new ArrayList<>();
        for(Node n:graph.nodes) {
            if (String.valueOf(n.nodeName).equals(nodeName)) {
                for(int l: n.lines) {
                    if(graph.lines.get(l).contains("if")) {
                        checkAndOrCond(r, l);
                    }
                }
            }
        }
        return r;
    }

    private void checkAndOrCond(ArrayList<String> r, int l) {
        if (graph.lines.get(l).contains("&&") || graph.lines.get(l).contains("||")){
            String[] splitCond;
            if (graph.lines.get(l).contains("&&"))
                splitCond = graph.lines.get(l).split("&&");
            else
                splitCond = graph.lines.get(l).split("\\|\\|");
            for (String s : splitCond) {
                r.add(findCondition(s));
                String[] variables = findVariables(s);
                r.add(variables[0]);
                r.add(variables[1]);
            }
        } else {
            r.add(findCondition(graph.lines.get(l)));
            String[] variables = findVariables(graph.lines.get(l));
            r.add(variables[0]);
            r.add(variables[1]);
        }
    }

    private boolean checkElse(String nodeName) {
        for(Node n:graph.nodes) {
            if (String.valueOf(n.nodeName).equals(nodeName)) {
                for(int l: n.lines) {
                    if(graph.lines.get(l).contains("else")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private ArrayList<String> elseIf(String nodeName){
        ArrayList<String> r = new ArrayList<>();
        for(Node n:graph.nodes) {
            if (String.valueOf(n.nodeName).equals(nodeName)) {
                for(int l: n.lines) {
                    if(graph.lines.get(l).contains("else if")) {
                        r.add(findCondition(graph.lines.get(l)));
                        String[] variables = findVariables(graph.lines.get(l));
                        r.add(variables[0]);
                        r.add(variables[1]);
                    }
                }
            }
        }
        return r;
    }

    private ArrayList<String> loop(String nodeName) {
        ArrayList<String> r = new ArrayList<>();
        for(Node n:graph.nodes) {
            if (String.valueOf(n.nodeName).equals(nodeName)) {
                for(int l: n.lines) {
                    if(graph.lines.get(l).contains("for") || graph.lines.get(l).contains("while")) {
                        checkAndOrCond(r, l);
                    }
                }
            }
        }
        return r;
    }

    private boolean alreadyTested(String nodeName, String [] nodeNames, int i) {
        for (int j = 0; j < i; j++) {
            if (nodeNames[j].equals(nodeName))
                return true;
        }
        return false;
    }

    private String findCondition(String line) {
        if(line.contains("<"))
            return "<";
        if(line.contains("<="))
            return "<=";
        if(line.contains("=="))
            return "==";
        if(line.contains(">="))
            return ">=";
        if(line.contains(">"))
            return ">";
        if (line.contains("!="))
            return "!=";
        return "";
    }

    private String notCondition(String c) {
        if(c.equals("<"))
            return ">=";
        if(c.equals("<="))
            return ">";
        if(c.equals("=="))
            return "!=";
        if(c.equals(">="))
            return "<";
        if(c.equals(">"))
            return "<=";
        if(c.equals("!="))
            return ("==");
        return "";
    }

    private String[] findVariables(String line) {
        String[] l;
        String[] v = new String[2];
        if (line.contains("for")) {
            l = line.split(";");
            v = splitByCondition(l);
        } else {
            l = line.split("\\(");
            ArrayList<String[]> r = new ArrayList<>();
            for (String s : l) {
                r.add(s.split("\\)"));
            }
            for (String[] strings : r) {
                v = splitByCondition(strings);
            }
        }
        return v;
    }

    private String[] splitByCondition(String[] l) {
        String[] v = new String[2];
        for (String s : l) {
            if (s.contains("<"))
                v = s.split("<");
            else if (s.contains("<="))
                v = s.split("<=");
            else if (s.contains(">"))
                v = s.split(">");
            else if (s.contains(">="))
                v = s.split(">=");
            else if (s.contains("=="))
                v = s.split("==");
            else if (s.contains("!="))
                v = s.split("!=");
        }
        return v;
    }
}
