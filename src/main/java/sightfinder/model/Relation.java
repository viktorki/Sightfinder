package sightfinder.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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

	@Column(name = "from_landmark_id")
	private Landmark fromLandmark;

	@Column(name = "to_landmark_id")
	private Landmark toLandmark;

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

	public Landmark getFromLandmark() {
		return fromLandmark;
	}

	public void setFromLandmark(Landmark fromLandmark) {
		this.fromLandmark = fromLandmark;
	}

	public Landmark getToLandmark() {
		return toLandmark;
	}

	public void setToLandmark(Landmark toLandmark) {
		this.toLandmark = toLandmark;
	}
}
