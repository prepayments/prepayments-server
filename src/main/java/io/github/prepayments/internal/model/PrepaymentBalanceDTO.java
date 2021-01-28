package io.github.prepayments.internal.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * This represents the prepayment with the amount of balance at a given date after we have applied
 * the correponding number of future amortization-entries
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PrepaymentBalanceDTO {

    private Long rowIndex;

    private String accountName;

    private String description;

    private String accountNumber;

    private String expenseAccountNumber;

    private String prepaymentNumber;

    private String prepaymentDate;

    private double prepaymentAmount;
}
