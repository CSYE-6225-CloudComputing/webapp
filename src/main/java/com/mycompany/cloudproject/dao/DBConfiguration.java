package com.mycompany.cloudproject.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Repository
public class DBConfiguration implements ConfigurationInterface {

    private static final Logger logger = LoggerFactory.getLogger(DBConfiguration.class.getName());

    @Autowired
    private DataSource dataSource;

    @Override
    public boolean getConfig() {
        return checkDBConnection();
    }

    public boolean checkDBConnection() {
        // Using try-with-resources to ensure Connection is closed
        try (Connection connection = dataSource.getConnection()) {
            if (!connection.isValid(10)) { // Wait for 2 seconds for a valid connection
                logger.error("Database connection is not valid.");
                return false;
            }
            logger.info("DB is connected.");
            return true;
        } catch (SQLException e) {
            logger.error("Not able to connect to database. Error: {}", e.getMessage());
            return false; // Connection failed
        }
    }
}
