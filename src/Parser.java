import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Parser {
    public ArrayList<String> lines = new ArrayList<>();

    public Parser(String path) throws IOException {
        FileReader fileReader =
                new FileReader(path);
        // Always wrap FileReader in BufferedReader.
        BufferedReader bufferedReader =
                new BufferedReader(fileReader);
        String line;
        int i = 0;
        while ((line = bufferedReader.readLine()) != null) {
            lines.add(line);
            //System.out.println(i + ":" + line);
            i++;
        }
        // Always close files.
        bufferedReader.close();

        formatCode();
        for (String s:lines)
            System.out.println(s);
    }

    private void formatCode() {
        for(int i=0; i<lines.size(); i++) {
            if(lines.get(i).contains("if") && !lines.get(i).contains("else if")) {
                if(!checkElse(i)) {
                    if(lines.get(i).contains("{")) {
                        addElseWithParenthesis(i);
                    } else {
                        lines.add(i+2, "else");
                        lines.add(i+3, "printf();");
                    }
                }
            }
        }
    }

    private boolean checkElse(int i) {
        for (int j=i; j<lines.size(); j++) {
            if(lines.get(j).contains("else"))
                return true;
        }
        return false;
    }

    private void addElseWithParenthesis(int i) {
        for (int j=i; j<lines.size(); j++) {
            if(lines.get(j).contains("}") && !lines.get(j).contains("while")) {
                lines.add(j+1, "else");
                lines.add(j+2, "printf();");
                break;
            }
        }
    }

}