package sightfinder.crawler;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import sightfinder.crawler.content.LandmarkData;
import sightfinder.crawler.content.PochivkaBGLandmark;
import sightfinder.util.Constants;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;

public class PochivkaBGCrawler extends WebCrawler {

	private static final Pattern latitudePattern = Pattern.compile(Constants.POCHIVKA_BG_LATITUDE_REGEX);
	private static final Pattern longtitudePattern = Pattern.compile(Constants.POCHIVKA_BG_LONGTITUDE_REGEX);
	
    @Override
    public boolean shouldVisit(Page page, WebURL url) {
    	return url.getURL().startsWith(Constants.POCHIVKA_BG_URL) || url.getURL().startsWith(Constants.POCHIVKA_BG_MOBILE_URL);
    }

    @Override
    public void visit(Page page) {
    	 if (!page.getWebURL().getURL().endsWith(Constants.POCHIVKA_BG_LANDMARK_PATH_SUFFIX)) {
    		 return;
    	 }
    	 
    	 String url = Constants.POCHIVKA_BG_MOBILE_URL + page.getWebURL().getPath();
    	 PochivkaBGLandmark landmark = parseLandmarkContent(url);
         LandmarkData.addLandmark(landmark);
    }

	private PochivkaBGLandmark parseLandmarkContent(String url) {
		PochivkaBGLandmark landmark = new PochivkaBGLandmark();
		try {
            Document doc = Jsoup.connect(url).get();
            String name = doc.select(Constants.POCHIVKA_BG_OBJECT_NAME_ELEMENT_SELECTOR).get(0).text();
            String description = doc.select(Constants.POCHIVKA_BG_OBJECT_DESCRIPTION_ELEMENT_SELECTOR).get(0).text();
            
            String pageScript = doc.select(Constants.POCHIVKA_BG_OBJECT_PAGE_SCRIPT_ELEMENT_SELECTOR).html();
            
            Matcher longtitudeMatcher = longtitudePattern.matcher(pageScript);    
            String longitude = longtitudeMatcher.find() ? longtitudeMatcher.group(1) : null;
            
            Matcher latitudeMatcher = latitudePattern.matcher(pageScript);    
            String latitude = latitudeMatcher.find() ? latitudeMatcher.group(1) : null;
           
            landmark.setName(name);
            landmark.setDescription(description);
            landmark.setLongitude(longitude);
            landmark.setLatitude(latitude);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return landmark;
	}
}
