package com.zegocloud.uikit.service.internal.interfaces;

import com.zegocloud.uikit.service.defines.ZegoAudioOutputDeviceChangedListener;
import com.zegocloud.uikit.service.defines.ZegoAudioVideoResourceMode;
import com.zegocloud.uikit.service.defines.ZegoCameraStateChangeListener;
import com.zegocloud.uikit.service.defines.ZegoMicrophoneStateChangeListener;
import com.zegocloud.uikit.service.defines.ZegoSoundLevelUpdateListener;
import com.zegocloud.uikit.service.defines.ZegoTurnOnYourCameraRequestListener;
import com.zegocloud.uikit.service.defines.ZegoTurnOnYourMicrophoneRequestListener;
import im.zego.zegoexpress.ZegoExpressEngine;
import im.zego.zegoexpress.callback.IZegoMixerStartCallback;
import im.zego.zegoexpress.callback.IZegoMixerStopCallback;
import im.zego.zegoexpress.constants.ZegoAudioRoute;
import im.zego.zegoexpress.entity.ZegoCanvas;
import im.zego.zegoexpress.entity.ZegoMixerTask;
import im.zego.zegoexpress.entity.ZegoPlayerConfig;

public interface IAudioVideoService {

    void useFrontFacingCamera(boolean isFrontFacing);

    boolean isMicrophoneOn(String userID);

    boolean isCameraOn(String userID);

    void setAudioOutputToSpeaker(boolean enable);

    ZegoAudioRoute getAudioRouteType();

    void turnMicrophoneOn(String userID, boolean on);

    void turnCameraOn(String userID, boolean on);

    void startPlayingAllAudioVideo();

    void stopPlayingAllAudioVideo();

    void mutePlayStreamAudio(String streamID, boolean mute);

    void mutePlayStreamVideo(String streamID, boolean mute);

    public void startMixerTask(ZegoMixerTask task, IZegoMixerStartCallback callback);

    public void stopMixerTask(ZegoMixerTask task, IZegoMixerStopCallback callback);

    public void startPlayingStream(String streamID, ZegoCanvas canvas);

    public void startPlayingStream(String streamID, ZegoCanvas canvas, ZegoPlayerConfig config);

    void addMicrophoneStateListener(ZegoMicrophoneStateChangeListener listener);

    void removeMicrophoneStateListener(ZegoMicrophoneStateChangeListener listener);

    void addCameraStateListener(ZegoCameraStateChangeListener listener);

    void removeCameraStateListener(ZegoCameraStateChangeListener listener);

    void addAudioOutputDeviceChangedListener(ZegoAudioOutputDeviceChangedListener listener);

    void removeAudioOutputDeviceChangedListener(ZegoAudioOutputDeviceChangedListener listener);

    void addSoundLevelUpdatedListener(ZegoSoundLevelUpdateListener listener);

    void removeSoundLevelUpdatedListener(ZegoSoundLevelUpdateListener listener);

    void addTurnOnYourCameraRequestListener(ZegoTurnOnYourCameraRequestListener listener);

    void removeTurnOnYourCameraRequestListener(ZegoTurnOnYourCameraRequestListener listener);

    void addTurnOnYourMicrophoneRequestListener(ZegoTurnOnYourMicrophoneRequestListener listener);

    void removeTurnOnYourMicrophoneRequestListener(ZegoTurnOnYourMicrophoneRequestListener listener);

    void setAudioVideoResourceMode(ZegoAudioVideoResourceMode mode);

    ZegoAudioVideoResourceMode getAudioVideoResourceMode();

    void stopPlayingStream(String streamID);

    void startPreview(ZegoCanvas canvas);

    void stopPreview();

    void startPublishingStream(String streamID);

    void stopPublishingStream();

    void openMicrophone(boolean open);

    void openCamera(boolean open);

    void enable3A(boolean enable);
}
