package sightfinder.util;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by krasimira on 16.02.16.
 */
public class ResourseFilesUtil {

    public static File getFileFromResources(String filepath) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        URL pipelineResource = classloader.getResource(filepath);
        File pipelineFile = null;
        try {
        	if (pipelineResource != null) {
        		pipelineFile = new File(pipelineResource.toURI());
        	}
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return pipelineFile;
    }
}
