package ftpupload;

import java.util.HashMap;
import java.util.Map;

public class FTPFileType {
	
	public static Map<String, String> ASCII;	
	static{
		
		ASCII=new HashMap<String, String>();
        ASCII.put("ASC","ASC");
        ASCII.put("C","C");
        ASCII.put("CPP","CPP");
        ASCII.put("CS","CS");
        ASCII.put("CSV","CSV");
        ASCII.put("H","H");
        ASCII.put("HTM","HTM");
        ASCII.put("HTML","HTML");
        ASCII.put("INF","INF");
        ASCII.put("INI","INI");
        ASCII.put("JAVA","JAVA");
        ASCII.put("KSH","KSH");
        ASCII.put("LOG","LOG");
        ASCII.put("PS","PS");
        ASCII.put("SH","SH");
        ASCII.put("SHTML","SHTML");
        ASCII.put("TXT","TXT");
        ASCII.put("UU","UU");
        ASCII.put("UUE","UUE");
        ASCII.put("XML","XML");
        ASCII.put("XSL","XSL");
        ASCII.put("MF", "MF");

	}
	
	public static boolean matchASCII(String filename){
		
		int pos=filename.lastIndexOf(".");
		String ext=filename.substring(pos+1).toUpperCase();
		if(ASCII.get(ext)!=null)
			return true;
		return false;
	}

}
