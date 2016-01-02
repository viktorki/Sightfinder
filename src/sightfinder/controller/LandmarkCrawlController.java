package sightfinder.controller;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import sightfinder.crawler.BGTourismCrawler;
import sightfinder.crawler.VisitBGCrawler;
import sightfinder.crawler.content.CrawledLandmark;
import sightfinder.crawler.content.LandmarkData;
import sightfinder.model.LandmarkType;
import sightfinder.service.LandmarkService;
import sightfinder.service.LandmarkTypeService;
import sightfinder.util.Constants;

@Controller
@RequestMapping("/crawl")
public class LandmarkCrawlController {

	@Autowired
	private LandmarkTypeService landmarkTypeService;

	@Autowired
	private LandmarkService landmarkService;

	@Autowired
	private CrawlConfig config;

	@RequestMapping("/bg-tourism")
	public void crawlBGTourism() {
		CrawlController controller = newCrawlControllerInstance();
		controller.addSeed(Constants.BG_TOURISM_OBJECT_TYPE_LIST_URL);
		controller.start(BGTourismCrawler.class, Constants.NUMBER_OF_CRAWLERS);

		processCollectedData();
	}

	@RequestMapping("/visit-bg")
	public void crawlVisitBG() {
		CrawlController controller = newCrawlControllerInstance();
		controller.addSeed(Constants.VISIT_BG_URL);
		controller.start(VisitBGCrawler.class, Constants.NUMBER_OF_CRAWLERS);

		processCollectedData();
	}

	private void processCollectedData() {
		for (CrawledLandmark landmark : LandmarkData.getAndClearCollectedLandmarks()) {
			String category = landmark.getCategory();

			LandmarkType landmarkType = landmarkTypeService.getOrCreate(category);
			landmarkService.save(landmark.toLandmark(landmarkType));
		}
	}

	private CrawlController newCrawlControllerInstance() {
		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		CrawlController controller = null;
		try {
			controller = new CrawlController(config, pageFetcher, robotstxtServer);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		return controller;
	}
}
