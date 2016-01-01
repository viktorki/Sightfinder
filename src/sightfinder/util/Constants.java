package sightfinder.util;

public class Constants {

    public static final String CRAWL_STORAGE_FOLDER = "D:\\dev\\crawler\\";

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
}
