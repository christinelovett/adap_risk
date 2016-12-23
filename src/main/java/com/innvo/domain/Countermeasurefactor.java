package com.innvo.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A Countermeasurefactor.
 */
@Entity
@Table(name = "countermeasurefactor")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "countermeasurefactor")
public class Countermeasurefactor implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Size(max = 25)
    @Column(name = "version", length = 25)
    private String version;

    @Column(name = "factorboolean")
    private Boolean factorboolean;

    @Column(name = "value", precision=10, scale=2)
    private BigDecimal value;

    @Size(max = 100)
    @Column(name = "comment", length = 100)
    private String comment;

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
    private Countermeasure countermeasure;

    @ManyToOne
    @NotNull
    private Countermeasurefactortype countermeasurefactortype;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public Countermeasurefactor version(String version) {
        this.version = version;
        return this;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Boolean isFactorboolean() {
        return factorboolean;
    }

    public Countermeasurefactor factorboolean(Boolean factorboolean) {
        this.factorboolean = factorboolean;
        return this;
    }

    public void setFactorboolean(Boolean factorboolean) {
        this.factorboolean = factorboolean;
    }

    public BigDecimal getValue() {
        return value;
    }

    public Countermeasurefactor value(BigDecimal value) {
        this.value = value;
        return this;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getComment() {
        return comment;
    }

    public Countermeasurefactor comment(String comment) {
        this.comment = comment;
        return this;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getStatus() {
        return status;
    }

    public Countermeasurefactor status(String status) {
        this.status = status;
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastmodifiedby() {
        return lastmodifiedby;
    }

    public Countermeasurefactor lastmodifiedby(String lastmodifiedby) {
        this.lastmodifiedby = lastmodifiedby;
        return this;
    }

    public void setLastmodifiedby(String lastmodifiedby) {
        this.lastmodifiedby = lastmodifiedby;
    }

    public ZonedDateTime getLastmodifieddatetime() {
        return lastmodifieddatetime;
    }

    public Countermeasurefactor lastmodifieddatetime(ZonedDateTime lastmodifieddatetime) {
        this.lastmodifieddatetime = lastmodifieddatetime;
        return this;
    }

    public void setLastmodifieddatetime(ZonedDateTime lastmodifieddatetime) {
        this.lastmodifieddatetime = lastmodifieddatetime;
    }

    public String getDomain() {
        return domain;
    }

    public Countermeasurefactor domain(String domain) {
        this.domain = domain;
        return this;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Countermeasure getCountermeasure() {
        return countermeasure;
    }

    public Countermeasurefactor countermeasure(Countermeasure countermeasure) {
        this.countermeasure = countermeasure;
        return this;
    }

    public void setCountermeasure(Countermeasure countermeasure) {
        this.countermeasure = countermeasure;
    }

    public Countermeasurefactortype getCountermeasurefactortype() {
        return countermeasurefactortype;
    }

    public Countermeasurefactor countermeasurefactortype(Countermeasurefactortype countermeasurefactortype) {
        this.countermeasurefactortype = countermeasurefactortype;
        return this;
    }

    public void setCountermeasurefactortype(Countermeasurefactortype countermeasurefactortype) {
        this.countermeasurefactortype = countermeasurefactortype;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Countermeasurefactor countermeasurefactor = (Countermeasurefactor) o;
        if(countermeasurefactor.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, countermeasurefactor.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Countermeasurefactor{" +
            "id=" + id +
            ", version='" + version + "'" +
            ", factorboolean='" + factorboolean + "'" +
            ", value='" + value + "'" +
            ", comment='" + comment + "'" +
            ", status='" + status + "'" +
            ", lastmodifiedby='" + lastmodifiedby + "'" +
            ", lastmodifieddatetime='" + lastmodifieddatetime + "'" +
            ", domain='" + domain + "'" +
            '}';
    }
}
