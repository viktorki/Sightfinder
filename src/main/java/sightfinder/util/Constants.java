package sightfinder.util;

import java.io.File;

public class Constants {

	public static final String CRAWL_STORAGE_FOLDER = System.getProperty("user.home") + File.separator + "crawler";

	public static final int NUMBER_OF_CRAWLERS = 1;

	public static final String BG_TOURISM_OBJECT_TYPE_LIST_URL = "http://bg-tourism.com/bg/sightseeings.php";

	public static final String BG_TOURISM_NATURE_OBJECT_LIST_PATH = "/bg/nature-objects-list.php";

	public static final String BG_TOURISM_NATURE_OBJECT_PATH = "/bg/nature-object-info.php";

	public static final String BG_TOURISM_NATURE_OBJECT_NAME_SELECTOR = "#content > table > tbody > tr:nth-child(2) > td:nth-child(2) > table:nth-child(1) > tbody > tr:nth-child(2) > td > table > tbody > tr:nth-child(1) > td > table > tbody > tr > td.blue-title-12 > strong";

	public static final String BG_TOURISM_NATURE_OBJECT_CATEGORY_SELECTOR = "#content > table > tbody > tr:nth-child(2) > td:nth-child(2) > table:nth-child(1) > tbody > tr:nth-child(2) > td > table > tbody > tr:nth-child(2) > td > table > tbody > tr > td > table > tbody > tr:nth-child(2) > td.list-link > table > tbody > tr:nth-child(1) > td > a > strong";

	public static final String BG_TOURISM_NATURE_OBJECT_DESCRIPTION_SELECTOR = "#content > table > tbody > tr:nth-child(2) > td:nth-child(2) > table:nth-child(1) > tbody > tr:nth-child(2) > td > table > tbody > tr:nth-child(2) > td > table > tbody > tr > td > table > tbody > tr:nth-child(4) > td > div";

	public static final String BG_TOURISM_HISTORIC_OBJECT_LIST_PATH = "/bg/historic-sublist.php";

	public static final String BG_TOURISM_HISTORIC_OBJECT_PATH = "/bg/historic-object-info.php";

	public static final String BG_TOURISM_HISTORIC_OBJECT_NAME_SELECTOR = "#content > table > tbody > tr:nth-child(2) > td:nth-child(2) > table:nth-child(1) > tbody > tr:nth-child(1) > td:nth-child(2) > table > tbody > tr:nth-child(7) > td > table > tbody > tr:nth-child(1) > td > table > tbody > tr > td.blue-title-12 > strong";

	public static final String BG_TOURISM_HISTORIC_OBJECT_CATEGORY_SELECTOR = "#content > table > tbody > tr:nth-child(2) > td:nth-child(2) > table:nth-child(1) > tbody > tr:nth-child(1) > td:nth-child(2) > table > tbody > tr:nth-child(7) > td > table > tbody > tr:nth-child(2) > td > table > tbody > tr > td > table > tbody > tr:nth-child(2) > td.list-link > table > tbody > tr:nth-child(1) > td.list-link";

	public static final String BG_TOURISM_HISTORIC_OBJECT_DESCRIPTION_SELECTOR = "#content > table > tbody > tr:nth-child(2) > td:nth-child(2) > table:nth-child(1) > tbody > tr:nth-child(1) > td:nth-child(2) > table > tbody > tr:nth-child(7) > td > table > tbody > tr:nth-child(2) > td > table > tbody > tr > td > table > tbody > tr:nth-child(4) > td > div";

	public static final String VISIT_BG_URL = "http://www.visit.bg/patevoditel/";

	public static final String VISIT_BG_OBJECT_CATEGORY_SELECTOR = ".top_bc ol li";

	public static final String VISIT_BG_OBJECT_ELEMENT = "itemprop";

	public static final String VISIT_BG_OBJECT_NAME_ELEMENT_VALUE = "name";

	public static final String VISIT_BG_OBJECT_DESCRIPTION_ELEMENT_VALUE = "description";

	public static final String VISIT_BG_OBJECT_LOCATION_ELEMENT_ATTRIBUTE = "data-name";

	public static final String VISIT_BG_OBJECT_LATITUDE_ARRTIBUTE = "data-lat";

	public static final String VISIT_BG_OBJECT_LONGITUDE_ARRTIBUTE = "data-lng";

	public static final String POCHIVKA_BG_URL = "http://pochivka.bg/";

	public static final String POCHIVKA_BG_MOBILE_URL = "http://mob.pochivka.bg";

	public static final String POCHIVKA_BG_OBJECTS_URL = POCHIVKA_BG_URL
			+ "sights/sights/loadSightByCategory?&destinationId=&page=";

	public static final int POCHIVKA_BG_OBJECTS_LIST_FIRST_PAGE_INDEX = 1;

	public static final int POCHIVKA_BG_OBJECT_LIST_PAGES_COUNT = 44;

	public static final String POCHIVKA_BG_LANDMARK_PATH_SUFFIX = "-z";

	public static final String POCHIVKA_BG_OBJECT_NAME_ELEMENT_SELECTOR = ".page-title > h2";

	public static final String POCHIVKA_BG_OBJECT_DESCRIPTION_ELEMENT_SELECTOR = ".panel-body";

	public static final String POCHIVKA_BG_OBJECT_PAGE_SCRIPT_ELEMENT_SELECTOR = "html > body > script";

	public static final String POCHIVKA_BG_LATITUDE_REGEX = "window\\.lat = (\\d+\\.\\d+)";

	public static final String POCHIVKA_BG_LONGTITUDE_REGEX = "window\\.lon = (\\d+\\.\\d+)";

	public static final String COOKIE_HEADER = "Cookie";

	public static final String COOKIE_HEADER_VALUE = "isMobile=0; showMobile=0;";

	public static final String DBPEDIA_URL = "http://bg.dbpedia.org";

	public static final String DBPEDIA_SEARCH_URL = DBPEDIA_URL + "/lucene/search?q=%s";

	public static final Source MERGED_ITEMS_SOURCE = null;

	public static final String DBPEDIA_EXTERNAL_LINK = "dbo:wikiPageExternalLink";

	public static final String FACBOOK_URL = "www.facebook.com/";

	public static final String FACEBOOK_INFO_TAB_PATH = "/info?tab=page_info";

	public static final Long MAX_DISTANCE = 10000L;

	public static final Long DISTANCE_ERROR = 10L;

	public static final Long RADIUS_OF_EARTH = 6378100L;
}
