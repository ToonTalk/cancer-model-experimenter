package uk.ac.ox.it.cancer_model.server;

import uk.ac.ox.it.cancer_model.client.ExperimentService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ExperimentServiceImpl extends RemoteServiceServlet implements
        ExperimentService {

    public String experimentServer(String input) throws IllegalArgumentException {
	String response = "Click on <a href='https://dl.dropboxusercontent.com/u/51973316/cancer2/output/mutation-50-50x50.html' target='_blank'>this link</a> to see the results of the experiment.";
	if (input != null && !input.isEmpty()) {
	    response += " An email will be sent to " + input + " when the results are ready.";
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
