package test;

import rcc.h2tlib.parser.*;
import java.io.File;

public class test {
	public static void main(String[] args){
		String folder = System.getProperty("user.dir").concat("\\hwp");
		File file = new File(folder);
		if (!file.isDirectory()){
			System.out.println("해당 디렉토리는 존재하지 않습니다.");
			System.exit(1);
		}
		int str_len = 0;
		String text_name;
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
			text_name = f.getName().substring(0,str_len-4);			
			
			String fo = folder.concat("\\result\\".concat(text_name)+ ".txt");

			HWPMeta meta = new HWPMeta();
			parser.GetText(filename, meta, fo);
		}
	}
}