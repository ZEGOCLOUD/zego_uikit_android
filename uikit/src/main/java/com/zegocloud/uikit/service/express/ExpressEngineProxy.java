package com.zegocloud.uikit.service.express;

import android.app.Application;
import android.text.TextUtils;
import im.zego.zegoexpress.ZegoExpressEngine;
import im.zego.zegoexpress.callback.IZegoCustomVideoProcessHandler;
import im.zego.zegoexpress.callback.IZegoEventHandler;
import im.zego.zegoexpress.callback.IZegoIMSendBarrageMessageCallback;
import im.zego.zegoexpress.callback.IZegoIMSendBroadcastMessageCallback;
import im.zego.zegoexpress.callback.IZegoIMSendCustomCommandCallback;
import im.zego.zegoexpress.callback.IZegoMixerStartCallback;
import im.zego.zegoexpress.callback.IZegoMixerStopCallback;
import im.zego.zegoexpress.callback.IZegoPublisherSetStreamExtraInfoCallback;
import im.zego.zegoexpress.callback.IZegoRoomLoginCallback;
import im.zego.zegoexpress.callback.IZegoRoomLogoutCallback;
import im.zego.zegoexpress.callback.IZegoRoomSetRoomExtraInfoCallback;
import im.zego.zegoexpress.callback.IZegoUploadLogResultCallback;
import im.zego.zegoexpress.constants.ZegoANSMode;
import im.zego.zegoexpress.constants.ZegoAudioRoute;
import im.zego.zegoexpress.constants.ZegoAudioSourceType;
import im.zego.zegoexpress.constants.ZegoOrientation;
import im.zego.zegoexpress.constants.ZegoOrientationMode;
import im.zego.zegoexpress.constants.ZegoPublishChannel;
import im.zego.zegoexpress.constants.ZegoScenario;
import im.zego.zegoexpress.constants.ZegoVideoSourceType;
import im.zego.zegoexpress.entity.ZegoAudioConfig;
import im.zego.zegoexpress.entity.ZegoCanvas;
import im.zego.zegoexpress.entity.ZegoCustomVideoProcessConfig;
import im.zego.zegoexpress.entity.ZegoEngineConfig;
import im.zego.zegoexpress.entity.ZegoEngineProfile;
import im.zego.zegoexpress.entity.ZegoMixerTask;
import im.zego.zegoexpress.entity.ZegoNetworkTimeInfo;
import im.zego.zegoexpress.entity.ZegoPlayerConfig;
import im.zego.zegoexpress.entity.ZegoRoomConfig;
import im.zego.zegoexpress.entity.ZegoScreenCaptureConfig;
import im.zego.zegoexpress.entity.ZegoUser;
import im.zego.zegoexpress.entity.ZegoVideoConfig;
import java.util.ArrayList;
import java.util.List;

public class ExpressEngineProxy {

    private static ZegoOrientation orientation;
    private SimpleExpressEventHandler expressEventHandler;
    private static int minBufferInterval = 0;
    private static int maxBufferInterval = 4000;

    public static void startPlayingStream(String streamID, ZegoCanvas canvas) {
        if (ZegoExpressEngine.getEngine() == null) {
            return;
        }
        setPlayStreamBufferIntervalRange(streamID, minBufferInterval, maxBufferInterval);
        ZegoExpressEngine.getEngine().startPlayingStream(streamID, canvas);
    }

    public static void startPlayingStream(String streamID, ZegoCanvas canvas, ZegoPlayerConfig config) {
        if (ZegoExpressEngine.getEngine() == null) {
            return;
        }
        setPlayStreamBufferIntervalRange(streamID, minBufferInterval, maxBufferInterval);
        ZegoExpressEngine.getEngine().startPlayingStream(streamID, canvas, config);
    }

    public static void startPlayingStream(String streamID) {
        if (ZegoExpressEngine.getEngine() == null) {
            return;
        }
        setPlayStreamBufferIntervalRange(streamID, minBufferInterval, maxBufferInterval);
        ZegoExpressEngine.getEngine().startPlayingStream(streamID);
    }

    public static void startPlayingStream(String streamID, ZegoPlayerConfig config) {
        if (ZegoExpressEngine.getEngine() == null) {
            return;
        }
        setPlayStreamBufferIntervalRange(streamID, minBufferInterval, maxBufferInterval);
        ZegoExpressEngine.getEngine().startPlayingStream(streamID, config);
    }

    public static void startPreview(ZegoCanvas canvas) {
        if (ZegoExpressEngine.getEngine() == null) {
            return;
        }
        ZegoExpressEngine.getEngine().startPreview(canvas);
    }

    public static void stopPreview() {
        if (ZegoExpressEngine.getEngine() == null) {
            return;
        }
        ZegoExpressEngine.getEngine().stopPreview();
    }

    public static void setAppOrientation(ZegoOrientation orientation) {
        ZegoExpressEngine.getEngine().setAppOrientation(orientation);
    }

    public static void setAppOrientationMode(ZegoOrientationMode orientationMode) {
        ZegoExpressEngine.getEngine().setAppOrientationMode(orientationMode);
    }

    public static ZegoAudioRoute getAudioRouteType() {
        return ZegoExpressEngine.getEngine().getAudioRouteType();
    }

    public static void useFrontCamera(boolean isFrontFacing) {
        if (ZegoExpressEngine.getEngine() == null) {
            return;
        }
        ZegoExpressEngine.getEngine().useFrontCamera(isFrontFacing);
    }

    public static void setAudioRouteToSpeaker(boolean routeToSpeaker) {
        if (ZegoExpressEngine.getEngine() == null) {
            return;
        }
        ZegoExpressEngine.getEngine().setAudioRouteToSpeaker(routeToSpeaker);
    }

    public static void muteMicrophone(boolean mute) {
        if (ZegoExpressEngine.getEngine() == null) {
            return;
        }
        ZegoExpressEngine.getEngine().muteMicrophone(mute);
    }

    public static void setStreamExtraInfo(String extraInfo, IZegoPublisherSetStreamExtraInfoCallback callback) {
        if (ZegoExpressEngine.getEngine() == null) {
            return;
        }
        ZegoExpressEngine.getEngine().setStreamExtraInfo(extraInfo, callback);
    }

    public static void startPublishingStream(String streamID) {
        if (ZegoExpressEngine.getEngine() == null) {
            return;
        }
        ZegoExpressEngine.getEngine().startPublishingStream(streamID);
    }

    public static void startPublishingStream(String streamID, ZegoPublishChannel channel) {
        if (ZegoExpressEngine.getEngine() == null) {
            return;
        }
        ZegoExpressEngine.getEngine().startPublishingStream(streamID, channel);
    }

    public static void setAudioConfig(ZegoAudioConfig config, ZegoPublishChannel channel) {
        ZegoExpressEngine.getEngine().setAudioConfig(config, channel);
    }

    public static void setPlayStreamBufferIntervalRange(int minBufferInterval, int maxBufferInterval) {
        ExpressEngineProxy.minBufferInterval = minBufferInterval;
        ExpressEngineProxy.maxBufferInterval = maxBufferInterval;
    }

    public static void setPlayStreamBufferIntervalRange(String streamID, int minBufferInterval, int maxBufferInterval) {
        ZegoExpressEngine.getEngine().setPlayStreamBufferIntervalRange(streamID, minBufferInterval, maxBufferInterval);
    }

    public static void setVideoConfig(ZegoVideoConfig config, ZegoPublishChannel channel) {
        ZegoExpressEngine.getEngine().setVideoConfig(config, channel);
    }

    public static ZegoVideoConfig getVideoConfig(ZegoPublishChannel channel) {
        return ZegoExpressEngine.getEngine().getVideoConfig(channel);
    }

    public static void stopPublishingStream() {
        if (ZegoExpressEngine.getEngine() == null) {
            return;
        }
        ZegoExpressEngine.getEngine().stopPublishingStream();
    }

    public static void stopPublishingStream(ZegoPublishChannel channel) {
        if (ZegoExpressEngine.getEngine() == null) {
            return;
        }
        ZegoExpressEngine.getEngine().stopPublishingStream(channel);
    }

    public static void enableCamera(boolean on) {
        if (ZegoExpressEngine.getEngine() == null) {
            return;
        }
        ZegoExpressEngine.getEngine().enableCamera(on);
    }

    public static void muteAllPlayStreamAudio(boolean mute) {
        if (ZegoExpressEngine.getEngine() == null) {
            return;
        }
        ZegoExpressEngine.getEngine().muteAllPlayStreamAudio(mute);
    }

    public static void muteAllPlayStreamVideo(boolean mute) {
        if (ZegoExpressEngine.getEngine() == null) {
            return;
        }
        ZegoExpressEngine.getEngine().muteAllPlayStreamVideo(mute);
    }

    public static void sendBroadcastMessage(String roomID, String message,
        IZegoIMSendBroadcastMessageCallback callback) {
        if (ZegoExpressEngine.getEngine() == null) {
            return;
        }
        ZegoExpressEngine.getEngine().sendBroadcastMessage(roomID, message, callback);
    }

    public static void loginRoom(String roomID, ZegoUser user, ZegoRoomConfig config,
        IZegoRoomLoginCallback iZegoRoomLoginCallback) {
        if (ZegoExpressEngine.getEngine() == null) {
            return;
        }
        ZegoExpressEngine.getEngine().loginRoom(roomID, user, config, iZegoRoomLoginCallback);
    }

    public static void startSoundLevelMonitor() {
        if (ZegoExpressEngine.getEngine() == null) {
            return;
        }
        ZegoExpressEngine.getEngine().startSoundLevelMonitor();
    }

    public static void logoutRoom(IZegoRoomLogoutCallback callback) {
        if (ZegoExpressEngine.getEngine() == null) {
            return;
        }
        ZegoExpressEngine.getEngine().logoutRoom(callback);
    }

    public static void setRoomExtraInfo(String roomID, String key, String value,
        IZegoRoomSetRoomExtraInfoCallback callback) {
        if (ZegoExpressEngine.getEngine() == null) {
            return;
        }
        ZegoExpressEngine.getEngine().setRoomExtraInfo(roomID, key, value, callback);
    }

    public static void sendCustomCommand(String roomID, String command, ArrayList<ZegoUser> toUserList,
        IZegoIMSendCustomCommandCallback callback) {
        if (ZegoExpressEngine.getEngine() == null) {
            return;
        }
        ZegoExpressEngine.getEngine().sendCustomCommand(roomID, command, toUserList, callback);
    }

    public static void stopPlayingStream(String streamID) {
        if (ZegoExpressEngine.getEngine() == null) {
            return;
        }
        ZegoExpressEngine.getEngine().stopPlayingStream(streamID);
    }

    public static void stopSoundLevelMonitor() {
        if (ZegoExpressEngine.getEngine() == null) {
            return;
        }
        ZegoExpressEngine.getEngine().stopSoundLevelMonitor();
    }

    public static ZegoExpressEngine getEngine() {
        return ZegoExpressEngine.getEngine();
    }

    public static void setEngineConfig(ZegoEngineConfig config) {
        ZegoExpressEngine.setEngineConfig(config);
    }

    public static void createEngine(ZegoEngineProfile profile, IZegoEventHandler iZegoEventHandler) {
        ZegoExpressEngine.createEngine(profile, iZegoEventHandler);
    }

    public static void setVideoSource(ZegoVideoSourceType source, ZegoPublishChannel channel) {
        ZegoExpressEngine.getEngine().setVideoSource(source, channel);
    }

    public static void setAudioSource(ZegoAudioSourceType source, ZegoPublishChannel channel) {
        ZegoExpressEngine.getEngine().setAudioSource(source, channel);
    }

    public static void startScreenCapture() {
        ZegoExpressEngine.getEngine().startScreenCapture();
    }

    public static void startScreenCapture(ZegoScreenCaptureConfig config) {
        ZegoExpressEngine.getEngine().startScreenCapture(config);
    }

    public static void stopScreenCapture() {
        ZegoExpressEngine.getEngine().stopScreenCapture();
    }

    public static long getNetworkTimeInfo() {
        ZegoNetworkTimeInfo networkTimeInfo = ZegoExpressEngine.getEngine().getNetworkTimeInfo();
        return networkTimeInfo.timestamp;
    }

    public static void renewToken(String roomID, String token) {
        ZegoExpressEngine.getEngine().renewToken(roomID, token);
    }

    public void createEngine(Application application, long appID, String appSign, ZegoScenario scenario) {
        ZegoEngineProfile profile = new ZegoEngineProfile();
        profile.appID = appID;
        if (!TextUtils.isEmpty(appSign)) {
            profile.appSign = appSign;
        }
        profile.scenario = scenario;
        profile.application = application;
        expressEventHandler = new SimpleExpressEventHandler();
        ZegoExpressEngine.createEngine(profile, null);
        ZegoExpressEngine.getEngine().setEventHandler(expressEventHandler);
    }

    public void enable3A(boolean enable) {
        if (ZegoExpressEngine.getEngine() == null) {
            return;
        }
        ZegoExpressEngine.getEngine().enableAGC(enable);
        ZegoExpressEngine.getEngine().enableAEC(enable);
        ZegoExpressEngine.getEngine().enableANS(enable);
        ZegoExpressEngine.getEngine().enableTransientANS(enable);
        if (enable) {
            ZegoExpressEngine.getEngine().setANSMode(ZegoANSMode.AGGRESSIVE);
        }
    }

    public void sendSEI(byte[] data) {
        if (ZegoExpressEngine.getEngine() == null) {
            return;
        }
        ZegoExpressEngine.getEngine().sendSEI(data);
    }


    public void startSoundLevelMonitor(int millisecond) {
        if (ZegoExpressEngine.getEngine() == null) {
            return;
        }
        ZegoExpressEngine.getEngine().startSoundLevelMonitor(millisecond);
    }

    public void addEventHandler(IZegoEventHandler eventHandler) {
        expressEventHandler.addEventHandler(eventHandler);
    }

    public void removeEventHandler(IZegoEventHandler eventHandler) {
        expressEventHandler.removeEventHandler(eventHandler);
    }

    public void removeEventHandlerList(List<IZegoEventHandler> list) {
        if (list.isEmpty()) {
            return;
        }
        expressEventHandler.removeEventHandlerList(list);
    }

    public void removeAllEventHandlers() {
        expressEventHandler.removeAllEventHandlers();
    }


    public void enableCustomVideoProcessing(boolean enable, ZegoCustomVideoProcessConfig config,
        ZegoPublishChannel channel) {
        if (ZegoExpressEngine.getEngine() == null) {
            return;
        }
        ZegoExpressEngine.getEngine().enableCustomVideoProcessing(enable, config, channel);
    }

    public void setCustomVideoProcessHandler(IZegoCustomVideoProcessHandler handler) {
        if (ZegoExpressEngine.getEngine() == null) {
            return;
        }
        ZegoExpressEngine.getEngine().setCustomVideoProcessHandler(handler);
    }

    public ZegoVideoConfig getVideoConfig() {
        if (ZegoExpressEngine.getEngine() == null) {
            return null;
        }
        return ZegoExpressEngine.getEngine().getVideoConfig();
    }

    public void sendCustomVideoProcessedTextureData(int textureID, int width, int height,
        long referenceTimeMillisecond) {
        if (ZegoExpressEngine.getEngine() == null) {
            return;
        }
        ZegoExpressEngine.getEngine()
            .sendCustomVideoProcessedTextureData(textureID, width, height, referenceTimeMillisecond);
    }

    public void sendBarrageMessage(String roomID, String message, IZegoIMSendBarrageMessageCallback callback) {
        if (ZegoExpressEngine.getEngine() == null) {
            return;
        }
        ZegoExpressEngine.getEngine().sendBarrageMessage(roomID, message, callback);
    }

    public void mutePlayStreamAudio(String streamID, boolean mute) {
        if (ZegoExpressEngine.getEngine() == null) {
            return;
        }
        ZegoExpressEngine.getEngine().mutePlayStreamAudio(streamID, mute);
    }

    public void mutePlayStreamVideo(String streamID, boolean mute) {
        if (ZegoExpressEngine.getEngine() == null) {
            return;
        }
        ZegoExpressEngine.getEngine().mutePlayStreamVideo(streamID, mute);
    }

    public void startMixerTask(ZegoMixerTask task, IZegoMixerStartCallback callback) {
        if (ZegoExpressEngine.getEngine() == null) {
            return;
        }
        ZegoExpressEngine.getEngine().startMixerTask(task, callback);
    }

    public void stopMixerTask(ZegoMixerTask task, IZegoMixerStopCallback callback) {
        if (ZegoExpressEngine.getEngine() == null) {
            return;
        }
        ZegoExpressEngine.getEngine().stopMixerTask(task, callback);
    }

    public void setRoomScenario(ZegoScenario scenario) {
        if (ZegoExpressEngine.getEngine() == null) {
            return;
        }
        ZegoExpressEngine.getEngine().setRoomScenario(scenario);
    }


    public void uploadLog(IZegoUploadLogResultCallback callback) {
        ZegoExpressEngine.getEngine().uploadLog(callback);
    }

}
