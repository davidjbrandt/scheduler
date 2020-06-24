package reports;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class ReportColumn {
    
    private final String TITLE;
    private final int BUFFER_SPACES = 2;
    private final String START_BUFFER = " ";
    
    private ArrayList<String> results = new ArrayList<>();
    private Iterator<String> iterator;
    private int width;
    
    public ReportColumn(String title) {
        TITLE = title;
        width = title.length() + BUFFER_SPACES;
    }
    
    public void addResult(String result) {
        results.add(result);
        width = Math.max(width, result.length() + BUFFER_SPACES);
    }
    
    public boolean hasNext() {
        return iterator.hasNext();
    }
    
    public String getNext() {
        return wrapSpaces(iterator.next());
    }
    
    public String getTitle() {
        iterator = results.iterator();
        return wrapSpaces(TITLE);
    }
    
    public String getLine() {
        return repeat("-", width) + "|";
    }
    
    private String wrapSpaces(String content) {
        int endSpaces = width - content.length() - START_BUFFER.length();
        String endBuffer = repeat(" ", endSpaces);
        return START_BUFFER + content + endBuffer + "|";
    }
    
    private String repeat(String string, int n) {
        return String.join("", Collections.nCopies(n, string));
    }
}
