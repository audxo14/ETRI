package Keyword_Finder.wordcloud.words;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class StringProcessor implements Iterable<WordCount>{
    private ArrayList<WordCount> words;
    
    public StringProcessor(String file_name) throws IOException {
        processString(file_name);
    }
    
    private void processString(String file_name) throws IOException {
    	File csv = new File(file_name);
    	BufferedReader br;
		try {
			br = Files.newBufferedReader(Paths.get(""+csv), Charset.forName("MS949"));
		
    	String line = "";
 
        PriorityQueue<WordCount> pq = new PriorityQueue<WordCount>();
        
        line = br.readLine();
        while((line = br.readLine()) != null){
        	String[] token = line.split(",", -1);
        	pq.add(new WordCount(new String(token[1].getBytes("UTF-8")), Integer.parseInt(token[2])));
        	
        }
        words = new ArrayList<WordCount>();
        while (!pq.isEmpty()) {
            WordCount wc = pq.poll();
            if (wc.word.length() > 1) words.add(wc);
        }        
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @Override
    public Iterator<WordCount> iterator() {
        return words.iterator();
    }
}