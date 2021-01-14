package io.github.prepayments.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * A PrepsFileUpload.
 */
@Entity
@Table(name = "preps_file_upload")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "prepsfileupload")
public class PrepsFileUpload implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "description", nullable = false)
    private String description;

    @NotNull
    @Column(name = "file_name", nullable = false, unique = true)
    private String fileName;

    @Column(name = "period_from")
    private LocalDate periodFrom;

    @Column(name = "period_to")
    private LocalDate periodTo;

    @NotNull
    @Column(name = "preps_file_type_id", nullable = false)
    private Long prepsFileTypeId;

    
    @Lob
    @Column(name = "data_file", nullable = false)
    private byte[] dataFile;

    @Column(name = "data_file_content_type", nullable = false)
    private String dataFileContentType;

    @Column(name = "upload_successful")
    private Boolean uploadSuccessful;

    @Column(name = "upload_processed")
    private Boolean uploadProcessed;

    
    @Column(name = "upload_token", unique = true)
    private String uploadToken;

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

    public PrepsFileUpload description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFileName() {
        return fileName;
    }

    public PrepsFileUpload fileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public LocalDate getPeriodFrom() {
        return periodFrom;
    }

    public PrepsFileUpload periodFrom(LocalDate periodFrom) {
        this.periodFrom = periodFrom;
        return this;
    }

    public void setPeriodFrom(LocalDate periodFrom) {
        this.periodFrom = periodFrom;
    }

    public LocalDate getPeriodTo() {
        return periodTo;
    }

    public PrepsFileUpload periodTo(LocalDate periodTo) {
        this.periodTo = periodTo;
        return this;
    }

    public void setPeriodTo(LocalDate periodTo) {
        this.periodTo = periodTo;
    }

    public Long getPrepsFileTypeId() {
        return prepsFileTypeId;
    }

    public PrepsFileUpload prepsFileTypeId(Long prepsFileTypeId) {
        this.prepsFileTypeId = prepsFileTypeId;
        return this;
    }

    public void setPrepsFileTypeId(Long prepsFileTypeId) {
        this.prepsFileTypeId = prepsFileTypeId;
    }

    public byte[] getDataFile() {
        return dataFile;
    }

    public PrepsFileUpload dataFile(byte[] dataFile) {
        this.dataFile = dataFile;
        return this;
    }

    public void setDataFile(byte[] dataFile) {
        this.dataFile = dataFile;
    }

    public String getDataFileContentType() {
        return dataFileContentType;
    }

    public PrepsFileUpload dataFileContentType(String dataFileContentType) {
        this.dataFileContentType = dataFileContentType;
        return this;
    }

    public void setDataFileContentType(String dataFileContentType) {
        this.dataFileContentType = dataFileContentType;
    }

    public Boolean isUploadSuccessful() {
        return uploadSuccessful;
    }

    public PrepsFileUpload uploadSuccessful(Boolean uploadSuccessful) {
        this.uploadSuccessful = uploadSuccessful;
        return this;
    }

    public void setUploadSuccessful(Boolean uploadSuccessful) {
        this.uploadSuccessful = uploadSuccessful;
    }

    public Boolean isUploadProcessed() {
        return uploadProcessed;
    }

    public PrepsFileUpload uploadProcessed(Boolean uploadProcessed) {
        this.uploadProcessed = uploadProcessed;
        return this;
    }

    public void setUploadProcessed(Boolean uploadProcessed) {
        this.uploadProcessed = uploadProcessed;
    }

    public String getUploadToken() {
        return uploadToken;
    }

    public PrepsFileUpload uploadToken(String uploadToken) {
        this.uploadToken = uploadToken;
        return this;
    }

    public void setUploadToken(String uploadToken) {
        this.uploadToken = uploadToken;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PrepsFileUpload)) {
            return false;
        }
        return id != null && id.equals(((PrepsFileUpload) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PrepsFileUpload{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            ", fileName='" + getFileName() + "'" +
            ", periodFrom='" + getPeriodFrom() + "'" +
            ", periodTo='" + getPeriodTo() + "'" +
            ", prepsFileTypeId=" + getPrepsFileTypeId() +
            ", dataFile='" + getDataFile() + "'" +
            ", dataFileContentType='" + getDataFileContentType() + "'" +
            ", uploadSuccessful='" + isUploadSuccessful() + "'" +
            ", uploadProcessed='" + isUploadProcessed() + "'" +
            ", uploadToken='" + getUploadToken() + "'" +
            "}";
    }
}
