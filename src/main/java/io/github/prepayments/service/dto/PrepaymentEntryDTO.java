package io.github.prepayments.service.dto;

import java.time.LocalDate;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A DTO for the {@link io.github.prepayments.domain.PrepaymentEntry} entity.
 */
public class PrepaymentEntryDTO implements Serializable {
    
    private Long id;

    private String accountName;

    private String description;

    private String accountNumber;

    private String prepaymentNumber;

    private LocalDate prepaymentDate;

    private BigDecimal transactionAmount;

    
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PrepaymentEntryDTO)) {
            return false;
        }

        return id != null && id.equals(((PrepaymentEntryDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PrepaymentEntryDTO{" +
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
