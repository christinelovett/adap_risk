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
 * A Pathway.
 */
@Entity
@Table(name = "pathway")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "pathway")
public class Pathway implements Serializable {

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

    @Column(name = "isrootnode")
    private Boolean isrootnode;

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
    @JoinTable(name = "pathway_category",
               joinColumns = @JoinColumn(name="pathways_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="categories_id", referencedColumnName="ID"))
    private Set<Category> categories = new HashSet<>();

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "pathway_subcategory",
               joinColumns = @JoinColumn(name="pathways_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="subcategories_id", referencedColumnName="ID"))
    private Set<Subcategory> subcategories = new HashSet<>();

    @OneToMany(mappedBy = "pathway")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Scenariopathwaymbr> scenariopathwaymbrs = new HashSet<>();

    @OneToMany(mappedBy = "parentpathway")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Pathwaypathwaymbr> parentpathwaypathwaybrs = new HashSet<>();

    @OneToMany(mappedBy = "childpathway")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Pathwaypathwaymbr> childpathwaypathwaymbrs = new HashSet<>();

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "pathway_weapon",
               joinColumns = @JoinColumn(name="pathways_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="weapons_id", referencedColumnName="ID"))
    private Set<Weapon> weapons = new HashSet<>();

    @OneToMany(mappedBy = "pathway")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Pathwaycountermeasurembr> pathwaycountermeasurembrs = new HashSet<>();

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

    public Boolean isIsrootnode() {
        return isrootnode;
    }

    public void setIsrootnode(Boolean isrootnode) {
        this.isrootnode = isrootnode;
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

    public Set<Pathwaypathwaymbr> getParentpathwaypathwaybrs() {
        return parentpathwaypathwaybrs;
    }

    public void setParentpathwaypathwaybrs(Set<Pathwaypathwaymbr> pathwaypathwaymbrs) {
        this.parentpathwaypathwaybrs = pathwaypathwaymbrs;
    }

    public Set<Pathwaypathwaymbr> getChildpathwaypathwaymbrs() {
        return childpathwaypathwaymbrs;
    }

    public void setChildpathwaypathwaymbrs(Set<Pathwaypathwaymbr> pathwaypathwaymbrs) {
        this.childpathwaypathwaymbrs = pathwaypathwaymbrs;
    }

    public Set<Weapon> getWeapons() {
        return weapons;
    }

    public void setWeapons(Set<Weapon> weapons) {
        this.weapons = weapons;
    }

    public Set<Pathwaycountermeasurembr> getPathwaycountermeasurembrs() {
        return pathwaycountermeasurembrs;
    }

    public void setPathwaycountermeasurembrs(Set<Pathwaycountermeasurembr> pathwaycountermeasurembrs) {
        this.pathwaycountermeasurembrs = pathwaycountermeasurembrs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Pathway pathway = (Pathway) o;
        if(pathway.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, pathway.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Pathway{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", nameshort='" + nameshort + "'" +
            ", description='" + description + "'" +
            ", isrootnode='" + isrootnode + "'" +
            ", isabstract='" + isabstract + "'" +
            ", status='" + status + "'" +
            ", lastmodifiedby='" + lastmodifiedby + "'" +
            ", lastmodifieddatetime='" + lastmodifieddatetime + "'" +
            ", domain='" + domain + "'" +
            '}';
    }
}
