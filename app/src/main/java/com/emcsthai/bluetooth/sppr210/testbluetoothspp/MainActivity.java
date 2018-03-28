package com.emcsthai.bluetooth.sppr210.testbluetoothspp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bixolon.printer.BixolonPrinter;
import com.bixolon.printer.utility.Utility;
import com.bumptech.glide.Glide;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ACTION_PICK = 2000;

    private BluetoothSPP bt;
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
        setContentView(R.layout.activity_main);

        initWidget();

        bt = new BluetoothSPP(this);
        bixolonPrinter = new BixolonPrinter(this, handler, null);
        bixolonPrinter.D = true;

        if (!bt.isBluetoothAvailable()) {
            Toast.makeText(getApplicationContext(), "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
            finish();
        }

        if (bt.isBluetoothEnabled()) {
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
        if (!bt.isServiceAvailable()) {
            bt.setAutoConnectionListener(autoConnectionListener);
            bt.setBluetoothConnectionListener(bluetoothConnectionListener);
            bt.setBluetoothStateListener(bluetoothStateListener);
            bt.setOnDataReceivedListener(onDataReceivedListener);
            bt.setDeviceTarget(BluetoothState.DEVICE_OTHER);
            bt.setupService();
            bt.startService(BluetoothState.DEVICE_OTHER);
        } else {
            txtIsConnect.setText("Service is not available.");
            txtIsConnect.setTextColor(getResources().getColor(R.color.colorRed));
        }
    }

    private void showBluetoothDialog() {

        String[] list_name = bt.getPairedDeviceName();
        final String[] list_address = bt.getPairedDeviceAddress();

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
                        bt.connect(addressSelect);
                        btnConnect.setEnabled(false);
                    }
                }).show();
    }

    public Bitmap convertBitmapToGrayScale(Bitmap bmpOriginal) {

        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayScale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayScale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayScale;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bt.stopService();
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
                        Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
                if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
                    bt.disconnect();
                } else {
                    // Method from this class
                    showBluetoothDialog();
                }
            }

            if (v == btnDisconnect) {
                bt.disconnect();
                txtIsConnect.setText("Disconnect");
                txtIsConnect.setTextColor(getResources().getColor(R.color.colorRed));
            }

            if (v == btnAutoConnect) {
                boolean isAutoConnecting = bt.isAutoConnecting();
                if (isAutoConnecting) {
                    bt.stopAutoConnect();
                    txtDeviceDetail.setText("Unknown");
                    txtDeviceDetail.setTextColor(getResources().getColor(R.color.colorBlack));
                    btnAutoConnect.setText("Auto Off");
                } else {
                    bt.autoConnect("SPP-R");
                    btnAutoConnect.setText("Auto On");
                }
            }

            if (v == btnPrint) {

                if (bitmapSelectedImage != null) {
                    LoadingDialogHandler.getInstance().showLoadingDialog(MainActivity.this, "",
                            "Printing, Please wait...");

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            bixolonPrinter.connect(addressSelect);

                            Bitmap bitmapGaryScale = convertBitmapToGrayScale(bitmapSelectedImage);

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
                    Toast.makeText(MainActivity.this, "not bitmap", Toast.LENGTH_LONG).show();
                }
            }

            if (v == btnRead) {

                LoadingDialogHandler.getInstance().showLoadingDialog(v.getContext(), "",
                        "Printing, Please wait...");

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        bixolonPrinter.connect(addressSelect);

                        bixolonPrinter.powerUpSmartCard();
                        bixolonPrinter.selectSmartCard(BixolonPrinter.SMART_CARD_SELECT_SMART_CARD);
                        bixolonPrinter.changeOperatingMode(BixolonPrinter.SMART_CARD_MODE_APDU);
                        bixolonPrinter.getSmartCardStatus();

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
