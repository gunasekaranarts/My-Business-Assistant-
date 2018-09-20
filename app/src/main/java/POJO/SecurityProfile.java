package POJO;

import java.io.Serializable;

/**
 * Created by USER on 21-06-2018.
 */

public class SecurityProfile implements Serializable {
    public int ProfileId;
    public String Name;
    public String Password;
    public String Email;
    public String Mobile;
    public String CompanyName;
    public String Address;
    public String BillHeader;

    public int getProfileId() {
        return ProfileId;
    }

    public void setProfileId(int profileId) {
        ProfileId = profileId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getCompanyName() {
        return CompanyName;
    }

    public void setCompanyName(String companyName) {
        CompanyName = companyName;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getBillHeader() {
        return BillHeader;
    }

    public void setBillHeader(String billHeader) {
        BillHeader = billHeader;
    }
}
