package ru.job4j.grabber.service;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.Properties;

public class Config {
    private static final Logger LOGGER = Logger.getLogger(Config.class);
    private final Properties properties = new Properties();

    public void load(String file) {
        try (var input = Config.class.getClassLoader().getResourceAsStream(file)) {
            properties.load(input);
        } catch (IOException io) {
            LOGGER.error(String.format("When load file : %s", file), io);
        }
    }

    public String get(String key) {
        return properties.getProperty(key);
    }
}
