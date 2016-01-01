package sightfinder.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import sightfinder.util.BGTourismLandmark;
import sightfinder.util.Constants;
import sightfinder.util.LandmarkData;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class BGTourismCrawler extends WebCrawler {

    @Override
    public boolean shouldVisit(Page page, WebURL url) {
	String path = url.getPath();

	return path.equals(Constants.BG_TOURISM_NATURE_OBJECT_LIST_PATH)
		|| path.equals(Constants.BG_TOURISM_NATURE_OBJECT_PATH)
		|| path.equals(Constants.BG_TOURISM_HISTORIC_OBJECT_LIST_PATH)
		|| path.equals(Constants.BG_TOURISM_HISTORIC_OBJECT_PATH);
    }

    @Override
    public void visit(Page page) {
	String path = page.getWebURL().getPath();
	if (path.equals(Constants.BG_TOURISM_NATURE_OBJECT_PATH)
		|| path.equals(Constants.BG_TOURISM_HISTORIC_OBJECT_PATH)) {
	    HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
	    String html = htmlParseData.getHtml();
	    Document document = Jsoup.parse(html);
	    String nameSelector;
	    String categorySelector;
	    String descriptionSelector;

	    if (path.equals(Constants.BG_TOURISM_NATURE_OBJECT_PATH)) {
		nameSelector = Constants.BG_TOURISM_NATURE_OBJECT_NAME_SELECTOR;
		categorySelector = Constants.BG_TOURISM_NATURE_OBJECT_CATEGORY_SELECTOR;
		descriptionSelector = Constants.BG_TOURISM_NATURE_OBJECT_DESCRIPTION_SELECTOR;
	    } else {
		nameSelector = Constants.BG_TOURISM_HISTORIC_OBJECT_NAME_SELECTOR;
		categorySelector = Constants.BG_TOURISM_HISTORIC_OBJECT_CATEGORY_SELECTOR;
		descriptionSelector = Constants.BG_TOURISM_HISTORIC_OBJECT_DESCRIPTION_SELECTOR;
	    }

	    String name = document.select(nameSelector).get(0).text();
	    String category = document.select(categorySelector).get(0).text();
	    String description = document.select(descriptionSelector).get(0)
		    .text();

	    BGTourismLandmark bgTourismLandmark = new BGTourismLandmark();

	    bgTourismLandmark.setName(name);
	    bgTourismLandmark.setCategory(category);
	    bgTourismLandmark.setDescription(description);
	    LandmarkData.bgTourismLandmarkList.add(bgTourismLandmark);
	}
    }
}
