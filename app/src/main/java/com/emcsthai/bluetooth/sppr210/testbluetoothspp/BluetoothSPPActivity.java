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
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bixolon.printer.BixolonPrinter;
import com.bixolon.printer.utility.Utility;
import com.emcsthai.bluetooth.sppr210.testbluetoothspp.MyUtility.EMCSUtility;
import com.emcsthai.bluetooth.sppr210.testbluetoothspp.MyUtility.LoadingDialogHandler;

import java.io.FileNotFoundException;
import java.io.InputStream;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;

public class BluetoothSPPActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ACTION_PICK = 2000;

    private BluetoothSPP bluetoothSPP;
    private BixolonPrinter bixolonPrinter;

    private TextView txtIsBluetooth;
    private TextView txtIsConnect;
    private TextView txtDeviceDetail;

    private Button btnOpenFromDeviceStorage;

    private ImageView imgClaimFrom;

    private EditText edtResult;

    private Button btnConnect;
    private Button btnDisconnect;
    private Button btnAutoConnect;
    private Button btnPrint;
    private Button btnRead;

    private Bitmap bitmapSelectedImage = null;
    private String addressSelect = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_spp);

        android.support.v7.app.ActionBar bar = getSupportActionBar();
        assert bar != null;
        bar.setTitle("BluetoothSPPActivity");

        initWidget();

        bluetoothSPP = new BluetoothSPP(this);
        bixolonPrinter = new BixolonPrinter(this, handler, null);
        BixolonPrinter.printLog(true);

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
    }

    private void initWidget() {

        txtIsBluetooth = findViewById(R.id.txtIsBluetooth);
        txtIsConnect = findViewById(R.id.txtIsConnect);
        txtDeviceDetail = findViewById(R.id.txtDeviceDetail);

        btnOpenFromDeviceStorage = findViewById(R.id.btnOpenFromDeviceStorage);

        imgClaimFrom = findViewById(R.id.imgClaimFrom);

        edtResult = findViewById(R.id.edtResult);

        btnConnect = findViewById(R.id.btnConnect);
        btnDisconnect = findViewById(R.id.btnDisconnect);
        btnAutoConnect = findViewById(R.id.btnAutoConnect);
        btnPrint = findViewById(R.id.btnPrint);
        btnRead = findViewById(R.id.btnRead);
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

        String[] list_name = bluetoothSPP.getPairedDeviceName();
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
                        addressSelect = list_address[which];
                        bluetoothSPP.connect(addressSelect);
                        btnConnect.setEnabled(false);
                    }
                }).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothSPP.stopService();
        bitmapSelectedImage = null;
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
                        Toast.makeText(BluetoothSPPActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    private final Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {
                case BixolonPrinter.MESSAGE_READ:
                    if (msg.arg1 == BixolonPrinter.PROCESS_SMART_CARD_EXCHANGE_APDU) {
                        edtResult.setText("APDU : " + Utility.toHexString((byte[]) msg.obj));
                    }
                    return true;
            }

            return false;
        }
    });
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
                    LoadingDialogHandler.getInstance().showLoadingDialog(BluetoothSPPActivity.this, "",
                            "Printing, Please wait...");

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            Bitmap bitmapGaryScale = EMCSUtility.convertBitmapToGrayScale(bitmapSelectedImage);

                            // do the thing that takes a long time
                            bixolonPrinter.printBitmap(bitmapGaryScale,
                                    BixolonPrinter.ALIGNMENT_CENTER,
                                    BixolonPrinter.BITMAP_WIDTH_FULL,
                                    88,
                                    false);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    LoadingDialogHandler.getInstance()
                                            .closeLoadingDialog();
                                }
                            });
                        }
                    }).start();
                } else {
                    Toast.makeText(BluetoothSPPActivity.this, "not bitmap", Toast.LENGTH_LONG).show();
                }
            }

            if (v == btnRead) {

                LoadingDialogHandler.getInstance().showLoadingDialog(v.getContext(), "",
                        "Printing, Please wait...");

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        bixolonPrinter.powerUpSmartCard();
                        bixolonPrinter.selectSmartCard(BixolonPrinter.SMART_CARD_SELECT_SMART_CARD);
                        bixolonPrinter.changeOperatingMode(BixolonPrinter.SMART_CARD_MODE_APDU);

                        byte[] apduSelect = new byte[]{0x00, (byte) 0xA4, 0x04, 0x00, 0x08, (byte) 0xA0, 0x00, 0x00, 0x00, 0x54, 0x48, 0x00, 0x01};
                        bixolonPrinter.exchangeApdu(apduSelect);

                        byte[] apduCID = new byte[]{(byte) 0x80, (byte) 0xB0, 0x00, 0x04, 0x02, 0x00, 0x0D};
                        bixolonPrinter.exchangeApdu(apduCID);

                        byte[] apduGetResponseCID = new byte[]{0x00, (byte) 0xC0, 0x00, 0x00, 0x0D};
                        bixolonPrinter.exchangeApdu(apduGetResponseCID);

                        byte[] apduNameEN = new byte[]{(byte) 0x80, (byte) 0xB0, 0x00, 0x75, 0x02, 0x00, 0x64};
                        bixolonPrinter.exchangeApdu(apduNameEN);

                        byte[] apduGetResponseNameEN = new byte[]{0x00, (byte) 0xC0, 0x00, 0x00, 0x64};
                        bixolonPrinter.exchangeApdu(apduGetResponseNameEN);

                        byte[] apduNameDateOfBirth = new byte[]{(byte) 0x80, (byte) 0xB0, 0x00, (byte) 0xD9, 0x02, 0x00, 0x08};
                        bixolonPrinter.exchangeApdu(apduNameDateOfBirth);

                        byte[] apduGetResponseDateOfBirth = new byte[]{0x00, (byte) 0xC0, 0x00, 0x00, 0x08};
                        bixolonPrinter.exchangeApdu(apduGetResponseDateOfBirth);

                        byte[] apduNameAddress = new byte[]{(byte) 0x80, (byte) 0xB0, 0x15, 0x79, 0x02, 0x00, 0x64};
                        bixolonPrinter.exchangeApdu(apduNameAddress);

                        byte[] apduGetResponseAddress = new byte[]{0x00, (byte) 0xC0, 0x00, 0x00, 0x64};
                        bixolonPrinter.exchangeApdu(apduGetResponseAddress);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LoadingDialogHandler.getInstance()
                                        .closeLoadingDialog();
                            }
                        });
                    }
                }).start();
            }
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
