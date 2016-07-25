import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
 
import com.opencsv.CSVReader;

public class Merge_CSV {

	private static String prog_bar = "";
	/*
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
			*/
	public static synchronized void loading(int total, int current) throws IOException, InterruptedException {
	    int mult_num = 50/total;
	    int progress = current * mult_num;
	    String percentage;
	    String num_process;
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
        	num_process = current + "/" + total + " ";
        	percentage = String.format("%.2f", (float)current / (float)total * 100);
        	prog_bar += "| " + num_process + percentage +"%";
			System.out.write("\r|".getBytes());
            System.out.write(prog_bar.getBytes());
            if(current == total)
            	System.out.write(" Done \r\n".getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
		
	private static void merge_csv(String current_dir) throws IOException
	{
		String csv_dir = current_dir.concat("\\csv\\");
		File csv_folder = new File(csv_dir);
		
		int str_len;
		String docu_name;
		List<String> csv_list = new ArrayList<String>();
		File[] csv_Filelist = csv_folder.listFiles();
		String contents= "";
		String[] content_split;

		if(csv_folder.isDirectory())								//Check files in txt folder
		{			
			for (File f:csv_Filelist)
			{
				String txt_filename = f.getName();
				str_len = txt_filename.length();
				docu_name = txt_filename.substring(0,str_len-4);
				String is_txt = txt_filename.substring(str_len-3,str_len);
				if (is_txt.equals("txt"))
					csv_list.add(docu_name);							//Get the names of txt files
			}
		}
		for (File f:csv_Filelist)
		{
			String txt_filename = f.getName();
			str_len = txt_filename.length();
			docu_name = txt_filename.substring(0,str_len-4);
			String date = txt_filename.substring(str_len - 14, str_len - 4);
			    	
			CSVReader reader = new CSVReader(new FileReader(f));
			
			String[] nextLine;
			if (f.getName().contains("google"))
	        {
		        while((nextLine = reader.readNext()) != null){
		        	if (nextLine[2].equals("발표처"))
		        		continue;
		        	contents += date + "," + nextLine[2].replace(",", " ").trim() + "," +  nextLine[3].replace(",", " ").replace("\"","").trim() + "," + nextLine[4].replace(",", " ").trim() + "\n";			        	
		        }
	        }
	        else
	        {
		        while((nextLine = reader.readNext()) != null){
		        	if (nextLine[1].equals("발표처"))
		        		continue;
		        	contents += date + "," + nextLine[1].replace(",", " ").trim() + "," +  nextLine[2].replace(",", " ").replace("\"","").trim() + "," + nextLine[3].replace(",", " ").trim() + "\n";
		        }	        	
	        }
			
			reader.close();
		}
		
		content_split = contents.split("\r\n");
				
		try {
			String csv_file = "\\result.csv";
			
			BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(new FileOutputStream(current_dir.concat(csv_file)), "MS949"));
			writer.write("날짜,발표처,제목,웹주소\n");
			for (String content:content_split)
			{
				writer.write(content + "\n");
			}
			
			writer.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		
		}

	}	
	
	
	public static void main(String args[]) throws IOException{
		String current_dir = System.getProperty("user.dir");	//Get the current Directory
		merge_csv(current_dir);
	}
}