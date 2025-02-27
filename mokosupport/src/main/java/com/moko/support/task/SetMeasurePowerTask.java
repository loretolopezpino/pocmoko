package com.moko.support.task;

import androidx.annotation.IntRange;

import com.moko.support.callback.MokoOrderTaskCallback;
import com.moko.support.entity.OrderType;

public class SetMeasurePowerTask extends OrderTask {
    public byte[] data;

    public SetMeasurePowerTask(MokoOrderTaskCallback callback) {
        super(OrderType.MEASURE_POWER, callback, OrderTask.RESPONSE_TYPE_WRITE);
    }

    public void setData(@IntRange(from = -127, to = 0) int measurePower) {
        this.data = new byte[1];
        data[0] = (byte) measurePower;
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
