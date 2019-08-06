package com.ljunggren.common.ssh;

public class SSHRequest {

    private SSHConnectionProperties sshConnectionProperties;
    private String executionString;
    private boolean keepAlive = false;
    
	public SSHConnectionProperties getSshConnectionProperties() {
		return sshConnectionProperties;
	}
	public String getExecutionString() {
		return executionString;
	}
	public boolean isKeepAlive() {
		return keepAlive;
	}
	public void setKeepAlive(boolean keepAlive) {
		this.keepAlive = keepAlive;
	}
	
	public SSHRequest(SSHConnectionProperties sshConnectionProperties,
			String executionString) {
		super();
		this.sshConnectionProperties = sshConnectionProperties;
		this.executionString = executionString;
	}
}
