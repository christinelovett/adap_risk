package com.innvo.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Scenario.
 */
@Entity
@Table(name = "scenario")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "scenario")
public class Scenario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @NotNull
    @Size(max = 20)
    @Column(name = "nameshort", length = 20, nullable = false)
    private String nameshort;

    @Size(max = 255)
    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "isabstract")
    private Boolean isabstract;

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
    private Recordtype recordtype;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "scenario_category",
               joinColumns = @JoinColumn(name="scenarios_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="categories_id", referencedColumnName="ID"))
    private Set<Category> categories = new HashSet<>();

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "scenario_subcategory",
               joinColumns = @JoinColumn(name="scenarios_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="subcategories_id", referencedColumnName="ID"))
    private Set<Subcategory> subcategories = new HashSet<>();

    @OneToMany(mappedBy = "scenario")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Scenariopathwaymbr> scenariopathwaymbrs = new HashSet<>();

    @OneToMany(mappedBy = "scenario")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Pathwaypathwaymbr> pathwaypathwaymbrs = new HashSet<>();
  
    @OneToMany(mappedBy = "scenario")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Pathwaycountermeasurembr> pathwaycountermeasurembs = new HashSet<>();

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

    public String getNameshort() {
        return nameshort;
    }

    public void setNameshort(String nameshort) {
        this.nameshort = nameshort;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean isIsabstract() {
        return isabstract;
    }

    public void setIsabstract(Boolean isabstract) {
        this.isabstract = isabstract;
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

    public Recordtype getRecordtype() {
        return recordtype;
    }

    public void setRecordtype(Recordtype recordtype) {
        this.recordtype = recordtype;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    public Set<Subcategory> getSubcategories() {
        return subcategories;
    }

    public void setSubcategories(Set<Subcategory> subcategories) {
        this.subcategories = subcategories;
    }

    public Set<Scenariopathwaymbr> getScenariopathwaymbrs() {
        return scenariopathwaymbrs;
    }

    public void setScenariopathwaymbrs(Set<Scenariopathwaymbr> scenariopathwaymbrs) {
        this.scenariopathwaymbrs = scenariopathwaymbrs;
    }
     
    public Set<Pathwaypathwaymbr> getPathwaypathwaymbrs() {
		return pathwaypathwaymbrs;
	}

	public void setPathwaypathwaymbrs(Set<Pathwaypathwaymbr> pathwaypathwaymbrs) {
		this.pathwaypathwaymbrs = pathwaypathwaymbrs;
	}

    public Set<Pathwaycountermeasurembr> getPathwaycountermeasurembs() {
		return pathwaycountermeasurembs;
	}

	public void setPathwaycountermeasurembs(Set<Pathwaycountermeasurembr> pathwaycountermeasurembs) {
		this.pathwaycountermeasurembs = pathwaycountermeasurembs;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Scenario scenario = (Scenario) o;
        if(scenario.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, scenario.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Scenario{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", nameshort='" + nameshort + "'" +
            ", description='" + description + "'" +
            ", isabstract='" + isabstract + "'" +
            ", status='" + status + "'" +
            ", lastmodifiedby='" + lastmodifiedby + "'" +
            ", lastmodifieddatetime='" + lastmodifieddatetime + "'" +
            ", domain='" + domain + "'" +
            '}';
    }
}
