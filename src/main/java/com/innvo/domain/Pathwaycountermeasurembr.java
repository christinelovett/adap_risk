package com.innvo.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A Pathwaycountermeasurembr.
 */
@Entity
@Table(name = "pathwaycountermeasurembr")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "pathwaycountermeasurembr")
public class Pathwaycountermeasurembr implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Size(max = 100)
    @Column(name = "comment", length = 100)
    private String comment;

    @Column(name = "xcoordinate")
    private Integer xcoordinate;
    
    @Column(name = "ycoordinate")
    private Integer ycoordinate;
    
    @NotNull
    @Size(max = 25)
    @Column(name = "status", length = 25, nullable = false)
    private String status;

    @NotNull
    @Size(max = 50)
    @Column(name = "lastmodifiedby", length = 50, nullable = false)
    private String lastmodifiedby;

    @NotNull
    @Column(name = "lastmodifieddatetime", nullable = false)
    private ZonedDateTime lastmodifieddatetime;

    @NotNull
    @Size(max = 25)
    @Column(name = "domain", length = 25, nullable = false)
    private String domain;

    @ManyToOne
    @NotNull
    private Pathway pathway;

    @ManyToOne
    @NotNull
    private Countermeasure countermeasure;
    
    @ManyToOne
    @NotNull
    private Scenario scenario;
    
    @NotNull
    private String parentInstance;
    
    @NotNull
    private String childInstance;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

	public Integer getXcoordinate() {
		return xcoordinate;
	}

	public void setXcoordinate(Integer xcoordinate) {
		this.xcoordinate = xcoordinate;
	}

	public Integer getYcoordinate() {
		return ycoordinate;
	}

	public void setYcoordinate(Integer ycoordinate) {
		this.ycoordinate = ycoordinate;
	}

	public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastmodifiedby() {
        return lastmodifiedby;
    }

    public void setLastmodifiedby(String lastmodifiedby) {
        this.lastmodifiedby = lastmodifiedby;
    }

    public ZonedDateTime getLastmodifieddatetime() {
        return lastmodifieddatetime;
    }

    public void setLastmodifieddatetime(ZonedDateTime lastmodifieddatetime) {
        this.lastmodifieddatetime = lastmodifieddatetime;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Pathway getPathway() {
        return pathway;
    }

    public void setPathway(Pathway pathway) {
        this.pathway = pathway;
    }

    public Countermeasure getCountermeasure() {
        return countermeasure;
    }

    public void setCountermeasure(Countermeasure countermeasure) {
        this.countermeasure = countermeasure;
    }

    public Scenario getScenario() {
		return scenario;
	}

	public void setScenario(Scenario scenario) {
		this.scenario = scenario;
	}

	public String getParentInstance() {
		return parentInstance;
	}

	public void setParentInstance(String parentInstance) {
		this.parentInstance = parentInstance;
	}

	public String getChildInstance() {
		return childInstance;
	}

	public void setChildInstance(String childInstance) {
		this.childInstance = childInstance;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Pathwaycountermeasurembr pathwaycountermeasurembr = (Pathwaycountermeasurembr) o;
        if(pathwaycountermeasurembr.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, pathwaycountermeasurembr.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Pathwaycountermeasurembr{" +
            "id=" + id +
            ", comment='" + comment + "'" +
            ", status='" + status + "'" +
            ", lastmodifiedby='" + lastmodifiedby + "'" +
            ", lastmodifieddatetime='" + lastmodifieddatetime + "'" +
            ", domain='" + domain + "'" +
            '}';
    }
}
