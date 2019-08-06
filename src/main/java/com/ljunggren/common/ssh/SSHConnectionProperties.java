package com.ljunggren.common.ssh;

import com.ljunggren.common.ssh.user.User;

public class SSHConnectionProperties {
    private String host;
    private String username;
    private String password;
    private int port = 22;
    private SSHConnectionPoolManager sshConnectionPoolManager;
    
    public SSHConnectionProperties setPort(int port) {
    	this.port = port;
    	return this;
    }
    
    public String getHost() {
        return host;
    }
    public String getUserName() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public int getPort() {
		return port;
	}
    public SSHConnectionPoolManager getSshConnectionPoolManager() {
    	return sshConnectionPoolManager;
    }

    public SSHConnectionProperties(String host, User user, SSHConnectionPoolManager sshConnectionPoolManager) {
		super();
		this.host = host;
		this.username = user.getUsername();
		this.password = user.getPassword();
		this.sshConnectionPoolManager = sshConnectionPoolManager;
    }
    
}
