# TestBluetoothSPP
# Support SPP-R210, SPP-R300

## OldSDKv236Activity (Used lib BixolonPrinterV236.jar)
- Feature Printer -> _Success_
- Feature Smart Card Reader -> _Success_ 
>- (Get *some* data is not available)
- Feature MSR -> _Fail_

## NewBXLSDKv127Activity (Used lib bixolon_printer_v127.jar)
- Feature Printer -> _Fail_
- Feature Smart Card Reader -> _Fail_ 
>- (Get *all* data is not available)
- Feature MSR -> _Fail_

## Reference
- ThaiNationalIDCard : https://github.com/chakphanu/ThaiNationalIDCard/blob/master/APDU.md
- Android-BluetoothSPPLibrary : https://github.com/akexorcist/Android-BluetoothSPPLibrary