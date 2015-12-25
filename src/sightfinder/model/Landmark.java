package sightfinder.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
}
