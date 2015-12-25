package sightfinder.model;

import java.io.Serializable;
import java.math.BigDecimal;
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
    private LandmarkType landMarkType;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "description")
    private String description;

    @Column(name = "working_time_from")
    @Temporal(TemporalType.TIME)
    private Date workingTimeFrom;

    @Column(name = "working_time_to")
    @Temporal(TemporalType.TIME)
    private Date workingTimeTo;

    @Column(name = "ticket_price")
    private BigDecimal ticketPrice;

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

    public LandmarkType getLandMarkType() {
	return landMarkType;
    }

    public void setLandMarkType(LandmarkType landMarkType) {
	this.landMarkType = landMarkType;
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
}
