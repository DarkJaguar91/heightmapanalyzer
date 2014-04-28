package util;

import java.io.File;

public class FileSizeCulculator {

	private static final String[] names= {"bytes", "Kilobytes", "Megabytes", "Gigabytes"};
	
	public static double calculateSize(File file){
		if (file.isFile()){
			return (file.length());
		}
		else {
			return (calcFolderSize(file));
		}
	}
	
	private static double calcFolderSize(File file){
		double size = 0;
		
		for (File f : file.listFiles()){
			if (f.isFile())
				size += f.length();
			else
				size += calcFolderSize(f);
		}		
		
		return size;
	}
	
	public static String convertToString(double size){
		int cnt = 0;
		while (size > 1023){	
			size /= 1024.0d;
			++cnt;
		}
		
		return String.format("%.2f%s" , size , names[cnt]);
	}
}
