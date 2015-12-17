/**
 * 
 */
package uk.ac.ox.it.cancer_model.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;
import net.sf.saxon.s9api.XsltTransformer;

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
		} else if (item.getSize() > 0) {
		    // Process form file field (input type="file").
		    String fileName = FilenameUtils.getName(item.getName());
		    InputStream fileContent = item.getInputStream();
		    File tempFile = File.createTempFile(fileName, "");
		    tempFile.deleteOnExit();
		    int dotIndex = fileName.lastIndexOf(".");
		    String extension = fileName.substring(dotIndex + 1);
		    ServletOutputStream writer = response.getOutputStream();
		    try (FileOutputStream out = new FileOutputStream(tempFile)) {
			IOUtils.copy(fileContent, out);
			if (extension.equals("zginml")) {
			    // based on http://examples.javacodegeeks.com/core-java/util/zip/zipinputstream/java-unzip-file-example/
			    FileInputStream fileInputStream = new FileInputStream(tempFile.toString());
			    ZipInputStream zipInput = new ZipInputStream(fileInputStream);
			    ZipEntry entry = zipInput.getNextEntry();
			    while (entry != null) {
				String entryName = entry.getName();
				if (entryName.endsWith(".ginml")) {
				    tempFile = File.createTempFile(fileName, ".ginml");
				    FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
				    int character = zipInput.read();
				    // copy first line
				    while (character != '\n') {
					fileOutputStream.write(character);
					character = zipInput.read();
				    }
				    // copy up to S in <!DOCTYPE gxl SYSTEM "http://ginsim.org/GINML_2_2.dtd">
				    // http://ginsim.org/GINML_2_2.dtd causes errors since no longer exists
				    while (character != 'S') {
					fileOutputStream.write(character);
					character = zipInput.read();
				    }
				    fileOutputStream.write('>');
				    // skip over rest of DOCTYPE declaration
				    while (character != '\n') {
					character = zipInput.read();
				    }
				    IOUtils.copy(zipInput, fileOutputStream);
				    extension = "ginml";
				    zipInput.closeEntry();
				    zipInput.close();
				    break; // only unzip the ginml file
				}
			    }
			}
			if (extension.equals("ginml")) {
			    File transformedTempFile = File.createTempFile(fileName, ".xml"); // .xml makes more sense
			    transformToGraphML(tempFile.toString(), transformedTempFile.toString());
			    tempFile = transformedTempFile;
			    fileName = "regulatoryGraph.html"; // fileName.substring(0, dotIndex) + ".html";
			}
		    } catch (Exception e) {
			e.printStackTrace();
			writer.print("Error: " + e.getMessage());
			return;
		    }
		    // add unique tokens around each file name since this will be wrapped up in minimal HTML
		    writer.print("file-name-token" + fileName + "file-name-token" + tempFile.toString() + "file-name-token");
		}
	    }
	} catch (FileUploadException e) {
	    throw new ServletException("Cannot parse multipart request.", e);
	}
    }
    
    void transformToGraphML(String inputFileName, String outputFileName) throws SaxonApiException {
	// based upon https://www.cs.duke.edu/courses/fall08/cps116/docs/saxon/samples/java/S9APIExamples.java
	Processor proc = new Processor(false);
        XsltCompiler comp = proc.newXsltCompiler();
        XsltExecutable exp = comp.compile(new StreamSource(new File("GXL-to-GraphML-new.xsl")));
        XdmNode source = proc.newDocumentBuilder().build(new StreamSource(new File(inputFileName)));
        Serializer out = new Serializer();
        out.setOutputProperty(Serializer.Property.METHOD, "xml"); // was html
        out.setOutputProperty(Serializer.Property.INDENT, "yes");
        out.setOutputFile(new File(outputFileName));
        XsltTransformer trans = exp.load();
        trans.setInitialContextNode(source);
        trans.setDestination(out);
        trans.transform();
    }

}
