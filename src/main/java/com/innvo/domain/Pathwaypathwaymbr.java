package com.innvo.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.innvo.domain.enumeration.Operator;

/**
 * A Pathwaypathwaymbr.
 */
@Entity
@Table(name = "pathwaypathwaymbr")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "pathwaypathwaymbr")
public class Pathwaypathwaymbr implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Size(max = 100)
    @Column(name = "comment", length = 100)
    private String comment;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "logicoperator", nullable = false)
    private Operator logicoperator;

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
    private Pathway parentpathway;

    @ManyToOne
    @NotNull
    private Pathway childpathway;
    
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

    public Operator getLogicoperator() {
        return logicoperator;
    }

    public void setLogicoperator(Operator logicoperator) {
        this.logicoperator = logicoperator;
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

    public Pathway getParentpathway() {
        return parentpathway;
    }

    public void setParentpathway(Pathway pathway) {
        this.parentpathway = pathway;
    }

    public Pathway getChildpathway() {
        return childpathway;
    }

    public void setChildpathway(Pathway pathway) {
        this.childpathway = pathway;
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
        Pathwaypathwaymbr pathwaypathwaymbr = (Pathwaypathwaymbr) o;
        if(pathwaypathwaymbr.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, pathwaypathwaymbr.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Pathwaypathwaymbr{" +
            "id=" + id +
            ", comment='" + comment + "'" +
            ", logicoperator='" + logicoperator + "'" +
            ", status='" + status + "'" +
            ", lastmodifiedby='" + lastmodifiedby + "'" +
            ", lastmodifieddatetime='" + lastmodifieddatetime + "'" +
            ", domain='" + domain + "'" +
            '}';
    }
}
