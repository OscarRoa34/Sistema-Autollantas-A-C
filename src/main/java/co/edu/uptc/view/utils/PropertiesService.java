package co.edu.uptc.view.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertiesService {

    Properties p = new Properties();

    public void load() {

        try {
            p.load(new FileInputStream("src\\main\\resources\\config.properties"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getProperties(String keyName) {
        load();
        return p.getProperty(keyName);
    }
}
