package io.github.prepayments.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;

import io.github.prepayments.domain.enumeration.PrepsFileMediumTypes;

import io.github.prepayments.domain.enumeration.PrepsFileModelType;

/**
 * A PrepsFileType.
 */
@Entity
@Table(name = "preps_file_type")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "prepsfiletype")
public class PrepsFileType implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "preps_file_type_name", nullable = false, unique = true)
    private String prepsFileTypeName;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "preps_file_medium_type", nullable = false)
    private PrepsFileMediumTypes prepsFileMediumType;

    @Column(name = "description")
    private String description;

    @Lob
    @Column(name = "file_template")
    private byte[] fileTemplate;

    @Column(name = "file_template_content_type")
    private String fileTemplateContentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "prepsfile_type")
    private PrepsFileModelType prepsfileType;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrepsFileTypeName() {
        return prepsFileTypeName;
    }

    public PrepsFileType prepsFileTypeName(String prepsFileTypeName) {
        this.prepsFileTypeName = prepsFileTypeName;
        return this;
    }

    public void setPrepsFileTypeName(String prepsFileTypeName) {
        this.prepsFileTypeName = prepsFileTypeName;
    }

    public PrepsFileMediumTypes getPrepsFileMediumType() {
        return prepsFileMediumType;
    }

    public PrepsFileType prepsFileMediumType(PrepsFileMediumTypes prepsFileMediumType) {
        this.prepsFileMediumType = prepsFileMediumType;
        return this;
    }

    public void setPrepsFileMediumType(PrepsFileMediumTypes prepsFileMediumType) {
        this.prepsFileMediumType = prepsFileMediumType;
    }

    public String getDescription() {
        return description;
    }

    public PrepsFileType description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getFileTemplate() {
        return fileTemplate;
    }

    public PrepsFileType fileTemplate(byte[] fileTemplate) {
        this.fileTemplate = fileTemplate;
        return this;
    }

    public void setFileTemplate(byte[] fileTemplate) {
        this.fileTemplate = fileTemplate;
    }

    public String getFileTemplateContentType() {
        return fileTemplateContentType;
    }

    public PrepsFileType fileTemplateContentType(String fileTemplateContentType) {
        this.fileTemplateContentType = fileTemplateContentType;
        return this;
    }

    public void setFileTemplateContentType(String fileTemplateContentType) {
        this.fileTemplateContentType = fileTemplateContentType;
    }

    public PrepsFileModelType getPrepsfileType() {
        return prepsfileType;
    }

    public PrepsFileType prepsfileType(PrepsFileModelType prepsfileType) {
        this.prepsfileType = prepsfileType;
        return this;
    }

    public void setPrepsfileType(PrepsFileModelType prepsfileType) {
        this.prepsfileType = prepsfileType;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PrepsFileType)) {
            return false;
        }
        return id != null && id.equals(((PrepsFileType) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PrepsFileType{" +
            "id=" + getId() +
            ", prepsFileTypeName='" + getPrepsFileTypeName() + "'" +
            ", prepsFileMediumType='" + getPrepsFileMediumType() + "'" +
            ", description='" + getDescription() + "'" +
            ", fileTemplate='" + getFileTemplate() + "'" +
            ", fileTemplateContentType='" + getFileTemplateContentType() + "'" +
            ", prepsfileType='" + getPrepsfileType() + "'" +
            "}";
    }
}
