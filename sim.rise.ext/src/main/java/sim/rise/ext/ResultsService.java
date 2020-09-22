package sim.rise.ext;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
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

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Component;

import sim.rise.ext.util.ConnectionFactory;
import sim.rise.ext.util.DbUtil;

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
		 Connection connection = ConnectionFactory.getConnection();
		
		 ResultSet results = null;
	        try {
	        	
	            connection = ConnectionFactory.getConnection();
	            String sql = "SELECT id, authorname, modelname, datecreated, description, inputfiles, outputfiles, debugfiles, simulator\n" + 
	            		"FROM public.framework where  modelname = ? and simulator= ? and authorname = ?";

						
						PreparedStatement pstmt = connection.prepareStatement(sql);
			
						pstmt.setString(1, framework);
			            pstmt.setString(2, servicetype);
			            pstmt.setString(3, username);

	            results = pstmt.executeQuery();
	           
	            while (results.next())
	            {	System.out.println( results.getInt(1) );
	            	System.out.println( results.getString(2) );
	            	
	            	InputStream input = new ByteArrayInputStream(results.getBytes(6) );
	            	  List<ZipEntry> entries = new ArrayList<ZipEntry>();
	            	  ZipInputStream zi = null;
	            	    try {
	            	        zi = new ZipInputStream(new ByteArrayInputStream(results.getBytes(6)));
	            	      
	            	        ZipEntry zipEntry = null;
	            	        while ((zipEntry = zi.getNextEntry()) != null) {
	            	            entries.add(zipEntry);
	            	        }
	            	    } finally {
	            	        if (zi != null) {
	            	            zi.close();
	            	        }
	            	    }
	            	   // return entries;
	    			System.out.println(input);
	            	System.out.println( results.getString(3) );
	            	System.out.println( results.getString(4) );
	            	System.out.println( results.getString(5)  );

	            	System.out.println( results.getString(7) );
	            	System.out.println( results.getString(8) );
	            	System.out.println( results.getString(9) );
	            }
	            
	        } catch (SQLException e) {
	            System.out.println("SQLException in get() method");
	            e.printStackTrace();
	        } finally {
	            DbUtil.close(connection);
	        }

	
return null;
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