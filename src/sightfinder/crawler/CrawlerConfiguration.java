package sightfinder.crawler;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicHeader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import sightfinder.util.Constants;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;

@Configuration
public class CrawlerConfiguration {

    @Bean
    public CrawlConfig getCrawlerControllerConfig() {
        CrawlConfig config = new CrawlConfig();

        List<BasicHeader> headers = new ArrayList<BasicHeader>();
        headers.add(new BasicHeader(Constants.COOKIE_HEADER, Constants.COOKIE_HEADER_VALUE));
        
        config.setDefaultHeaders(headers);
        config.setCrawlStorageFolder(Constants.CRAWL_STORAGE_FOLDER);
        config.setPolitenessDelay(1000);
        config.setMaxDepthOfCrawling(2);
        config.setIncludeBinaryContentInCrawling(false);
        config.setResumableCrawling(false);
        return config;
    }
}