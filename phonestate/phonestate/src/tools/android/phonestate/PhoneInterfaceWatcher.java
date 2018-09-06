package tools.android.phonestate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneInterfaceWatcher extends BroadcastReceiver {

    private boolean enableLogcat = false;
    private boolean stop = false;

    private Handler mHandler = new Handler(new HandlerThread("PhoneState-single-thread") {{
        start();
    }}.getLooper());

    public PhoneInterfaceWatcher() {
    }

    public PhoneInterfaceWatcher(boolean enableLogcat) {
        this.enableLogcat = enableLogcat;
    }

    public void pause() {
        say("PhoneWatcher", "pause");
        this.stop = true;
    }

    public void resume() {
        say("PhoneWatcher", "resume");
        this.stop = false;
    }

    public void release() {
        say("PhoneWatcher", "unregist");
        this.mHandler = null;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        PhoneState state = getCurrentPhoneState(context);
        say("PhoneWatcher", "receive intent|current state|" + state);
        if (stop) return;
//        onReceiveIntent(context);
        mHandler.post(new ContextRunnable(context, state) {
            @Override
            public void run(Context context, PhoneState state) {
                onReceiveIntent(context, state);
            }
        });
    }

    public static PhoneState getCurrentPhoneState(Context context) {
        PhoneState state = PhoneState.UNKNOWN;
        try {
            int intState = -1;
            TelephonyManager manager = (TelephonyManager) context.getApplicationContext().getSystemService(android.content.Context.TELEPHONY_SERVICE);
            intState = manager.getCallState();
            if (TelephonyManager.CALL_STATE_IDLE == intState) {
                state = PhoneState.NOT_IN_USE;
            } else if (TelephonyManager.CALL_STATE_RINGING == intState) {
                state = PhoneState.RECEIVING;
            } else if (TelephonyManager.CALL_STATE_OFFHOOK == intState) {
                state = PhoneState.SENDING;
            }
        } catch (Throwable t) {
            t.printStackTrace();
            state = PhoneState.UNKNOWN;
        }
        return state;
    }

    abstract class ContextRunnable implements Runnable {
        Context context = null;
        PhoneState state = null;
        public ContextRunnable(Context context, PhoneState state) {
            this.context = context;
            this.state = state;
        }
        @Override
        public void run() {
            run(this.context, this.state);
        }
        abstract public void run(Context context, PhoneState state);
    }

    protected void phoneNotInUse() {
        say("PhoneWatcher", "call phoneNotInUse");
    }

    protected void phoneReceiving() {
        say("PhoneWatcher", "call phoneReceiving");
    }

    protected void phoneSending() {
        say("PhoneWatcher", "call phoneSending");
    }

    private int flags = 0xFF;

    private void onReceiveIntent(Context context, PhoneState state) {
        if (PhoneState.NOT_IN_USE == state) {
            say("PhoneWatcher", "is not in use");
            flags = 0xFF;
            phoneNotInUse();
            say("PhoneWatcher", "ret@0");
            return;
        }
        if (PhoneState.RECEIVING == state) {
            say("NetworkWatcher", "is receiving");
            if (((flags >> 0) & 0x01) == 1) {
                flags = flags & 0xFE | 0xFE;
                phoneReceiving();
            }
            say("PhoneWatcher", "ret@1");
            return;
        }
        if (PhoneState.SENDING == state) {
            say("PhoneWatcher", "is sending");
            if (((flags >> 1) & 0x01) == 1) {
                flags = flags & 0xFC | 0x01;
                phoneSending();
            }
            say("PhoneWatcher", "ret@2");
            return;
        }

    }

    protected void say(String who, String what) {
        if (enableLogcat) {
            Log.d(who, what);
        }
    }
}
