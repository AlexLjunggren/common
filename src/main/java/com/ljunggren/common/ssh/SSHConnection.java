package com.ljunggren.common.ssh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
	
	public synchronized String execute(SSHRequest sshRequest) throws JSchException, IOException  {
		if (session == null || !session.isConnected()) {
			startSession(sshRequest);
		}
        String command = sshRequest.getExecutionString();
        StringBuilder output = new StringBuilder();
        ChannelExec channel=(ChannelExec) session.openChannel("exec");
        BufferedReader in = new BufferedReader(new InputStreamReader(channel.getInputStream()));
        channel.setCommand(command);
        channel.connect();

        String msg = null;
        while((msg =in.readLine()) != null) {
            output.append(msg);
        }
        channel.disconnect();
        if (!keepAlive) {
            disconnect();
        }
        return output.toString().trim();
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
