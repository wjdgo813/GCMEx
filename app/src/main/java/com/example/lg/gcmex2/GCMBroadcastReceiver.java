package com.example.lg.gcmex2;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.net.URLDecoder;

public class GCMBroadcastReceiver extends WakefulBroadcastReceiver {
	private static final String TAG = "GCMBroadcastReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {		//�곷�諛⑹씠 硫붿떆吏� 蹂대궪��  intent�� 遺�媛��곸씤 �뺣낫濡� �ъ슜
		String action = intent.getAction();	
		Log.d(TAG, "action : " + action);
		
		if (action != null) {
			if (action.equals("com.google.android.c2dm.intent.RECEIVE")) { // �몄떆 硫붿떆吏� �섏떊 ��
				String from = intent.getStringExtra("from");
				String command = intent.getStringExtra("command");		// �쒕쾭�먯꽌 蹂대궦 command �쇰뒗 �ㅼ쓽 value 媛� 
				String type = intent.getStringExtra("type");		// �쒕쾭�먯꽌 蹂대궦 type �쇰뒗 �ㅼ쓽 value 媛� 
				String rawData = intent.getStringExtra("data");		// �쒕쾭�먯꽌 蹂대궦 data �쇰뒗 �ㅼ쓽 value 媛�
				String data = "";
				try {
					data = URLDecoder.decode(rawData, "UTF-8");
				} catch(Exception ex) {
					ex.printStackTrace();
				}
				
				Log.v(TAG, "from : " + from + ", command : " + command + ", type : " + type + ", data : " + data);
				
				// �≫떚鍮꾪떚濡� �꾨떖
				sendToActivity(context, from, command, type, data);
				
			} else {
				Log.d(TAG, "Unknown action : " + action);
			}
		} else {
			Log.d(TAG, "action is null.");
		}
		
	}

	/**
	 * 硫붿씤 �≫떚鍮꾪떚濡� �섏떊�� �몄떆 硫붿떆吏��� �곗씠�� �꾨떖
	 * 
	 * @param context
	 * @param command
	 * @param type
	 * @param data
	 */
	private void sendToActivity(Context context, String from, String command, String type, String data) {
		Intent intent = new Intent(context, MainActivity.class);
		intent.putExtra("from", from);
		intent.putExtra("command", command);
		intent.putExtra("type", type);
		intent.putExtra("data", data);
		
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);

		context.startActivity(intent);
	}

}