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
    DataSource dataSource;

    @Override
    public boolean getConfig() {
        return checkDBConnection();
    }

    public boolean checkDBConnection() {
        Connection connection = null;
        try {

            connection = dataSource.getConnection();
            if (!connection.isValid(1000)) {

                throw new SQLException("Database connection is not valid.");
            }
            logger.info("DB is connected.");
            return true;

        } catch (Exception e) {
            logger.error("Not able to connect to database.");
            return false; // Connection failed
        } finally {

        }
    }
}

