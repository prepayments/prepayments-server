package io.github.prepayments.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.LocalDateFilter;

/**
 * Criteria class for the {@link io.github.prepayments.domain.PrepsFileUpload} entity. This class is used
 * in {@link io.github.prepayments.web.rest.PrepsFileUploadResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /preps-file-uploads?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class PrepsFileUploadCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter description;

    private StringFilter fileName;

    private LocalDateFilter periodFrom;

    private LocalDateFilter periodTo;

    private LongFilter prepsFileTypeId;

    private BooleanFilter uploadSuccessful;

    private BooleanFilter uploadProcessed;

    private StringFilter uploadToken;

    public PrepsFileUploadCriteria() {
    }

    public PrepsFileUploadCriteria(PrepsFileUploadCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.fileName = other.fileName == null ? null : other.fileName.copy();
        this.periodFrom = other.periodFrom == null ? null : other.periodFrom.copy();
        this.periodTo = other.periodTo == null ? null : other.periodTo.copy();
        this.prepsFileTypeId = other.prepsFileTypeId == null ? null : other.prepsFileTypeId.copy();
        this.uploadSuccessful = other.uploadSuccessful == null ? null : other.uploadSuccessful.copy();
        this.uploadProcessed = other.uploadProcessed == null ? null : other.uploadProcessed.copy();
        this.uploadToken = other.uploadToken == null ? null : other.uploadToken.copy();
    }

    @Override
    public PrepsFileUploadCriteria copy() {
        return new PrepsFileUploadCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getDescription() {
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public StringFilter getFileName() {
        return fileName;
    }

    public void setFileName(StringFilter fileName) {
        this.fileName = fileName;
    }

    public LocalDateFilter getPeriodFrom() {
        return periodFrom;
    }

    public void setPeriodFrom(LocalDateFilter periodFrom) {
        this.periodFrom = periodFrom;
    }

    public LocalDateFilter getPeriodTo() {
        return periodTo;
    }

    public void setPeriodTo(LocalDateFilter periodTo) {
        this.periodTo = periodTo;
    }

    public LongFilter getPrepsFileTypeId() {
        return prepsFileTypeId;
    }

    public void setPrepsFileTypeId(LongFilter prepsFileTypeId) {
        this.prepsFileTypeId = prepsFileTypeId;
    }

    public BooleanFilter getUploadSuccessful() {
        return uploadSuccessful;
    }

    public void setUploadSuccessful(BooleanFilter uploadSuccessful) {
        this.uploadSuccessful = uploadSuccessful;
    }

    public BooleanFilter getUploadProcessed() {
        return uploadProcessed;
    }

    public void setUploadProcessed(BooleanFilter uploadProcessed) {
        this.uploadProcessed = uploadProcessed;
    }

    public StringFilter getUploadToken() {
        return uploadToken;
    }

    public void setUploadToken(StringFilter uploadToken) {
        this.uploadToken = uploadToken;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PrepsFileUploadCriteria that = (PrepsFileUploadCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(description, that.description) &&
            Objects.equals(fileName, that.fileName) &&
            Objects.equals(periodFrom, that.periodFrom) &&
            Objects.equals(periodTo, that.periodTo) &&
            Objects.equals(prepsFileTypeId, that.prepsFileTypeId) &&
            Objects.equals(uploadSuccessful, that.uploadSuccessful) &&
            Objects.equals(uploadProcessed, that.uploadProcessed) &&
            Objects.equals(uploadToken, that.uploadToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        description,
        fileName,
        periodFrom,
        periodTo,
        prepsFileTypeId,
        uploadSuccessful,
        uploadProcessed,
        uploadToken
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PrepsFileUploadCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (description != null ? "description=" + description + ", " : "") +
                (fileName != null ? "fileName=" + fileName + ", " : "") +
                (periodFrom != null ? "periodFrom=" + periodFrom + ", " : "") +
                (periodTo != null ? "periodTo=" + periodTo + ", " : "") +
                (prepsFileTypeId != null ? "prepsFileTypeId=" + prepsFileTypeId + ", " : "") +
                (uploadSuccessful != null ? "uploadSuccessful=" + uploadSuccessful + ", " : "") +
                (uploadProcessed != null ? "uploadProcessed=" + uploadProcessed + ", " : "") +
                (uploadToken != null ? "uploadToken=" + uploadToken + ", " : "") +
            "}";
    }

}
