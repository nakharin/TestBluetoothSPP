package com.emcsthai.bluetooth.sppr210.testbluetoothspp.MyUtility;

/**
 * Link Reference : https://github.com/chakphanu/ThaiNationalIDCard/blob/master/APDU.md
 */
public class ThaiApdu {

    private static byte[] select = new byte[]{0x00, (byte) 0xA4, 0x04, 0x00, 0x08, (byte) 0xA0, 0x00, 0x00, 0x00, 0x54, 0x48, 0x00, 0x01};
    private static byte[] cid = new byte[]{(byte) 0x80, (byte) 0xB0, 0x00, 0x04, 0x02, 0x00, 0x0D};
    private static byte[] responseCid = new byte[]{0x00, (byte) 0xC0, 0x00, 0x00, 0x0D};
    private static byte[] nameTH = new byte[]{(byte) 0x80, (byte) 0xB0, 0x00, 0x11, 0x02, 0x00, 0x64};
    private static byte[] responseNameTH = new byte[]{0x00, (byte) 0xC0, 0x00, 0x00, 0x64};
    private static byte[] nameEN = new byte[]{(byte) 0x80, (byte) 0xB0, 0x00, 0x75, 0x02, 0x00, 0x64};
    private static byte[] responseNameEN = new byte[]{0x00, (byte) 0xC0, 0x00, 0x00, 0x64};
    private static byte[] gender = new byte[]{(byte) 0x80, (byte) 0xB0, 0x00, (byte) 0xE1, 0x20, 0x00, 0x01};
    private static byte[] responseGender = new byte[]{0x00, (byte) 0xC0, 0x00, 0x00, 0x01};
    private static byte[] dateOfBirth = new byte[]{(byte) 0x80, (byte) 0xB0, 0x00, (byte) 0xD9, 0x02, 0x00, 0x08};
    private static byte[] responseDateOfBirth = new byte[]{0x00, (byte) 0xC0, 0x00, 0x00, 0x08};
    private static byte[] address = new byte[]{(byte) 0x80, (byte) 0xB0, 0x15, 0x79, 0x02, 0x00, 0x64};
    private static byte[] responseAddress = new byte[]{0x00, (byte) 0xC0, 0x00, 0x00, 0x64};

    public static byte[] getSelect() {
        return select;
    }

    public static byte[] getCID() {
        return cid;
    }

    public static byte[] getResponseCID() {
        return responseCid;
    }

    public static byte[] getNameTH() {
        return nameTH;
    }

    public static byte[] getResponseNameTH() {
        return responseNameTH;
    }

    public static byte[] getNameEN() {
        return nameEN;
    }

    public static byte[] getResponseNameEN() {
        return responseNameEN;
    }

    public static byte[] getGender() {
        return gender;
    }

    public static byte[] getResponseGender() {
        return responseGender;
    }

    public static byte[] getDateOfBirth() {
        return dateOfBirth;
    }

    public static byte[] getResponseDateOfBirth() {
        return responseDateOfBirth;
    }

    public static byte[] getAddress() {
        return address;
    }

    public static byte[] getResponseAddress() {
        return responseAddress;
    }
}
