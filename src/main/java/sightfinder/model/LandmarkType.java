package sightfinder.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "landmark_type")
public class LandmarkType implements Serializable {

	private static final long serialVersionUID = 7870576408440968981L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;

	@Column(name = "name", unique = true)
	private String name;

	@Column(name = "has_working_time")
	private Boolean hasWorkingTime;

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

	public Boolean getHasWorkingTime() {
		return hasWorkingTime;
	}

	public void setHasWorkingTime(Boolean hasWorkingTime) {
		this.hasWorkingTime = hasWorkingTime;
	}
}
