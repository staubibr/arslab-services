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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.github.underscore.lodash.U;

import sim.rise.ext.util.ConnectionFactory;
import sim.rise.ext.util.DbUtil;

@Component
public class FrameworkService {
		
	public JSONObject getFramework(String username, String servicetype, String framework) throws IOException {
		 String URL =  "http://vs1.sce.carleton.ca:8080/cdpp/sim/workspaces" ;
		 	URL = URL.concat("/");
			URL = URL.concat(username);
			URL = URL.concat("/");
			URL = URL.concat(servicetype);
			URL = URL.concat("/");
			URL = URL.concat(framework);
			URL = URL.concat("?fmt=xml");

			 URL obj = new URL(URL);
			
				HttpURLConnection con = (HttpURLConnection) obj.openConnection();
				con.setRequestMethod("GET");

				int responseCode = con.getResponseCode();
				System.out.println("GET Response Code :: " + con.getResponseCode());
				if (responseCode == HttpURLConnection.HTTP_OK) { // success
					BufferedReader in = new BufferedReader(new InputStreamReader(
							con.getInputStream()));
					String inputLine;
					StringBuffer response = new StringBuffer();
					
					while ((inputLine = in.readLine()) != null) {
						response.append(inputLine);
					}
					in.close();
					
					JSONObject soapDatainJsonObject = XML.toJSONObject(response.toString());
		
			//		System.out.println(response.toString());
					return soapDatainJsonObject;
				} else {
				
					JSONObject JsonObject = XML.toJSONObject("GET request not worked");
					return JsonObject;
				}
	
	}

	public int frameworkPOST(String username, String servicetype, String framework,String zdirvar, String oldPassword, MultipartFile file, String configframework)
			 throws IOException
	{     
		
		 JSONObject configuration = new JSONObject(configframework.toLowerCase());
		 byte [] byteArr=file.getBytes();

   	  List<ZipEntry> entries = new ArrayList<ZipEntry>();
   	  ZipInputStream zi = null;
   	    try {
   	        zi = new ZipInputStream(new ByteArrayInputStream(byteArr));
   	      
   	        ZipEntry zipEntry = null;
   	        while ((zipEntry = zi.getNextEntry()) != null) {
   	            entries.add(zipEntry);
   	        }
   	    } finally {
   	        if (zi != null) {
   	            zi.close();
   	        }
   	    }
   	 String[] strArray =  new String[20] ;
   	String[] fileName = new String[20] ;
   	 for (int i = 0; i < entries.size(); i++) {
   		strArray[i] = entries.get(i).toString();
   		String segments[] = strArray[i].split("/");
   		fileName[i] = segments[segments.length - 1];
   	
   	 }
  	JSONArray ja = new JSONArray();
 	 for (int i = 0; i < fileName.length; i++) {
    	if(fileName[i] != null) {
    		String ext1 = FilenameUtils.getExtension(fileName[i]);
    		JSONObject jo = new JSONObject();
    	
    		if(ext1.equals("ma"))
    		{
    		
    			jo.put("-ftype", "ma");
    			jo.put("#text", fileName[i]);
    		
    		}
    		else if(ext1.equals("val"))
    		{
    			jo.put("-ftype", "val");
    			jo.put("#text", fileName[i]);
    		}
    		else if(ext1.equals("ev"))
    		{
    			jo.put("-ftype", "ev");
    			jo.put("#text", fileName[i]);
    		}
    		else if(ext1.equals("pal"))
    		{
    			jo.put("-ftype", "pal");
    			jo.put("#text", fileName[i]);
    		}
    		else if(ext1.equals("cpp"))
    		{
    			jo.put("-ftype", "src");
    			jo.put("-class", FilenameUtils.getBaseName(fileName[i]));
    			jo.put("#text", fileName[i]);
    		}
    		else if(ext1.equals("h"))
    		{
    			jo.put("-ftype", "hdr");
    			jo.put("-class",  FilenameUtils.getBaseName(fileName[i]));
    			jo.put("#text", fileName[i]);
    		}
 
    		
    		if(jo.isEmpty())
    		 continue;
    		else
    			ja.put(jo);
    	}

    	 }


	JSONObject fileObj = new JSONObject();
	JSONObject jsonObject = new JSONObject();
	JSONObject serverObj = new JSONObject();
	JSONObject serverObj2 = new JSONObject();
	JSONObject serverObj3 = new JSONObject();
	
	JSONObject json = new JSONObject();
	fileObj.put("File", ja);
	serverObj.put("Servers", serverObj2);
	serverObj2.put("Server", serverObj3);
	serverObj3.put("-IP", "localhost");
	serverObj3.put("-PORT", "8080");
	Iterator<String> keys = configuration.keys();

	while(keys.hasNext()) {
	    String key = keys.next();
	   if(key.toLowerCase().equals("zone"))
	   {
		   serverObj3.put("Zone", configuration.get("zone"));
	   }
	   else if(key.toLowerCase().equals("model"))
	   {
		   serverObj3.put("MODEL", configuration.get("model"));
	   }
	
	}
	//serverObj2.put("Server", serverObj3);
	
	jsonObject.put("Doc",configuration.get("description") );
	jsonObject.put("Files", fileObj );
	jsonObject.put(servicetype,serverObj );
	
	json.put("ConfigFramework", jsonObject);
		
		
		int responsePUT = updateFramework( username,  servicetype,  framework,  oldPassword,  json.toString() , configuration);
		int responsePOST = 0;
		if(responsePUT == 200 || responsePUT == 201)
		{
		 responsePOST = postFiles( username,  servicetype,  framework,zdirvar,  oldPassword,  file);
				}
		if((responsePUT == 200 || responsePUT == 201) && responsePOST == 200 )
		return 200;
		else
			return -1;
	}

	

	private int postFiles(String username, String servicetype, String framework, String zdirvar, String oldPassword,
			MultipartFile file) throws IOException {
		String URL =  "http://vs1.sce.carleton.ca:8080/cdpp/sim/workspaces" ;

		URL = URL.concat("/");
		URL = URL.concat(username);
		URL = URL.concat("/");
		URL = URL.concat(servicetype);
		URL = URL.concat("/");
		URL = URL.concat(framework);
		URL = URL.concat("?zdir=");
		URL = URL.concat(zdirvar);
		ResponseEntity<String> response = null;
		String message = "Files sent!!";
		
	    try {	
	    	// AUTHORIZATION
	    	String auth = username + ":" + oldPassword;
	    	byte[] auth64 = Base64.encodeBase64(auth.getBytes(StandardCharsets.UTF_8));
	    	// HEADERS PREPARATION
	        HttpHeaders headers = new HttpHeaders();
	        headers.add("Content-Type", "application/zip");
	        headers.add("Authorization", "Basic " + new String(auth64, "UTF-8"));
	    	// BODY PREPARATION
	        Resource fileResource = file.getResource();
			LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
			map.add("file", fileResource);
			// SEND REQUEST
	        RestTemplate restTemplate = new RestTemplate();
	        HttpEntity<byte[]> requestEntity = new HttpEntity<byte[]>(file.getBytes(), headers);
	        response = restTemplate.postForEntity(URL, requestEntity, String.class);
	    } 
	    catch (HttpStatusCodeException e) {
	        message = e.getResponseBodyAsString();
	    } 
	    catch (Exception e) {
	        message = e.getMessage();
	    }
	    if( response.getStatusCodeValue()==200)
	    { Connection connection = ConnectionFactory.getConnection();
	    	try{
	    		
	    		InputStream initialStream = file.getInputStream();
	 
				String sql = "UPDATE framework SET  inputfiles = ? where modelname = ? and simulator= ? and authorname = ?";
				
				PreparedStatement pstmt = connection.prepareStatement(sql);
				pstmt.setBinaryStream(1, initialStream);;
				pstmt.setString(2, framework);
	            pstmt.setString(3, servicetype);
	            pstmt.setString(4, username);
	            pstmt.executeUpdate();
			
			}
	    	catch (SQLException e) {
	            System.out.println("SQLException in get() method");
	            e.printStackTrace();
	        } finally {
	            DbUtil.close(connection);
	        }
				
	    }
	    System.out.println(message);
		return response.getStatusCodeValue();
		
	}

	public int deleteFramework(String username, String servicetype, String framework, String oldPassword) 
			 throws IOException
	{
		 String URL =  "http://vs1.sce.carleton.ca:8080/cdpp/sim/workspaces" ;

			URL = URL.concat("/");
			URL = URL.concat(username);
			URL = URL.concat("/");
			URL = URL.concat(servicetype);
			URL = URL.concat("/");
			URL = URL.concat(framework);
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

	public int updateFramework(String username, String servicetype, String framework, String oldPassword, String json, JSONObject configuration) 
			 throws IOException
	{ 	
		 String URL =  "http://vs1.sce.carleton.ca:8080/cdpp/sim/workspaces" ;

		  String xml = U.jsonToXml(json.toString());
	
		URL = URL.concat("/");
		URL = URL.concat(username);
		URL = URL.concat("/");
		URL = URL.concat(servicetype);
		URL = URL.concat("/");
		URL = URL.concat(framework);
	
		
		
		URL obj = new URL(URL);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("PUT");

		con.setRequestProperty("Content-Type", "text/xml");
		  String auth = username + ":" + oldPassword;
		  byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.UTF_8));
		  String authValue = "Basic " + new String(encodedAuth);

		con.setRequestProperty("Authorization", authValue);
		
		con.setDoOutput(true);
		OutputStream outStream = con.getOutputStream();
		OutputStreamWriter outStreamWriter = new OutputStreamWriter(outStream, "UTF-8");
	
		outStreamWriter.write(xml);
		outStreamWriter.flush();
		outStreamWriter.close();
		outStream.close();

		

		int responseCode = con.getResponseCode();
		System.out.println("PUT Response Code :: " + responseCode);

		if (responseCode == 200)//update
		{ Connection connection = ConnectionFactory.getConnection();
			try{
				String sql = "UPDATE framework SET  description = ? where modelname = ? and simulator= ? and authorname = ?";
				
				PreparedStatement pstmt = connection.prepareStatement(sql);
				pstmt.setString(1,  (String) configuration.get("description"));
				pstmt.setString(2, framework);
	            pstmt.setString(3, servicetype);
	            pstmt.setString(4, username);
	           
	            pstmt.executeUpdate();
			
			}
			catch (SQLException e) {
	            System.out.println("SQLException in get() method");
	            e.printStackTrace();
	        } finally {
	            DbUtil.close(connection);
	        }
				
		}
		else if (responseCode == 201)//create
		{ Connection connection = ConnectionFactory.getConnection();
			try{
				String sql = "INSERT INTO public.framework" + 
						"(modelname, simulator, authorname, datecreated, description)" + 
						"VALUES(?,?,?,?,?);\n" ;
				java.sql.Timestamp date = new java.sql.Timestamp(new java.util.Date().getTime());
				PreparedStatement pstmt = connection.prepareStatement(sql);
	            pstmt.setString(1, framework);
	            pstmt.setString(2, servicetype);
	            pstmt.setString(3, username);
	            pstmt.setTimestamp(4, date);
	            pstmt.setString(5, (String) configuration.get("description"));
	            

	            pstmt.executeUpdate();
			
			}
			catch (SQLException e) {
	            System.out.println("SQLException in get() method");
	            e.printStackTrace();
	        } finally {
	            DbUtil.close(connection);
	        }
			
		}
		if (responseCode == HttpURLConnection.HTTP_OK) { //success
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

//			System.out.println(response.toString());
		} else {
			System.out.println("PUT request not worked");
		}
		return responseCode;
	}

	
}
