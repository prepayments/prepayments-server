package io.github.prepayments.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * A PrepaymentEntry.
 */
@Entity
@Table(name = "prepayment_entry")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "prepaymententry")
public class PrepaymentEntry implements Serializable {

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

    @Column(name = "prepayment_number")
    private String prepaymentNumber;

    @Column(name = "prepayment_date")
    private LocalDate prepaymentDate;

    @Column(name = "transaction_amount", precision = 21, scale = 2)
    private BigDecimal transactionAmount;

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

    public PrepaymentEntry accountName(String accountName) {
        this.accountName = accountName;
        return this;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getDescription() {
        return description;
    }

    public PrepaymentEntry description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public PrepaymentEntry accountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
        return this;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getPrepaymentNumber() {
        return prepaymentNumber;
    }

    public PrepaymentEntry prepaymentNumber(String prepaymentNumber) {
        this.prepaymentNumber = prepaymentNumber;
        return this;
    }

    public void setPrepaymentNumber(String prepaymentNumber) {
        this.prepaymentNumber = prepaymentNumber;
    }

    public LocalDate getPrepaymentDate() {
        return prepaymentDate;
    }

    public PrepaymentEntry prepaymentDate(LocalDate prepaymentDate) {
        this.prepaymentDate = prepaymentDate;
        return this;
    }

    public void setPrepaymentDate(LocalDate prepaymentDate) {
        this.prepaymentDate = prepaymentDate;
    }

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public PrepaymentEntry transactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
        return this;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PrepaymentEntry)) {
            return false;
        }
        return id != null && id.equals(((PrepaymentEntry) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PrepaymentEntry{" +
            "id=" + getId() +
            ", accountName='" + getAccountName() + "'" +
            ", description='" + getDescription() + "'" +
            ", accountNumber='" + getAccountNumber() + "'" +
            ", prepaymentNumber='" + getPrepaymentNumber() + "'" +
            ", prepaymentDate='" + getPrepaymentDate() + "'" +
            ", transactionAmount=" + getTransactionAmount() +
            "}";
    }
}
