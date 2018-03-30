package com.emcsthai.bluetooth.sppr210.testbluetoothspp;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bixolon.printer.utility.Utility;
import com.bxl.config.editor.BXLConfigLoader;
import com.bxl.util.BXLUtility;
import com.emcsthai.bluetooth.sppr210.testbluetoothspp.MyUtility.EMCSUtility;
import com.emcsthai.bluetooth.sppr210.testbluetoothspp.MyUtility.ThaiApdu;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import jpos.JposException;
import jpos.MSR;
import jpos.MSRConst;
import jpos.POSPrinter;
import jpos.POSPrinterConst;
import jpos.SmartCardRW;
import jpos.SmartCardRWConst;
import jpos.config.JposEntry;
import jpos.events.DataEvent;
import jpos.events.DataListener;

public class NewBXLSDKv127Activity extends AppCompatActivity {

    private static final int REQUEST_CODE_ACTION_PICK = 2000;

    private BluetoothSPP bluetoothSPP;
    private BXLConfigLoader bxlConfigLoader;
    private POSPrinter posPrinter;
    private SmartCardRW smartCardRW;
    private MSR msr;

    private TextView txtIsBluetooth;
    private TextView txtIsConnect;
    private TextView txtDeviceDetail;

    private RadioGroup rdgMode;

    private Button btnOpenFromDeviceStorage;

    private ImageView imgClaimFrom;

    private EditText edtResult;

    private Button btnConnect;
    private Button btnDisconnect;
    private Button btnAutoConnect;
    private Button btnPrint;
    private Button btnRead;

    private Bitmap bitmapSelectedImage = null;
    private String deviceNameSelect = "";
    private String deviceAddressSelect = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_bxl_sdk_v127);

        android.support.v7.app.ActionBar bar = getSupportActionBar();
        assert bar != null;
        bar.setTitle("NewBXLSDKv127Activity");

        initWidget();

        initBxlConfigLoader();

        bluetoothSPP = new BluetoothSPP(this);
        posPrinter = new POSPrinter(this);
        smartCardRW = new SmartCardRW();
        msr = new MSR();

        if (!bluetoothSPP.isBluetoothAvailable()) {
            Toast.makeText(getApplicationContext(), "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
            finish();
        }

        if (bluetoothSPP.isBluetoothEnabled()) {
            txtIsBluetooth.setText("Bluetooth On");
            txtIsBluetooth.setTextColor(getResources().getColor(R.color.colorGreen));

            // Method from this class
            setup();

        } else {
            txtIsBluetooth.setText("Bluetooth Off");
            txtIsBluetooth.setTextColor(getResources().getColor(R.color.colorRed));

            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        }

        btnOpenFromDeviceStorage.setOnClickListener(onClickListener);
        btnConnect.setOnClickListener(onClickListener);
        btnDisconnect.setOnClickListener(onClickListener);
        btnAutoConnect.setOnClickListener(onClickListener);
        btnPrint.setOnClickListener(onClickListener);
        btnRead.setOnClickListener(onClickListener);

        rdgMode.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    private void initWidget() {

        txtIsBluetooth = findViewById(R.id.txtIsBluetooth);
        txtIsConnect = findViewById(R.id.txtIsConnect);
        txtDeviceDetail = findViewById(R.id.txtDeviceDetail);

        rdgMode = findViewById(R.id.rdgMode);

        btnOpenFromDeviceStorage = findViewById(R.id.btnOpenFromDeviceStorage);

        imgClaimFrom = findViewById(R.id.imgClaimFrom);

        edtResult = findViewById(R.id.edtResult);

        btnConnect = findViewById(R.id.btnConnect);
        btnDisconnect = findViewById(R.id.btnDisconnect);
        btnAutoConnect = findViewById(R.id.btnAutoConnect);
        btnPrint = findViewById(R.id.btnPrint);
        btnRead = findViewById(R.id.btnRead);
    }


    private void initBxlConfigLoader() {
        bxlConfigLoader = new BXLConfigLoader(this);
        try {
            bxlConfigLoader.openFile();
        } catch (Exception e) {
            e.printStackTrace();
            bxlConfigLoader.newFile();
        }
    }

    private void setup() {
        if (!bluetoothSPP.isServiceAvailable()) {
            bluetoothSPP.setAutoConnectionListener(autoConnectionListener);
            bluetoothSPP.setBluetoothConnectionListener(bluetoothConnectionListener);
            bluetoothSPP.setBluetoothStateListener(bluetoothStateListener);
            bluetoothSPP.setOnDataReceivedListener(onDataReceivedListener);
            bluetoothSPP.setDeviceTarget(BluetoothState.DEVICE_OTHER);
            bluetoothSPP.setupService();
            bluetoothSPP.startService(BluetoothState.DEVICE_OTHER);
        } else {
            txtIsConnect.setText("Service is not available.");
            txtIsConnect.setTextColor(getResources().getColor(R.color.colorRed));
        }
    }

    private void showBluetoothDialog() {

        final String[] list_name = bluetoothSPP.getPairedDeviceName();
        final String[] list_address = bluetoothSPP.getPairedDeviceAddress();

        final String[] items = new String[list_name.length];

        for (int i = 0; i < list_name.length; i++) {
            String name = list_name[i];
            String address = list_address[i];

            items[i] = name + " [" + address + "]";
        }

        new AlertDialog.Builder(this).setTitle("Paired Bluetooth printers")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deviceNameSelect = list_name[which];
                        deviceAddressSelect = list_address[which];
                        bluetoothSPP.connect(deviceAddressSelect);
                        btnConnect.setEnabled(false);

                        saveBxlConfigLoader();

                        openAndSetup();
                    }
                }).show();
    }

    private void saveBxlConfigLoader() {
        try {
            for (Object entry : bxlConfigLoader.getEntries()) {
                JposEntry jposEntry = (JposEntry) entry;
                bxlConfigLoader.removeEntry(jposEntry.getLogicalName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {

            switch (rdgMode.getCheckedRadioButtonId()) {
                case R.id.rdoPrinter:
                    bxlConfigLoader.addEntry(deviceNameSelect,
                            BXLConfigLoader.DEVICE_CATEGORY_POS_PRINTER,
                            deviceNameSelect,
                            BXLConfigLoader.DEVICE_BUS_BLUETOOTH,
                            deviceAddressSelect);
                    break;
                case R.id.rdoCardReader:
                    bxlConfigLoader.addEntry(deviceNameSelect,
                            BXLConfigLoader.DEVICE_CATEGORY_SMART_CARD_RW,
                            deviceNameSelect,
                            BXLConfigLoader.DEVICE_BUS_BLUETOOTH,
                            deviceAddressSelect);
                    break;
                case R.id.rdoMsr:
                    bxlConfigLoader.addEntry(deviceNameSelect,
                            BXLConfigLoader.DEVICE_CATEGORY_MSR,
                            deviceNameSelect,
                            BXLConfigLoader.DEVICE_BUS_BLUETOOTH,
                            deviceAddressSelect);
                    break;
            }

            bxlConfigLoader.saveFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openAndSetup() {
        try {
            switch (rdgMode.getCheckedRadioButtonId()) {
                case R.id.rdoPrinter:
                    posPrinter.open(deviceNameSelect);
                    posPrinter.claim(0);
                    posPrinter.setDeviceEnabled(true);
                    break;
                case R.id.rdoCardReader:
                    smartCardRW.open(deviceNameSelect);
                    smartCardRW.claim(0);
                    smartCardRW.setDeviceEnabled(true);
                    smartCardRW.setSCSlot(0x01 << (Integer.SIZE - 1));
                    smartCardRW.setIsoEmvMode(SmartCardRWConst.SC_CMODE_EMV);
                    break;
                case R.id.rdoMsr:
                    msr.open(deviceNameSelect);
                    msr.claim(0);
                    msr.setDeviceEnabled(true);
                    msr.setAutoDisable(false);
                    msr.addDataListener(dataListener);
                    msr.setDataEventEnabled(true);
                    msr.setDataEncryptionAlgorithm(MSRConst.MSR_DE_NONE);
                    break;
            }
        } catch (JposException e1) {
            e1.printStackTrace();
            edtResult.setText("Error e1 : " + e1.getMessage());
        }
    }

    private void getDataSmartCard() {
        if (smartCardRW != null) {
            String[] data = new String[]{
                    new String(ThaiApdu.getSelect()),
                    new String(ThaiApdu.getCID()),
                    new String(ThaiApdu.getResponseCID())
            };
            int[] count = new int[1];

            try {
                smartCardRW.readData(SmartCardRWConst.SC_READ_DATA, count, data);
                edtResult.setText("Hex : " + BXLUtility.toHexString(data[0].getBytes()));
            } catch (JposException e5) {
                e5.printStackTrace();
                edtResult.setText("Error e5: " + e5.getMessage());
            }
        }
    }

    private void getTrackDataMsr() {
        if (msr != null) {
            try {

                String msrResult = "";

                byte[] track1 = msr.getTrack1Data();
                byte[] track2 = msr.getTrack2Data();
                byte[] track3 = msr.getTrack3Data();

                msrResult += "track1 : " + EMCSUtility.getUTF8FromAsciiBytes(track1) + "\n";
                msrResult += "track2 : " + EMCSUtility.getUTF8FromAsciiBytes(track2) + "\n";
                msrResult += "track3 : " + EMCSUtility.getUTF8FromAsciiBytes(track3);

                edtResult.setText(msrResult);

            } catch (JposException e6) {
                e6.printStackTrace();
                edtResult.setText("Error e6: " + e6.getMessage());
            }
        }
    }

    private void closeAll() {
        try {
            switch (rdgMode.getCheckedRadioButtonId()) {
                case R.id.rdoPrinter:
                    posPrinter.release();
                    posPrinter.close();
                    break;
                case R.id.rdoCardReader:
                    smartCardRW.release();
                    smartCardRW.close();
                    break;
                case R.id.rdoMsr:
                    msr.release();
                    msr.close();
                    msr.setDeviceEnabled(false);
                    break;
            }
        } catch (JposException e2) {
            e2.printStackTrace();
            Toast.makeText(this, "Error e2 : " + e2.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void closePrinter() {
        if (posPrinter != null) {
            try {
                posPrinter.release();
                posPrinter.close();
            } catch (JposException e7) {
                e7.printStackTrace();
                edtResult.setText("Error e7 : " + e7.getMessage());
            }
        }
    }

    private void closeSmartCardRW() {
        if (smartCardRW != null) {
            try {
                smartCardRW.release();
                smartCardRW.close();
            } catch (JposException e8) {
                e8.printStackTrace();
                edtResult.setText("Error e8 : " + e8.getMessage());
            }
        }
    }

    private void closeMsr() {
        if (msr != null) {
            try {
                msr.release();
                msr.close();
            } catch (JposException e9) {
                e9.printStackTrace();
                edtResult.setText("Error e9 : " + e9.getMessage());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothSPP.stopService();
        bitmapSelectedImage = null;
        // Method from this class
        closeAll();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case BluetoothState.REQUEST_ENABLE_BT:

                if (resultCode == Activity.RESULT_OK) {
                    // Method from this class
                    setup();
                    txtIsBluetooth.setText("Bluetooth On");
                    txtIsBluetooth.setTextColor(getResources().getColor(R.color.colorGreen));
                } else {
                    txtIsBluetooth.setText("Bluetooth was not enabled.");
                    txtIsBluetooth.setTextColor(getResources().getColor(R.color.colorRed));
                }
                break;
            case REQUEST_CODE_ACTION_PICK:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    try {
                        final Uri imageUri = data.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        bitmapSelectedImage = BitmapFactory.decodeStream(imageStream);
                        imgClaimFrom.setImageBitmap(bitmapSelectedImage);
                        imgClaimFrom.setVisibility(View.VISIBLE);
                        btnOpenFromDeviceStorage.setVisibility(View.GONE);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Toast.makeText(NewBXLSDKv127Activity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (group.getCheckedRadioButtonId()) {
                case R.id.rdoPrinter:
                    // Method from this class
                    closeSmartCardRW();
                    // Method from this class
                    closeMsr();
                    break;
                case R.id.rdoCardReader:
                    // Method from this class
                    closePrinter();
                    // Method from this class
                    closeMsr();
                    break;
                case R.id.rdoMsr:
                    // Method from this class
                    closePrinter();
                    // Method from this class
                    closeSmartCardRW();
                    break;
            }
        }
    };

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (v == btnOpenFromDeviceStorage) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, REQUEST_CODE_ACTION_PICK);
            }

            if (v == btnConnect) {
                if (bluetoothSPP.getServiceState() == BluetoothState.STATE_CONNECTED) {
                    bluetoothSPP.disconnect();
                } else {
                    // Method from this class
                    showBluetoothDialog();
                }
            }

            if (v == btnDisconnect) {
                bluetoothSPP.disconnect();
                txtIsConnect.setText("Disconnect");
                txtIsConnect.setTextColor(getResources().getColor(R.color.colorRed));
                try {
                    posPrinter.close();
                } catch (JposException e3) {
                    e3.printStackTrace();
                    edtResult.setText("Error e3 : " + e3.getMessage());
                }
            }

            if (v == btnAutoConnect) {
                boolean isAutoConnecting = bluetoothSPP.isAutoConnecting();
                if (isAutoConnecting) {
                    bluetoothSPP.stopAutoConnect();
                    txtDeviceDetail.setText("Unknown");
                    txtDeviceDetail.setTextColor(getResources().getColor(R.color.colorBlack));
                    btnAutoConnect.setText("Auto Off");
                } else {
                    bluetoothSPP.autoConnect("SPP-R");
                    btnAutoConnect.setText("Auto On");
                }
            }

            if (v == btnPrint) {

                if (bitmapSelectedImage != null) {
                    Bitmap bitmapGaryScale = EMCSUtility.convertBitmapToGrayScale(bitmapSelectedImage);
                    try {

                        ByteBuffer buffer = ByteBuffer.allocate(4);
                        buffer.put((byte) POSPrinterConst.PTR_S_RECEIPT);
                        buffer.put((byte) 50);
                        buffer.put((byte) 1);
                        buffer.put((byte) 0x00);

                        posPrinter.printBitmap(buffer.getInt(0), bitmapGaryScale,
                                posPrinter.getRecLineWidth(), POSPrinterConst.PTR_BC_CENTER);

                    } catch (JposException e4) {
                        e4.printStackTrace();
                        edtResult.setText("Error e4: " + e4.getMessage());
                    }
                } else {
                    Toast.makeText(NewBXLSDKv127Activity.this, "not bitmap", Toast.LENGTH_LONG).show();
                }
            }

            if (v == btnRead) {
                switch (rdgMode.getCheckedRadioButtonId()) {
                    case R.id.rdoCardReader:
                        // Method from this class
                        getDataSmartCard();
                        break;
                    case R.id.rdoMsr:
                        // Method from this class
                        getTrackDataMsr();
                        break;
                }
            }
        }
    };

    private final DataListener dataListener = new DataListener() {
        @Override
        public void dataOccurred(DataEvent dataEvent) {
            // Method from this class
            getTrackDataMsr();
        }
    };

    private final BluetoothSPP.AutoConnectionListener autoConnectionListener = new BluetoothSPP.AutoConnectionListener() {
        @Override
        public void onAutoConnectionStarted() {
            txtIsConnect.setText("Auto Connected");
            txtIsConnect.setTextColor(getResources().getColor(R.color.colorGreen));
        }

        @Override
        public void onNewConnection(String name, String address) {
            txtDeviceDetail.setText(name + " (" + address + ")");
            txtDeviceDetail.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
    };

    private final BluetoothSPP.BluetoothConnectionListener bluetoothConnectionListener = new BluetoothSPP.BluetoothConnectionListener() {
        @Override
        public void onDeviceConnected(String name, String address) {
            txtIsConnect.setText("Connected");
            txtIsConnect.setTextColor(getResources().getColor(R.color.colorGreen));
            txtDeviceDetail.setText(name + " (" + address + ")");
            txtDeviceDetail.setTextColor(getResources().getColor(R.color.colorPrimary));
        }

        @Override
        public void onDeviceDisconnected() {
        }

        @Override
        public void onDeviceConnectionFailed() {
            txtIsConnect.setText("Connection Failed");
            txtIsConnect.setTextColor(getResources().getColor(R.color.colorRed));
            btnConnect.setEnabled(true);
        }
    };

    private final BluetoothSPP.BluetoothStateListener bluetoothStateListener = new BluetoothSPP.BluetoothStateListener() {
        @Override
        public void onServiceStateChanged(int state) {

            switch (state) {
                case BluetoothState.STATE_LISTEN:
                    btnConnect.setEnabled(false);
                    txtIsConnect.setText("Disconnected");
                    txtIsConnect.setTextColor(getResources().getColor(R.color.colorRed));
                    btnConnect.setEnabled(true);
                    txtDeviceDetail.setText("Unknown");
                    txtDeviceDetail.setTextColor(getResources().getColor(R.color.colorBlack));
                    break;
                case BluetoothState.STATE_CONNECTING:
                    txtIsConnect.setText("Connecting...");
                    txtIsConnect.setTextColor(getResources().getColor(R.color.colorGreen));
                    break;
                case BluetoothState.STATE_NULL:
                    txtIsConnect.setText("Service is not available");
                    txtIsConnect.setTextColor(getResources().getColor(R.color.colorRed));
                    break;
            }
        }
    };

    private final BluetoothSPP.OnDataReceivedListener onDataReceivedListener = new BluetoothSPP.OnDataReceivedListener() {
        @Override
        public void onDataReceived(byte[] data, String message) {
            edtResult.setText("Message : " + message
                    + "\nHex : " + Utility.toHexString(data));
        }
    };
}
