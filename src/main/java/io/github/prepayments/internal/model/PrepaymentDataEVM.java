package io.github.prepayments.internal.model;

import com.poiji.annotation.ExcelCell;
import com.poiji.annotation.ExcelRow;

import java.io.Serializable;

public class PrepaymentDataEVM implements Serializable {

    @ExcelRow
    private Long rowIndex;

    @ExcelCell(0)
    private String accountName;

    @ExcelCell(1)
    private String description;

    @ExcelCell(2)
    private String accountNumber;

    @ExcelCell(3)
    private String expenseAccountNumber;

    @ExcelCell(4)
    private String prepaymentNumber;

    @ExcelCell(5)
    private String prepaymentDate;

    @ExcelCell(6)
    private double prepaymentAmount;

    @ExcelCell(7)
    private int prepaymentPeriods;
}
