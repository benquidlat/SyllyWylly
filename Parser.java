import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.BufferedWriter;
import java.io.File; 
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser{
    public static void main(String[] args) {
        try{
            File file = new File("///E:/DOWNLOADS/F24_CS2336.pdf");
            PDDocument document = PDDocument.load(file);

            PDFTextStripper pdfStripper = new PDFTextStripper();

            String text = pdfStripper.getText(document);
            document.close();
            System.out.println(text);

            List<syllEvent> events = parser(text);
            
            writeToCSV(events);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<syllEvent> parser(String text) {
        List<syllEvent> events = new ArrayList<>();
    
        // Pattern for MM/DD format
        Pattern mmddPattern = Pattern.compile("(\\d{1,2})/(\\d{1,2})\\s+(.+?)(?=\\s+\\d{1,2}/\\d{1,2}|$)");
        Matcher mmddMatcher = mmddPattern.matcher(text);
    
        // Pattern for Month Name format (e.g., Oct 23)
        Pattern monthNamePattern = Pattern.compile("(\\w{3})\\s+(\\d{1,2})\\s+(.+?)(?=\\s+\\w{3}\\s+\\d{1,2}|$)");
        Matcher monthNameMatcher = monthNamePattern.matcher(text);
    
        // Match MM/DD format
        while (mmddMatcher.find()) {
            String month = mmddMatcher.group(1).trim();
            String day = mmddMatcher.group(2).trim();
            String event = mmddMatcher.group(3).trim();
    
            if (Integer.parseInt(month) > 0 && Integer.parseInt(month) < 13) {
                String date = month + "/" + day;
                syllEvent addedEvent = new syllEvent(date, event, "0");
                events.add(addedEvent);
            }
        }
    
        // Match Month Name format
        while (monthNameMatcher.find()) {
            String month = monthNameMatcher.group(1).trim(); // e.g., "Oct"
            String day = monthNameMatcher.group(2).trim();
            String event = monthNameMatcher.group(3).trim();
    
            // Convert month name to number
            int monthNumber = monthToNumber(month);
            if (monthNumber > 0) {
                String date = monthNumber + "/" + day; // Convert to MM/DD
                syllEvent addedEvent = new syllEvent(date, event, "0");
                events.add(addedEvent);
            }
        }
    
        return events;
    }

private static int monthToNumber(String month) {
    switch (month.toLowerCase()) {
        case "jan":
        case "january": return 1;
        case "feb":
        case "february": return 2;
        case "mar":
        case "march": return 3;
        case "apr":
        case "april": return 4;
        case "may": return 5;
        case "jun":
        case "june": return 6;
        case "jul":
        case "july": return 7;
        case "aug":
        case "august": return 8;
        case "sep":
        case "september": return 9;
        case "oct":
        case "october": return 10;
        case "nov":
        case "november": return 11;
        case "dec":
        case "december": return 12;
        default: return -1; // Invalid month
    }
}

    public static void writeToCSV(List<syllEvent> events) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data.csv"))) {
            writer.write("Date,Event,Importance\n");
            for (syllEvent event : events) {
                String formattedEvent = event.getEvent().replace(",", " ");
                writer.write(String.format("%s, %s, %s\n", event.getDate(), formattedEvent, event.getImportance()));
                System.out.println(formattedEvent);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class syllEvent{
    private String date;
    private String event;
    private String importance;

    public syllEvent(String date, String event, String importance){
        this.date = date;
        this.event = event;
        this.importance = importance;
    }

    //getters
    public String getDate(){
        return date;
    }

    public String getEvent(){
        return event;
    }

    public String getImportance(){
        return importance;
    }

    //setters
    public void setDate(String date){
        this.date = date;
    }

    public void setEvent(String event){
        this.event = event;
    }

    public void setImportance(String importance) {
        this.importance = importance;
    }

    public String toString() {
        return "Date: " + date + ", Event: " + event;
    }
}