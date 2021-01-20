package io.github.prepayments.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;

import io.github.prepayments.domain.enumeration.CompilationStatus;

import io.github.prepayments.domain.enumeration.CompilationType;

/**
 * A CompilationRequest.
 */
@Entity
@Table(name = "compilation_request")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "compilationrequest")
public class CompilationRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "description")
    private String description;

    @Column(name = "file_upload_id")
    private Long fileUploadId;

    @Enumerated(EnumType.STRING)
    @Column(name = "compilation_status")
    private CompilationStatus compilationStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "compilation_type")
    private CompilationType compilationType;

    @Column(name = "compilation_token")
    private String compilationToken;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public CompilationRequest description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getFileUploadId() {
        return fileUploadId;
    }

    public CompilationRequest fileUploadId(Long fileUploadId) {
        this.fileUploadId = fileUploadId;
        return this;
    }

    public void setFileUploadId(Long fileUploadId) {
        this.fileUploadId = fileUploadId;
    }

    public CompilationStatus getCompilationStatus() {
        return compilationStatus;
    }

    public CompilationRequest compilationStatus(CompilationStatus compilationStatus) {
        this.compilationStatus = compilationStatus;
        return this;
    }

    public void setCompilationStatus(CompilationStatus compilationStatus) {
        this.compilationStatus = compilationStatus;
    }

    public CompilationType getCompilationType() {
        return compilationType;
    }

    public CompilationRequest compilationType(CompilationType compilationType) {
        this.compilationType = compilationType;
        return this;
    }

    public void setCompilationType(CompilationType compilationType) {
        this.compilationType = compilationType;
    }

    public String getCompilationToken() {
        return compilationToken;
    }

    public CompilationRequest compilationToken(String compilationToken) {
        this.compilationToken = compilationToken;
        return this;
    }

    public void setCompilationToken(String compilationToken) {
        this.compilationToken = compilationToken;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CompilationRequest)) {
            return false;
        }
        return id != null && id.equals(((CompilationRequest) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CompilationRequest{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            ", fileUploadId=" + getFileUploadId() +
            ", compilationStatus='" + getCompilationStatus() + "'" +
            ", compilationType='" + getCompilationType() + "'" +
            ", compilationToken='" + getCompilationToken() + "'" +
            "}";
    }
}
