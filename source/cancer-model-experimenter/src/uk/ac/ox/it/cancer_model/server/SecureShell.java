/**
 * 
 */
package uk.ac.ox.it.cancer_model.server;

import java.io.FileInputStream;
import java.io.OutputStream;
import org.apache.commons.io.IOUtils;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * @author Ken Kahn
 *
 */
public class SecureShell {

    /**
     * A class for sending shell commands to ARC and transferring files
     */
    
    private JSch sshClient;
    
    private Session session;
    
    public SecureShell() {
	sshClient = new JSch();
	// only for public key authentication

	try {
//	    sshClient.addIdentity("location to private key file");
	    session = sshClient.getSession("oucs0030", "arcus-b.arc.ox.ac.uk");
	    java.util.Properties config=new java.util.Properties();
	    config.put("StrictHostKeyChecking", "no");
	    session.setConfig(config);
            // only for password authentication
	    session.setPassword(""); // replace with https://docs.oracle.com/javase/7/docs/api/java/util/Properties.html
	    session.connect();
        } catch (JSchException e) {
	    e.printStackTrace();
        }
    }
    
    public void uploadFile(String localPath, String remotePath) {
	ChannelSftp sftpChannel = null;
	try {
	  sftpChannel = (ChannelSftp) session.openChannel("sftp");
	  sftpChannel.connect();
	  OutputStream outputStream = sftpChannel.put(remotePath);
	  FileInputStream fileInputStream = new FileInputStream(localPath);
	  IOUtils.copy(fileInputStream, outputStream);
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	  if (sftpChannel != null) {
	    sftpChannel.disconnect();
	  }
	}
    }

}
