package io.github.prepayments.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import io.github.prepayments.domain.enumeration.PrepsFileMediumTypes;
import io.github.prepayments.domain.enumeration.PrepsFileModelType;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link io.github.prepayments.domain.PrepsFileType} entity. This class is used
 * in {@link io.github.prepayments.web.rest.PrepsFileTypeResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /preps-file-types?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class PrepsFileTypeCriteria implements Serializable, Criteria {
    /**
     * Class for filtering PrepsFileMediumTypes
     */
    public static class PrepsFileMediumTypesFilter extends Filter<PrepsFileMediumTypes> {

        public PrepsFileMediumTypesFilter() {
        }

        public PrepsFileMediumTypesFilter(PrepsFileMediumTypesFilter filter) {
            super(filter);
        }

        @Override
        public PrepsFileMediumTypesFilter copy() {
            return new PrepsFileMediumTypesFilter(this);
        }

    }
    /**
     * Class for filtering PrepsFileModelType
     */
    public static class PrepsFileModelTypeFilter extends Filter<PrepsFileModelType> {

        public PrepsFileModelTypeFilter() {
        }

        public PrepsFileModelTypeFilter(PrepsFileModelTypeFilter filter) {
            super(filter);
        }

        @Override
        public PrepsFileModelTypeFilter copy() {
            return new PrepsFileModelTypeFilter(this);
        }

    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter prepsFileTypeName;

    private PrepsFileMediumTypesFilter prepsFileMediumType;

    private StringFilter description;

    private PrepsFileModelTypeFilter prepsfileType;

    public PrepsFileTypeCriteria() {
    }

    public PrepsFileTypeCriteria(PrepsFileTypeCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.prepsFileTypeName = other.prepsFileTypeName == null ? null : other.prepsFileTypeName.copy();
        this.prepsFileMediumType = other.prepsFileMediumType == null ? null : other.prepsFileMediumType.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.prepsfileType = other.prepsfileType == null ? null : other.prepsfileType.copy();
    }

    @Override
    public PrepsFileTypeCriteria copy() {
        return new PrepsFileTypeCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getPrepsFileTypeName() {
        return prepsFileTypeName;
    }

    public void setPrepsFileTypeName(StringFilter prepsFileTypeName) {
        this.prepsFileTypeName = prepsFileTypeName;
    }

    public PrepsFileMediumTypesFilter getPrepsFileMediumType() {
        return prepsFileMediumType;
    }

    public void setPrepsFileMediumType(PrepsFileMediumTypesFilter prepsFileMediumType) {
        this.prepsFileMediumType = prepsFileMediumType;
    }

    public StringFilter getDescription() {
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public PrepsFileModelTypeFilter getPrepsfileType() {
        return prepsfileType;
    }

    public void setPrepsfileType(PrepsFileModelTypeFilter prepsfileType) {
        this.prepsfileType = prepsfileType;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PrepsFileTypeCriteria that = (PrepsFileTypeCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(prepsFileTypeName, that.prepsFileTypeName) &&
            Objects.equals(prepsFileMediumType, that.prepsFileMediumType) &&
            Objects.equals(description, that.description) &&
            Objects.equals(prepsfileType, that.prepsfileType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        prepsFileTypeName,
        prepsFileMediumType,
        description,
        prepsfileType
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PrepsFileTypeCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (prepsFileTypeName != null ? "prepsFileTypeName=" + prepsFileTypeName + ", " : "") +
                (prepsFileMediumType != null ? "prepsFileMediumType=" + prepsFileMediumType + ", " : "") +
                (description != null ? "description=" + description + ", " : "") +
                (prepsfileType != null ? "prepsfileType=" + prepsfileType + ", " : "") +
            "}";
    }

}
