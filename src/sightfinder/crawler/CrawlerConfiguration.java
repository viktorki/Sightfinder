package sightfinder.crawler;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sightfinder.util.Constants;

@Configuration
public class CrawlerConfiguration {

    @Bean
    public CrawlConfig getCrawlerControllerConfig() {
        CrawlConfig config = new CrawlConfig();

        config.setCrawlStorageFolder(Constants.CRAWL_STORAGE_FOLDER);
        config.setPolitenessDelay(1000);
        config.setMaxDepthOfCrawling(2);
        config.setIncludeBinaryContentInCrawling(false);
        config.setResumableCrawling(false);
        return config;
    }

}
