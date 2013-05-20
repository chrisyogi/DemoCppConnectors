package com.ericsson.ecut.collector.cpp.helper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;

public class CPPUtil {
	public static long parseUploadStats(String printout){
		StringTokenizer st = new StringTokenizer(printout, "\n\r");
		String line;
		while(st.hasMoreTokens()){
			if((line = st.nextToken()).contains("UPLOAD terminations busy")){
				String[] results = line.split(" ");
				if(results.length == 4)
					try{
						return Long.parseLong(results[3].trim());
					}catch(Exception e){}
				}
		}
		return 0;
	}
	
	
	public static InputStream getFileStream(String fileName){	    
	    try {
		    File file = new File(fileName);
		    return new BufferedInputStream(new FileInputStream(file));
	    } catch (FileNotFoundException e) {
	      e.printStackTrace();
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
		return null;
	}
	
	public static String getFile(String fileName){
	    File file = new File(fileName);
	    FileInputStream fis = null;
	    BufferedInputStream bis = null;
	    StringBuilder sb = new StringBuilder();
	    
	    try {
	      fis = new FileInputStream(file);

	      // Here BufferedInputStream is added for fast reading.
	      bis = new BufferedInputStream(fis);

	      // dis.available() returns 0 if the file does not have more lines.
	      while (bis.available() != 0) {

	      // this statement reads the line from the file and print it to
	        // the console.
	        //System.out.println((char)bis.read());
	    	  sb.append((char)bis.read());
	      }

	      // dispose all the resources after using them.
	      fis.close();
	      bis.close();
	    } catch (FileNotFoundException e) {
	      e.printStackTrace();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	    return sb.toString();
	}
}
