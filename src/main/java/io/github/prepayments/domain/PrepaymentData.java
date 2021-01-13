package io.github.prepayments.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * A PrepaymentData.
 */
@Entity
@Table(name = "prepayment_data")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "prepaymentdata")
public class PrepaymentData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "account_name")
    private String accountName;

    @Column(name = "description")
    private String description;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "expense_account_number")
    private String expenseAccountNumber;

    @Column(name = "prepayment_number")
    private String prepaymentNumber;

    @Column(name = "prepayment_date")
    private LocalDate prepaymentDate;

    @Column(name = "prepayment_amount", precision = 21, scale = 2)
    private BigDecimal prepaymentAmount;

    @NotNull
    @Column(name = "prepayment_periods", nullable = false)
    private Integer prepaymentPeriods;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountName() {
        return accountName;
    }

    public PrepaymentData accountName(String accountName) {
        this.accountName = accountName;
        return this;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getDescription() {
        return description;
    }

    public PrepaymentData description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public PrepaymentData accountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
        return this;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getExpenseAccountNumber() {
        return expenseAccountNumber;
    }

    public PrepaymentData expenseAccountNumber(String expenseAccountNumber) {
        this.expenseAccountNumber = expenseAccountNumber;
        return this;
    }

    public void setExpenseAccountNumber(String expenseAccountNumber) {
        this.expenseAccountNumber = expenseAccountNumber;
    }

    public String getPrepaymentNumber() {
        return prepaymentNumber;
    }

    public PrepaymentData prepaymentNumber(String prepaymentNumber) {
        this.prepaymentNumber = prepaymentNumber;
        return this;
    }

    public void setPrepaymentNumber(String prepaymentNumber) {
        this.prepaymentNumber = prepaymentNumber;
    }

    public LocalDate getPrepaymentDate() {
        return prepaymentDate;
    }

    public PrepaymentData prepaymentDate(LocalDate prepaymentDate) {
        this.prepaymentDate = prepaymentDate;
        return this;
    }

    public void setPrepaymentDate(LocalDate prepaymentDate) {
        this.prepaymentDate = prepaymentDate;
    }

    public BigDecimal getPrepaymentAmount() {
        return prepaymentAmount;
    }

    public PrepaymentData prepaymentAmount(BigDecimal prepaymentAmount) {
        this.prepaymentAmount = prepaymentAmount;
        return this;
    }

    public void setPrepaymentAmount(BigDecimal prepaymentAmount) {
        this.prepaymentAmount = prepaymentAmount;
    }

    public Integer getPrepaymentPeriods() {
        return prepaymentPeriods;
    }

    public PrepaymentData prepaymentPeriods(Integer prepaymentPeriods) {
        this.prepaymentPeriods = prepaymentPeriods;
        return this;
    }

    public void setPrepaymentPeriods(Integer prepaymentPeriods) {
        this.prepaymentPeriods = prepaymentPeriods;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PrepaymentData)) {
            return false;
        }
        return id != null && id.equals(((PrepaymentData) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PrepaymentData{" +
            "id=" + getId() +
            ", accountName='" + getAccountName() + "'" +
            ", description='" + getDescription() + "'" +
            ", accountNumber='" + getAccountNumber() + "'" +
            ", expenseAccountNumber='" + getExpenseAccountNumber() + "'" +
            ", prepaymentNumber='" + getPrepaymentNumber() + "'" +
            ", prepaymentDate='" + getPrepaymentDate() + "'" +
            ", prepaymentAmount=" + getPrepaymentAmount() +
            ", prepaymentPeriods=" + getPrepaymentPeriods() +
            "}";
    }
}
