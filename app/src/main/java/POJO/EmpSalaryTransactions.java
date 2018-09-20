package POJO;

import java.io.Serializable;

/**
 * Created by USER on 06-08-2018.
 */

public class EmpSalaryTransactions implements Serializable {

    public int SalaryTransactionId;
    public int EmployeeId;
    public String SalaryTransType;
    public String TransactionDate;
    public int TransactionAmount;
    public String TransDesc;

    public int getSalaryTransactionId() {
        return SalaryTransactionId;
    }

    public void setSalaryTransactionId(int salaryTransactionId) {
        SalaryTransactionId = salaryTransactionId;
    }

    public int getEmployeeId() {
        return EmployeeId;
    }

    public void setEmployeeId(int employeeId) {
        EmployeeId = employeeId;
    }

    public String getSalaryTransType() {
        return SalaryTransType;
    }

    public void setSalaryTransType(String salaryTransType) {
        SalaryTransType = salaryTransType;
    }

    public String getTransactionDate() {
        return TransactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        TransactionDate = transactionDate;
    }

    public int getTransactionAmount() {
        return TransactionAmount;
    }

    public void setTransactionAmount(int transactionAmount) {
        TransactionAmount = transactionAmount;
    }

    public String getTransDesc() {
        return TransDesc;
    }

    public void setTransDesc(String transDesc) {
        TransDesc = transDesc;
    }
}
