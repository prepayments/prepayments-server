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
import io.github.jhipster.service.filter.BigDecimalFilter;
import io.github.jhipster.service.filter.LocalDateFilter;

/**
 * Criteria class for the {@link io.github.prepayments.domain.PrepaymentData} entity. This class is used
 * in {@link io.github.prepayments.web.rest.PrepaymentDataResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /prepayment-data?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class PrepaymentDataCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter accountName;

    private StringFilter description;

    private StringFilter accountNumber;

    private StringFilter expenseAccountNumber;

    private StringFilter prepaymentNumber;

    private LocalDateFilter prepaymentDate;

    private BigDecimalFilter prepaymentAmount;

    private IntegerFilter prepaymentPeriods;

    public PrepaymentDataCriteria() {
    }

    public PrepaymentDataCriteria(PrepaymentDataCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.accountName = other.accountName == null ? null : other.accountName.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.accountNumber = other.accountNumber == null ? null : other.accountNumber.copy();
        this.expenseAccountNumber = other.expenseAccountNumber == null ? null : other.expenseAccountNumber.copy();
        this.prepaymentNumber = other.prepaymentNumber == null ? null : other.prepaymentNumber.copy();
        this.prepaymentDate = other.prepaymentDate == null ? null : other.prepaymentDate.copy();
        this.prepaymentAmount = other.prepaymentAmount == null ? null : other.prepaymentAmount.copy();
        this.prepaymentPeriods = other.prepaymentPeriods == null ? null : other.prepaymentPeriods.copy();
    }

    @Override
    public PrepaymentDataCriteria copy() {
        return new PrepaymentDataCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getAccountName() {
        return accountName;
    }

    public void setAccountName(StringFilter accountName) {
        this.accountName = accountName;
    }

    public StringFilter getDescription() {
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public StringFilter getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(StringFilter accountNumber) {
        this.accountNumber = accountNumber;
    }

    public StringFilter getExpenseAccountNumber() {
        return expenseAccountNumber;
    }

    public void setExpenseAccountNumber(StringFilter expenseAccountNumber) {
        this.expenseAccountNumber = expenseAccountNumber;
    }

    public StringFilter getPrepaymentNumber() {
        return prepaymentNumber;
    }

    public void setPrepaymentNumber(StringFilter prepaymentNumber) {
        this.prepaymentNumber = prepaymentNumber;
    }

    public LocalDateFilter getPrepaymentDate() {
        return prepaymentDate;
    }

    public void setPrepaymentDate(LocalDateFilter prepaymentDate) {
        this.prepaymentDate = prepaymentDate;
    }

    public BigDecimalFilter getPrepaymentAmount() {
        return prepaymentAmount;
    }

    public void setPrepaymentAmount(BigDecimalFilter prepaymentAmount) {
        this.prepaymentAmount = prepaymentAmount;
    }

    public IntegerFilter getPrepaymentPeriods() {
        return prepaymentPeriods;
    }

    public void setPrepaymentPeriods(IntegerFilter prepaymentPeriods) {
        this.prepaymentPeriods = prepaymentPeriods;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PrepaymentDataCriteria that = (PrepaymentDataCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(accountName, that.accountName) &&
            Objects.equals(description, that.description) &&
            Objects.equals(accountNumber, that.accountNumber) &&
            Objects.equals(expenseAccountNumber, that.expenseAccountNumber) &&
            Objects.equals(prepaymentNumber, that.prepaymentNumber) &&
            Objects.equals(prepaymentDate, that.prepaymentDate) &&
            Objects.equals(prepaymentAmount, that.prepaymentAmount) &&
            Objects.equals(prepaymentPeriods, that.prepaymentPeriods);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        accountName,
        description,
        accountNumber,
        expenseAccountNumber,
        prepaymentNumber,
        prepaymentDate,
        prepaymentAmount,
        prepaymentPeriods
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PrepaymentDataCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (accountName != null ? "accountName=" + accountName + ", " : "") +
                (description != null ? "description=" + description + ", " : "") +
                (accountNumber != null ? "accountNumber=" + accountNumber + ", " : "") +
                (expenseAccountNumber != null ? "expenseAccountNumber=" + expenseAccountNumber + ", " : "") +
                (prepaymentNumber != null ? "prepaymentNumber=" + prepaymentNumber + ", " : "") +
                (prepaymentDate != null ? "prepaymentDate=" + prepaymentDate + ", " : "") +
                (prepaymentAmount != null ? "prepaymentAmount=" + prepaymentAmount + ", " : "") +
                (prepaymentPeriods != null ? "prepaymentPeriods=" + prepaymentPeriods + ", " : "") +
            "}";
    }

}
