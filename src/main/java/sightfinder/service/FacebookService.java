package sightfinder.service;

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import sightfinder.model.Landmark;
import sightfinder.util.Constants;

@Service
public class FacebookService {
	public DateTimeFormatter timeFormat = DateTimeFormat.forPattern("HH:mm");
	
	public Landmark updateWorkingTime(String externalLink, Landmark landmark) {
		if (this.matchURL(externalLink)) {
			Document doc = null;
			try {
				doc = Jsoup.connect(externalLink + Constants.FACEBOOK_INFO_TAB_PATH).get();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			Element hours = doc.getElementsByClass("HoursEditorHoursDiv").last();
			if (hours != null) {
				String workingTime = hours.text();
				String[] fromTo = workingTime.split("-");
				if (fromTo.length >= 2) {
					DateTime from = timeFormat.parseDateTime(fromTo[0].trim());
					landmark.setWorkingTimeFrom(from.toDate());
					
					DateTime to = timeFormat.parseDateTime(fromTo[1].trim());
					landmark.setWorkingTimeTo(to.toDate());
					return landmark;
				}
			}
		}
		return null;
	}
	
	private boolean matchURL(String url) {
		return url.contains(Constants.FACBOOK_URL);
	}
}
