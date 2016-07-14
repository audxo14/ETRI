package kor_wr_freq;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import kr.co.shineware.nlp.komoran.core.analyzer.Komoran;
import kr.co.shineware.util.common.model.Pair;

public class kor_wr_freq {
	public static List sortByValue(final Map map){
        List<String> list = new ArrayList();
        list.addAll(map.keySet());
         
        Collections.sort(list,new Comparator(){
             
            public int compare(Object o1,Object o2){
                Object v1 = map.get(o1);
                Object v2 = map.get(o2);
                 
                return ((Comparable) v1).compareTo(v2);
            }
             
        });
        Collections.reverse(list); // 주석시 오름차순
        return list;
    }
	
	static List<String> removeDuplicates(List<String> stopwords) {

		// Store unique items in result.
		List<String> result = new ArrayList<>();

		// Record encountered Strings in HashSet.
		HashSet<String> set = new HashSet<>();

		// Loop over argument list.
		for (String item : stopwords) {

		    // If String is not in set, add it to the list and the set.
		    if (!set.contains(item)) {
			result.add(item);
			set.add(item);
		    }
		}
		return result;
	}
	
	public static void main(String args[]) throws IOException{
		try {
			String csv_file = "test.csv";
			String collocation = "";
			List<String> cols = new ArrayList();
			boolean firstN = false;
			boolean secondN = false;
			BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(new FileOutputStream(csv_file), "MS949"));
			writer.write("구분 , 단어 , 횟수\n");
			Map<String, Integer> map = new HashMap<String, Integer>();
			Map<String, Integer> colsmap = new HashMap<String, Integer>();
			BufferedReader br = new BufferedReader(new FileReader("document\\회의록2.txt"));
			BufferedReader br_sw = Files.newBufferedReader(Paths.get("stopwords.txt"), Charset.forName("MS949"));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			String line_sw = br_sw.readLine();
			List<String> tempstopwords = new ArrayList();
			while(line_sw != null){
				tempstopwords.add(new String(line_sw.getBytes("UTF-8")));
				line_sw = br_sw.readLine();
				
			}
			while(line != null){
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			
			List<String> stopwords = removeDuplicates(tempstopwords);
			System.out.println(stopwords);
			List<String> word_list = new ArrayList<String>();
			Komoran komoran = new Komoran("C:\\Users\\KimHyeMin\\eclipse\\java-neon\\eclipse\\komoran\\models-light");
			List<List<Pair<String, String>>> result = komoran.analyze(sb.toString());
			for (List<Pair<String, String>> eojeolResult : result){
				for (Pair<String, String> wordMorph : eojeolResult){
					//System.out.println(wordMorph);
					if( wordMorph.getSecond().equals("NNG")){
						word_list.add(wordMorph.getFirst());
						
						if(firstN == false){
							firstN = true;
							collocation = wordMorph.getFirst();
						}else if(firstN ==true && secondN == false){
							collocation = collocation + " "+ wordMorph.getFirst();
							cols.add(collocation);
							collocation = wordMorph.getFirst();
						}
					}else{
						collocation = "";
						firstN = false;
					}
				}
			}
			int max = 0;
			//System.out.println(word_list);
			for (String temp : word_list) {
				Integer count = map.get(temp);
				map.put(temp, (count == null) ? 1 : count + 1);
			}
			
			for (String tempcols : cols) {
				Integer count = colsmap.get(tempcols);
				colsmap.put(tempcols, (count == null) ? 1 : count + 1);
			}
			
			Iterator it = sortByValue(map).iterator();
			int i = 0;
			boolean is_stop = false;
			while(i < 19 && it.hasNext()){
				String temp = (String) it.next();
				
				
				i = i+1;
				
				for (int j = 0; j < stopwords.size(); j= j+1){
					if(temp.equals(stopwords.get(j))){
						is_stop = true;
						break;
					}
				}
				
				if( is_stop == true){
					i = i-1;
				}else{
					
					writer.write("unigram ,"+temp+" ,"+map.get(temp)+"\n");
					if( i==19 ){
						max = map.get(temp);
					}
				}
				
				is_stop = false;
			}
			String word1 = "";
			String word2 = "";
			Iterator itcols = sortByValue(colsmap).iterator();
			boolean moremax = true;
			is_stop = false;
			System.out.println(colsmap);
			while(moremax && itcols.hasNext()){
				String tempcols = (String) itcols.next();
				int index = tempcols.indexOf(" ");
				word1 = tempcols.substring(0, index);
				word2 = tempcols.substring(index+1);
				moremax = max < colsmap.get(tempcols);
				if(moremax){
					for (int j = 0; j < stopwords.size(); j= j+1){
						if(word1.equals(stopwords.get(j))){
							is_stop = true;
							break;
						}
						if(word2.equals(stopwords.get(j))){
							is_stop = true;
							break;
						}
					}
				}
				
				if( is_stop == false){
					writer.write("Bigram ,"+ tempcols+" ,"+colsmap.get(tempcols)+ "\n");
				}
				
				is_stop = false;
			}
			
			writer.close();
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		
		}
		
}
}
