package com.moko.support;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.moko.support.callback.MokoResponseCallback;
import com.moko.support.entity.OrderType;
import com.moko.support.log.LogModule;
import com.moko.support.utils.MokoUtils;

import java.util.UUID;

import no.nordicsemi.android.ble.BleManager;
import no.nordicsemi.android.ble.BleManagerCallbacks;
import no.nordicsemi.android.ble.callback.DataReceivedCallback;
import no.nordicsemi.android.ble.data.Data;

public class MokoBleManager extends BleManager<BleManagerCallbacks> {

    private MokoResponseCallback mMokoResponseCallback;
    private static MokoBleManager managerInstance = null;
    private final static UUID SERVICE_UUID = UUID.fromString("0000FF00-0000-1000-8000-00805F9B34FB");
    private final static UUID STORE_DATA_UUID = UUID.fromString("0000FF0E-0000-1000-8000-00805F9B34FB");

    private Handler handler;

    private BluetoothGattCharacteristic storeDataCharacteristic;

    public static synchronized MokoBleManager getMokoBleManager(final Context context) {
        if (managerInstance == null) {
            managerInstance = new MokoBleManager(context);
        }
        return managerInstance;
    }

    @Override
    public void log(int priority, @NonNull String message) {
        LogModule.v(message);
    }

    public MokoBleManager(@NonNull Context context) {
        super(context);
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    private void runOnUiThread(@NonNull final Runnable runnable) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            handler.post(runnable);
        } else {
            runnable.run();
        }
    }

    public void setBeaconResponseCallback(MokoResponseCallback mMokoResponseCallback) {
        this.mMokoResponseCallback = mMokoResponseCallback;
    }

    @NonNull
    @Override
    protected BleManagerGattCallback getGattCallback() {
        return new MokoBleManagerGattCallback();
    }

    public class MokoBleManagerGattCallback extends BleManagerGattCallback {
        @Override
        protected void initialize() {
            enableBatteryLevelNotifications();
            readBatteryLevel();
        }

        @Override
        protected boolean isRequiredServiceSupported(@NonNull BluetoothGatt gatt) {
            final BluetoothGattService service = gatt.getService(SERVICE_UUID);
            if (service != null) {
                storeDataCharacteristic = service.getCharacteristic(STORE_DATA_UUID);
            }
            return true;
        }

        @Override
        protected void onDeviceDisconnected() {

        }

        @Override
        protected void onCharacteristicNotified(final @NonNull BluetoothGatt gatt, final @NonNull BluetoothGattCharacteristic characteristic) {
            if (characteristic.getUuid().toString().equals(OrderType.STORE_DATA_NOTIFY.getUuid())) {
                return;
            }
            LogModule.e("onCharacteristicChanged");
            LogModule.e("device to app : " + MokoUtils.bytesToHexString(characteristic.getValue()));
            mMokoResponseCallback.onCharacteristicChanged(characteristic, characteristic.getValue());

        }

        @Override
        protected void onCharacteristicWrite(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic) {
            LogModule.e("onCharacteristicWrite");
            LogModule.e("device to app : " + MokoUtils.bytesToHexString(characteristic.getValue()));
            mMokoResponseCallback.onCharacteristicWrite(characteristic.getValue());
        }

        @Override
        protected void onCharacteristicRead(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic) {
            LogModule.e("onCharacteristicRead");
            LogModule.e("device to app : " + MokoUtils.bytesToHexString(characteristic.getValue()));
            mMokoResponseCallback.onCharacteristicRead(characteristic.getValue());
        }

        @Override
        protected void onDescriptorWrite(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattDescriptor descriptor) {
            mMokoResponseCallback.onDescriptorWrite();
        }

        @Override
        protected void onBatteryValueReceived(@NonNull BluetoothGatt gatt, int value) {
            LogModule.e(String.format("Battery:%d", value));
            mMokoResponseCallback.onBatteryValueReceived(gatt);
        }
    }

    public void enableStoreDataNotify() {
        setIndicationCallback(storeDataCharacteristic).with(new DataReceivedCallback() {
            @Override
            public void onDataReceived(@NonNull BluetoothDevice device, @NonNull Data data) {
                final byte[] value = data.getValue();
                LogModule.e("onDataReceived");
                LogModule.e("device to app : " + MokoUtils.bytesToHexString(value));
                mMokoResponseCallback.onCharacteristicChanged(storeDataCharacteristic, value);
            }
        });
        enableNotifications(storeDataCharacteristic).enqueue();
    }

    public void disableStoreDataNotify() {
        disableNotifications(storeDataCharacteristic).enqueue();
    }

}
