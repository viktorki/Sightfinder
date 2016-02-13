package sightfinder.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import sightfinder.util.Source;

@Entity
@Table(name = "landmark")
public class Landmark implements Serializable {

	private static final long serialVersionUID = 5896624910195658012L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;

	@Column(name = "name")
	private String name;

	@ManyToOne
	@JoinColumn(name = "landmark_type_id")
	private LandmarkType landmarkType;

	@Column(name = "latitude")
	private Double latitude;

	@Column(name = "longitude")
	private Double longitude;

	@Column(name = "description")
	@Type(type = "text")
	private String description;

	@Column(name = "working_time_from")
	@Temporal(TemporalType.TIME)
	private Date workingTimeFrom;

	@Column(name = "working_time_to")
	@Temporal(TemporalType.TIME)
	private Date workingTimeTo;

	@Column(name = "ticket_price")
	private BigDecimal ticketPrice;

	@Column(name = "source")
	private Source source;

	@Transient
	private Double distance;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LandmarkType getLandmarkType() {
		return landmarkType;
	}

	public void setLandmarkType(LandmarkType landmarkType) {
		this.landmarkType = landmarkType;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getWorkingTimeFrom() {
		return workingTimeFrom;
	}

	public void setWorkingTimeFrom(Date workingTimeFrom) {
		this.workingTimeFrom = workingTimeFrom;
	}

	public Date getWorkingTimeTo() {
		return workingTimeTo;
	}

	public void setWorkingTimeTo(Date workingTimeTo) {
		this.workingTimeTo = workingTimeTo;
	}

	public BigDecimal getTicketPrice() {
		return ticketPrice;
	}

	public void setTicketPrice(BigDecimal ticketPrice) {
		this.ticketPrice = ticketPrice;
	}

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public Landmark mergeWith(Landmark anotherLandmark) {
		if (anotherLandmark.getSource() != source) {
			Landmark newLandmark = new Landmark();
			newLandmark.setId(this.id);
			newLandmark.setLandmarkType(this.landmarkType != null ? this.landmarkType : anotherLandmark
					.getLandmarkType());
			newLandmark.setDescription(mergeDescriptions(description, anotherLandmark.getDescription()));
			newLandmark.setName(this.name);
			newLandmark.setLongitude(this.longitude != null ? this.longitude : anotherLandmark.getLongitude());
			newLandmark.setLatitude(this.latitude != null ? this.latitude : anotherLandmark.getLatitude());
			return newLandmark;
		} else {
			return this;
		}
	}

	private static String mergeDescriptions(String description1, String description2) {
		if (description1.equals(description2)) {
			return description1;
		}

		if (description1.endsWith(description2)) {
			return description1;
		}

		return description1 + ". " + description2;
	}

	public static Comparator<Landmark> LandmarkDistanceComparator = new Comparator<Landmark>() {
		public int compare(Landmark landmark1, Landmark landmark2) {
			return landmark1.getDistance().compareTo(landmark2.getDistance());
		}
	};
}
