package hwp2txt;

import rcc.h2tlib.parser.*;
import java.io.File;

public class hwp2txt {
	public static void main(String[] args){
		String current_dir = System.getProperty("user.dir");	//Get the current Directory
		String folder = current_dir.concat("\\hwp");			//Get the folder containing hwp files
		File file = new File(folder);
		if (!file.isDirectory()){
			System.out.println("해당 디렉토리는 존재하지 않습니다.");
			System.exit(1);
		}
		int str_len = 0;
		String text_name;
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
			
			File dir = new File(result_dir);
		
			if(!dir.isDirectory())							//If there's no folder named "result"
				dir.mkdirs();								//Create it
			
			String fo = result_dir.concat("\\".concat(text_name)+ ".txt");	//Create txt file with hwp contents

			HWPMeta meta = new HWPMeta();
			parser.GetText(filename, meta, fo);
		}
	}
}
