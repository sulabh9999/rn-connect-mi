package com.example.myapplication.info;

import static com.example.myapplication.common.UUIDs.CHAR_STEPS;
import static com.example.myapplication.common.UUIDs.SERVICE1;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.util.Log;
import java.util.UUID;

public class InfoReceiver  {

    private String TAG = "Ranu BLE";
    private BluetoothGattCharacteristic stepsChar;

    private BluetoothGatt btGatt;

    private String steps = "0";
    private String battery = "0";

    public InfoReceiver() {

    }

    public void updateInfoChars(BluetoothGatt gatt){
        this.btGatt = gatt;
        BluetoothGattService service1 = btGatt.getService(UUID.fromString(SERVICE1));
        stepsChar = service1.getCharacteristic(UUID.fromString(CHAR_STEPS));
        btGatt.readCharacteristic(stepsChar);
    }


//    @ReactMethod
    private void getInfo() {
        if(btGatt != null && stepsChar != null){
            btGatt.readCharacteristic(stepsChar);
        }
//        successCallback.invoke(null, steps, battery);
        Log.i(TAG, "BATTERY INFO: "+ battery);
    }

    /**
     * Updates steps variable with current step value on device side
     * @param value - an array with step value
     *
     */
    public void handleInfoData(final byte[] value) {
        if(value != null){
            byte receivedSteps = value[1];
            steps = String.valueOf(receivedSteps);
        }
    }

    /**
     * Updates steps variable with current battery value on device side
     * @param value - an array with battery value
     *
     */
    public void handleBatteryData(final byte[] value) {
        if(value != null){
            byte currentSteps = value[1];
            battery = String.valueOf(currentSteps);
        }
    }

//    @Nonnull
//    @Override
//    public String getName() {
//        return InfoReceiver.class.getSimpleName();
//    }
}
