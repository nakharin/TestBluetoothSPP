package com.emcsthai.bluetooth.sppr210.testbluetoothspp.MyUtility;

/**
 * Link Reference : https://github.com/chakphanu/ThaiNationalIDCard/blob/master/APDU.md
 */
public class ThaiApdu {

    private static byte[] select = new byte[]{0x00, (byte) 0xa4, 0x04, 0x00, 0x08, (byte) 0xa0, 0x00, 0x00, 0x00, 0x54, 0x48, 0x00, 0x01};
    private static byte[] citizenID = new byte[]{(byte) 0x80, (byte) 0xb0, 0x00, 0x04, 0x02, 0x00, 0x0d};
    // Full Name Thai + Eng + BirthDate + Sex
    private static byte[] personInfo = new byte[]{(byte) 0x80, (byte) 0xb0, 0x00, 0x11, 0x02, 0x00, (byte) 0xd1};
    private static byte[] address = new byte[]{(byte) 0x80, (byte) 0xb0, 0x15, 0x79, 0x02, 0x00, 0x64};

    public static byte[] getSelect() {
        return select;
    }

    public static byte[] getCitizenID() {
        return citizenID;
    }

    public static byte[] getPersonInfo() {
        return personInfo;
    }

    public static byte[] getAddress() {
        return address;
    }

    private static byte[] responseCitizenID = new byte[]{0x00, (byte) 0xc0, 0x00, 0x00, 0x0d};
    private static byte[] responsePersonInfo = new byte[]{0x00, (byte) 0xc0, 0x00, 0x00, (byte) 0xd1};
    private static byte[] responseAddress = new byte[]{0x00, (byte) 0xc0, 0x00, 0x00, 0x64};

    public static byte[] getResponseCitizenID() {
        return responseCitizenID;
    }

    public static byte[] getResponsePersonInfo() {
        return responsePersonInfo;
    }

    public static byte[] getResponseAddress() {
        return responseAddress;
    }
}
