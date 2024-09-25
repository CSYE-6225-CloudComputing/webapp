package com.mycompany.cloudproject.service;

import com.mycompany.cloudproject.dao.ConfigurationInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfigService {

    @Autowired
    ConfigurationInterface config;

    public boolean getConfig(){
           return config.getConfig();
    }
}
