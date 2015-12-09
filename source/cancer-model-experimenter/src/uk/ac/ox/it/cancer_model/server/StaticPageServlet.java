/**
 * 
 */
package uk.ac.ox.it.cancer_model.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Responds to web page requests by getting the file from ARC
 * 
 * @author Ken Kahn
 *
 */
public class StaticPageServlet extends HttpServlet {
    
    private static final long serialVersionUID = 3075405196542994862L;
    private static final String HTML_CONTENT_TYPE = "text/html; charset=utf-8";
    private static final String PLAIN_CONTENT_TYPE = "text/plain; charset=utf-8";
      
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, java.io.IOException {
	// following may try again (but only once due to use firstTime flag)
	respondToGet(request, response, true);
    }
    
    protected void respondToGet(HttpServletRequest request, HttpServletResponse response, boolean firstTime) 
	    throws IOException {
	String pathInfo = request.getPathInfo();
	if (pathInfo == null) {
	    return;
	}
	response.setCharacterEncoding("UTF-8");
	int dotIndex = pathInfo.lastIndexOf(".");
	String extension = pathInfo.substring(dotIndex + 1);
	int numberIndex = pathInfo.indexOf("batches-");
	SecureShell secureShell = new SecureShell();
	ServletOutputStream outputStream = response.getOutputStream();
	if (numberIndex > 0) {
	    String numberOfBatches = pathInfo.substring(1, numberIndex);
	    String uuid = pathInfo.substring(numberIndex+"batches-".length(), dotIndex);
	    if (extension.equalsIgnoreCase("html")) {
		response.setContentType(HTML_CONTENT_TYPE);
	    } else {
		response.setContentType(PLAIN_CONTENT_TYPE);
	    }
	    String remoteFileName = "/home/donc-onconet/oucs0030/cancer-outputs/" + uuid + "." + extension;
	    
	    String command = "cd ~/cancer/ && bash make_html.sh " + uuid + " " + numberOfBatches;
	    secureShell.execute(command);
	    secureShell.copyRemoteFile(remoteFileName, outputStream);
	} else {
	    // is some auxiliary file such as CSS or JS
	    secureShell.copyRemoteFile("/home/donc-onconet/oucs0030/cancer-outputs" + pathInfo, outputStream);
	}
	secureShell.close();
    }

}
