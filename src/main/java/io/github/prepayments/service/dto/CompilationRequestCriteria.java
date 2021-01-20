package io.github.prepayments.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import io.github.prepayments.domain.enumeration.CompilationStatus;
import io.github.prepayments.domain.enumeration.CompilationType;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link io.github.prepayments.domain.CompilationRequest} entity. This class is used
 * in {@link io.github.prepayments.web.rest.CompilationRequestResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /compilation-requests?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class CompilationRequestCriteria implements Serializable, Criteria {
    /**
     * Class for filtering CompilationStatus
     */
    public static class CompilationStatusFilter extends Filter<CompilationStatus> {

        public CompilationStatusFilter() {
        }

        public CompilationStatusFilter(CompilationStatusFilter filter) {
            super(filter);
        }

        @Override
        public CompilationStatusFilter copy() {
            return new CompilationStatusFilter(this);
        }

    }
    /**
     * Class for filtering CompilationType
     */
    public static class CompilationTypeFilter extends Filter<CompilationType> {

        public CompilationTypeFilter() {
        }

        public CompilationTypeFilter(CompilationTypeFilter filter) {
            super(filter);
        }

        @Override
        public CompilationTypeFilter copy() {
            return new CompilationTypeFilter(this);
        }

    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter description;

    private LongFilter fileUploadId;

    private CompilationStatusFilter compilationStatus;

    private CompilationTypeFilter compilationType;

    private StringFilter compilationToken;

    public CompilationRequestCriteria() {
    }

    public CompilationRequestCriteria(CompilationRequestCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.fileUploadId = other.fileUploadId == null ? null : other.fileUploadId.copy();
        this.compilationStatus = other.compilationStatus == null ? null : other.compilationStatus.copy();
        this.compilationType = other.compilationType == null ? null : other.compilationType.copy();
        this.compilationToken = other.compilationToken == null ? null : other.compilationToken.copy();
    }

    @Override
    public CompilationRequestCriteria copy() {
        return new CompilationRequestCriteria(this);
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

    public LongFilter getFileUploadId() {
        return fileUploadId;
    }

    public void setFileUploadId(LongFilter fileUploadId) {
        this.fileUploadId = fileUploadId;
    }

    public CompilationStatusFilter getCompilationStatus() {
        return compilationStatus;
    }

    public void setCompilationStatus(CompilationStatusFilter compilationStatus) {
        this.compilationStatus = compilationStatus;
    }

    public CompilationTypeFilter getCompilationType() {
        return compilationType;
    }

    public void setCompilationType(CompilationTypeFilter compilationType) {
        this.compilationType = compilationType;
    }

    public StringFilter getCompilationToken() {
        return compilationToken;
    }

    public void setCompilationToken(StringFilter compilationToken) {
        this.compilationToken = compilationToken;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CompilationRequestCriteria that = (CompilationRequestCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(description, that.description) &&
            Objects.equals(fileUploadId, that.fileUploadId) &&
            Objects.equals(compilationStatus, that.compilationStatus) &&
            Objects.equals(compilationType, that.compilationType) &&
            Objects.equals(compilationToken, that.compilationToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        description,
        fileUploadId,
        compilationStatus,
        compilationType,
        compilationToken
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CompilationRequestCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (description != null ? "description=" + description + ", " : "") +
                (fileUploadId != null ? "fileUploadId=" + fileUploadId + ", " : "") +
                (compilationStatus != null ? "compilationStatus=" + compilationStatus + ", " : "") +
                (compilationType != null ? "compilationType=" + compilationType + ", " : "") +
                (compilationToken != null ? "compilationToken=" + compilationToken + ", " : "") +
            "}";
    }

}
