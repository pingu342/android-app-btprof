package jp.saka.btprof;

import android.content.Intent;
import android.app.Activity;
import android.os.Bundle;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;

import java.util.List;

public class BtprofActivity extends Activity
{
	private static final int REQUEST_ENABLE_BT=1;
	private BluetoothAdapter mBluetoothAdapter=null;
	private BluetoothHeadset mBluetoothHeadset=null;
	private TextView mTextView=null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Button b = (Button)findViewById(R.id.EnumerateBluetoothButton);
		b.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				if (mBluetoothAdapter == null) {
					mTextView.append("NO Bluetooth Adapter.\n");
					return;
				}

				if (mBluetoothHeadset == null) {
					mTextView.append("NO Bluetooth Headset Proxy.\n");
					return;
				}

				// STATE_CONNECTED状態のデバイス一覧を取得
				List<BluetoothDevice> devices = mBluetoothHeadset.getConnectedDevices();

				for (BluetoothDevice d : devices) {
					mTextView.append("BluetoothHeadset\n");
					mTextView.append(" @Name: " + d.getName() + "\n");
					mTextView.append(" @Class: " + d.getBluetoothClass().toString() + "\n");
					mTextView.append(" @Detail: " + d.toString() + "\n");
				}
			}
		});

		mTextView = (TextView)findViewById(R.id.TextView);

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			// デバイスはBluetoothをサポートしていない
			mTextView.append("Bluetooth NOT supported.\n");
			return;
		}

		if (!mBluetoothAdapter.isEnabled()) {
			// Bluetoothが無効化されている
			mTextView.append("Enable Bluetooth.\n");
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		} else {
			mTextView.append("Bluetooth Enabled.\n");
		}

		boolean success = mBluetoothAdapter.getProfileProxy(getApplicationContext(), new ServiceListener() {

			// BluetoothHeadset制御用のプロキシーオブジェクトを取得
			public void onServiceConnected(int profile, BluetoothProfile proxy) {

				if (profile == BluetoothProfile.HEADSET) {

					// BluetoothHeadset制御用のプロキシーオブジェクト
					mBluetoothHeadset = (BluetoothHeadset)proxy;
					mTextView.append("Success to Get BluetoothHeadset Proxy.\n");

				}
			}

			public void onServiceDisconnected(int profile) {
			}

		}, BluetoothProfile.HEADSET);

		if (!success) {
			mTextView.append("Failure to Get BluetoothHeadset Proxy.\n");
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mBluetoothAdapter != null && mBluetoothHeadset != null) {
			mBluetoothAdapter.closeProfileProxy(BluetoothProfile.HEADSET, mBluetoothHeadset);
			mBluetoothHeadset = null;
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE_BT) {
			if (resultCode == RESULT_OK) {
				mTextView.append("Bluetooth Enabled.\n");
			} else {
				mTextView.append("Failure to Enable Bluetooth.\n");
			}
		}
	}

}
