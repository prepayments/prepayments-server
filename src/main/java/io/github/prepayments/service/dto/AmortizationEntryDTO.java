package io.github.prepayments.service.dto;

import java.time.LocalDate;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A DTO for the {@link io.github.prepayments.domain.AmortizationEntry} entity.
 */
public class AmortizationEntryDTO implements Serializable {
    
    private Long id;

    private String accountName;

    private String description;

    private String accountNumber;

    private String expenseAccountNumber;

    private String prepaymentNumber;

    private LocalDate prepaymentDate;

    private BigDecimal transactionAmount;

    private LocalDate amortizationDate;

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getExpenseAccountNumber() {
        return expenseAccountNumber;
    }

    public void setExpenseAccountNumber(String expenseAccountNumber) {
        this.expenseAccountNumber = expenseAccountNumber;
    }

    public String getPrepaymentNumber() {
        return prepaymentNumber;
    }

    public void setPrepaymentNumber(String prepaymentNumber) {
        this.prepaymentNumber = prepaymentNumber;
    }

    public LocalDate getPrepaymentDate() {
        return prepaymentDate;
    }

    public void setPrepaymentDate(LocalDate prepaymentDate) {
        this.prepaymentDate = prepaymentDate;
    }

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public LocalDate getAmortizationDate() {
        return amortizationDate;
    }

    public void setAmortizationDate(LocalDate amortizationDate) {
        this.amortizationDate = amortizationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AmortizationEntryDTO)) {
            return false;
        }

        return id != null && id.equals(((AmortizationEntryDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AmortizationEntryDTO{" +
            "id=" + getId() +
            ", accountName='" + getAccountName() + "'" +
            ", description='" + getDescription() + "'" +
            ", accountNumber='" + getAccountNumber() + "'" +
            ", expenseAccountNumber='" + getExpenseAccountNumber() + "'" +
            ", prepaymentNumber='" + getPrepaymentNumber() + "'" +
            ", prepaymentDate='" + getPrepaymentDate() + "'" +
            ", transactionAmount=" + getTransactionAmount() +
            ", amortizationDate='" + getAmortizationDate() + "'" +
            "}";
    }
}
