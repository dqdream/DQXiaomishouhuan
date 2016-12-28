package com.dq.other;

import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattDescriptor;
import android.os.Bundle;

import com.dq.bluetooth.R;

public class MainAty extends Activity {
	UniversalBluetoothLE universalBluetoothLE;
	BluetoothGatt mBluetoothGatt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity);
		universalBluetoothLE = UniversalBluetoothLE.inistance(this);
		universalBluetoothLE.openBbletooth();

		// universalBluetoothLE.bluetoothDeviceList
		mBluetoothGatt = universalBluetoothLE.getConnectGatt(
				universalBluetoothLE.bluetoothDeviceList.get(0), true,
				new BluetoothGattCallback() {
					@Override
					public void onDescriptorRead(BluetoothGatt gatt,
							BluetoothGattDescriptor descriptor, int status) {
						
						super.onDescriptorRead(gatt,descriptor, status);
					}
				});
		mBluetoothGatt.connect();
	}
}
