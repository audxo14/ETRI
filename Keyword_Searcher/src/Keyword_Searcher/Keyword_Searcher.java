package Keyword_Searcher;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.opencsv.CSVReader;

import Keyword_Searcher.wordcloud.WordCloud;
import kr.co.shineware.nlp.komoran.core.analyzer.Komoran;
import kr.co.shineware.util.common.model.Pair;
import rcc.h2tlib.parser.*;

public class Keyword_Searcher {

	private static String prog_bar = "";
	private static Map<String, Long> map;
	private static Map<String, Long> colsmap;
	private static Map<String, Long> key_map;
	private static Map<String, Long> bi_key_map;
			
	public static synchronized void loading(int total, int current) throws IOException, InterruptedException {
	    int mult_num = 50/total;
	    int progress = current * mult_num;
	    String percentage;
	    try {
        	prog_bar = "";
        	if (current == total)
        		prog_bar = "**************************************************";
        	else
        	{
	        	for (int i = 0; i < progress; i++)
	        		prog_bar += "*";
	        	for (int j = progress; j < 50; j++)
	        		prog_bar += "-";
        	}
        	percentage = String.format("%.2f", (float)current / (float)total * 100);
        	prog_bar += "| " + percentage +"%";
			System.out.write("\r|".getBytes());
            System.out.write(prog_bar.getBytes());
            if(current == total)
            	System.out.write(" Done \r\n".getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    private static void pressAnyKeyToExit()
    { 
           System.out.println("\nPress Enter to exit...");
           try
           {
               System.in.read();
               System.exit(1);
           }  
           catch(Exception e)
           {}  
    }
	
	@SuppressWarnings("unchecked")
	private static List<String> sortByValue(final Map<String, Long> maps){
        List<String> list = new ArrayList<String>();
        list.addAll(maps.keySet());
         
        Collections.sort(list,new Comparator<Object>(){
             
            public int compare(Object o1,Object o2){
                Object v1 = maps.get(o1);
                Object v2 = maps.get(o2);
                
                return ((Comparable<Object>) v1).compareTo(v2);
            }
             
        });
        Collections.reverse(list);
        return list;
    }
	

	private static List<String> removeDuplicates(List<String> stopwords) {				//To remove the duplicate words in stopwords.txt

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
	
	private static void hwp2txt(String current_dir, File[] result_Filelist) throws IOException{								//To convert hwp files to txt files
		String hwp_folder = current_dir.concat("\\1. source");			//Get the folder containing hwp files
		String txt_folder = current_dir.concat("\\txt");
		File hwp_file = new File(hwp_folder);
		File txt_dir = new File(txt_folder);
		
		if (!hwp_file.isDirectory()){
			System.out.println("No such Directory (1. source) found...");
			pressAnyKeyToExit();
		}
		
		File[] hwp_Filelist= hwp_file.listFiles();
		H2TParser parser = new H2TParser();
			
		String text_name;
		
		List<String> hwp_list = new ArrayList<String>();
		
		for (File hwp_f:hwp_Filelist)									//check the number of hwp files in hwp folder
		{
			String hwp_filename = hwp_f.getName();
			int str_len = hwp_filename.length();
			String hwp_name = hwp_filename.substring(0,str_len-4);		//delete the suffix ".hwp"	
			hwp_list.add(hwp_name);			
		}
		
		if(!txt_dir.isDirectory())
			txt_dir.mkdir();
		
		System.out.println("Converting files into txt files...");
		int total_txt = hwp_list.size();
		int index = 0;
		for (File hwp_f:hwp_Filelist)
		{
			String filename = hwp_folder.concat("\\".concat(hwp_f.getName()));
			String hwp_filename = hwp_f.getName();
			int str_len = hwp_f.getName().length();
			text_name = hwp_filename.substring(0,str_len-4);		//delete the suffix ".hwp"	
			
			boolean fig = parser.IsHanFile(filename);
			
			if (hwp_f.isDirectory())
				continue;
			
			if(!fig)
			{
				int flag_set = 0;
				int date_flag = 0;
				int link_flag = 0;
				
				@SuppressWarnings("resource")
				CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(hwp_f), "MS949"));
				String contents = null;
				String[] nextLine;
		        while((nextLine = reader.readNext()) != null)
		        {
		        	if(flag_set == 0)
		        	{
			        	for(int i = 0; i < nextLine.length; i++)
			        	{
			        		if(nextLine[i].equals("날짜"))
			        			date_flag = i;
			        		else if(nextLine[i].equals("웹주소"))
			        			link_flag = i;
			        		else
			        			continue;
			        		
			        		flag_set = 1;
			        		continue;
			        	}
		        	}
		        	
		        	for (int i = 0; i < nextLine.length; i++)
		        	{
		        		if(i == date_flag || i == link_flag)
		        			continue;
		        		contents += nextLine[i] + " ";
		        	}
		        }
		        
		        index++;
				String fo = txt_folder.concat("\\".concat(text_name)+ ".txt");	//Create txt file with hwp contents
				BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fo));
				
				bufferedWriter.write(contents);
				bufferedWriter.close();

				try {
					loading(total_txt, index);
				} catch (IOException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}        
			}	
			else
			{
				index++;
				String fo = txt_folder.concat("\\".concat(text_name)+ ".txt");	//Create txt file with hwp contents
				HWPMeta meta = new HWPMeta();
				parser.GetText(filename, meta, fo);

				try {
					loading(total_txt, index);
				} catch (IOException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private static void Analyze(String f, File[] docu_Filelist, 
			File docu_folder, String model_dir, String docu_dir) throws IOException
	{
		List<String> cols = new ArrayList<String>();
		boolean firstN = false;
		boolean secondN = false;
		String collocation = "";
		
		String first_word = "";
		String second_word = "";
		String bigram = "";
		ArrayList<String> bigram_list = new ArrayList<String>();
						
		BufferedReader br = new BufferedReader(new FileReader(docu_dir.concat(f).concat(".txt")));
		StringBuilder sb = new StringBuilder();
		String line = br.readLine();
		while(line != null){
			sb.append(line);
			sb.append(System.lineSeparator());
			line = br.readLine();
		}
		
		br.close();
		
		docu_Filelist = docu_folder.listFiles();
		
		String eng_word = sb.toString().toLowerCase();		

		Stream<String> stream = Stream.of(eng_word.split("[^a-z&0-9]+")).parallel();
		map = stream.collect(Collectors.groupingBy(String::toString,Collectors.counting()));
		
		Stream<String> bi_stream = Stream.of(eng_word.split("[^a-z&0-9]+")).parallel();
		List<String> bi_list = bi_stream.collect(Collectors.toList());
		
		for (int i = 0; i + 1 < bi_list.size(); i++)
		{
			first_word = bi_list.get(i);
			second_word = bi_list.get(i+1);
			if (first_word.equals(second_word))
				continue;
			bigram = first_word + " " + second_word;
			bigram_list.add(bigram);			
		}
		
		for (String tmp_bi : bigram_list)
		{
			Long count = colsmap.get(tmp_bi);
			colsmap.put(tmp_bi, (count == null) ? 1 : count + 1);					
		}
		
		
		List<String> word_list = new ArrayList<String>();
		Komoran komoran = new Komoran(model_dir);
		@SuppressWarnings("unchecked")
		List<List<Pair<String, String>>> result = komoran.analyze(sb.toString());
		for (List<Pair<String, String>> eojeolResult : result){
			for (Pair<String, String> wordMorph : eojeolResult){
				//System.out.println(wordMorph);
				if( wordMorph.getSecond().equals("NNG")){
					word_list.add(wordMorph.getFirst());
					
					if(firstN == false){
						firstN = true;
						collocation = wordMorph.getFirst();
					}else if(firstN ==true && secondN == false){			//Bigram
						collocation = collocation + wordMorph.getFirst();
						cols.add(collocation);
						collocation = wordMorph.getFirst();
					}
				}else{
					collocation = "";
					firstN = false;
				}
			}
		}
		
		for (String temp : word_list) {
			Long count = map.get(temp);
			map.put(temp, (count == null) ? 1 : count + 1);
		}
		
		for (String tempcols : cols) {
			Long count = colsmap.get(tempcols);
			colsmap.put(tempcols, (count == null) ? 1 : count + 1);
		}
	}
		
	private static void find_keyword(String current_dir) throws IOException
	{
		String model_dir = current_dir.concat("\\models");			//Get the model files
		String stop_dir = current_dir.concat("\\word_list.txt");
		
		File stop_file = new File(stop_dir);
		
		if(!stop_file.isFile())
		{
			stop_file.createNewFile();
		}
		
		BufferedReader br_sw = Files.newBufferedReader(Paths.get("word_list.txt"), Charset.forName("MS949"));
		String line_sw = br_sw.readLine();
		List<String> keywords = new ArrayList<String>();
		List<String> same_keyword = new ArrayList<String>();
		
		while(line_sw != null){
			String tmp = (new String(line_sw.getBytes("UTF-8"))).toLowerCase().trim();
			//String[] tmp_list = tmp.split("=");
			if (tmp.contains("="))
			{
				same_keyword.add(tmp);
				line_sw = br_sw.readLine();
				continue;
			}
			keywords.add(tmp);
			line_sw = br_sw.readLine();
		}
				
		String docu_dir = current_dir.concat("\\txt\\");
		File docu_folder = new File(docu_dir);
		
		int str_len;
		String docu_name;
		List<String> docu_list = new ArrayList<String>();
		
		String result_dir = current_dir.concat("\\2_1. csv\\");		//result folder
		File result_folder = new File(result_dir);
		if (!result_folder.isDirectory()){							//create the folder if it doesn't exist
			result_folder.mkdir();
		}
		
		File[] result_Filelist = result_folder.listFiles();

		hwp2txt(current_dir, result_Filelist);						//Execute hwp2txt() method
		
		File[] docu_Filelist = docu_folder.listFiles();
		
		if(docu_folder.isDirectory())								//Check files in txt folder
		{			
			for (File f:docu_Filelist)
			{
				String txt_filename = f.getName();
				str_len = txt_filename.length();
				docu_name = txt_filename.substring(0,str_len-4);
				String is_txt = txt_filename.substring(str_len-3,str_len);
				if (is_txt.equals("txt"))
					docu_list.add(docu_name);							//Get the names of txt files
			}
		}
		
		System.out.println("");
		System.out.println("Getting Keywords from txt files...");
		int total_csv = docu_Filelist.length;
		int index = 1;
				
		for (String f:docu_list)
		{
			map = new HashMap<String, Long>();
			colsmap = new HashMap<String, Long>();
			key_map = new HashMap<String, Long>();
			bi_key_map = new HashMap<String, Long>();
			
			try {
				keywords = removeDuplicates(keywords);
				
				//English_Analyze(f, docu_Filelist, docu_folder, model_dir, docu_dir);
				Analyze(f, docu_Filelist, docu_folder, model_dir, docu_dir);
				
				for (File del_file:docu_Filelist)
				{
					String txt_filename = del_file.getName();
					str_len = txt_filename.length();
					docu_name = txt_filename.substring(0,str_len-4);
					if (docu_name.equals(f))
					{
						del_file.delete();						
						break;
					}
					else
						continue;
				}

				String csv_file = f.concat(".csv");
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(result_dir.concat(csv_file)), "MS949"));
				writer.write("구분, 단어, 횟수\n");

				for (String str_keyword:same_keyword)
				{
					String[] tmp_list = str_keyword.split("=");
					Long tmp_freq = (long) 0;
					Long tmp_val = (long) 0;
					for(String tmp:tmp_list)
					{
						tmp_val = map.get(tmp.replace(" ","").trim());
						if(tmp_val != null)
							tmp_freq += map.get(tmp.replace(" ", "").trim());
						else
							tmp_freq += colsmap.get(tmp.replace(" ","").trim());
					}
					
					if(map.get(tmp_list[0].trim()) != null)
						key_map.put(tmp_list[0].trim(), tmp_freq);
					else
						bi_key_map.put(tmp_list[0].trim(), tmp_freq);
				}
				
				for (String str:keywords)
				{
					String tmp = str.replace(" ", "").trim();
					if (colsmap.get(tmp) != null)
						bi_key_map.put(tmp, (colsmap.get(tmp) == null) ? 0: colsmap.get(tmp));
					else
						key_map.put(tmp, (map.get(tmp) == null) ? 0: map.get(tmp));
				}
				// Unigram
				if(!key_map.isEmpty())
				{
					Iterator<?> it = sortByValue(key_map).iterator();
					while(it.hasNext()){
						String temp = (String) it.next();
						writer.write("unigram ,"+temp+" ,"+key_map.get(temp)+"\n");
					}
				}

				// Bigram
				if(!bi_key_map.isEmpty())
				{
					Iterator<?> itcols = sortByValue(bi_key_map).iterator();
					while(itcols.hasNext()){
						String tempcols = (String) itcols.next();
						writer.write("Bigram ,"+ tempcols+" ," + bi_key_map.get(tempcols)+ "\n");
	
					}
				}
							
				writer.close();
				
				try {
					loading(total_csv, index);
				} catch (IOException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			
			}
			index++;
		}	
	}	
	
	
	public static void main(String args[]) throws IOException{
		String current_dir = System.getProperty("user.dir");	//Get the current Directory
		String txt = current_dir.concat("\\txt");
		find_keyword(current_dir);
		File tmp_folder = new File(txt);
		tmp_folder.delete();
		WordCloud.main(current_dir);
		pressAnyKeyToExit();
	}
}