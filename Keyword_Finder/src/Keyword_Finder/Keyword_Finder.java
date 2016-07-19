package Keyword_Finder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import kr.co.shineware.nlp.komoran.core.analyzer.Komoran;
import kr.co.shineware.util.common.model.Pair;
import rcc.h2tlib.parser.*;

import Keyword_Finder.wordcloud.WordCloud;

public class Keyword_Finder {

	private static String prog_bar = "";
	private static Map<String, Long> map;
	private static Map<String, Long> colsmap;
	private static String[] eng_stop = {"a", "as", "able", "about", "above", "according", 
			"accordingly", "across", "actually", "after", "afterwards", "again", "against", 
			"aint", "all", "allow", "allows", "almost", "alone", "along", "already", "also", 
			"although", "always", "am", "among", "amongst", "an", "and", "another", "any", "anybody", 
			"anyhow", "anyone", "anything", "anyway", "anyways", "anywhere", "apart", "appear", "appreciate", 
			"appropriate", "are", "arent", "around", "as", "aside", "ask", "asking", "associated", "at", "available",
			"away", "awfully", "be", "became", "because", "become", "becomes", "becoming", "been", "before", 
			"beforehand", "behind", "being", "believe", "below", "beside", "besides", "best", "better", "between", 
			"beyond", "both", "brief", "but", "by", "cmon", "cs", "came", "can", "cant", "cannot", "cant", 
			"cause", "causes", "certain", "certainly", "changes", "clearly", "co", "com", "come", "comes", 
			"concerning", "consequently", "consider", "considering", "contain", "containing", "contains", 
			"corresponding", "could", "couldnt", "course", "currently", "definitely", "described", "despite", 
			"did", "didnt", "different", "do", "does", "doesnt", "doing", "dont", "done", "down", "downwards", 
			"during", "each", "edu", "eg", "eight", "either", "else", "elsewhere", "enough", "entirely", 
			"especially", "et", "etc", "even", "ever", "every", "everybody", "everyone", "everything", "everywhere", 
			"ex", "exactly", "example", "except", "far", "few", "ff", "fifth", "first", "five", "followed", "following", 
			"follows", "for", "former", "formerly", "forth", "four", "from", "further", "furthermore", "get", "gets", 
			"getting", "given", "gives", "go", "goes", "going", "gone", "got", "gotten", "greetings", "had", "hadnt", 
			"happens", "hardly", "has", "hasnt", "have", "havent", "having", "he", "hes", "hello", "help", "hence", 
			"her", "here", "heres", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "hi", "him", "himself", 
			"his", "hither", "hopefully", "how", "howbeit", "however", "i", "id", "ill", "im", "ive", "ie", "if", "ignored", 
			"immediate", "in", "inasmuch", "inc", "indeed", "indicate", "indicated", "indicates", "inner", "insofar", 
			"instead", "into", "inward", "is", "isnt", "it", "itd", "itll", "its", "its", "itself", "just", "keep", "keeps", 
			"kept", "know", "knows", "known", "last", "lately", "later", "latter", "latterly", "least", "less", "lest", 
			"let", "lets", "like", "liked", "likely", "little", "look", "looking", "looks", "ltd", "mainly", "many", "may", 
			"maybe", "me", "mean", "meanwhile", "merely", "might", "more", "moreover", "most", "mostly", "much", "must", 
			"my", "myself", "name", "namely", "nd", "near", "nearly", "necessary", "need", "needs", "neither", "never", 
			"nevertheless", "new", "next", "nine", "no", "nobody", "non", "none", "noone", "nor", "normally", "not", 
			"nothing", "novel", "now", "nowhere", "obviously", "of", "off", "often", "oh", "ok", "okay", "old", "on", 
			"once", "one", "ones", "only", "onto", "or", "other", "others", "otherwise", "ought", "our", "ours", "ourselves",
			"out", "outside", "over", "overall", "own", "particular", "particularly", "per", "perhaps", "placed", "please", 
			"plus", "possible", "presumably", "probably", "provides", "que", "quite", "qv", "rather", "rd", "re", "really", 
			"reasonably", "regarding", "regardless", "regards", "relatively", "respectively", "right", "said", "same", "saw",
			"say", "saying", "says", "second", "secondly", "see", "seeing", "seem", "seemed", "seeming", "seems", "seen", 
			"self", "selves", "sensible", "sent", "serious", "seriously", "seven", "several", "shall", "she", "should", 
			"shouldnt", "since", "six", "so", "some", "somebody", "somehow", "someone", "something", "sometime", "sometimes",
			"somewhat", "somewhere", "soon", "sorry", "specified", "specify", "specifying", "still", "sub", "such", "sup", 
			"sure", "ts", "take", "taken", "tell", "tends", "th", "than", "thank", "thanks", "thanx", "that", "thats", 
			"thats", "the", "their", "theirs", "them", "themselves", "then", "thence", "there", "theres", "thereafter", 
			"thereby", "therefore", "therein", "theres", "thereupon", "these", "they", "theyd", "theyll", "theyre", 
			"theyve", "think", "third", "this", "thorough", "thoroughly", "those", "though", "three", "through", 
			"throughout", "thru", "thus", "to", "together", "too", "took", "toward", "towards", "tried", "tries", "truly", 
			"try", "trying", "twice", "two", "un", "under", "unfortunately", "unless", "unlikely", "until", "unto", "up", 
			"upon", "us", "use", "used", "useful", "uses", "using", "usually", "value", "various", "very", "via", "viz", 
			"vs", "want", "wants", "was", "wasnt", "way", "we", "wed", "well", "were", "weve", "welcome", "well", "went", 
			"were", "werent", "what", "whats", "whatever", "when", "whence", "whenever", "where", "wheres", "whereafter", 
			"whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whos", 
			"whoever", "whole", "whom", "whose", "why", "will", "willing", "wish", "with", "within", "without", "wont", 
			"wonder", "would", "would", "wouldnt", "www", "yes", "yet", "you", "youd", "youll", "youre", "youve", "your", 
			"yours", "yourself", "yourselves", "zero", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "b", "c", 
			"d", "e", "f", "g", "h", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
	private static String[] eng_bi_stop = {"how to", "thank you", "good morning"};
			
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
	
	private static void hwp2txt(String current_dir, File[] result_Filelist){								//To convert hwp files to txt files
		String hwp_folder = current_dir.concat("\\hwp");			//Get the folder containing hwp files
		String txt_folder = current_dir.concat("\\txt");
		File hwp_file = new File(hwp_folder);
		File txt_dir = new File(txt_folder);
		
		if (!hwp_file.isDirectory()){
			System.out.println("No such Directory (hwp) found...");
			pressAnyKeyToExit();
		}
		
		File[] hwp_Filelist= hwp_file.listFiles();
		H2TParser parser = new H2TParser();
			
		String text_name;
		int flag = 0;
		
		List<String> csv_list = new ArrayList<String>();
		List<String> hwp_list = new ArrayList<String>();
		
		for (File hwp_f:hwp_Filelist)									//check the number of hwp files in hwp folder
		{
			String hwp_filename = hwp_f.getName();
			int str_len = hwp_filename.length();
			String hwp_name = hwp_filename.substring(0,str_len-4);		//delete the suffix ".hwp"	
			String is_hwp = hwp_filename.substring(str_len-3, str_len);
			if (is_hwp.equals("hwp"))
				hwp_list.add(hwp_name);			
		}
		
		for (File result_file:result_Filelist)							//check the number of csv files in csv folder
		{
			String csv_filename = result_file.getName();
			int str_len = csv_filename.length();
			String csv_name = csv_filename.substring(0,str_len-4);		//delete the suffix ".csv"	
			String is_csv = csv_filename.substring(str_len-3, str_len);
			if (is_csv.equals("csv"))
				csv_list.add(csv_name);			
		}
		
		if(!txt_dir.isDirectory())
			txt_dir.mkdir();
		
		System.out.println("Converting hwp files into txt files...");
		int total_txt = hwp_list.size();
		total_txt -= csv_list.size();
		int index = 0;
		for (File hwp_f:hwp_Filelist)
		{
			flag = 0;
			String filename = hwp_folder.concat("\\".concat(hwp_f.getName()));
			String hwp_filename = hwp_f.getName();
			int str_len = hwp_f.getName().length();
			text_name = hwp_filename.substring(0,str_len-4);		//delete the suffix ".hwp"	
			
			boolean fig = parser.IsHanFile(filename);
			
			if (hwp_f.isDirectory())
				continue;
			
			if(!fig)
			{
				continue;
			}	
			
			for (String csv_name:csv_list)
			{
				if(csv_name.equals(text_name))
				{
					flag = 1;
					break;
				}
			}
			
			if (flag == 0)
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

		Stream<String> stream = Stream.of(eng_word.split("\\W+")).parallel();
		map = stream.collect(Collectors.groupingBy(String::toString,Collectors.counting()));
		
		Stream<String> bi_stream = Stream.of(eng_word.split("\\W+")).parallel();
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
		String stop_dir = current_dir.concat("\\stopwords.txt");
		
		File stop_file = new File(stop_dir);
		
		if(!stop_file.isFile())
		{
			stop_file.createNewFile();
		}
		
		BufferedReader br_sw = Files.newBufferedReader(Paths.get("stopwords.txt"), Charset.forName("MS949"));
		String line_sw = br_sw.readLine();
		List<String> stopwords = new ArrayList<String>();		
		
		while(line_sw != null){
			stopwords.add(new String(line_sw.getBytes("UTF-8")).toLowerCase());
			line_sw = br_sw.readLine();
			
		}
				
		String docu_dir = current_dir.concat("\\txt\\");
		File docu_folder = new File(docu_dir);
		
		int str_len;
		String docu_name;
		List<String> docu_list = new ArrayList<String>();
		
		String result_dir = current_dir.concat("\\csv\\");		//result folder
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
		System.out.println("Getting Keywords from hwp files...");
		int total_csv = docu_Filelist.length;
		int index = 1;
		Long max = (long) 0;
				
		for (String f:docu_list)
		{
			map = new HashMap<String, Long>();
			colsmap = new HashMap<String, Long>();
			
			try {
				stopwords = removeDuplicates(stopwords);
				
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
				BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(new FileOutputStream(result_dir.concat(csv_file)), "MS949"));
				writer.write("구분, 단어, 횟수\n");
				
				
				// Unigram
				Iterator<?> it = sortByValue(map).iterator();
				int i = 0;
				boolean is_stop = false;
				while(i < 99 && it.hasNext()){
					String temp = (String) it.next();
					
					i = i+1;
					
					for (int j = 0; j < stopwords.size(); j= j+1){
						if(temp.equals(stopwords.get(j))){
							is_stop = true;
							break;
						}
					}
					
					for (int j = 0; j < eng_stop.length; j= j+1){
						if(temp.equals(eng_stop[j])){
							is_stop = true;
							break;
						}
					}
					
					if( is_stop == true){
						i = i-1;
					}else{
						
						writer.write("unigram ,"+temp+" ,"+map.get(temp)+"\n");
						max = map.get(temp);
					
					}
					
					is_stop = false;
				}
				
				// Bigram
				Iterator<?> itcols = sortByValue(colsmap).iterator();
				boolean moremax = true;
				is_stop = false;
				while(moremax && itcols.hasNext()){
					String tempcols = (String) itcols.next();
					moremax = max < colsmap.get(tempcols);
					
					for (int j = 0; j < stopwords.size(); j= j+1){
						if(tempcols.equals(stopwords.get(j))){
							is_stop = true;
							break;
						}
					}
					

					for (int j = 0; j < eng_bi_stop.length; j= j+1){
						if(tempcols.equals(eng_bi_stop[j])){
							is_stop = true;
							break;
						}
					}
					
					if( is_stop == false){
						writer.write("Bigram ,"+ tempcols+" ,"+colsmap.get(tempcols)+ "\n");
					}
					
					is_stop = false;
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
		//WordCloud word_cloud = new WordCloud();
		WordCloud.main(current_dir);
		pressAnyKeyToExit();
	}
}