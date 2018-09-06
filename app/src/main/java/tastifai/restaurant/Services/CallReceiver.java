package tastifai.restaurant.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import static tastifai.restaurant.Activities.MainActivity.mediaPlayer;

public class CallReceiver extends BroadcastReceiver {
    TelephonyManager telManager;
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {


        this.context = context;

        telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        telManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);

    }

    private final PhoneStateListener phoneListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            try {
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING: {
                        if (mediaPlayer.isPlaying())
                            mediaPlayer.stop();
                        break;
                    }
                    case TelephonyManager.CALL_STATE_OFFHOOK: {

                        break;
                    }
                    case TelephonyManager.CALL_STATE_IDLE: {
                        if (!mediaPlayer.isPlaying())
                            mediaPlayer.start();
                        break;
                    }
                    default: {
                    }
                }
            } catch (Exception ex) {

            }
        }
    };
}
