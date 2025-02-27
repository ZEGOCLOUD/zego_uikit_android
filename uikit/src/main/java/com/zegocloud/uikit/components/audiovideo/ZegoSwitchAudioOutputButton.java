package com.zegocloud.uikit.components.audiovideo;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;
import com.zegocloud.uikit.R;
import com.zegocloud.uikit.components.internal.BaseView;
import com.zegocloud.uikit.service.defines.ZegoAudioOutputDevice;
import com.zegocloud.uikit.service.defines.ZegoAudioOutputDeviceChangedListener;
import com.zegocloud.uikit.service.internal.UIKitCore;

public class ZegoSwitchAudioOutputButton extends BaseView {

    private Drawable iconSpeaker;
    private Drawable iconEarpiece;
    private Drawable iconBluetooth;
    private ZegoAudioOutputDeviceChangedListener audioOutputDeviceChangedListener = this::onAudioChange;

    public ZegoSwitchAudioOutputButton(Context context) {
        super(context);
    }

    public ZegoSwitchAudioOutputButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static final int SPEAKER = 0;
    public static final int EARPIECE = 1;
    public static final int BLUETOOTH = 2;
    public static final int HEADPHONE = 3;
    private int audioDevice;

    @Override
    protected void initView(Context context) {
        super.initView(context);
        setSpeakerImageIcon(R.drawable.zego_uikit_icon_speaker);
        setEarpieceIcon(R.drawable.zego_uikit_icon_speaker_off);
        setBluetoothIcon(R.drawable.zego_uikit_icon_bluetooth);
        setOnClickListener(null);
    }

    @Override
    public void initWidgetListener() {
        UIKitCore.getInstance().addAudioOutputDeviceChangedListenerInternal(audioOutputDeviceChangedListener);
    }

    @Override
    protected void unInitWidgetListener() {
        UIKitCore.getInstance().removeAudioOutputDeviceChangedListenerInternal(audioOutputDeviceChangedListener);
    }

    @Override
    public void invokedWhenClick() {
        if (audioDevice == HEADPHONE || audioDevice == BLUETOOTH) {
            return;
        }
        if (audioDevice == SPEAKER || audioDevice == EARPIECE) {
            boolean useSpeaker = audioDevice == SPEAKER;
            UIKitCore.getInstance().setAudioOutputToSpeaker(!useSpeaker);
        }
    }

    public void useSpeaker(boolean isSpeaker) {
        UIKitCore.getInstance().setAudioOutputToSpeaker(isSpeaker);
        if (isSpeaker) {
            setImageDrawable(iconSpeaker);
            audioDevice = SPEAKER;
        }
    }

    private void onAudioChange(ZegoAudioOutputDevice audioOutput) {
        if (audioOutput == ZegoAudioOutputDevice.SPEAKER) {
            setImageDrawable(iconSpeaker);
            audioDevice = SPEAKER;
        } else if (audioOutput == ZegoAudioOutputDevice.EARPIECE) {
            setImageDrawable(iconEarpiece);
            audioDevice = EARPIECE;
        } else if (audioOutput == ZegoAudioOutputDevice.HEADPHONE) {
            setImageDrawable(iconEarpiece);
            audioDevice = HEADPHONE;
        } else if (audioOutput == ZegoAudioOutputDevice.BLUETOOTH) {
            setImageDrawable(iconBluetooth);
            audioDevice = BLUETOOTH;
        }
    }

    public void setIcon(@DrawableRes int iconSpeaker, @DrawableRes int iconEarpiece,
        @DrawableRes int iconBluetooth) {
        setSpeakerImageIcon(iconSpeaker);
        setEarpieceIcon(iconEarpiece);
        setBluetoothIcon(iconBluetooth);
    }

    public void setSpeakerImageIcon(@DrawableRes int iconSpeaker) {
        this.iconSpeaker = ContextCompat.getDrawable(getContext(), iconSpeaker);
    }

    public void setSpeakerImageIcon(Drawable iconSpeaker) {
        this.iconSpeaker = iconSpeaker;
    }

    public void setEarpieceIcon(@DrawableRes int iconEarpiece) {
        this.iconEarpiece = ContextCompat.getDrawable(getContext(), iconEarpiece);
    }

    public void setEarpieceIcon(Drawable iconEarpiece) {
        this.iconEarpiece = iconEarpiece;
    }

    public void setBluetoothIcon(@DrawableRes int iconBluetooth) {
        this.iconBluetooth = ContextCompat.getDrawable(getContext(), iconBluetooth);
    }

    public void setBluetoothIcon(Drawable iconBluetooth) {
        this.iconBluetooth = iconBluetooth;
    }

    public boolean isSpeaker() {
        return audioDevice == SPEAKER;
    }
}
