package sightfinder.util;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by krasimira on 16.02.16.
 */
public class ResourceFilesUtil {

    public static File getFileFromResources(String filepath) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        URL resourceURL = classloader.getResource(filepath);
        File resourceFile = null;
        try {
        	if (resourceURL != null) {
        		resourceFile = new File(resourceURL.toURI());
        	}
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return resourceFile;
    }
}
