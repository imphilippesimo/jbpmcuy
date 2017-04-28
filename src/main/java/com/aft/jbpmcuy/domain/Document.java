package com.aft.jbpmcuy.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Document.
 */
@Entity
@Table(name = "document")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Document implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "doc_ref", nullable = false)
    private String docRef;

    @Column(name = "treated")
    private Boolean treated;

    @Column(name = "validated")
    private Boolean validated;

    @ManyToOne
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDocRef() {
        return docRef;
    }

    public Document docRef(String docRef) {
        this.docRef = docRef;
        return this;
    }

    public void setDocRef(String docRef) {
        this.docRef = docRef;
    }

    public Boolean isTreated() {
        return treated;
    }

    public Document treated(Boolean treated) {
        this.treated = treated;
        return this;
    }

    public void setTreated(Boolean treated) {
        this.treated = treated;
    }

    public Boolean isValidated() {
        return validated;
    }

    public Document validated(Boolean validated) {
        this.validated = validated;
        return this;
    }

    public void setValidated(Boolean validated) {
        this.validated = validated;
    }

    public User getUser() {
        return user;
    }

    public Document user(User user) {
        this.user = user;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Document document = (Document) o;
        if (document.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, document.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Document{" +
            "id=" + id +
            ", docRef='" + docRef + "'" +
            ", treated='" + treated + "'" +
            ", validated='" + validated + "'" +
            '}';
    }
}
