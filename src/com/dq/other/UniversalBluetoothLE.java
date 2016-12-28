package com.dq.other;

import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

public class UniversalBluetoothLE {
	// UniversalBluetoothLE
	public static UniversalBluetoothLE universalBluetoothLE;

	private Context context;
	// BluetoothAdapter
	private BluetoothAdapter mBluetoothAdapter;
	// BluetoothManager
	private BluetoothManager bluetoothManager;

	// ��������������
	public static final int REQUEST_ENABLE_BLUETOOTH = 10010;

	// �Ƿ�����ɨ�������豸
	private boolean mScanning;
	// ����ɨ��ʱ��
	private static final long SCAN_PERIOD = 10000;

	// ����ɨ��ķ���
	BluetoothAdapter.LeScanCallback leScanCallback;
	// ��������list
	List<BluetoothDevice> bluetoothDeviceList = new ArrayList<BluetoothDevice>();

	Handler mHandler = new Handler();

	LeScanListenter leScanListenter;

	private UniversalBluetoothLE(Context context) {
		this.context = context;
		// �õ�BluetoothManager
		this.bluetoothManager = (BluetoothManager) context
				.getSystemService(Context.BLUETOOTH_SERVICE);
		// �õ�BluetoothAdapter
		this.mBluetoothAdapter = bluetoothManager.getAdapter();

		// ���������Ļص�
		leScanCallback = new BluetoothAdapter.LeScanCallback() {

			@Override
			public void onLeScan(BluetoothDevice device, int rssi,
					byte[] scanRecord) {
				bluetoothDeviceList.add(device);

				// ���������б�
				leScanListenter.leScanCallBack(bluetoothDeviceList);

			}
		};
	}

	/**
	 * ��õ�UniversalBluetoothLE����
	 * 
	 * @param context
	 * @return
	 */
	public static UniversalBluetoothLE inistance(Context context) {
		if (universalBluetoothLE == null) {
			universalBluetoothLE = new UniversalBluetoothLE(context);
		}
		return universalBluetoothLE;
	}

	/**
	 * ��������Ƿ�򿪲��������������ķ���
	 */
	public void openBbletooth() {
		// �ж������Ƿ���
		if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
			// ������
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			context.startActivity(enableIntent);
		}
	}

	/**
	 * ��ʼ��true�������false������ɨ��
	 * 
	 * @param enable
	 */
	private void scanLeDevice(final boolean enable) {
		if (enable && mScanning == false) {
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mScanning = false;
					mBluetoothAdapter.stopLeScan(leScanCallback);
				}
			}, SCAN_PERIOD);

			mScanning = true;
			mBluetoothAdapter.startLeScan(leScanCallback);
		} else {
			mScanning = false;
			mBluetoothAdapter.stopLeScan(leScanCallback);
		}
	}

	/**
	 * ��ʼ���������豸
	 * 
	 * @param leScanListenter
	 *            ���������豸�Ļص��������豸�б?
	 */
	public void startScanLeDevice(final LeScanListenter leScanListenter) {
		bluetoothDeviceList.clear();
		this.leScanListenter = leScanListenter;
		scanLeDevice(true);
	}

	/**
	 * ֹͣ�����豸
	 */
	public void stopScanLeDevice() {
		if (leScanCallback == null)
			return;
		scanLeDevice(false);
	}

	/**
	 * ���������Ļص�
	 */
	public interface LeScanListenter {
		void leScanCallBack(List<BluetoothDevice> bluetoothDeviceList);
	}

	/**
	 * �õ�BluetoothGatt
	 * 
	 * @param device
	 *            �豸
	 * @param autoConnect
	 *            �Ƿ��Զ�����
	 * @param bluetoothGattCallback
	 *            �ص�
	 */
	public BluetoothGatt getConnectGatt(BluetoothDevice device,
			boolean autoConnect, BluetoothGattCallback bluetoothGattCallback) {
		return device.connectGatt(context, autoConnect, bluetoothGattCallback);
	}

}
