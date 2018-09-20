package POJO;

import java.io.Serializable;

/**
 * Created by USER on 22-06-2018.
 */

public class Employee implements Serializable {

    public int EmployeeId;
    public String EmployeeName;
    public String EmpMobile;
    public String EmpAlternateNo;
    public String EmpJoinDate;
    public String EmpReleaveDate;
    public String EmpRemarks;
    public int EmployeeTypeId;
    public String EmpStatus;
    public byte[] EmployeePhoto;

    public int getEmployeeId() {
        return EmployeeId;
    }

    public void setEmployeeId(int employeeId) {
        EmployeeId = employeeId;
    }

    public String getEmployeeName() {
        return EmployeeName;
    }

    public void setEmployeeName(String employeeName) {
        EmployeeName = employeeName;
    }

    public String getEmpMobile() {
        return EmpMobile;
    }

    public void setEmpMobile(String empMobile) {
        EmpMobile = empMobile;
    }

    public String getEmpAlternateNo() {
        return EmpAlternateNo;
    }

    public void setEmpAlternateNo(String empAlternateNo) {
        EmpAlternateNo = empAlternateNo;
    }

    public String getEmpJoinDate() {
        return EmpJoinDate;
    }

    public void setEmpJoinDate(String empJoinDate) {
        EmpJoinDate = empJoinDate;
    }

    public String getEmpReleaveDate() {
        return EmpReleaveDate;
    }

    public void setEmpReleaveDate(String empReleaveDate) {
        EmpReleaveDate = empReleaveDate;
    }

    public String getEmpRemarks() {
        return EmpRemarks;
    }

    public void setEmpRemarks(String empRemarks) {
        EmpRemarks = empRemarks;
    }

    public int getEmployeeTypeId() {
        return EmployeeTypeId;
    }

    public void setEmployeeTypeId(int employeeTypeId) {
        EmployeeTypeId = employeeTypeId;
    }

    public String getEmpStatus() {
        return EmpStatus;
    }

    public void setEmpStatus(String empStatus) {
        EmpStatus = empStatus;
    }

    public byte[] getEmployeePhoto() {
        return EmployeePhoto;
    }

    public void setEmployeePhoto(byte[] employeePhoto) {
        EmployeePhoto = employeePhoto;
    }
}
