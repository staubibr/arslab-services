package services;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.springframework.mock.web.MockMultipartFile;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import components.CustomException;
import components.FilesMap;
import components.Utilities;
import models.Parsed;
import parsers.IParser;
import util.ConnectionFactory;
import util.DbUtil;

@Component
public class ResultsService {
	
	
	
	/*
	 * Simulation Results -  Get & Delete
	 */
	public InputStream getSimResults(String username, String servicetype, String framework)  throws IOException {
		 String URL =  "http://vs1.sce.carleton.ca:8080/cdpp/sim/workspaces" ;
		 	URL = URL.concat("/");
			URL = URL.concat(username);
			URL = URL.concat("/");
			URL = URL.concat(servicetype);
			URL = URL.concat("/");
			URL = URL.concat(framework);
			URL = URL.concat("/results");
			 URL obj = new URL(URL);
				HttpURLConnection con = (HttpURLConnection) obj.openConnection();
				con.setRequestMethod("GET");
				con.setRequestProperty("Content-Type", "application/zip");
				int responseCode = con.getResponseCode();
				System.out.println("GET Response Code :: " + con.getResponseCode());
				if (responseCode == HttpURLConnection.HTTP_OK) { // success
				
					//	InputStreamReader filestream = new InputStreamReader(con.getInputStream());
					InputStream is = con.getInputStream();
//					
//					try{
//			    		
//						String sql = "UPDATE framework SET  outputfiles = ? where frameworkname = ? and servicename= ? and workspacename = ?";
//						
//						PreparedStatement pstmt = connection.prepareStatement(sql);
//						pstmt.setBinaryStream(1, is);;
//						pstmt.setString(2, framework);
//			            pstmt.setString(3, servicetype);
//			            pstmt.setString(4, username);
//
//			            pstmt.executeUpdate();
//					
//					}
//					catch (SQLException e) {
//			            System.out.println("SQLException in get() method");
//			            e.printStackTrace();
//			        } finally {
//			            DbUtil.close(connection);
//			        }
//					
					return is;
				} else {
				
					
					return null;
				}
	
	}

	public int deleteSimResults(String username, String servicetype, String framework, String oldPassword) throws IOException {
		 String URL =  "http://vs1.sce.carleton.ca:8080/cdpp/sim/workspaces" ;

			URL = URL.concat("/");
			URL = URL.concat(username);
			URL = URL.concat("/");
			URL = URL.concat(servicetype);
			URL = URL.concat("/");
			URL = URL.concat(framework);
			URL = URL.concat("/results");
			URL obj = new URL(URL);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("DELETE");
			//con.setRequestProperty("Accept", "application/xml");
			con.setRequestProperty("Content-Type", "text/xml");
			 String auth = username + ":" + oldPassword;
			  byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.UTF_8));
			  String authValue = "Basic " + new String(encodedAuth);

			con.setRequestProperty("Authorization", authValue);
			// For POST only - START
			con.setDoOutput(true);
			OutputStream outStream = con.getOutputStream();
			OutputStreamWriter outStreamWriter = new OutputStreamWriter(outStream, "UTF-8");
			outStreamWriter.flush();
			outStreamWriter.close();
			outStream.close();
			// For POST only - END

			int responseCode = con.getResponseCode();
			System.out.println("DELETE Response Code :: " + responseCode);

			if (responseCode == HttpURLConnection.HTTP_OK) { //success
				BufferedReader in = new BufferedReader(new InputStreamReader(
						con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				// print result
			//	System.out.println(response.toString());
			
			} else {
				System.out.println("DELETE request not worked");
			}
			return responseCode;

	}
	/*
	 * Simulation Debug - Get & Delete 
	 */
	
	public InputStream getDebugResults(String username, String servicetype, String framework)  throws IOException {
		 String URL =  "http://vs1.sce.carleton.ca:8080/cdpp/sim/workspaces" ;
		 	URL = URL.concat("/");
			URL = URL.concat(username);
			URL = URL.concat("/");
			URL = URL.concat(servicetype);
			URL = URL.concat("/");
			URL = URL.concat(framework);
			URL = URL.concat("/debug");

			 URL obj = new URL(URL);
				HttpURLConnection con = (HttpURLConnection) obj.openConnection();
				con.setRequestMethod("GET");
				con.setRequestProperty("Content-Type", "application/zip");
				int responseCode = con.getResponseCode();
				System.out.println("GET Response Code :: " + con.getResponseCode());
				if (responseCode == HttpURLConnection.HTTP_OK) { // success
					
				//	InputStreamReader filestream = new InputStreamReader(con.getInputStream());
					InputStream is = con.getInputStream();
//					
//					try{
//			    		
//						String sql = "UPDATE framework SET  debugfiles = ? where frameworkname = ? and servicename= ? and workspacename = ?";
//						
//						PreparedStatement pstmt = connection.prepareStatement(sql);
//						pstmt.setBinaryStream(1, is);;
//						pstmt.setString(2, framework);
//			            pstmt.setString(3, servicetype);
//			            pstmt.setString(4, username);
//			            pstmt.executeUpdate();
//					
//					}
//					catch (SQLException e) {
//			            System.out.println("SQLException in get() method");
//			            e.printStackTrace();
//			        } finally {
//			            DbUtil.close(connection);
//			        }
//					
					return is;
				} else {
				
					
					return null;
				}
	
	}

	public int deleteDebugResults(String username, String servicetype, String framework, String oldPassword) throws IOException {
		 String URL =  "http://vs1.sce.carleton.ca:8080/cdpp/sim/workspaces" ;

			URL = URL.concat("/");
			URL = URL.concat(username);
			URL = URL.concat("/");
			URL = URL.concat(servicetype);
			URL = URL.concat("/");
			URL = URL.concat(framework);
			URL = URL.concat("/debug");
			URL obj = new URL(URL);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("DELETE");
			//con.setRequestProperty("Accept", "application/xml");
			con.setRequestProperty("Content-Type", "text/xml");
			 String auth = username + ":" + oldPassword;
			  byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.UTF_8));
			  String authValue = "Basic " + new String(encodedAuth);

			con.setRequestProperty("Authorization", authValue);
			// For POST only - START
			con.setDoOutput(true);
			OutputStream outStream = con.getOutputStream();
			OutputStreamWriter outStreamWriter = new OutputStreamWriter(outStream, "UTF-8");
			outStreamWriter.flush();
			outStreamWriter.close();
			outStream.close();
			// For POST only - END

			int responseCode = con.getResponseCode();
			System.out.println("DELETE Response Code :: " + responseCode);

			if (responseCode == HttpURLConnection.HTTP_OK) { //success
				BufferedReader in = new BufferedReader(new InputStreamReader(
						con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				// print result
			//	System.out.println(response.toString());
			
			} else {
				System.out.println("DELETE request not worked");
			}
			return responseCode;

	}

	public String getAllInfo(String username, String servicetype, String framework) throws IOException {
		List<File> simFiles = new ArrayList<>(); 
		List<MultipartFile> simMultipartFiles = new ArrayList<>(); 
		Connection connection = ConnectionFactory.getConnection();
		// InputStream debug = getDebugResults(username,  servicetype,  framework);
		 InputStream result = getSimResults(username,  servicetype,  framework);
		 ZipInputStream result_stream = null;
	
		try {
		
		  result_stream =  new ZipInputStream(result);
	
 	        ZipEntry zipEntry1 = null;
 	       while ((zipEntry1 = result_stream.getNextEntry()) != null) {
 	    	  String fileName = zipEntry1.getName();
 	    	 System.out.println(zipEntry1.getSize());
 	    	    File newFile = new File(fileName);
	    	    if (zipEntry1.isDirectory()) {
 	    	   // 	System.out.println("isDirectory  " + newFile);
 	    	        newFile.mkdirs();
 	    	    } else {
 	    	    	
 	    	    	simFiles.add( newFile);
 	    	    }
 	            
 	       }
 	      result_stream.close();
 	     
 	        
	} catch (IOException ec) {
        System.out.println("Error while extract the zip: "+ec);
    }
	
		 ResultSet dbresults = null;
	        try {
	        	
	            connection = ConnectionFactory.getConnection();
	            String sql = "SELECT id, authorname, modelname, datecreated, description, inputfiles, outputfiles, debugfiles, simulator\n" + 
	            		"FROM public.framework where  modelname = ? and simulator= ? and authorname = ?";

						
						PreparedStatement pstmt = connection.prepareStatement(sql);
			
						pstmt.setString(1, framework);
			            pstmt.setString(2, servicetype);
			            pstmt.setString(3, username);

			            dbresults = pstmt.executeQuery();
	           
	            while (dbresults.next())
	            {	
	            	
	         
	            	  ZipInputStream zi = null;
	            	    try {
	            	        zi = new ZipInputStream(new ByteArrayInputStream(dbresults.getBytes(6)));
	            	      
	            	        ZipEntry zipEntry = null;
	            	        while ((zipEntry = zi.getNextEntry()) != null) {
	            	        	 String fileName = zipEntry.getName();
	            	 	    	    File inputFile = new File(  File.separator + fileName);

	            	 	    	    if (zipEntry.isDirectory()) {
	            	 	    	    	System.out.println("isDirectory  " + inputFile);
	            	 	    	    
	            	 	    	    } else {
	            	 	    	    	
	            	 	    	    	simFiles.add( inputFile);
	            	 	    	    }
	             	        
	            	        }
	            	        zi.close();
	             	        
		   	            	  
	            	    } 
	            	    catch (IOException e) {
	            	        System.out.println("Error while extract the zip: "+e);
	            	    }
	            	    finally {
	            	        if (zi != null) {
	            	            zi.close();
	            	        }
	            	    } 
	            	    for(int i = 0; i < simFiles.size(); i++) 
	                	{ 
//         	 	    	   FileInputStream fileStream = new FileInputStream(simFiles.get(i));
//        	 	    	    MultipartFile multipartFile = new MockMultipartFile( simFiles.get(i).getName(), fileStream);
//        	 	    	   
//        	 	    	    
//	            	    	simMultipartFiles.add( multipartFile);
//	                		System.out.println(simMultipartFiles.get(i)); 
	                		System.out.println(simFiles.get(i)); 
	                	} 
	            //	    postFiles( simFiles);
	            	    
//	            	System.out.println( dbresults.getInt(1) );
//		            System.out.println( dbresults.getString(2) );
//           		System.out.println( dbresults.getString(3) );
//	            	System.out.println( dbresults.getString(4) );
//	            	System.out.println( dbresults.getString(5)  );
//
//	            	System.out.println( dbresults.getString(7) );
//	            	System.out.println( dbresults.getString(8) );
//	            	System.out.println( dbresults.getString(9) );
	            }
	            
	        } catch (SQLException e) {
	            System.out.println("SQLException in get() method");
	            e.printStackTrace();
	        } finally {
	            DbUtil.close(connection);
	        }

	
return null;
}

	private void postFiles(List<MultipartFile> simFiles ) throws IOException {
		try {
			FilesMap map = Utilities.Convert(simFiles);
			IParser parser = new parsers.auto.Auto();
			Parsed result = parser.Parse(map);
			
			map.Close();
			
			ResponseEntity<byte[]> ax= Utilities.ByteArrayResponse(result.name, result.toZipByteArray());
			System.out.println(ax);
		} 
		catch (Exception e) {
		  	throw new CustomException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
	
	
	public String getDebugResultsURL(String username, String servicetype, String framework) {
		 String URL =  "http://vs1.sce.carleton.ca:8080/cdpp/sim/workspaces" ;
		URL = URL.concat("/");
		URL = URL.concat(username);
		URL = URL.concat("/");
		URL = URL.concat(servicetype);
		URL = URL.concat("/");
		URL = URL.concat(framework);
		URL = URL.concat("/debug");
		return URL;
	}

	public String getSimResultsURL(String username, String servicetype, String framework) {
		 String URL =  "http://vs1.sce.carleton.ca:8080/cdpp/sim/workspaces" ;
		 	URL = URL.concat("/");
			URL = URL.concat(username);
			URL = URL.concat("/");
			URL = URL.concat(servicetype);
			URL = URL.concat("/");
			URL = URL.concat(framework);
			URL = URL.concat("/results");
		return URL;
	}
}