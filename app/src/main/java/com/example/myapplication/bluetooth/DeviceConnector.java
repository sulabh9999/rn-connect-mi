package com.example.myapplication.bluetooth;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

//import com.example.attempt1.ui.login.Config;
//import com.example.attempt1.ui.login.common.GattCallback;
//import com.example.attempt1.ui.login.hr.HeartBeatMeasurer;

//import com.facebook.react.bridge.Callback;
//import com.facebook.react.bridge.ReactApplicationContext;
//import com.facebook.react.bridge.ReactContextBaseJavaModule;
//import com.facebook.react.bridge.ReactMethod;
//import com.sbp.common.GattCallback;
//import com.sbp.metric.hr.HeartBeatMeasurer;
//import com.sbp.metric.hr.HeartBeatMeasurerPackage;

import java.util.Objects;

//import javax.annotation.Nonnull;

import static android.content.Context.BLUETOOTH_SERVICE;

import com.example.myapplication.Config;
import com.example.myapplication.common.GattCallback;
import com.example.myapplication.hr.HeartBeatMeasurer;
//import static com.sbp.common.ModuleStorage.getModuleStorage;

/**
 *  Declares main set of methods which will be used by react UI during data fetching procedure.
 *  Last one includes only device connection. Make sure your miband device has
 *  "Allow 3-rd party connect" option ON
 * @author  Spayker
 * @version 1.0
 * @since   06/01/2019
 */
public class DeviceConnector  {

    private String TAG = "Ranu BLE";

    // Bluetooth variable section
    private BluetoothGatt bluetoothGatt;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private GattCallback gattCallback;
    private ProgressDialog searchProgressDialog;
    private String currentDeviceMacAddress;
    private HeartBeatMeasurer heartBeatMeasurer;

    public DeviceConnector() {
        heartBeatMeasurer = new HeartBeatMeasurer();
        gattCallback = new GattCallback(heartBeatMeasurer);
    }

    public void startBonding() {
        try {
            Log.i(TAG, "Start Pairing... with: " + bluetoothDevice.getName());
            bluetoothDevice.createBond();
        } catch (Exception e) {
            Log.i(TAG, "Error..." + e.getMessage());
        }

    }

    public BluetoothDevice getDevice() {
        return bluetoothDevice;
    }




//    @ReactMethod // Callback successCallback
    public void discoverDevices() {
        Context mainContext = Config.context;

//        bluetoothAdapter = ((BluetoothManager) Objects.requireNonNull(BLUETOOTH_SERVICE)
//                .getSystemService(BLUETOOTH_SERVICE))
//                .getAdapter();

        bluetoothAdapter = ((BluetoothManager) Objects.requireNonNull(mainContext)
                .getSystemService(BLUETOOTH_SERVICE))
                .getAdapter();

//        bluetoothAdapter = ((BluetoothManager) Config.context.getSystemService(BLUETOOTH_SERVICE)).getAdapter();

        searchProgressDialog = new ProgressDialog(mainContext);
        searchProgressDialog.setIndeterminate(true);
        searchProgressDialog.setTitle("MiBand Bluetooth Scanner");
        searchProgressDialog.setMessage("Searching...");
        searchProgressDialog.setCancelable(false);
        searchProgressDialog.show();

        if (!bluetoothAdapter.isEnabled()) {
//            ((AppCompatActivity)mainContext)
//                    .startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
//                            1);
        }

        final DeviceScanCallback deviceScanCallback = new DeviceScanCallback();
        BluetoothLeScanner bluetoothScanner = bluetoothAdapter.getBluetoothLeScanner();
        if(bluetoothScanner != null){
            bluetoothScanner.startScan(deviceScanCallback);
        }

        final int DISCOVERY_TIME_DELAY_IN_MS = 15000;
        new Handler().postDelayed(() -> {
            bluetoothAdapter.getBluetoothLeScanner().stopScan(deviceScanCallback);
            searchProgressDialog.dismiss();
//          successCallback.invoke(null, deviceScanCallback.getDiscoveredDevices());
            Log.i(TAG, "DISCOVERD DEVICES: "+ deviceScanCallback.getDiscoveredDevices());
            linkWithDevice(deviceScanCallback.getDiscoveredDevices().get("deviceMac"), false);
        }, DISCOVERY_TIME_DELAY_IN_MS);


    }


//    @ReactMethod
    public void linkWithDevice(String macAddress, boolean isAuto) {
        currentDeviceMacAddress = macAddress;
        updateBluetoothGatt(isAuto); // first time
//        getModuleStorage().getHeartBeatMeasurerPackage()
//                .getHeartBeatMeasurer()

        heartBeatMeasurer.updateBluetoothConfig(bluetoothGatt);
//        successCallback.invoke(null, bluetoothGatt.getDevice().getBondState());
        Log.i(TAG, "LINKED DEVICE: "+bluetoothGatt.getDevice().getBondState());

    }

//    @ReactMethod
    void disconnectDevice() {
        if(bluetoothGatt != null){
            bluetoothGatt.disconnect();
            bluetoothGatt = null;
        }
        bluetoothDevice = null;
        bluetoothAdapter = null;
        Config.bluetoothDevice = bluetoothDevice;

//        successCallback.invoke(null, 0);
    }

//    @ReactMethod
    private void getDeviceBondLevel(){
        if (bluetoothGatt == null){
            Log.i(TAG, "getDeviceBondLevel: 00 NO IDEA");
        } else {
//            successCallback.invoke(null, bluetoothGatt.getDevice().getBondState());
            Log.i(TAG, "getDeviceBondLevel: "+ bluetoothGatt.getDevice().getBondState());
        }
    }

//    @Nonnull
//    @Override
//    public String getName() {
//        return DeviceConnector.class.getSimpleName();
//    }

    private void updateBluetoothGatt(boolean isAuto){
        Context mainContext = Config.context; // getReactApplicationContext().getCurrentActivity();
        bluetoothAdapter = ((BluetoothManager) Objects.requireNonNull(mainContext)
                .getSystemService(BLUETOOTH_SERVICE))
                .getAdapter();

        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(currentDeviceMacAddress);
        setBluetoothDevice(device);
//        HeartBeatMeasurerPackage hBMeasurerPackage = getModuleStorage().getHeartBeatMeasurerPackage();
//        HeartBeatMeasurer heartBeatMeasurer = hBMeasurerPackage.getHeartBeatMeasurer();
        gattCallback = new GattCallback(heartBeatMeasurer);
        bluetoothGatt = bluetoothDevice.connectGatt(mainContext, isAuto, gattCallback);
    }

    void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
        Config.bluetoothDevice = bluetoothDevice;
    }


}
