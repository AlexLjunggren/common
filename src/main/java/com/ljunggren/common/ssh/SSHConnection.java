package com.ljunggren.common.ssh;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SSHConnection {
	
	private Session session;
	private String username;
	private String host;
	private int port;
	private boolean keepAlive = false;
	private JSchException sessionException;
	private Date lastRetry;
	
	public synchronized String execute(SSHRequest sshRequest) throws JSchException, IOException, InterruptedException  {
		if (session == null || !session.isConnected()) {
			startSession(sshRequest);
		}
        String command = sshRequest.getExecutionString();
        ChannelExec channel=(ChannelExec) session.openChannel("exec");
		InputStream inputStream = channel.getInputStream();
		InputStream errorStream = channel.getExtInputStream();
        channel.setCommand(command);
        channel.setInputStream(null);
        channel.connect();

        String output = readResponse(channel, inputStream, errorStream);
        channel.disconnect();
        if (!keepAlive) {
            disconnect();
        }
        return output.trim();
	}
	
	private String readResponse(ChannelExec channel, InputStream inputStream, InputStream errorStream) throws IOException, InterruptedException {
		// code pulled from jsch example doc
		StringBuilder sb = new StringBuilder();
		byte[] tmp = new byte[1024];
		while (true) {
			while (inputStream.available() > 0) {
				int i = inputStream.read(tmp, 0, 1024);
				if (i < 0) {
					break;
				}
				sb.append(new String(tmp, 0, i));
			}
			while (errorStream.available() > 0) {
				int i = errorStream.read(tmp, 0, 1024);
				if (i < 0) {
					break;
				}
				sb.append(new String(tmp, 0, i));
			}
			if (channel.isClosed()) {
				if ((inputStream.available() > 0) || (errorStream.available() > 0)) {
					continue;
				}
				break;
			}
			Thread.sleep(1000);
		}
		return sb.toString();
	}
	
	public void startSession(SSHRequest sshRequest) throws JSchException {
		startSession(sshRequest, 0);
	}
	
    public void startSession(SSHRequest sshRequest, int retries) throws JSchException {
    	if (inRetryCooldown()) {
    		throw sessionException;
    	}
    	
    	host = sshRequest.getSshConnectionProperties().getHost();
        username = sshRequest.getSshConnectionProperties().getUserName();
        port = sshRequest.getSshConnectionProperties().getPort();
        String password = sshRequest.getSshConnectionProperties().getPassword();

        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        config.put("PreferredAuthentications", "password");
        JSch jsch = new JSch();
        
        try {
			session = jsch.getSession(username, host, port);
	        session.setPassword(password);
	        session.setConfig(config);
	        session.connect(2000);
		} 
        catch (JSchException e) {
			if (++retries < 3) {
				startSession(sshRequest, retries);
			}
			else {
				lastRetry = new Date();
				sessionException = e;
				throw sessionException;
			}
		}
		keepAlive = sshRequest.isKeepAlive();
    }
    
    private boolean inRetryCooldown() {
    	if (lastRetry == null) {
    		return false;
    	}
    	long secondsSinceLastRetry = (new Date().getTime() - lastRetry.getTime()) / 1000;
    	return secondsSinceLastRetry < 30;
    }
    
    public void disconnect() {
        session.disconnect();
    }

	public String getUsername() {
		return username;
	}
	public String getHost() {
		return host;
	}
	public int getPort() {
		return port;
	}
    
}
