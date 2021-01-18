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

    public PrepaymentDataEVM(Long rowIndex, String accountName, String description, String accountNumber, String expenseAccountNumber, String prepaymentNumber, String prepaymentDate,
                             double prepaymentAmount, int prepaymentPeriods) {
        this.rowIndex = rowIndex;
        this.accountName = accountName;
        this.description = description;
        this.accountNumber = accountNumber;
        this.expenseAccountNumber = expenseAccountNumber;
        this.prepaymentNumber = prepaymentNumber;
        this.prepaymentDate = prepaymentDate;
        this.prepaymentAmount = prepaymentAmount;
        this.prepaymentPeriods = prepaymentPeriods;
    }

    public PrepaymentDataEVM() {
    }

    public static PrepaymentDataEVMBuilder builder() {
        return new PrepaymentDataEVMBuilder();
    }

    public Long getRowIndex() {
        return this.rowIndex;
    }

    public String getAccountName() {
        return this.accountName;
    }

    public String getDescription() {
        return this.description;
    }

    public String getAccountNumber() {
        return this.accountNumber;
    }

    public String getExpenseAccountNumber() {
        return this.expenseAccountNumber;
    }

    public String getPrepaymentNumber() {
        return this.prepaymentNumber;
    }

    public String getPrepaymentDate() {
        return this.prepaymentDate;
    }

    public double getPrepaymentAmount() {
        return this.prepaymentAmount;
    }

    public int getPrepaymentPeriods() {
        return this.prepaymentPeriods;
    }

    public void setRowIndex(Long rowIndex) {
        this.rowIndex = rowIndex;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setExpenseAccountNumber(String expenseAccountNumber) {
        this.expenseAccountNumber = expenseAccountNumber;
    }

    public void setPrepaymentNumber(String prepaymentNumber) {
        this.prepaymentNumber = prepaymentNumber;
    }

    public void setPrepaymentDate(String prepaymentDate) {
        this.prepaymentDate = prepaymentDate;
    }

    public void setPrepaymentAmount(double prepaymentAmount) {
        this.prepaymentAmount = prepaymentAmount;
    }

    public void setPrepaymentPeriods(int prepaymentPeriods) {
        this.prepaymentPeriods = prepaymentPeriods;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PrepaymentDataEVM)) {
            return false;
        }
        final PrepaymentDataEVM other = (PrepaymentDataEVM) o;
        if (!other.canEqual((Object) this)) {
            return false;
        }
        final Object this$rowIndex = this.getRowIndex();
        final Object other$rowIndex = other.getRowIndex();
        if (this$rowIndex == null ? other$rowIndex != null : !this$rowIndex.equals(other$rowIndex)) {
            return false;
        }
        final Object this$accountName = this.getAccountName();
        final Object other$accountName = other.getAccountName();
        if (this$accountName == null ? other$accountName != null : !this$accountName.equals(other$accountName)) {
            return false;
        }
        final Object this$description = this.getDescription();
        final Object other$description = other.getDescription();
        if (this$description == null ? other$description != null : !this$description.equals(other$description)) {
            return false;
        }
        final Object this$accountNumber = this.getAccountNumber();
        final Object other$accountNumber = other.getAccountNumber();
        if (this$accountNumber == null ? other$accountNumber != null : !this$accountNumber.equals(other$accountNumber)) {
            return false;
        }
        final Object this$expenseAccountNumber = this.getExpenseAccountNumber();
        final Object other$expenseAccountNumber = other.getExpenseAccountNumber();
        if (this$expenseAccountNumber == null ? other$expenseAccountNumber != null : !this$expenseAccountNumber.equals(other$expenseAccountNumber)) {
            return false;
        }
        final Object this$prepaymentNumber = this.getPrepaymentNumber();
        final Object other$prepaymentNumber = other.getPrepaymentNumber();
        if (this$prepaymentNumber == null ? other$prepaymentNumber != null : !this$prepaymentNumber.equals(other$prepaymentNumber)) {
            return false;
        }
        final Object this$prepaymentDate = this.getPrepaymentDate();
        final Object other$prepaymentDate = other.getPrepaymentDate();
        if (this$prepaymentDate == null ? other$prepaymentDate != null : !this$prepaymentDate.equals(other$prepaymentDate)) {
            return false;
        }
        if (Double.compare(this.getPrepaymentAmount(), other.getPrepaymentAmount()) != 0) {
            return false;
        }
        if (this.getPrepaymentPeriods() != other.getPrepaymentPeriods()) {
            return false;
        }
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof PrepaymentDataEVM;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $rowIndex = this.getRowIndex();
        result = result * PRIME + ($rowIndex == null ? 43 : $rowIndex.hashCode());
        final Object $accountName = this.getAccountName();
        result = result * PRIME + ($accountName == null ? 43 : $accountName.hashCode());
        final Object $description = this.getDescription();
        result = result * PRIME + ($description == null ? 43 : $description.hashCode());
        final Object $accountNumber = this.getAccountNumber();
        result = result * PRIME + ($accountNumber == null ? 43 : $accountNumber.hashCode());
        final Object $expenseAccountNumber = this.getExpenseAccountNumber();
        result = result * PRIME + ($expenseAccountNumber == null ? 43 : $expenseAccountNumber.hashCode());
        final Object $prepaymentNumber = this.getPrepaymentNumber();
        result = result * PRIME + ($prepaymentNumber == null ? 43 : $prepaymentNumber.hashCode());
        final Object $prepaymentDate = this.getPrepaymentDate();
        result = result * PRIME + ($prepaymentDate == null ? 43 : $prepaymentDate.hashCode());
        final long $prepaymentAmount = Double.doubleToLongBits(this.getPrepaymentAmount());
        result = result * PRIME + (int) ($prepaymentAmount >>> 32 ^ $prepaymentAmount);
        result = result * PRIME + this.getPrepaymentPeriods();
        return result;
    }

    public String toString() {
        return "PrepaymentDataEVM(rowIndex=" + this.getRowIndex() + ", accountName=" + this.getAccountName() + ", description=" + this.getDescription() + ", accountNumber=" + this.getAccountNumber() +
            ", expenseAccountNumber=" + this.getExpenseAccountNumber() + ", prepaymentNumber=" + this.getPrepaymentNumber() + ", prepaymentDate=" + this.getPrepaymentDate() + ", prepaymentAmount=" +
            this.getPrepaymentAmount() + ", prepaymentPeriods=" + this.getPrepaymentPeriods() + ")";
    }

    public static class PrepaymentDataEVMBuilder {
        private Long rowIndex;
        private String accountName;
        private String description;
        private String accountNumber;
        private String expenseAccountNumber;
        private String prepaymentNumber;
        private String prepaymentDate;
        private double prepaymentAmount;
        private int prepaymentPeriods;

        PrepaymentDataEVMBuilder() {
        }

        public PrepaymentDataEVM.PrepaymentDataEVMBuilder rowIndex(Long rowIndex) {
            this.rowIndex = rowIndex;
            return this;
        }

        public PrepaymentDataEVM.PrepaymentDataEVMBuilder accountName(String accountName) {
            this.accountName = accountName;
            return this;
        }

        public PrepaymentDataEVM.PrepaymentDataEVMBuilder description(String description) {
            this.description = description;
            return this;
        }

        public PrepaymentDataEVM.PrepaymentDataEVMBuilder accountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
            return this;
        }

        public PrepaymentDataEVM.PrepaymentDataEVMBuilder expenseAccountNumber(String expenseAccountNumber) {
            this.expenseAccountNumber = expenseAccountNumber;
            return this;
        }

        public PrepaymentDataEVM.PrepaymentDataEVMBuilder prepaymentNumber(String prepaymentNumber) {
            this.prepaymentNumber = prepaymentNumber;
            return this;
        }

        public PrepaymentDataEVM.PrepaymentDataEVMBuilder prepaymentDate(String prepaymentDate) {
            this.prepaymentDate = prepaymentDate;
            return this;
        }

        public PrepaymentDataEVM.PrepaymentDataEVMBuilder prepaymentAmount(double prepaymentAmount) {
            this.prepaymentAmount = prepaymentAmount;
            return this;
        }

        public PrepaymentDataEVM.PrepaymentDataEVMBuilder prepaymentPeriods(int prepaymentPeriods) {
            this.prepaymentPeriods = prepaymentPeriods;
            return this;
        }

        public PrepaymentDataEVM build() {
            return new PrepaymentDataEVM(rowIndex, accountName, description, accountNumber, expenseAccountNumber, prepaymentNumber, prepaymentDate, prepaymentAmount, prepaymentPeriods);
        }

        public String toString() {
            return "PrepaymentDataEVM.PrepaymentDataEVMBuilder(rowIndex=" + this.rowIndex + ", accountName=" + this.accountName + ", description=" + this.description + ", accountNumber=" +
                this.accountNumber + ", expenseAccountNumber=" + this.expenseAccountNumber + ", prepaymentNumber=" + this.prepaymentNumber + ", prepaymentDate=" + this.prepaymentDate +
                ", prepaymentAmount=" + this.prepaymentAmount + ", prepaymentPeriods=" + this.prepaymentPeriods + ")";
        }
    }
}
