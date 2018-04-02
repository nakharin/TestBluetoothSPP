package com.emcsthai.bluetooth.sppr210.testbluetoothspp.MyUtility;

/**
 * Link Reference : https://github.com/chakphanu/ThaiNationalIDCard/blob/master/APDU.md
 */
public class ThaiApdu {

    private static byte[] select = new byte[]{(byte) 0x00, (byte) (byte) 0xa4, (byte) 0x04, (byte) 0x00, (byte) 0x08, (byte) 0xa0, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x54, (byte) 0x48, (byte) 0x00, (byte) 0x01};
    private static byte[] cid = new byte[]{(byte) 0x80, (byte) 0xb0, (byte) 0x00, (byte) 0x04, (byte) 0x02, (byte) 0x00, (byte) 0x0d};
    private static byte[] responseCid = new byte[]{(byte) 0x00, (byte) 0xc0, (byte) 0x00, (byte) 0x00, (byte) 0x0d};
    private static byte[] nameTH = new byte[]{(byte) 0x80, (byte) 0xb0, (byte) 0x00, (byte) 0x11, (byte) 0x02, (byte) 0x00, (byte) 0x64};
    private static byte[] responseNameTH = new byte[]{(byte) 0x00, (byte) 0xc0, (byte) 0x00, (byte) 0x00, (byte) 0x64};
    private static byte[] nameEN = new byte[]{(byte) 0x80, (byte) 0xb0, (byte) 0x00, (byte) 0x75, (byte) 0x02, (byte) 0x00, (byte) 0x64};
    private static byte[] responseNameEN = new byte[]{(byte) 0x00, (byte) 0xc0, (byte) 0x00, (byte) 0x00, (byte) 0x64};
    private static byte[] gender = new byte[]{(byte) 0x80, (byte) 0xb0, (byte) 0x00, (byte) 0xe1, (byte) 0x20, (byte) 0x00, (byte) 0x01};
    private static byte[] responseGender = new byte[]{(byte) 0x00, (byte) 0xc0, (byte) 0x00, (byte) 0x00, (byte) 0x01};
    private static byte[] dateOfBirth = new byte[]{(byte) 0x80, (byte) 0xb0, (byte) 0x00, (byte) 0xd9, (byte) 0x02, (byte) 0x00, (byte) 0x08};
    private static byte[] responseDateOfBirth = new byte[]{(byte) 0x00, (byte) 0xc0, (byte) 0x00, (byte) 0x00, (byte) 0x08};
    private static byte[] address = new byte[]{(byte) 0x80, (byte) 0xb0, (byte) 0x15, (byte) 0x79, (byte) 0x02, (byte) 0x00, (byte) 0x64};
    private static byte[] responseAddress = new byte[]{(byte) 0x00, (byte) 0xc0, (byte) 0x00, (byte) 0x00, (byte) 0x64};

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
