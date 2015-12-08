package uk.ac.ox.it.cancer_model.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

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
	                           HashMap<String, String> serverFiles) throws IllegalArgumentException {
	String response = "Click on <a href='https://dl.dropboxusercontent.com/u/51973316/cancer2/output/mutation-50-50x50.html' target='_blank'>this link</a> to see the results of the experiment.<br>";
	if (email != null && !email.isEmpty()) {
	    response += " An email will be sent to " + escapeHtml(email) + " when the results are ready."; 
	}
	try {
	    File tempFile = File.createTempFile("parameters", ".txt");
	    FileOutputStream stream = new FileOutputStream(tempFile);
	    for (int i = 0; i < parameterNames.size(); i++) {
		String setting = "set the-" + parameterNames.get(i) + " " + parameterValues.get(i) + "\n";
		stream.write(setting.getBytes());
            }
	    String timeSetting = "set the-start-time \"" + startTime + "\"\n";
	    stream.write(timeSetting.getBytes());
	    stream.close();
	    tempFile.deleteOnExit();
	    serverFiles.put("parameters.txt", tempFile.toString());
	    SecureShell secureShell = new SecureShell();
	    Set<Entry<String, String>> entrySet = serverFiles.entrySet();
	    for (Entry<String, String> entry: entrySet) {
		secureShell.uploadFile(entry.getValue(), "/home/donc-onconet/oucs0030/cancer/" + entry.getKey());
	    }
        } catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
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
