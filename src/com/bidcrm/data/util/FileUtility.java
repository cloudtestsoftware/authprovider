package com.bidcrm.data.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class FileUtility {
	static Log logger = LogFactory.getLog(FileUtility.class);
	private  FileWriter m_fileWriter = null;
	private  BufferedWriter m_bufWriter = null;
	private boolean m_bbuffered = false;

	//protected static FileReader m_filDataInput ;
	//protected  static BufferedReader m_bufDataInput ;
	
	public void writeToFile(String strmesg)
	{
		try
		{
			if(m_bufWriter != null)
			{
				m_bufWriter.write(strmesg);
				m_bufWriter.newLine();
			}
			else if (m_fileWriter != null)
			{
				m_fileWriter.write(strmesg);
				m_fileWriter.write("\n");

			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception message is : "+ e.getMessage());
		}

	}
	// Flushes the contents in a buffer to a file.

		public void flush()
		{
			if (m_bufWriter == null)
				return;
			try
			{
				m_bufWriter.flush();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				System.out.println("Exception message is : "+ e.getMessage());
			}
		}


		// Closes the file

		public void closeFile()
		{
			try
			{
				// Checks if the file is closed or not
				// before closing.


				if (m_bufWriter != null)
				{
					m_bufWriter.close();
				}
				if (m_fileWriter != null)
				{
					m_fileWriter.close();
				}
				m_fileWriter = null;
				m_bufWriter = null;
			}
			catch(Exception e)
			{
				e.printStackTrace();
				System.out.println("Exception message is : "+ e.getMessage());
			}
		}
	public static void  writeToFile(String path, String text)
	  {
	  try{
		  // Create file 
		  FileWriter fstream = new FileWriter(path);
		  BufferedWriter out = new BufferedWriter(fstream);
		  out.write(text);
		  //Close the output stream
		  out.close();
		  }catch (Exception e){//Catch exception if any
		  System.err.println("Error: " + e.getMessage());
		  }
	  }
	public static boolean createDir(String path){
		File f = new File(path);
		boolean success=false;
		if(!f.isFile()&&!f.exists() ){			
			f.mkdirs();
			success=true;
		}
		return(success);
		  
	}
	public static BufferedReader readInputFile(String filepath){
		BufferedReader buffer=null;
		try{
			// open data input file for reading and result file for writing
			FileReader reader = new FileReader(filepath);
			buffer = new BufferedReader(reader);
			buffer.mark(50000);

		} catch (IOException e){
			e.printStackTrace();
			logger.error("IOException encountered: " + e.getMessage() + "\n");
		} catch (Exception e){
			e.printStackTrace();
			logger.error("Exception encountered: " + e.getMessage() + "\n");
		}
		return(buffer);
	}

	
	public static String readFileContent(String filepath){
		
		BufferedReader br = null;
		String output="";
		try {
 
			String sCurrentLine;
 
			br = new BufferedReader(new FileReader(filepath));
 
			while ((sCurrentLine = br.readLine()) != null) {
				output+="\n"+sCurrentLine;
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return output;
 
	}

	
    private boolean isMatchedFile(File file,String fileExtension){
        if(file.exists()&&file.isFile()&&(file.getName().endsWith(fileExtension)||file.getName().contains(fileExtension))){
            return true;
        }else{
            return false;
        }
    }
    
   
    private boolean isMatchedDir(File file,String dirname){
        if(file.isDirectory()&&file.getAbsolutePath().contains(dirname)){
            return true;
        }else{
            return false;
        }
    }
	public static void deleteFile(String file){
		
		 try{
			  File f1 = new File(file);
			  boolean success = f1.delete();
			  if (!success){
				  logger.info("Failed to delete file at: "+file );
				
			  }else{
				  logger.info("Delete file at: "+file );
				
			    }
		 }catch(Exception e){
			 logger.info("Failed to delete file at: "+file );
		 }
		  
	}
	public static void createTextFile(String absolute_path, String text){
		
    	logger.info("Creating text file at: "+absolute_path );
    	try {
    		File target= new File(absolute_path);
    		boolean issuccess=false;
    		if(target.exists()){
    			logger.info("Deleting target File="+absolute_path);
    			issuccess=target.delete();
    		}
    		
			FileWriter file= new FileWriter(new File(absolute_path));
			file.write(text);			
			file.close();
		 logger.info("Saved text file at: "+absolute_path );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.info("Problem Occured while saving the HTML Report file html_report.html for "+absolute_path +" "+e.getMessage() );
		}
    	
    }
	
	public static void renameFile( String oldFileName, String newFileName) {
		try{
			  File oldName = new File(oldFileName);
		      File newName = new File(newFileName);
		      if(oldName.renameTo(newName)) {
		    	  logger.info("Renamed file "+oldFileName+" to "+newFileName );	         
		      } else {
		    	  logger.info("Failed to renamed file "+oldFileName+" to "+newFileName );  
		      }
		}catch(Exception e){
			logger.info("Could not found file "+oldFileName );	 
		}
	   }
	
	 public static void copyFile(String srFile, String dtFile){
		  try{
			  File f1 = new File(srFile);
			  File f2 = new File(dtFile);
			  InputStream in = new FileInputStream(f1);
			  OutputStream out = new FileOutputStream(f2);
		
			  byte[] buf = new byte[1024];
			  int len;
			  while ((len = in.read(buf)) > 0){
				  out.write(buf, 0, len);
			  }
			  in.close();
			  out.close();
			  
		  }catch(FileNotFoundException ex){
			  logger.error(ex.getMessage() + " in the specified directory." );	        
			 
		  }
		  catch(IOException e){
			  logger.error(e.getMessage());	        
			
		  }
	  }
		
	 // parses the input file and puts the key-value pairs in the vector.
	//  Also, resets the bufreader to the point marked before.
	 	public Vector parseInputFile(BufferedReader bufReader, String strHeader) throws IOException
		{
			int i=0;
			strHeader =  "[" + strHeader + "]";
			Vector vBuf = new Vector();
			String str = bufReader.readLine();
			while(str != null)
			{
				if(str.equalsIgnoreCase(strHeader))
				{
					while(!str.equalsIgnoreCase("[end]"))
					{
					      vBuf.addElement(str);
	                                      str = bufReader.readLine();
					}
	                                bufReader.reset();
					return vBuf;
				}
				str = bufReader.readLine();
			}
	                bufReader.reset();
			return vBuf;
		}
	  // parses the input file and puts the key-value pairs in the vector.
	//  Also, resets the bufreader to the point marked before.
	 	public Vector parseInputXmlFile(BufferedReader bufReader, String strHeader) throws IOException
		{
			int i=0;
	                String endTag="</"+strHeader+">";
			strHeader =  "<" + strHeader + ">";
			Vector vBuf = new Vector();
			String str = bufReader.readLine();
			while(str != null)
			{
				if(str.equalsIgnoreCase(strHeader))
				{
					while(!str.equalsIgnoreCase(endTag))
					{
					      vBuf.addElement(str);
	                                      str = bufReader.readLine();
					}
	                                bufReader.reset();
					return vBuf;
				}
				str = bufReader.readLine();
			}
	                bufReader.reset();
			return vBuf;
		}
	 // Parses the vector for the value, given the key.
		public String parseInputValue(Vector vBuf, String sKey)
		{
			for (int i=0; i<vBuf.size(); i++)
			{
				String strElm = new String();
				strElm = (String) vBuf.elementAt(i);
				if(strElm.toUpperCase().startsWith(sKey.toUpperCase()))
				{
					int idx = strElm.indexOf("=");
					strElm = strElm.substring(idx+1);
					return strElm;
				}
			}
			return null;
		}
	
		public void openNewFile(String filepath,String filename,boolean isBuffered){

			try{
				// open log file for writing
				m_bbuffered = isBuffered;
				File m_file = new File(filepath);
				if(!m_file.isDirectory())
					m_file.mkdirs();
				m_fileWriter = new FileWriter(filepath+File.separator+filename);
				if (isBuffered) {
					m_bufWriter = new BufferedWriter(m_fileWriter);
				}


			} catch (IOException e){
				e.printStackTrace();
			} catch (Exception e){
				e.printStackTrace();
			}

		}
		public void openFile(String filepath,String filename,boolean isBuffered){

			try{
				// open log file for writing
				m_bbuffered = isBuffered;

				m_fileWriter = new FileWriter(filepath+ File.separator+filename);
				if (isBuffered) {
					m_bufWriter = new BufferedWriter(m_fileWriter);
				}


			} catch (IOException e){
				e.printStackTrace();
			} catch (Exception e){
				e.printStackTrace();
			}

		}
		public void  openFile(String filename,boolean isBuffered){

			try{
				// open log file for writing
				m_bbuffered = isBuffered;
				m_fileWriter = new FileWriter(filename);
				if (isBuffered) {
					m_bufWriter = new BufferedWriter(m_fileWriter);
				}

			} catch (IOException e){
				e.printStackTrace();
			} catch (Exception e){
				e.printStackTrace();
			}

		}
		
		 public static String htmlEscape( String s ) {
		        s = s.replaceAll("&", "&amp;");
		        s = s.replaceAll("\\\"", "&quot;");
		        s = s.replaceAll("\\\'", "&apos;");
		        //s = s.replaceAll("<", "&lt;");
		        //s = s.replaceAll(">", "&gt;");
		        return s;
		    }
        
		 public static boolean verifyDirectory(String path){
			 File m_file = new File(path);
				if(!m_file.isDirectory()){
					m_file.mkdirs();
				}
				return true;
		 }

}
