package com.sap.jaxrs.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import com.sap.jaxrs.model.MessageData;
import com.sap.jaxrs.model.RegistrationData;

@Path("/WASRestServer")
public class WASRestServer {

	private final String API_KEY = "AIzaSyDlE-xdhOvodsRe-0U_FCTZ1lmSUDunZpE";
	
	@Path("/isAlive")
	@Produces(MediaType.TEXT_HTML)
	@GET
	public String getHello() {
		return "Server is up and running!";
	}
	
	@Path("/list")
	@Produces(MediaType.TEXT_HTML)
	@GET
	public String listRegisteredUsers() {
		String result = "Registered users\n\n";
		Properties props = getPropFile();
        for (Object key: props.keySet()) {
          	result += key + ":" + props.getProperty((String) key) + "\n";
        }
		return result;
	}

	@POST
	@Path("/broadcast")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_HTML)
	public String broadcast(MessageData md) {
		 JSONObject jGcmData = new JSONObject();
         JSONObject jData = new JSONObject();
         String resp="";
         jData.put("title", "Your message");
         jData.put("message", md.getMessage());

         Properties props = getPropFile();
         jGcmData.put("to", props.getProperty(md.getChannel()));
         jGcmData.put("data", jData);

/*         
         MY_MESSAGE="GERRIT JOB DONE"
      		 curl -X POST -H "Content-Type: application/json" -H "Authorization: key=AIzaSyDlE-xdhOvodsRe-0U_FCTZ1lmSUDunZpE" -d '{ "data": {
       		     "title": "JENKINS TEST PASSED",
       		     "message": "'"$MY_MESSAGE"'"
      		   },
       		   "to" : "dgngSAc9EDU:APA91bGYL4oAl37QxSq87nZ6L9Uib-JiZrU9H_i9c2-192TQdFmjFb_dsIOqqJfdiPDHSfkOzGdFcz1D_nZfeVDtNQa7cB-xLC_edHJtUX4Quoms3a88kSvzLo37O1XFmK85yv03nJ--"
      		 }
      		 ' "https://gcm-http.googleapis.com/gcm/send"
*/
         
         HttpURLConnection conn = null;
//         URL url;
         try {             
        	 URL url = new URL("https://gcm-http.googleapis.com/gcm/send ");
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Authorization", "key=" + API_KEY);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setUseCaches(false);
			conn.setDoOutput(true);
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Cache-Control", "no-cache");
//			conn.setConnectTimeout(200000);
//			conn.setReadTimeout(10000);
			// Send GCM message content.
			OutputStream outputStream;
	
			outputStream = conn.getOutputStream();
		
			outputStream.write(jGcmData.toString().getBytes());

			// Read GCM response.
			InputStream inputStream = conn.getInputStream();
			resp = IOUtils.toString(inputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			conn.disconnect();
		}
         return resp;
         
	}
	
	@POST
	@Path("/register")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_HTML)
	public String register(RegistrationData reg) {
		String status;
		try {
			savePropertiesToFile(reg.getChannel(), reg.getDeviceToken());
			status = "OK";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			status = "FAILED";
		}
		return "registering " + reg.getChannel() + ", " + reg.getDeviceToken() + " " + status;
	}
	
	@SuppressWarnings("resource")
	private void savePropertiesToFile(String channel, String token) throws IOException {
		Properties props = getPropFile();
		props.setProperty(channel, token);

		File configFile = new File("keys.properties");
		 
		FileWriter writer = new FileWriter(configFile);
		try {
		    writer = new FileWriter(configFile);
		    props.store(writer, "========");
		} catch (FileNotFoundException ex) {
		    // file does not exist
		} catch (IOException ex) {
		    // I/O error
        } finally {
        	if (writer != null) {
        		try {
        			writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        }
    }
	
	private Properties getPropFile() {
		Properties props = new Properties();
		FileReader reader = null;
		try {
			File configFile = new File("keys.properties");
		    reader = new FileReader(configFile);
		    props.load(reader);
		} catch (FileNotFoundException ex) {
		    // file does not exist
		} catch (IOException ex) {
		    // I/O error
        } finally {
        	if (reader != null) {
        		try {
        			reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        }
	    return props;
	}
}
