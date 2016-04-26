package com.example.lg.gcmex2;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class GCMIntentService extends IntentService {

	private static final String TAG = "GCMIntentService";

	/**
	 * �앹꽦��
	 */
    public GCMIntentService() {
        super(TAG);

        Log.d(TAG, "GCMIntentService() called.");
    }

    /*
     * �꾨떖諛쏆� �명뀗�� 泥섎━
     */
	@Override
	protected void onHandleIntent(Intent intent) {
		String action = intent.getAction();
		
		Log.d(TAG, "action : " + action);
		
	}

}