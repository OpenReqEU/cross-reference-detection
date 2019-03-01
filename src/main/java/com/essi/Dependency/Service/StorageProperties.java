package com.essi.Dependency.Service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.nio.file.Path;
import java.nio.file.Paths;

@ConfigurationProperties("storage")
public class StorageProperties {

    /**
     * Folder location for storing files
     */
    private String location = "upload-dir";

    public String getLocation() {
	Path userDir = Paths.get(System.getProperty("user.dir"));
	return userDir.resolve(location).toString();
    }

    public void setLocation(String location) {
	this.location = location;
    }

}