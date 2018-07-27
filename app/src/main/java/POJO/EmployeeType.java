package POJO;

import java.io.Serializable;



/**
 * Created by USER on 22-06-2018.
 */

public class EmployeeType implements Serializable {
    public int EmployeeTypeId;
    public String EmployeeType;
    public String EmployeeTypeDesc;

    public int getEmployeeTypeId() {
        return EmployeeTypeId;
    }

    public void setEmployeeTypeId(int employeeTypeId) {
        EmployeeTypeId = employeeTypeId;
    }

    public String getEmployeeType() {
        return EmployeeType;
    }

    public void setEmployeeType(String employeeType) {
        EmployeeType = employeeType;
    }

    public String getEmployeeTypeDesc() {
        return EmployeeTypeDesc;
    }

    public void setEmployeeTypeDesc(String employeeTypeDesc) {
        EmployeeTypeDesc = employeeTypeDesc;
    }
}
