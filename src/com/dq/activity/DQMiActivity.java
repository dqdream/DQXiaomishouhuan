package com.dq.activity;

import java.util.Arrays;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dq.bluetooth.R;
import com.dq.xiaomisdk.ActionCallback;
import com.dq.xiaomisdk.MiBand;
import com.dq.xiaomisdk.model.BatteryInfo;
import com.dq.xiaomisdk.model.UserInfo;
import com.dq.xiaomisdk.model.VibrationMode;
import com.dq.xiaomisdklisteners.HeartRateNotifyListener;
import com.dq.xiaomisdklisteners.NotifyListener;
import com.dq.xiaomisdklisteners.RealtimeStepsNotifyListener;

public class DQMiActivity extends Activity implements OnClickListener {
	public final String TAG = "DQMiActivity";
	TextView text_connect;
	Button button1, button2, button3, button4, button5, button6,button0;
	private MiBand miband;
	BluetoothDevice device;
	private ScrollView scrollView;
	boolean isConnect = false;
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				text_connect.append("\r\n");
				text_connect.append(msg.getData().getString("data"));
				scrollView.fullScroll(ScrollView.FOCUS_DOWN);
			}else if (msg.what==2) {
				 miband.startHeartRateScan();
			}else if (msg.what==3) {
				 miband.enableRealtimeStepsNotify();
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mi_activity);
		init();
	}

	private void fingID() {
		text_connect = (TextView) findViewById(R.id.text_connect);
		button1 = (Button) findViewById(R.id.button1);
		button2 = (Button) findViewById(R.id.button2);
		button3 = (Button) findViewById(R.id.button3);
		button4 = (Button) findViewById(R.id.button4);
		button5 = (Button) findViewById(R.id.button5);
		button6 = (Button) findViewById(R.id.button6);
		button0 = (Button) findViewById(R.id.button0);
		scrollView = (ScrollView) findViewById(R.id.scrollView);
		button0.setOnClickListener(this);
		button1.setOnClickListener(this);
		button2.setOnClickListener(this);
		button3.setOnClickListener(this);
		button4.setOnClickListener(this);
		button5.setOnClickListener(this);
		button6.setOnClickListener(this);
	}

	private void init() {
		fingID();
		Intent intent = this.getIntent();
		device = intent.getParcelableExtra("device");
		miband = new MiBand(this);
		sendMsg(device.getName()+"|"+device.getAddress());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button1:
			final ProgressDialog pd = ProgressDialog.show(this, "",
					"努力运行中, 请稍后......");
			miband.connect(device, new ActionCallback() {

				@Override
				public void onSuccess(Object data) {
					pd.dismiss();
					Log.d(TAG, "连接成功!!!");
					isConnect = true;
					sendMsg("连接成功，请设置用户信息");
					miband.setDisconnectedListener(new NotifyListener() {
						@Override
						public void onNotify(byte[] data) {
							Log.d(TAG, "连接断开!!!");
							isConnect = false;
							sendMsg("连接断开");

						}
					});
				}

				@Override
				public void onFail(int errorCode, String msg) {
					pd.dismiss();
					isConnect = false;
					sendMsg("连接失败");
					Log.d(TAG, "connect fail, code:" + errorCode + ",mgs:"
							+ msg);
				}
			});
			break;
		case R.id.button2:
			UserInfo userInfo = new UserInfo(718536517, 1, 25, 170, 60, "D梦", 0);
            Log.d(TAG, "setUserInfo:" + userInfo.toString() + ",data:" + Arrays.toString(userInfo.getBytes(miband.getDevice().getAddress())));
            miband.setUserInfo(userInfo);
            sendMsg("手环如果震动，请拍一下手环进行配对");
			break;
		case R.id.button0:
			if (isConnect) {
				  miband.getBatteryInfo(new ActionCallback() {

                      @Override
                      public void onSuccess(Object data) {
                          BatteryInfo info = (BatteryInfo) data;
                          Log.d(TAG, info.toString());
                          sendMsg("当前电量："+info.toString());
                      }

                      @Override
                      public void onFail(int errorCode, String msg) {
                          Log.d(TAG, "getBatteryInfo fail");
                          sendMsg("电量获取失败");
                      }
                  });
			}
			break;
		case R.id.button3:
			if (isConnect) {
				miband.startVibration(VibrationMode.VIBRATION_WITH_LED);
				sendMsg("手环震动");
			}
			break;
		case R.id.button4:
			if (isConnect) {
				 miband.setHeartRateScanListener(new HeartRateNotifyListener() {
                     @Override
                     public void onNotify(int heartRate) {
                         Log.d(TAG, "heart rate: " + heartRate);
                         sendMsg("心率结果："+heartRate);
                     }
                 });
				 handler.sendEmptyMessageDelayed(2, 1000);
			}
			break;
		case R.id.button5:
			if (isConnect) {
				miband.setRealtimeStepsNotifyListener(new RealtimeStepsNotifyListener() {

                    @Override
                    public void onNotify(int steps) {
                        Log.d(TAG, "RealtimeStepsNotifyListener:" + steps);
                        sendMsg("当前步数："+steps);
                    }
                });
				sendMsg("请晃动手环10-20下");
				handler.sendEmptyMessageDelayed(3, 1000);
			}
			break;
		case R.id.button6:
			if (isConnect) {
				miband.disableRealtimeStepsNotify();
			}
			break;
		}

	}

	public void sendMsg(String data) {
		Message msg = new Message();
		msg.what = 1;
		Bundle bundle = new Bundle();
		bundle.putString("data", data);
		msg.setData(bundle);
		handler.sendMessage(msg);
	}
}
