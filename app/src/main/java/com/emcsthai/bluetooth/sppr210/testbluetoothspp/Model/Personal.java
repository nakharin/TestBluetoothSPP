package com.emcsthai.bluetooth.sppr210.testbluetoothspp.Model;

import android.util.Log;

import java.util.Calendar;

public class Personal {

    private static final String TAG = "Personal";

    private String personal;
    private String address;
    private String issueExpire;
    private String citizenId;
    private String[] thPersonal;
    private String[] enPersonal;
    private String photoJpeg;

    public String getCitizenId() {
        return citizenId.replaceAll("[^\\d]", "");
    }

    public void setCitizenId(String citizenId) {
        this.citizenId = citizenId;
    }

    public String getPhotoRaw() {
        return photoJpeg;
    }

    public void setPhotoRaw(String value) {
        photoJpeg = value;
    }

    public void setPersonalInfo(String value) {
        personal = value;
        thPersonal = personal.substring(0, 100).split("#");
        enPersonal = personal.substring(100, 200).split("#");

        Log.i(TAG, "PersonalInfoTH : " + personal.substring(0, 100));
        Log.i(TAG, "PersonalInfoEN : " + personal.substring(100, 200));
    }

    public String getDateOfBirth() {
        int birthYear = Integer.valueOf(personal.substring(200, 204)) - 543;
        String birthMonth = personal.substring(204, 206);
        String birthDay = personal.substring(206, 208);

        String dateOfBirth = birthDay + "-" + birthMonth + "-" + (String.valueOf(birthYear));

        Log.i(TAG, "DateOfBirth : " + dateOfBirth);

        return dateOfBirth;
    }

    public String getSex() {
        String sex = personal.substring(208, 209);
        Log.i(TAG, "Sex : " + sex);
        return sex;
    }

    public int getAge() {
        String dateOfBirth = getDateOfBirth();
        String date[] = dateOfBirth.split("-");
        Calendar calendar = Calendar.getInstance();
        int birthDayYear = Integer.parseInt(date[2]);
        int currentYear = calendar.get(Calendar.YEAR);
        int age = currentYear - birthDayYear;
        Log.i(TAG, "Age : " + age);
        return age;
    }

    public String getThPrefix() {
        String thPrefix = thPersonal[0].trim();
        Log.i(TAG, "THPrefix : " + thPrefix);
        return thPrefix;
    }

    public String getTHFirstName() {
        String thFirstName = thPersonal[1].trim();
        Log.i(TAG, "THFirstName : " + thFirstName);
        return thFirstName;
    }

    public String getTHLastName() {
        String thLastName = thPersonal[3].trim();
        Log.i(TAG, "THLastName : " + thLastName);
        return thLastName;
    }

    public String getENPrefix() {
        String enPrefix = enPersonal[0].trim();
        Log.i(TAG, "ENPrefix : " + enPrefix);
        return enPrefix;
    }

    public String getENFirstName() {
        String enFirstName = enPersonal[1].trim();
        Log.i(TAG, "ENFirstName : " + enFirstName);
        return enFirstName;
    }

    public String getENLastName() {
        String enLastName = enPersonal[3].trim();
        Log.i(TAG, "ENLastName : " + enLastName);
        return enLastName;
    }

    public String getType() {
        String type = personal.split("#")[0].trim();
        Log.i(TAG, "Type: ");
        return type;
    }

    public String getAddress() {
        String address =  this.address.replace("#", " ").substring(0, this.address.length());
        Log.i(TAG, "Address : " + address);
        return address;
    }

    public void setAddress(String value) {
        address = value.trim();
    }

    public String getHouseNo() {
        return address.split("#")[0].trim();
    }

    public String getVillageNo() {
        return address.split("#")[1].trim();
    }

    public String getLane() {
        return address.split("#")[2].trim();
    }

    public String getRoad() {
        return address.split("#")[3].trim();
    }

    public String getDistrict() {
        return address.split("#")[5].trim().replace("ตำบล", "").replace("แขวง", "");
    }

    public String getSubDistrict() {
        String district = address.split("#")[6].trim().replace("อำเภอ", "").replace("เขต", "");
        Log.i(TAG, "district : " + district);
        return district;
    }

    public String getProvince() {
        String provinceName = address.split("#")[7].trim().replace("จังหวัด", "").replace(" ", "");
        String province =  provinceName.substring(0, provinceName.length());
        Log.i(TAG, "Province : " + province);
        return province;
    }

    public void setIssueExpire(String value) {
        issueExpire = value.trim();
    }

    public String getDateTimeIssue() {
        int issueYear = Integer.valueOf(issueExpire.substring(0, 4)) - 543;
        String issueMonth = issueExpire.substring(4, 6);
        String issueDay = issueExpire.substring(6, 8);
        return issueDay + "-" + issueMonth + "-" + (String.valueOf(issueYear));
    }

    public String getDateExpire() {
        int expireYear = Integer.valueOf(issueExpire.substring(8, 12)) - 543;
        String expireMonth = issueExpire.substring(12, 14);
        String expireDay = issueExpire.substring(14, 16);

        return expireDay + "-" + expireMonth + "-" + (String.valueOf(expireYear));
    }
}
