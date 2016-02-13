package sightfinder.crawler;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import sightfinder.crawler.content.LandmarkData;
import sightfinder.crawler.content.VisitBGLandmark;
import sightfinder.util.Constants;

import java.io.IOException;


public class VisitBGCrawler extends WebCrawler {
	
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        return url.getURL().startsWith(Constants.VISIT_BG_URL);
    }

    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();
        if (getPathPartsCount(page) < 4) {
            // If the url doesn't lead to a landmark
            return;
        }

        VisitBGLandmark landmark = parseLandmarkContent(url);
        LandmarkData.addLandmark(landmark);
    }

    private static VisitBGLandmark parseLandmarkContent(String url) {
        VisitBGLandmark landmark = new VisitBGLandmark();
        try {
            Document doc = Jsoup.connect(url).get();
            String name = doc.getElementsByAttributeValue(
                    Constants.VISIT_BG_OBJECT_ELEMENT, Constants.VISIT_BG_OBJECT_NAME_ELEMENT_VALUE).text();
            String description = doc.getElementsByAttributeValue(
                    Constants.VISIT_BG_OBJECT_ELEMENT, Constants.VISIT_BG_OBJECT_DESCRIPTION_ELEMENT_VALUE).text();
            String category = doc.select(Constants.VISIT_BG_OBJECT_CATEGORY_SELECTOR).get(2).text();

            Elements latitudeElement = doc.getElementsByAttributeValueStarting(
                    Constants.VISIT_BG_OBJECT_LOCATION_ELEMENT_ATTRIBUTE, name);
            String latitude = latitudeElement.attr(Constants.VISIT_BG_OBJECT_LATITUDE_ARRTIBUTE);
            String longitude = latitudeElement.attr(Constants.VISIT_BG_OBJECT_LONGITUDE_ARRTIBUTE);

            landmark.setName(name);
            landmark.setDescription(description);
            landmark.setLongitude(longitude);
            landmark.setLatitude(latitude);
            landmark.setCategory(category);

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return landmark;
    }

    private static int getPathPartsCount(Page page) {
        return page.getWebURL().getPath().split("/").length;
    }

}
