package uk.ac.ox.it.cancer_model.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import uk.ac.ox.it.cancer_model.client.ExperimentService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ExperimentServiceImpl extends RemoteServiceServlet implements
        ExperimentService {

    public String experimentServer(long numberOfReplicates,
	                           String email,
	                           String startTime,
	                           ArrayList<String> parameterNames,
	                           ArrayList<Double> parameterValues,
	                           HashMap<String, String> serverFiles,
	                           boolean run3d,
	                           String host) throws IllegalArgumentException {
	String uuid = UUID.randomUUID().toString();
	String url = "http://" + host + "/run/" + numberOfReplicates/16 + "batches-" + uuid + ".html";
	String response = "Click on <a href='" + url + "' target='_blank'>this link</a> to see the results of the experiment.<br>";
	if (email != null && !email.isEmpty()) {
	    response += " An email will be sent to " + escapeHtml(email) + " when all the results are ready."; 
	} else {
	    email = "none"; // checked by the shell scripts on ARC
	}
	SecureShell secureShell = null;
	String queue = "production";
	try {
	    File tempFile = File.createTempFile("parameters", ".txt");
	    FileOutputStream stream = new FileOutputStream(tempFile);
	    for (int i = 0; i < parameterNames.size(); i++) {
		String name = parameterNames.get(i);
		Double value = parameterValues.get(i);
		String setting = "set " + name + " " + value + "\n";
		stream.write(setting.getBytes());
		if (name.equals("the-maximum-number-of-ticks") && value <= 200) {
		    queue = "dev";
		}
            }
	    String timeSetting = "set the-start-time \"" + startTime + "\"\n";
	    stream.write(timeSetting.getBytes());
	    stream.close();
	    tempFile.deleteOnExit();
	    serverFiles.put("parameters.txt", tempFile.toString());
	    try  {
		secureShell = new SecureShell();	
	    } catch (Exception e) {
		return "The following error occurred trying to contact the ARC computers: " + e.getMessage();
	    }
	    String experimentFolder = "/home/donc-onconet/oucs0030/cancer/" + uuid + "/";
	    secureShell.execute("cd ~/cancer/ && bash create_experiment_folder.sh " + uuid);
	    Set<Entry<String, String>> entrySet = serverFiles.entrySet();
	    for (Entry<String, String> entry: entrySet) {
		secureShell.uploadFile(entry.getValue(), experimentFolder + entry.getKey());
	    }    
	    String command = "cd ~/cancer/ && bash experiment.sh " + uuid + " " + numberOfReplicates/16 + " " + queue + " " + email;
	    if (run3d) {
		command += " 3d";
	    }
	    secureShell.execute(command);
        } catch (IOException e) {
	    e.printStackTrace();
        } finally {
            if (secureShell != null) {
        	secureShell.close();
            }
        }
	return response;
    }

    /**
     * Escape an html string. Escaping data received from the client helps to
     * prevent cross-site script vulnerabilities.
     * 
     * @param html the html string to escape
     * @return the escaped string
     */
    private String escapeHtml(String html) {
	if (html == null) {
	    return null;
	}
	return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
	        .replaceAll(">", "&gt;");
    }
}
