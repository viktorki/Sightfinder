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
}
