package hwp2txt;

import rcc.h2tlib.parser.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class hwp2txt {
	public static void main(String[] args){
		String current_dir = System.getProperty("user.dir");	//Get the current Directory
		String folder = current_dir.concat("\\hwp");			//Get the folder containing hwp files
		String txt_folder = current_dir.concat("\\result");
		File txt_file = new File(txt_folder);
		List<String> txt_list = new ArrayList<String>();
		
		int str_len;
		String text_name;
		int flag;
		
		if(!txt_file.isDirectory())
		{
			System.out.println("해당 디렉토리는 존재하지 않습니다.");
		}
		else
		{
			File[] txt_Filelist = txt_file.listFiles();
			
			for (File f:txt_Filelist)
			{
				String txt_filename = f.getName();
				str_len = txt_filename.length();
				text_name = txt_filename.substring(0,str_len-4);
				txt_list.add(text_name);
			}
		}
		
		File file = new File(folder);
		if (!file.isDirectory()){
			System.out.println("해당 디렉토리는 존재하지 않습니다.");
			System.exit(1);
		}
		
		String result_dir;
		File[] list= file.listFiles();
		H2TParser parser = new H2TParser();
		
		
		for ( File f:list){
			String filename = folder.concat("\\".concat(f.getName()));
			
			if (f.isDirectory())
				continue;
			
			boolean fig = parser.IsHanFile(filename);
			if(!fig)
			{
				continue;
			}
			
			str_len = f.getName().length();
			text_name = f.getName().substring(0,str_len-4);		//delete the suffix ".hwp"		
			result_dir = current_dir.concat("\\result");
			flag = 0;
			
			for(String result_txt:txt_list)				//Check whether there are parsed hwp files in advance
			{
				if(text_name.equals(result_txt))
				{
					flag = 1;
					break;
				}
			}
			
			if(flag == 1)
				continue;
			
			File dir = new File(result_dir);
		
			if(!dir.isDirectory())							//If there's no folder named "result"
				dir.mkdirs();								//Create it
			
			String fo = result_dir.concat("\\".concat(text_name)+ ".txt");	//Create txt file with hwp contents

			HWPMeta meta = new HWPMeta();
			parser.GetText(filename, meta, fo);
		}
	}
}
