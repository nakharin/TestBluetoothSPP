package com.emcsthai.bluetooth.sppr210.testbluetoothspp;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bixolon.printer.BixolonPrinter;
import com.emcsthai.bluetooth.sppr210.testbluetoothspp.MyUtility.EMCSUtility;
import com.emcsthai.bluetooth.sppr210.testbluetoothspp.MyUtility.LoadingDialogHandler;
import com.emcsthai.bluetooth.sppr210.testbluetoothspp.MyUtility.ThaiApdu;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;

public class OldSDKv236Activity extends AppCompatActivity {

    private static final int REQUEST_CODE_ACTION_PICK = 2000;

    private BluetoothSPP bluetoothSPP;
    private BixolonPrinter bixolonPrinter;

    private TextView txtIsBluetooth;
    private TextView txtIsConnect;
    private TextView txtDeviceDetail;

    private RadioGroup rdgMode;

    private Button btnOpenFromDeviceStorage;

    private ImageView imgClaimFrom;

    private EditText edtResult;

    private Button btnConnect;
    private Button btnDisconnect;
    private Button btnPrint;
    private Button btnRead;

    private Bitmap bitmapSelectedImage = null;

    private String deviceSelect;

    private ArrayList<byte[]> arrByte = new ArrayList<>();

    private HashMap<Integer, byte[]> apduCommand = new HashMap<>();

    private int index = 0;

    private static final String ACTION_GET_MSR_TRACK_DATA = "com.bixolon.anction.GET_MSR_TRACK_DATA";
    private static final String EXTRA_NAME_MSR_TRACK_DATA = "MsrTrackData";

    private byte[] mTrack1Data;
    private byte[] mTrack2Data;
    private byte[] mTrack3Data;

    private String msrResult = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_sdk_v236);

        android.support.v7.app.ActionBar bar = getSupportActionBar();
        assert bar != null;
        bar.setTitle("OldSDKv236Activity");

        // Method from this class
        initWidget();

        // Method from this class
        initAPDU();

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
        btnPrint = findViewById(R.id.btnPrint);
        btnRead = findViewById(R.id.btnRead);
    }

    private void initAPDU() {

        apduCommand.put(0, ThaiApdu.getSelect());
        apduCommand.put(1, ThaiApdu.getCID());
        apduCommand.put(2, ThaiApdu.getResponseCID());
        apduCommand.put(3, ThaiApdu.getNameTH());
        apduCommand.put(4, ThaiApdu.getResponseNameTH());
        apduCommand.put(5, ThaiApdu.getNameEN());
        apduCommand.put(6, ThaiApdu.getResponseNameEN());
        apduCommand.put(7, ThaiApdu.getGender());
        apduCommand.put(8, ThaiApdu.getResponseGender());
        apduCommand.put(9, ThaiApdu.getDateOfBirth());
        apduCommand.put(10, ThaiApdu.getResponseDateOfBirth());
        apduCommand.put(11, ThaiApdu.getAddress());
        apduCommand.put(12, ThaiApdu.getResponseAddress());
    }

    private void setup() {
        if (!bluetoothSPP.isServiceAvailable()) {
            bluetoothSPP.setupService();
            bluetoothSPP.startService(BluetoothState.DEVICE_OTHER);
        } else {
            txtIsConnect.setText("Service is not available.");
            txtIsConnect.setTextColor(getResources().getColor(R.color.colorRed));
        }
    }

    private void showBluetoothDialog(final Set<BluetoothDevice> pairedDevices) {

        final String[] items = new String[pairedDevices.size()];
        int index = 0;
        for (BluetoothDevice device : pairedDevices) {
            String name = device.getName();
            String address = device.getAddress();
            items[index++] = name + " [" + address + "]";
        }

        new AlertDialog.Builder(this).setTitle("Paired Bluetooth printers")
                .setItems(items, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deviceSelect = items[which];
                        int indexStart = deviceSelect.indexOf("[") + 1;
                        int indexEnd = deviceSelect.indexOf("]");
                        String address = deviceSelect.substring(indexStart, indexEnd);
                        bixolonPrinter.connect(address);
                    }
                }).show();
    }

    private void showResult() {

        String message = "";
        for (byte[] bytes : arrByte) {
            if (bytes != null) {
                message += EMCSUtility.GetUTF8FromAsciiBytes(bytes) + "\n";
            }
        }

        edtResult.setText(message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bitmapSelectedImage = null;
        bluetoothSPP.stopService();
        if (bixolonPrinter != null) {
            if (rdgMode.getCheckedRadioButtonId() == R.id.rdoMsr) {
                bixolonPrinter.cancelMsrReaderMode();
            }
            bixolonPrinter.disconnect();
        }
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
                        Toast.makeText(OldSDKv236Activity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    private final Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {
                case BixolonPrinter.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BixolonPrinter.STATE_CONNECTED:
                            txtIsConnect.setText("Connected");
                            txtIsConnect.setTextColor(getResources().getColor(R.color.colorGreen));
                            txtDeviceDetail.setText(deviceSelect);
                            txtDeviceDetail.setTextColor(getResources().getColor(R.color.colorPrimary));

                            bixolonPrinter.selectSmartCard(BixolonPrinter.SMART_CARD_SELECT_SMART_CARD);
                            bixolonPrinter.changeOperatingMode(BixolonPrinter.SMART_CARD_MODE_APDU);

                            break;
                        case BixolonPrinter.STATE_CONNECTING:
                            txtIsConnect.setText("Connecting...");
                            txtIsConnect.setTextColor(getResources().getColor(R.color.colorGreen));
                            btnConnect.setEnabled(false);
                            break;

                        case BixolonPrinter.STATE_NONE:
                            txtIsConnect.setText("Disconnected");
                            txtIsConnect.setTextColor(getResources().getColor(R.color.colorRed));
                            txtDeviceDetail.setText("Unknown");
                            txtDeviceDetail.setTextColor(getResources().getColor(R.color.colorBlack));
                            btnConnect.setEnabled(true);
                            break;
                    }
                    return true;

                case BixolonPrinter.MESSAGE_READ:
                    switch (msg.arg1) {
                        case BixolonPrinter.PROCESS_SMART_CARD_EXCHANGE_APDU:
                            arrByte.add((byte[]) msg.obj);

                            if (index < apduCommand.size() - 1) {
                                index = index + 1;
                                bixolonPrinter.exchangeApdu(apduCommand.get(index));
                            } else {
                                index = 0;
                                bixolonPrinter.powerDownSmartCard();
                                LoadingDialogHandler.getInstance().closeLoadingDialog();
                                Toast.makeText(OldSDKv236Activity.this, "Read Completed", Toast.LENGTH_SHORT).show();

                                // Method from this class
                                showResult();
                            }

                            break;
                        case BixolonPrinter.PROCESS_GET_MSR_MODE:
                            // TODO: 30/3/2018 AD

                            break;
                    }
                    return true;

                case BixolonPrinter.MESSAGE_BLUETOOTH_DEVICE_SET:
                    if (msg.obj != null) {
                        showBluetoothDialog((Set<BluetoothDevice>) msg.obj);
                    }
                    return true;
            }

            return false;
        }
    });

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_GET_MSR_TRACK_DATA)) {
                Bundle bundle = intent.getBundleExtra(EXTRA_NAME_MSR_TRACK_DATA);

                edtResult.setText("");

                mTrack1Data = bundle.getByteArray(BixolonPrinter.KEY_STRING_MSR_TRACK1);
                if (mTrack1Data != null) {
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            msrResult += EMCSUtility.GetUTF8FromAsciiBytes(mTrack1Data) + "\n";
                        }
                    }, 100);
                }

                mTrack2Data = bundle.getByteArray(BixolonPrinter.KEY_STRING_MSR_TRACK2);
                if (mTrack2Data != null) {
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            msrResult += EMCSUtility.GetUTF8FromAsciiBytes(mTrack2Data) + "\n";
                        }
                    }, 100);
                }

                mTrack3Data = bundle.getByteArray(BixolonPrinter.KEY_STRING_MSR_TRACK3);
                if (mTrack3Data != null) {
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            msrResult += EMCSUtility.GetUTF8FromAsciiBytes(mTrack3Data);
                        }
                    }, 100);
                }

                edtResult.setText(msrResult);
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
                    bixolonPrinter.findBluetoothPrinters();
                }
            }

            if (v == btnDisconnect) {
                bixolonPrinter.disconnect();
            }

            if (v == btnPrint) {

                if (bitmapSelectedImage != null) {
                    LoadingDialogHandler.getInstance().showLoadingDialog(OldSDKv236Activity.this, "",
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
                    Toast.makeText(OldSDKv236Activity.this, "no bitmap", Toast.LENGTH_LONG).show();
                }
            }

            if (v == btnRead) {

                arrByte.clear();
                bixolonPrinter.powerUpSmartCard();

                LoadingDialogHandler.getInstance().showLoadingDialog(v.getContext(), "",
                        "Printing, Please wait...");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        bixolonPrinter.exchangeApdu(apduCommand.get(0));
                    }
                }).start();
            }
        }
    };

    private final RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

            switch (group.getCheckedRadioButtonId()) {
                case R.id.rdoCardReader:
                    bixolonPrinter.cancelMsrReaderMode();
                    break;
                case R.id.rdoMsr:
                    bixolonPrinter.setMsrReaderMode();
                    IntentFilter filter = new IntentFilter();
                    filter.addAction(ACTION_GET_MSR_TRACK_DATA);
                    registerReceiver(mReceiver, filter);
                    break;
            }
        }
    };
}
