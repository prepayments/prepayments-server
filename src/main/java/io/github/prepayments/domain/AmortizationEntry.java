package io.github.prepayments.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * A AmortizationEntry.
 */
@Entity
@Table(name = "amortization_entry")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "amortizationentry")
public class AmortizationEntry implements Serializable {

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

    @Column(name = "transaction_amount", precision = 21, scale = 2)
    private BigDecimal transactionAmount;

    @Column(name = "amortization_date")
    private LocalDate amortizationDate;

    @Column(name = "upload_token")
    private String uploadToken;

    @Column(name = "prepayment_data_id")
    private Long prepaymentDataId;

    @Column(name = "compilation_token")
    private String compilationToken;

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

    public AmortizationEntry accountName(String accountName) {
        this.accountName = accountName;
        return this;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getDescription() {
        return description;
    }

    public AmortizationEntry description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public AmortizationEntry accountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
        return this;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getExpenseAccountNumber() {
        return expenseAccountNumber;
    }

    public AmortizationEntry expenseAccountNumber(String expenseAccountNumber) {
        this.expenseAccountNumber = expenseAccountNumber;
        return this;
    }

    public void setExpenseAccountNumber(String expenseAccountNumber) {
        this.expenseAccountNumber = expenseAccountNumber;
    }

    public String getPrepaymentNumber() {
        return prepaymentNumber;
    }

    public AmortizationEntry prepaymentNumber(String prepaymentNumber) {
        this.prepaymentNumber = prepaymentNumber;
        return this;
    }

    public void setPrepaymentNumber(String prepaymentNumber) {
        this.prepaymentNumber = prepaymentNumber;
    }

    public LocalDate getPrepaymentDate() {
        return prepaymentDate;
    }

    public AmortizationEntry prepaymentDate(LocalDate prepaymentDate) {
        this.prepaymentDate = prepaymentDate;
        return this;
    }

    public void setPrepaymentDate(LocalDate prepaymentDate) {
        this.prepaymentDate = prepaymentDate;
    }

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public AmortizationEntry transactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
        return this;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public LocalDate getAmortizationDate() {
        return amortizationDate;
    }

    public AmortizationEntry amortizationDate(LocalDate amortizationDate) {
        this.amortizationDate = amortizationDate;
        return this;
    }

    public void setAmortizationDate(LocalDate amortizationDate) {
        this.amortizationDate = amortizationDate;
    }

    public String getUploadToken() {
        return uploadToken;
    }

    public AmortizationEntry uploadToken(String uploadToken) {
        this.uploadToken = uploadToken;
        return this;
    }

    public void setUploadToken(String uploadToken) {
        this.uploadToken = uploadToken;
    }

    public Long getPrepaymentDataId() {
        return prepaymentDataId;
    }

    public AmortizationEntry prepaymentDataId(Long prepaymentDataId) {
        this.prepaymentDataId = prepaymentDataId;
        return this;
    }

    public void setPrepaymentDataId(Long prepaymentDataId) {
        this.prepaymentDataId = prepaymentDataId;
    }

    public String getCompilationToken() {
        return compilationToken;
    }

    public AmortizationEntry compilationToken(String compilationToken) {
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
        if (!(o instanceof AmortizationEntry)) {
            return false;
        }
        return id != null && id.equals(((AmortizationEntry) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AmortizationEntry{" +
            "id=" + getId() +
            ", accountName='" + getAccountName() + "'" +
            ", description='" + getDescription() + "'" +
            ", accountNumber='" + getAccountNumber() + "'" +
            ", expenseAccountNumber='" + getExpenseAccountNumber() + "'" +
            ", prepaymentNumber='" + getPrepaymentNumber() + "'" +
            ", prepaymentDate='" + getPrepaymentDate() + "'" +
            ", transactionAmount=" + getTransactionAmount() +
            ", amortizationDate='" + getAmortizationDate() + "'" +
            ", uploadToken='" + getUploadToken() + "'" +
            ", prepaymentDataId=" + getPrepaymentDataId() +
            ", compilationToken='" + getCompilationToken() + "'" +
            "}";
    }
}
