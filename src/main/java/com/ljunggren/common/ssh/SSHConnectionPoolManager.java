package com.ljunggren.common.ssh;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.jcraft.jsch.JSchException;

public class SSHConnectionPoolManager {

	private Set<SSHConnection> sshConnections = new HashSet<SSHConnection>();
	
	public String execute(SSHRequest sshRequest) throws JSchException, IOException {
		SSHConnectionProperties requestedConnection = sshRequest.getSshConnectionProperties();
		SSHConnection registeredConnection = sshConnections.stream()
				.filter(connection -> isSameConnection(connection, requestedConnection))
				.findFirst()
				.orElse(null);
		if (registeredConnection == null) {
			sshRequest.setKeepAlive(true);
			SSHConnection sshConnection = new SSHConnection();
			sshConnections.add(sshConnection);
			return sshConnection.execute(sshRequest);
		}
		return registeredConnection.execute(sshRequest);
	}
	
	public boolean isSameConnection(SSHConnection sshConnection, SSHConnectionProperties requestedConnection) {
		return sshConnection.getHost().equals(requestedConnection.getHost()) &&
				sshConnection.getPort() == requestedConnection.getPort() &&
				sshConnection.getUsername().equals(requestedConnection.getUserName());
	}
	
	public void closeAllConnections() {
		sshConnections.stream().forEach(connection -> connection.disconnect());
	}
	
	public int getNumberOfConnections() {
		return sshConnections.size();
	}
	
}
