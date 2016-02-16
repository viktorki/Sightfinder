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
@Table(name = "relation")
public class Relation implements Serializable {

	private static final long serialVersionUID = -749632060110514253L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;

	@Column(name = "type")
	private String type;

	@ManyToOne
	@JoinColumn(name = "landmark_id")
	private Landmark landmark;

	@Column(name = "properties")
	private String properties;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Landmark getLandmark() {
		return landmark;
	}

	public void setLandmark(Landmark landmark) {
		this.landmark = landmark;
	}

	public String getProperties() {
		return properties;
	}

	public void setProperties(String properties) {
		this.properties = properties;
	}
}
