/**
 * 
 */
package uk.ac.ox.it.cancer_model.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

import com.jcraft.jsch.ChannelExec;
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

    public SecureShell() throws JSchException, IOException {
	sshClient = new JSch();
	// only for public key authentication
	//	    sshClient.addIdentity("location to private key file");
	session = sshClient.getSession("oucs0030", "arcus-b.arc.ox.ac.uk");
	java.util.Properties config=new java.util.Properties();
	config.put("StrictHostKeyChecking", "no");
	session.setConfig(config);
	// the source code cannot contain the password so store it locally (for now)
	Properties properties = new Properties();
	//	    properties.setProperty("password", "");
	//	    FileOutputStream output = new FileOutputStream("config.properties");
	//	    properties.store(output, null);
	FileInputStream input = new FileInputStream("config.properties");
	properties.load(input);
	String password = properties.getProperty("password");
	session.setPassword(password);
	session.connect();
    }
    
    public boolean uploadFile(String localPath, String remotePath) {
	ChannelSftp sftpChannel = null;
	try {
	    sftpChannel = (ChannelSftp) session.openChannel("sftp");
	    sftpChannel.connect();
	    OutputStream outputStream = sftpChannel.put(remotePath);
	    FileInputStream fileInputStream = new FileInputStream(localPath);
	    IOUtils.copy(fileInputStream, outputStream);
	    return true;
	} catch (Exception e) {
	    System.err.println("Trying to copy " + localPath + " to " + remotePath);
	    e.printStackTrace();
	} finally {
	    if (sftpChannel != null) {
		sftpChannel.disconnect();
	    }
	}
	return false;
    }
    
//    public void downloadFile(String localPath, String remotePath) {
//	ChannelSftp sftpChannel = null;
//	try {
//	    sftpChannel = (ChannelSftp) session.openChannel("sftp");
//	    sftpChannel.connect();
//	    InputStream inputStream = sftpChannel.get(remotePath);
//	    FileOutputStream fileOutputStream = new FileOutputStream(localPath);
//	    IOUtils.copy(inputStream, fileOutputStream);
//	} catch (Exception e) {
//	    System.err.println("Trying to copy " + remotePath + " to " + localPath);
//	    e.printStackTrace();
//	} finally {
//	    if (sftpChannel != null) {
//		sftpChannel.disconnect();
//	    }
//	}
//    }
    
    public boolean copyRemoteFile(String remotePath, OutputStream outputStream) {
	ChannelSftp sftpChannel = null;
	try {
	    sftpChannel = (ChannelSftp) session.openChannel("sftp");
	    sftpChannel.connect();
	    InputStream inputStream = sftpChannel.get(remotePath);
	    IOUtils.copy(inputStream, outputStream);
	    return true;
	} catch (Exception e) {
	    System.err.println("Trying to copy " + remotePath + " to an output stream.");
	    e.printStackTrace();
	} finally {
	    if (sftpChannel != null) {
		sftpChannel.disconnect();
	    }
	}
	return false;
    }
    
    public String execute(String command) {
	ChannelExec execChannel = null;
	try {
	    execChannel = (ChannelExec) session.openChannel("exec");
	    execChannel.setCommand(command);
	    InputStream outputFromShell = execChannel.getInputStream();
	    execChannel.connect();
	    String response = new String(IOUtils.toCharArray(outputFromShell));
	    System.out.println("Command: " + command + "; response: " + response); // while debugging
	    return response;
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    if (execChannel != null) {
		execChannel.disconnect();
	    }
	}
	return null;
    }

    public void close() {
	session.disconnect();
    }

}
