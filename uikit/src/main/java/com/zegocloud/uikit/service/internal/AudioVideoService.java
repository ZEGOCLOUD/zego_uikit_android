package com.zegocloud.uikit.service.internal;

import android.util.Log;
import com.zegocloud.uikit.service.defines.ZegoAudioOutputDevice;
import com.zegocloud.uikit.service.defines.ZegoAudioOutputDeviceChangedListener;
import com.zegocloud.uikit.service.defines.ZegoCameraStateChangeListener;
import com.zegocloud.uikit.service.defines.ZegoMicrophoneStateChangeListener;
import com.zegocloud.uikit.service.defines.ZegoSendInRoomCommandCallback;
import com.zegocloud.uikit.service.defines.ZegoSoundLevelUpdateListener;
import com.zegocloud.uikit.service.defines.ZegoTurnOnYourCameraRequestListener;
import com.zegocloud.uikit.service.defines.ZegoTurnOnYourMicrophoneRequestListener;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;
import com.zegocloud.uikit.service.express.ExpressEngineProxy;
import im.zego.zegoexpress.ZegoExpressEngine;
import im.zego.zegoexpress.constants.ZegoAudioRoute;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class AudioVideoService {

    private NotifyList<ZegoMicrophoneStateChangeListener> micStateListeners = new NotifyList<>();
    private NotifyList<ZegoCameraStateChangeListener> cameraStateListeners = new NotifyList<>();
    private NotifyList<ZegoAudioOutputDeviceChangedListener> audioOutputListeners = new NotifyList<>();
    private NotifyList<ZegoSoundLevelUpdateListener> soundLevelListeners = new NotifyList<>();
    private NotifyList<ZegoTurnOnYourCameraRequestListener> turnOnYourCameraRequestListenerNotifyList = new NotifyList<>();
    private NotifyList<ZegoTurnOnYourMicrophoneRequestListener> turnOnYourMicrophoneRequestListenerNotifyList = new NotifyList<>();

    public void addMicrophoneStateListener(ZegoMicrophoneStateChangeListener listener, boolean weakRef) {
        micStateListeners.addListener(listener, weakRef);
    }


    public void removeMicrophoneStateListener(ZegoMicrophoneStateChangeListener listener, boolean weakRef) {
        micStateListeners.removeListener(listener, weakRef);
    }


    public void addCameraStateListener(ZegoCameraStateChangeListener listener, boolean weakRef) {
        cameraStateListeners.addListener(listener, weakRef);
    }

    public void removeCameraStateListener(ZegoCameraStateChangeListener listener, boolean weakRef) {
        cameraStateListeners.removeListener(listener, weakRef);
    }


    public void addAudioOutputDeviceChangedListener(ZegoAudioOutputDeviceChangedListener listener, boolean weakRef) {
        audioOutputListeners.addListener(listener, weakRef);

        ZegoAudioRoute audioRouteType = ExpressEngineProxy.getAudioRouteType();
        ZegoAudioOutputDevice audioRoute = ZegoAudioOutputDevice.getAudioOutputDevice(audioRouteType.value());
        listener.onAudioOutputDeviceChanged(audioRoute);
    }


    public void removeAudioOutputDeviceChangedListener(ZegoAudioOutputDeviceChangedListener listener, boolean weakRef) {
        audioOutputListeners.removeListener(listener, weakRef);
    }

    public void addTurnOnYourCameraRequestListener(ZegoTurnOnYourCameraRequestListener listener, boolean weakRef) {
        turnOnYourCameraRequestListenerNotifyList.addListener(listener, weakRef);
    }

    public void removeTurnOnYourCameraRequestListener(ZegoTurnOnYourCameraRequestListener listener, boolean weakRef) {
        turnOnYourCameraRequestListenerNotifyList.removeListener(listener, weakRef);
    }

    void addTurnOnYourMicrophoneRequestListener(ZegoTurnOnYourMicrophoneRequestListener listener, boolean weakRef) {
        turnOnYourMicrophoneRequestListenerNotifyList.addListener(listener, weakRef);
    }

    void removeTurnOnYourMicrophoneRequestListener(ZegoTurnOnYourMicrophoneRequestListener listener, boolean weakRef) {
        turnOnYourMicrophoneRequestListenerNotifyList.removeListener(listener, weakRef);
    }

    public void addSoundLevelUpdatedListener(ZegoSoundLevelUpdateListener listener, boolean weakRef) {
        soundLevelListeners.addListener(listener, weakRef);

        UIKitCoreUser userInfo = UIKitCore.getInstance().getLocalCoreUser();
        if (userInfo != null) {
            listener.onSoundLevelUpdate(userInfo.getUIKitUser(), userInfo.soundLevel);
        }
    }

    public void removeSoundLevelUpdatedListener(ZegoSoundLevelUpdateListener listener, boolean weakRef) {
        soundLevelListeners.removeListener(listener, weakRef);
    }

    public void clear() {
        micStateListeners.clear();
        cameraStateListeners.clear();
        audioOutputListeners.clear();
        soundLevelListeners.clear();
        turnOnYourCameraRequestListenerNotifyList.clear();
        turnOnYourMicrophoneRequestListenerNotifyList.clear();
    }

    public void notifyAudioRouteChange(ZegoAudioRoute zegoAudioRoute) {
        audioOutputListeners.notifyAllListener(audioOutputChangeListener -> {
            ZegoAudioOutputDevice audioRoute = ZegoAudioOutputDevice.getAudioOutputDevice(zegoAudioRoute.value());
            audioOutputChangeListener.onAudioOutputDeviceChanged(audioRoute);
        });
    }

    public void notifyMicStateChange(UIKitCoreUser coreUser, boolean on) {
        micStateListeners.notifyAllListener(microphoneStateChangeListener -> {
            microphoneStateChangeListener.onMicrophoneOn(coreUser.getUIKitUser(), on);
        });
    }

    public void notifyCameraStateChange(UIKitCoreUser coreUser, boolean on) {
        cameraStateListeners.notifyAllListener(cameraStateChangeListener -> {
            cameraStateChangeListener.onCameraOn(coreUser.getUIKitUser(), on);
        });
    }

    public void notifySoundLevelUpdate(String userID, float soundLevel) {
        ZegoUIKitUser uiKitUser = UIKitCore.getInstance().getUser(userID);
        soundLevelListeners.notifyAllListener(soundLevelUpdateListener -> {
            soundLevelUpdateListener.onSoundLevelUpdate(uiKitUser, soundLevel);
        });
    }

    public void useFrontFacingCamera(boolean isFrontFacing) {
        ExpressEngineProxy.useFrontCamera(isFrontFacing);
    }

    public void setAudioOutputToSpeaker(boolean enable) {
        ExpressEngineProxy.setAudioRouteToSpeaker(enable);
    }

    public ZegoAudioRoute getAudioRouteType() {
        return ZegoExpressEngine.getEngine().getAudioRouteType();
    }

    public void turnMicrophoneOn(String userID, boolean on) {
        UIKitCore uiKitCore = UIKitCore.getInstance();
        UIKitCoreUser coreUser = uiKitCore.getUserbyUserID(userID);
        if (coreUser != null) {
            boolean stateChanged = (coreUser.isMicOpen != on);
            if (uiKitCore.isLocalUser(userID)) {
                ExpressEngineProxy.muteMicrophone(!on);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("isCameraOn", uiKitCore.isCameraOn(userID));
                    jsonObject.put("isMicrophoneOn", on);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String extraInfo = jsonObject.toString();
                ExpressEngineProxy.setStreamExtraInfo(extraInfo, errorCode -> {

                });
                if (on) {
                    String streamID = UIKitCore.generateCameraStreamID(uiKitCore.getRoom().roomID, userID);
                    ExpressEngineProxy.startPublishingStream(streamID);
                } else {
                    if (!uiKitCore.isCameraOn(userID)) {
                        ExpressEngineProxy.stopPublishingStream();
                    }
                }
                coreUser.isMicOpen = on;
                if (stateChanged) {
                    notifyMicStateChange(coreUser, on);
                }
            } else {
                ArrayList<String> userIDs = new ArrayList<>();
                if (!uiKitCore.isLargeRoom()) {
                    userIDs.add(userID);
                }
                JSONObject jsonObject = new JSONObject();
                JSONArray jsonArray = new JSONArray(userIDs);
                try {
                    if (on) {
                        jsonObject.put("zego_turn_microphone_on", jsonArray);
                    } else {
                        jsonObject.put("zego_turn_microphone_off", jsonArray);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String command = jsonObject.toString();
                UIKitCore.getInstance().sendInRoomCommand(command, userIDs, new ZegoSendInRoomCommandCallback() {
                    @Override
                    public void onResult(int errorCode) {

                    }
                });
            }
        }
    }

    public void turnCameraOn(String userID, boolean on) {
        UIKitCore uiKitCore = UIKitCore.getInstance();
        UIKitCoreUser coreUser = uiKitCore.getUserbyUserID(userID);
        if (coreUser != null) {
            boolean stateChanged = (coreUser.isCameraOpen != on);
            if (uiKitCore.isLocalUser(userID)) {
                ExpressEngineProxy.enableCamera(on);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("isCameraOn", on);
                    jsonObject.put("isMicrophoneOn", uiKitCore.isMicrophoneOn(userID));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String extraInfo = jsonObject.toString();
                ExpressEngineProxy.setStreamExtraInfo(extraInfo, errorCode -> {

                });
                if (on) {
                    String streamID = UIKitCore.generateCameraStreamID(uiKitCore.getRoom().roomID, userID);
                    ExpressEngineProxy.startPublishingStream(streamID);
                } else {
                    if (!uiKitCore.isMicrophoneOn(userID)) {
                        ExpressEngineProxy.stopPublishingStream();
                    }
                }
                coreUser.isCameraOpen = on;
                if (stateChanged) {
                    notifyCameraStateChange(coreUser, on);
                }
            } else {
                ArrayList<String> userIDs = new ArrayList<>();
                if (!uiKitCore.isLargeRoom()) {
                    userIDs.add(userID);
                }
                JSONObject jsonObject = new JSONObject();
                JSONArray jsonArray = new JSONArray(userIDs);
                try {
                    if (on) {
                        jsonObject.put("zego_turn_camera_on", jsonArray);
                    } else {
                        jsonObject.put("zego_turn_camera_off", jsonArray);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String command = jsonObject.toString();
                UIKitCore.getInstance().sendInRoomCommand(command, userIDs, errorCode -> {

                });
            }
        }
    }

    public void startPlayingAllAudioVideo() {
        ExpressEngineProxy.muteAllPlayStreamAudio(false);
        ExpressEngineProxy.muteAllPlayStreamVideo(false);
    }

    public void stopPlayingAllAudioVideo() {
        ExpressEngineProxy.muteAllPlayStreamAudio(true);
        ExpressEngineProxy.muteAllPlayStreamVideo(true);
    }

    public void notifyTurnMicrophoneCommand(ZegoUIKitUser uiKitUser, boolean turnOn) {
        if (turnOn) {
            turnOnYourMicrophoneRequestListenerNotifyList.notifyAllListener(
                zegoTurnOnYourMicrophoneRequestListener -> zegoTurnOnYourMicrophoneRequestListener.onTurnOnYourMicrophoneRequest(
                    uiKitUser));
        }
    }

    public void notifyTurnCameraCommand(ZegoUIKitUser uiKitUser, boolean turnOn) {
        if (turnOn) {
            turnOnYourCameraRequestListenerNotifyList.notifyAllListener(
                zegoTurnOnYourCameraRequestListener -> zegoTurnOnYourCameraRequestListener.onTurnOnYourCameraRequest(
                    uiKitUser));
        }
    }

    /**
     * will not stop stream when close camera
     *
     * @param open
     */
    public void openCamera(boolean open) {
        UIKitCoreUser localCoreUser = UIKitCore.getInstance().getLocalCoreUser();
        if (localCoreUser != null) {
            boolean stateChanged = (localCoreUser.isCameraOpen != open);
            ExpressEngineProxy.enableCamera(open);
            localCoreUser.isCameraOpen = open;
            if (stateChanged) {
                notifyCameraStateChange(localCoreUser, open);
            }
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("isCameraOn", open);
                jsonObject.put("isMicrophoneOn", localCoreUser.isMicOpen);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String extraInfo = jsonObject.toString();
            ExpressEngineProxy.setStreamExtraInfo(extraInfo, errorCode -> {

            });
        }
    }

    /**
     * will not stop stream when close mic
     *
     * @param open
     */
    public void openMicrophone(boolean open) {
        UIKitCoreUser localCoreUser = UIKitCore.getInstance().getLocalCoreUser();
        if (localCoreUser != null) {
            boolean stateChanged = (localCoreUser.isMicOpen != open);
            ExpressEngineProxy.muteMicrophone(!open);
            localCoreUser.isMicOpen = open;
            if (stateChanged) {
                notifyMicStateChange(localCoreUser, open);
            }
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("isCameraOn", localCoreUser.isCameraOpen);
                jsonObject.put("isMicrophoneOn", open);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String extraInfo = jsonObject.toString();
            ExpressEngineProxy.setStreamExtraInfo(extraInfo, errorCode -> {

            });
        }
    }
}
