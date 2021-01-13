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
 * Criteria class for the {@link io.github.prepayments.domain.PrepaymentEntry} entity. This class is used
 * in {@link io.github.prepayments.web.rest.PrepaymentEntryResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /prepayment-entries?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class PrepaymentEntryCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter accountName;

    private StringFilter description;

    private StringFilter accountNumber;

    private StringFilter prepaymentNumber;

    private LocalDateFilter prepaymentDate;

    private BigDecimalFilter transactionAmount;

    public PrepaymentEntryCriteria() {
    }

    public PrepaymentEntryCriteria(PrepaymentEntryCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.accountName = other.accountName == null ? null : other.accountName.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.accountNumber = other.accountNumber == null ? null : other.accountNumber.copy();
        this.prepaymentNumber = other.prepaymentNumber == null ? null : other.prepaymentNumber.copy();
        this.prepaymentDate = other.prepaymentDate == null ? null : other.prepaymentDate.copy();
        this.transactionAmount = other.transactionAmount == null ? null : other.transactionAmount.copy();
    }

    @Override
    public PrepaymentEntryCriteria copy() {
        return new PrepaymentEntryCriteria(this);
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

    public BigDecimalFilter getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(BigDecimalFilter transactionAmount) {
        this.transactionAmount = transactionAmount;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PrepaymentEntryCriteria that = (PrepaymentEntryCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(accountName, that.accountName) &&
            Objects.equals(description, that.description) &&
            Objects.equals(accountNumber, that.accountNumber) &&
            Objects.equals(prepaymentNumber, that.prepaymentNumber) &&
            Objects.equals(prepaymentDate, that.prepaymentDate) &&
            Objects.equals(transactionAmount, that.transactionAmount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        accountName,
        description,
        accountNumber,
        prepaymentNumber,
        prepaymentDate,
        transactionAmount
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PrepaymentEntryCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (accountName != null ? "accountName=" + accountName + ", " : "") +
                (description != null ? "description=" + description + ", " : "") +
                (accountNumber != null ? "accountNumber=" + accountNumber + ", " : "") +
                (prepaymentNumber != null ? "prepaymentNumber=" + prepaymentNumber + ", " : "") +
                (prepaymentDate != null ? "prepaymentDate=" + prepaymentDate + ", " : "") +
                (transactionAmount != null ? "transactionAmount=" + transactionAmount + ", " : "") +
            "}";
    }

}
