package tastifai.restaurant.Utilities;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.example.rohannevrikar.restaurant.R;


public class BellManager {

    private Context mContext;
    private MediaPlayer mMediaPlayer;
    private AudioManager mAudioManager;
    private TelephonyManager mTelephonyManager;
    private PhoneStateListener phoneStateListener;

    private int originalVolume;
    private boolean isPausedInCall = false;
    private boolean shouldPlay = true;

    public BellManager(Context mContext) {
        this.mContext = mContext;

        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        mTelephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

        if (mAudioManager == null)
            Log.d("BellManager", "Unable to create MediaPlayer");
        if (mTelephonyManager == null)
            Log.d("BellManager", "Unable to get Telephony Manager");

        setMaxVolume();
        setupMediaPlayer();
        setupPauseOnCall();
    }

    public void play() {
        shouldPlay = true;

        if(!isPausedInCall){
            setMaxVolume();
            if(!mMediaPlayer.isPlaying())
                mMediaPlayer.start();
        }
    }

    public void stop() {
        shouldPlay = false;

        if(mMediaPlayer.isPlaying())
            mMediaPlayer.pause();

        restoreOriginalVolume();
    }
    public void destroy(){
        mMediaPlayer.stop();
        mMediaPlayer.release();
        mTelephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
    }

    private void setMaxVolume() {
        if (mAudioManager != null) {
            originalVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        }
    }

    private void restoreOriginalVolume() {
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0);
    }

    private void setupMediaPlayer() {
        mMediaPlayer = MediaPlayer.create(mContext, R.raw.alarm);
        mMediaPlayer.setWakeMode(mContext, PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setLooping(true);
    }

    private void setupPauseOnCall() {
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        if(mMediaPlayer.isPlaying())
                            mMediaPlayer.pause();

                        isPausedInCall = true;
                        break;

                    case TelephonyManager.CALL_STATE_IDLE:
                        if (isPausedInCall && shouldPlay) {
                            isPausedInCall = false;
                            mMediaPlayer.start();
                        }

                        break;
                }
            }
        };

        if (mTelephonyManager != null)
            mTelephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        else
            Toast.makeText(mContext, "Unable to get Telephone Permission", Toast.LENGTH_SHORT).show();
    }
}
