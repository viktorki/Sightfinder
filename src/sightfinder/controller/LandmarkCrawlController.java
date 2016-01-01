package sightfinder.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import sightfinder.crawler.BGTourismCrawler;
import sightfinder.model.Landmark;
import sightfinder.model.LandmarkType;
import sightfinder.service.LandmarkService;
import sightfinder.service.LandmarkTypeService;
import sightfinder.util.BGTourismLandmark;
import sightfinder.util.Constants;
import sightfinder.util.LandmarkData;
import sightfinder.util.Source;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

@Controller
@RequestMapping("/crawl")
public class LandmarkCrawlController {

    @Autowired
    private LandmarkTypeService landmarkTypeService;

    @Autowired
    private LandmarkService landmarkService;

    @RequestMapping("/bg-tourism")
    public void crawlBGTourism() throws Exception {
	CrawlConfig config = new CrawlConfig();

	config.setCrawlStorageFolder(Constants.CRAWL_STORAGE_FOLDER);
	config.setPolitenessDelay(1000);
	config.setMaxDepthOfCrawling(2);
	config.setIncludeBinaryContentInCrawling(false);
	config.setResumableCrawling(false);

	PageFetcher pageFetcher = new PageFetcher(config);
	RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
	RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig,
		pageFetcher);
	CrawlController controller = new CrawlController(config, pageFetcher,
		robotstxtServer);

	controller.addSeed(Constants.BG_TOURISM_OBJECT_TYPE_LIST_URL);
	controller.start(BGTourismCrawler.class, Constants.NUMBER_OF_CRAWLERS);

	for (BGTourismLandmark bgTourismLandmark : LandmarkData.bgTourismLandmarkList) {
	    Landmark landmark = new Landmark();
	    String name = bgTourismLandmark.getName();
	    String category = bgTourismLandmark.getCategory();
	    String description = bgTourismLandmark.getDescription();

	    LandmarkType landmarkType = landmarkTypeService
		    .findByName(category);

	    if (landmarkType == null) {
		landmarkType = new LandmarkType();

		landmarkType.setName(category);

		landmarkType = landmarkTypeService.save(landmarkType);
	    }

	    landmark.setName(name);
	    landmark.setLandmarkType(landmarkType);
	    landmark.setDescription(description);
	    landmark.setSource(Source.BG_TOURISM);
	    landmarkService.save(landmark);
	}

	LandmarkData.bgTourismLandmarkList.clear();
    }
}
