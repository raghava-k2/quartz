package com.kvr.dao;

import java.sql.Connection;

public class GlobalDAO {
	public Connection connection;

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

}
