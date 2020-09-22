package sim.rise.ext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.stereotype.Component;
@Component 
public class WorkspaceService {
	
	
	public JSONObject getAllWorkspaces() throws IOException {
		 String URL =  "http://vs1.sce.carleton.ca:8080/cdpp/sim/workspaces" ;

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

	public JSONObject getWorkspace(String username) throws IOException {
		String URL =  "http://vs1.sce.carleton.ca:8080/cdpp/sim/workspaces" ;

		URL = URL.concat("/");
		URL = URL.concat(username);
		URL = URL.concat("?fmt=xml");
		
		 URL obj = new URL(URL);
			
		 HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
		 conn.setRequestMethod("GET");

			
			int responseCode = conn.getResponseCode();
			System.out.println("GET Response Code :: " + conn.getResponseCode());
			if (responseCode == HttpURLConnection.HTTP_OK) { // success
				BufferedReader in = new BufferedReader(new InputStreamReader(
						conn.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
				
				
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				
				JSONObject soapDatainJsonObject = XML.toJSONObject(response.toString());
		
			//	System.out.println(response.toString());
				return soapDatainJsonObject;
			} else {
			//	System.out.println("GET request not worked");
				JSONObject JsonObject = XML.toJSONObject("GET request not worked");
				return JsonObject;
			}
	}


	
	
	public  int updateWorkspace(String username,String oldPassword, String accountProperties) throws IOException {
	 String URL =  "http://vs1.sce.carleton.ca:8080/cdpp/sim/workspaces" ;

		 JSONObject json = new JSONObject(accountProperties.toString());
		  String xml = XML.toString(json);
	
		URL = URL.concat("/");
		URL = URL.concat(username);
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
		//String requestedXml= "<Account><Password>AAAA</Password><Admin>false</Admin></Account>";
		outStreamWriter.write(xml);
		outStreamWriter.flush();
		outStreamWriter.close();
		outStream.close();

		

		int responseCode = con.getResponseCode();
		System.out.println("PUT Response Code :: " + responseCode);

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
	public  int deleteWorkspace(String username, String oldPassword) throws IOException {
	 String URL =  "http://vs1.sce.carleton.ca:8080/cdpp/sim/workspaces" ;

		URL = URL.concat("/");
		URL = URL.concat(username);
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


}
