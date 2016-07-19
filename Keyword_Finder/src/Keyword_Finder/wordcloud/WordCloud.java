package Keyword_Finder.wordcloud;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import Keyword_Finder.wordcloud.image.CloudImageGenerator;
import Keyword_Finder.wordcloud.words.StringProcessor;

import java.awt.Component;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import Keyword_Finder.Keyword_Finder;

public class WordCloud {
    public static final int WIDTH = 1000;
    public static final int HEIGHT = 1000;
    public static final int PADDING = 30;

    public static void main(String current_dir) throws IOException {  
    	
    	int str_len;
    	int flag;
		String csv_folder = current_dir.concat("\\csv");
		String cloud_folder = current_dir.concat("\\cloud");
		
		File csv_dir = new File(csv_folder);
		File[] csv_Filelist= csv_dir.listFiles();
		List<String> csv_list = new ArrayList<String>();
		
		File cloud_dir = new File(cloud_folder);
		List<String> cloud_list = new ArrayList<String>();
			
		if(!cloud_dir.isDirectory())
			cloud_dir.mkdir();
		
		File[] cloud_Filelist = cloud_dir.listFiles();

		for (File csv_file:csv_Filelist)
		{
			String csv_filename = csv_file.getName();
			str_len = csv_filename.length();
			String csv_name = csv_filename.substring(0,str_len-4);		//delete the suffix ".csv"	
			String is_csv = csv_filename.substring(str_len-3,str_len);
			if (is_csv.equals("csv"))
				csv_list.add(csv_name);	
		}		
				
		for (File cloud_file:cloud_Filelist)
		{
			String cloud_filename = cloud_file.getName();
			str_len = cloud_filename.length();
			String cloud_name = cloud_filename.substring(0,str_len-4);		//delete the suffix ".png"
			String is_png = cloud_filename.substring(str_len-3,str_len);
			if (is_png.equals("png"))
				cloud_list.add(cloud_name);			
		}

		System.out.println("\nCreating Word Cloud Image files from csv files...");

		int total_img = csv_list.size();
		total_img -= cloud_list.size();		
		int index = 0;
		for (File csv_f:csv_Filelist)
		{
			flag = 0;
			String filename = csv_folder.concat("\\".concat(csv_f.getName()));
			String csv_filename = csv_f.getName();
			str_len = csv_f.getName().length();
			String img_name = csv_filename.substring(0,str_len-4);		//delete the suffix ".png"
			String is_csv = csv_filename.substring(str_len-3,str_len);
			
			if (!is_csv.equals("csv"))
				continue;
			
			for (String cloud_name:cloud_list)
			{
				if(cloud_name.equals(img_name))
				{
					flag = 1;
					break;
				}
			}
			
			if (flag == 0)
			{
				index++;
		    	StringProcessor strProcessor = new StringProcessor(filename);
		        CloudImageGenerator generator = new CloudImageGenerator(WIDTH, HEIGHT, PADDING);
		        
		        JFrame frame = new JFrame("Word Cloud");
		        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		        frame.setLocationByPlatform(true);
		        frame.pack();
		        Insets insets = frame.getInsets();
		        frame.setSize(calcScreenSize(insets));
		        frame.getContentPane().add(new CloudViewer(generator.generateImage(strProcessor, System.currentTimeMillis())));
		        frame.setVisible(true); 
		        BufferedImage bImg= getScreenShot(frame);
		        frame.dispose(); //Destroy the JFrame object
		        
		        File outputfile = new File(cloud_folder.concat("\\")+img_name+".png");
		        ImageIO.write(bImg, "png", outputfile);  
		        
		        try {
					Keyword_Finder.loading(total_img, index);
				} catch (IOException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        
			}
		}
    }
    
    private static BufferedImage getScreenShot(Component component)
    {
        BufferedImage image = getImage(component);
        // call the Component's paint method, using
        // the Graphics object of the image.
        component.paint(image.getGraphics());
        return image;
    }
    
    public static BufferedImage getImage(Component c) {
        BufferedImage bi = null;
        try {
            bi = new BufferedImage(c.getWidth(),c.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d =bi.createGraphics();
            c.print(g2d);
            g2d.dispose();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bi;
    }
    
    /**
     * This function generates a list of words to be filtered when a cloud is generated
     */
    private static Dimension calcScreenSize(Insets insets) {
        int width = insets.left + insets.right + WIDTH + PADDING * 2;
        int height = insets.top + insets.bottom + HEIGHT + PADDING * 2;
        return new Dimension(width, height);
    }
}
