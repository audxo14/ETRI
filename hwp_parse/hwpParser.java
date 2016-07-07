package hwpParser;
import rcc.h2tlib.parser.*;
import java.io.File;

public class hwpParser {
	public static void main(String[] args){
		String folder = System.getProperty("user.dir").concat("\\hwp");
		System.out.println(folder);
		File file = new File(folder);
		if (!file.isDirectory()){
			System.out.println("�ش� ���丮�� �������� �ʽ��ϴ�.");
			System.exit(1);
		}
		
		File[] list= file.listFiles();
		H2TParser parser = new H2TParser();
		for ( File f:list){
			String filename = folder.concat("\\".concat(f.getName()));
			boolean fig = parser.IsHanFile(filename);
			if(fig)
				System.out.println("HWP �����Դϴ�.");
			else
				System.out.println("HWP ������ �ƴմϴ�.");
			String fo = folder.concat("\\result.txt");
			HWPMeta meta = new HWPMeta();
			boolean e = parser.GetText(filename, meta, fo);
			if(e){
				System.out.println("HWP �����Դϴ�.");
		        System.out.println("title => " + meta.getTitle());
		        System.out.println("subject => " + meta.getSubject());
		        System.out.println("author => " + meta.getAuthor());
		        System.out.println("createtime => " + meta.getCreatetime());
		        System.out.println("keyword => " + meta.getKeyword());
		        System.out.println("comment => " + meta.getComment());
		        if(meta.getVer() == HWPVER.HML2) System.out.println("ver => HML2");
		        if(meta.getVer() == HWPVER.HWP3) System.out.println("ver => HWP3");
		        if(meta.getVer() == HWPVER.HWP5) System.out.println("ver => HWP5");
			}
		}
	}
}
