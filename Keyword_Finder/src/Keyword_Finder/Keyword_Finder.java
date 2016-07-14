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
import kr.co.shineware.nlp.komoran.core.analyzer.Komoran;
import kr.co.shineware.util.common.model.Pair;
import rcc.h2tlib.parser.*;

public class Keyword_Finder {
	@SuppressWarnings("unchecked")
	static List<String> sortByValue(final Map<String, Integer> map){
        List<String> list = new ArrayList<String>();
        list.addAll(map.keySet());
         
        Collections.sort(list,new Comparator<Object>(){
             
            public int compare(Object o1,Object o2){
                Object v1 = map.get(o1);
                Object v2 = map.get(o2);
                 
                return ((Comparable<Object>) v1).compareTo(v2);
            }
             
        });
        Collections.reverse(list);
        return list;
    }
	

	static List<String> removeDuplicates(List<String> stopwords) {				//To remove the duplicate words in stopwords.txt

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
	static void hwp2txt(String current_dir, File[] result_Filelist){								//To convert hwp files to txt files
		String hwp_folder = current_dir.concat("\\hwp");			//Get the folder containing hwp files
		String txt_folder = current_dir.concat("\\txt");
		File hwp_file = new File(hwp_folder);
		File txt_dir = new File(txt_folder);
		
		if (!hwp_file.isDirectory()){
			System.out.println("해당 디렉토리가 없습니다..");
			System.exit(1);
		}
		
		File[] hwp_Filelist= hwp_file.listFiles();
		H2TParser parser = new H2TParser();
			
		String text_name;
		int flag = 0;
		
		List<String> csv_list = new ArrayList<String>();
		for (File result_file:result_Filelist)
		{
			String csv_filename = result_file.getName();
			int str_len = csv_filename.length();
			String csv_name = csv_filename.substring(0,str_len-4);		//delete the suffix ".hwp"	
			csv_list.add(csv_name);			
		}
		
		if(!txt_dir.isDirectory())
			txt_dir.mkdir();
		
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
				String fo = txt_folder.concat("\\".concat(text_name)+ ".txt");	//Create txt file with hwp contents
				HWPMeta meta = new HWPMeta();
				parser.GetText(filename, meta, fo);
			}
		}
	}
	
	static void find_keyword(String current_dir) throws IOException
	{
		String collocation = "";		
		String word1;
		String word2;
		
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
			stopwords.add(new String(line_sw.getBytes("UTF-8")));
			line_sw = br_sw.readLine();
			
		}
				
		String docu_dir = current_dir.concat("\\txt\\");
		File docu_folder = new File(docu_dir);
		int str_len;
		String docu_name;
		List<String> docu_list = new ArrayList<String>();
		
		String result_dir = current_dir.concat("\\result\\");		//result folder
		File result_folder = new File(result_dir);
		if (!result_folder.isDirectory()){							//create the folder if it doesn't exist
			result_folder.mkdir();
		}
		
		File[] result_Filelist = result_folder.listFiles();

		hwp2txt(current_dir, result_Filelist);													//Execute hwp2txt() method
		File[] docu_Filelist = docu_folder.listFiles();
		
		if(docu_folder.isDirectory())								//Check files in txt folder
		{			
			for (File f:docu_Filelist)
			{
				String txt_filename = f.getName();
				str_len = txt_filename.length();
				docu_name = txt_filename.substring(0,str_len-4);
				docu_list.add(docu_name);							//Get the names of txt files
			}
		}
		
		for (String f:docu_list)
		{
			try {
				List<String> cols = new ArrayList<String>();
				boolean firstN = false;
				boolean secondN = false;
				
				Map<String, Integer> map = new HashMap<String, Integer>();
				Map<String, Integer> colsmap = new HashMap<String, Integer>();
				
				BufferedReader br = new BufferedReader(new FileReader(docu_dir.concat(f).concat(".txt")));
				StringBuilder sb = new StringBuilder();
				String line = br.readLine();
				int max = 0;

				while(line != null){
					sb.append(line);
					sb.append(System.lineSeparator());
					line = br.readLine();
				}
				
				br.close();
				
				docu_Filelist = docu_folder.listFiles();
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
				
				stopwords = removeDuplicates(stopwords);
				
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
				
				for (String temp : word_list) {
					Integer count = map.get(temp);
					map.put(temp, (count == null) ? 1 : count + 1);
				}
				
				for (String tempcols : cols) {
					Integer count = colsmap.get(tempcols);
					colsmap.put(tempcols, (count == null) ? 1 : count + 1);
				}

				String csv_file = f.concat(".csv");
				BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(new FileOutputStream(result_dir.concat(csv_file)), "MS949"));
				writer.write("구분, 단어, 횟수\n");
				
				Iterator<?> it = sortByValue(map).iterator();
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
				
				Iterator<?> itcols = sortByValue(colsmap).iterator();
				boolean moremax = true;
				is_stop = false;
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
	
	public static void main(String args[]) throws IOException{
		String current_dir = System.getProperty("user.dir");	//Get the current Directory
		String txt = current_dir.concat("\\txt");
		find_keyword(current_dir);
		File tmp_folder = new File(txt);
		tmp_folder.delete();
	}
}