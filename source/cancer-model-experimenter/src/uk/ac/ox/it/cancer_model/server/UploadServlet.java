/**
 * 
 */
package uk.ac.ox.it.cancer_model.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

/**
 * @author Ken Kahn
 *
 */

public class UploadServlet extends HttpServlet {

    /**
     * Receives configuration files from the user
     */
    private static final long serialVersionUID = -3538876153792111483L;
    
    public void doPost(HttpServletRequest request, HttpServletResponse response)
           throws ServletException, java.io.IOException {
	// based on http://stackoverflow.com/questions/2422468/how-to-upload-files-to-server-using-jsp-servlet/2424824#2424824
	try {
	        List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
	        for (FileItem item : items) {
	            if (item.isFormField()) {
	                // Process regular form field (input type="text|radio|checkbox|etc", select, etc).
//	                String fieldName = item.getFieldName();
//	                String fieldValue = item.getString();
	                // ... (do your job here)
	            } else {
	                // Process form file field (input type="file").
	                String fileName = FilenameUtils.getName(item.getName());
	                InputStream fileContent = item.getInputStream();
	                final File tempFile = File.createTempFile(fileName, "");
	                tempFile.deleteOnExit();
	                try (FileOutputStream out = new FileOutputStream(tempFile)) {
	                    IOUtils.copy(fileContent, out);
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	                ServletOutputStream writer = response.getOutputStream();
	                // add unique tokens around each file name since this will be wrapped up in minimal HTML
	                writer.print("file-name-token" + fileName + "file-name-token" + tempFile.toString() + "file-name-token");
	            }
	        }
	    } catch (FileUploadException e) {
	        throw new ServletException("Cannot parse multipart request.", e);
	    }

    }

}
