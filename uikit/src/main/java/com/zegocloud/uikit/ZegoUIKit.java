package com.zegocloud.uikit;

import android.app.Application;
import android.content.Context;
import androidx.annotation.NonNull;
import com.elvishew.xlog.XLog;
import com.elvishew.xlog.flattener.ClassicFlattener;
import com.elvishew.xlog.printer.Printer;
import com.elvishew.xlog.printer.file.FilePrinter;
import com.elvishew.xlog.printer.file.backup.FileSizeBackupStrategy2;
import com.elvishew.xlog.printer.file.clean.FileLastModifiedCleanStrategy;
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator;
import com.zegocloud.uikit.components.common.ZegoPresetResolution;
import com.zegocloud.uikit.internal.ZegoUIKitLanguage;
import com.zegocloud.uikit.plugin.beauty.IBeautyPlugin;
import com.zegocloud.uikit.plugin.common.IZegoUIKitSignalingPlugin;
import com.zegocloud.uikit.service.defines.RoomStateChangedListener;
import com.zegocloud.uikit.service.defines.ZegoAudioOutputDeviceChangedListener;
import com.zegocloud.uikit.service.defines.ZegoAudioVideoResourceMode;
import com.zegocloud.uikit.service.defines.ZegoAudioVideoUpdateListener;
import com.zegocloud.uikit.service.defines.ZegoBarrageMessageListener;
import com.zegocloud.uikit.service.defines.ZegoCameraStateChangeListener;
import com.zegocloud.uikit.service.defines.ZegoInRoomCommandListener;
import com.zegocloud.uikit.service.defines.ZegoInRoomMessage;
import com.zegocloud.uikit.service.defines.ZegoInRoomMessageListener;
import com.zegocloud.uikit.service.defines.ZegoInRoomMessageSendStateListener;
import com.zegocloud.uikit.service.defines.ZegoMeRemovedFromRoomListener;
import com.zegocloud.uikit.service.defines.ZegoMicrophoneStateChangeListener;
import com.zegocloud.uikit.service.defines.ZegoOnlySelfInRoomListener;
import com.zegocloud.uikit.service.defines.ZegoRoomPropertyUpdateListener;
import com.zegocloud.uikit.service.defines.ZegoScenario;
import com.zegocloud.uikit.service.defines.ZegoScreenSharingUpdateListener;
import com.zegocloud.uikit.service.defines.ZegoSendInRoomCommandCallback;
import com.zegocloud.uikit.service.defines.ZegoSoundLevelUpdateListener;
import com.zegocloud.uikit.service.defines.ZegoTurnOnYourCameraRequestListener;
import com.zegocloud.uikit.service.defines.ZegoTurnOnYourMicrophoneRequestListener;
import com.zegocloud.uikit.service.defines.ZegoUIKitCallback;
import com.zegocloud.uikit.service.defines.ZegoUIKitRoom;
import com.zegocloud.uikit.service.defines.ZegoUIKitTokenExpireListener;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;
import com.zegocloud.uikit.service.defines.ZegoUserCountOrPropertyChangedListener;
import com.zegocloud.uikit.service.defines.ZegoUserUpdateListener;
import com.zegocloud.uikit.service.express.ExpressEngineProxy;
import com.zegocloud.uikit.service.express.IExpressEngineEventHandler;
import com.zegocloud.uikit.service.internal.UIKitCore;
import com.zegocloud.uikit.service.internal.interfaces.IUIKitCore;
import im.zego.zegoexpress.callback.IZegoIMSendBarrageMessageCallback;
import im.zego.zegoexpress.callback.IZegoMixerStartCallback;
import im.zego.zegoexpress.callback.IZegoMixerStopCallback;
import im.zego.zegoexpress.constants.ZegoAudioRoute;
import im.zego.zegoexpress.constants.ZegoOrientation;
import im.zego.zegoexpress.entity.ZegoCanvas;
import im.zego.zegoexpress.entity.ZegoMixerTask;
import im.zego.zegoexpress.entity.ZegoPlayerConfig;
import im.zego.zegoexpress.entity.ZegoVideoConfig;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import timber.log.Timber;

public class ZegoUIKit {

    private static IUIKitCore uiKitCore = UIKitCore.getInstance();
    private static String logFileDir;

    public static boolean init(Application application, Long appID, String appSign, ZegoScenario scenario) {
        return uiKitCore.init(application, appID, appSign, scenario);
    }

    public static boolean initExpressEngine(Application application, Long appID, String appSign,
        ZegoScenario scenario) {
        return uiKitCore.initExpressEngine(application, appID, appSign, scenario);
    }

    public static boolean isExpressEngineInitSucceed() {
        return uiKitCore.isExpressEngineInitSucceed();
    }

    public static void useFrontFacingCamera(boolean isFrontFacing) {
        uiKitCore.useFrontFacingCamera(isFrontFacing);
    }

    public static boolean isMicrophoneOn(String userID) {
        return uiKitCore.isMicrophoneOn(userID);
    }

    public static boolean isCameraOn(String userID) {
        return uiKitCore.isCameraOn(userID);
    }

    public static void setAudioOutputToSpeaker(boolean enable) {
        uiKitCore.setAudioOutputToSpeaker(enable);
    }

    public static ZegoAudioRoute getAudioRouteType() {
        return uiKitCore.getAudioRouteType();
    }

    public static void turnMicrophoneOn(String userID, boolean on) {
        uiKitCore.turnMicrophoneOn(userID, on);
    }

    public static void turnCameraOn(String userID, boolean on) {
        uiKitCore.turnCameraOn(userID, on);
    }

    public static void addMicrophoneStateListener(ZegoMicrophoneStateChangeListener listener) {
        uiKitCore.addMicrophoneStateListener(listener);
    }


    public static void removeMicrophoneStateListener(ZegoMicrophoneStateChangeListener listener) {
        uiKitCore.removeMicrophoneStateListener(listener);
    }


    public static void addCameraStateListener(ZegoCameraStateChangeListener listener) {
        uiKitCore.addCameraStateListener(listener);
    }


    public static void removeCameraStateListener(ZegoCameraStateChangeListener listener) {
        uiKitCore.removeCameraStateListener(listener);
    }


    public static void addAudioOutputDeviceChangedListener(ZegoAudioOutputDeviceChangedListener listener) {
        uiKitCore.addAudioOutputDeviceChangedListener(listener);
    }

    public static void removeAudioOutputDeviceChangedListener(ZegoAudioOutputDeviceChangedListener listener) {
        uiKitCore.removeAudioOutputDeviceChangedListener(listener);
    }

    public static void setAppOrientation(ZegoOrientation orientation) {
        ExpressEngineProxy.setAppOrientation(orientation);
    }

    public static void addSoundLevelUpdatedListener(ZegoSoundLevelUpdateListener listener) {
        uiKitCore.addSoundLevelUpdatedListener(listener);
    }

    public static void removeSoundLevelUpdatedListener(ZegoSoundLevelUpdateListener listener) {
        uiKitCore.removeSoundLevelUpdatedListener(listener);
    }


    public static void joinRoom(String roomID, ZegoUIKitCallback callback) {
        uiKitCore.joinRoom(roomID, callback);
    }

    public static void joinRoom(String roomID, boolean markAsLargeRoom, ZegoUIKitCallback callback) {
        uiKitCore.joinRoom(roomID, markAsLargeRoom, callback);
    }


    public static void leaveRoom() {
        uiKitCore.leaveRoom();
    }

    public static ZegoUIKitRoom getRoom() {
        return uiKitCore.getRoom();
    }

    public static void setRoomProperty(String key, String value) {
        uiKitCore.setRoomProperty(key, value);
    }

    public static void updateRoomProperties(Map<String, String> newProperties) {
        uiKitCore.updateRoomProperties(newProperties);
    }

    public static Map<String, String> getRoomProperties() {
        return uiKitCore.getRoomProperties();
    }

    public static void addRoomPropertyUpdateListener(ZegoRoomPropertyUpdateListener listener) {
        uiKitCore.addRoomPropertyUpdateListener(listener);
    }

    public static void removeRoomPropertyUpdateListener(ZegoRoomPropertyUpdateListener listener) {
        uiKitCore.removeRoomPropertyUpdateListener(listener);
    }

    public static void login(String userID, String userName) {
        uiKitCore.login(userID, userName, null);
    }

    public static void login(String userID, String userName, ZegoUIKitCallback callback) {
        uiKitCore.login(userID, userName, callback);
    }

    public static void logout() {
        uiKitCore.logout();
    }

    public static ZegoUIKitUser getUser(String userID) {
        return uiKitCore.getUser(userID);
    }

    public static List<ZegoUIKitUser> getAllUsers() {
        return uiKitCore.getAllUsers();
    }

    public static void addUserUpdateListener(ZegoUserUpdateListener listener) {
        uiKitCore.addUserUpdateListener(listener);
    }

    public static void removeUserUpdateListener(ZegoUserUpdateListener listener) {
        uiKitCore.removeUserUpdateListener(listener);
    }

    public static void addOnOnlySelfInRoomListener(ZegoOnlySelfInRoomListener listener) {
        uiKitCore.addOnOnlySelfInRoomListener(listener);
    }

    public static void removeOnOnlySelfInRoomListener(ZegoOnlySelfInRoomListener listener) {
        uiKitCore.removeOnOnlySelfInRoomListener(listener);
    }

    public static void addAudioVideoUpdateListener(ZegoAudioVideoUpdateListener listener) {
        uiKitCore.addAudioVideoUpdateListener(listener);
    }

    public static void removeAudioVideoUpdateListener(ZegoAudioVideoUpdateListener listener) {
        uiKitCore.removeAudioVideoUpdateListener(listener);
    }

    public static String getVersion() {
        return UIKitCore.getInstance().getVersion();
    }

    public static List<ZegoInRoomMessage> getInRoomMessages() {
        return uiKitCore.getInRoomMessages();
    }

    public static void sendInRoomMessage(String message) {
        uiKitCore.sendInRoomMessage(message);
    }

    public static void sendInRoomMessage(String message, ZegoInRoomMessageSendStateListener listener) {
        uiKitCore.sendInRoomMessage(message, listener);
    }

    public static void addInRoomMessageReceivedListener(ZegoInRoomMessageListener listener) {
        uiKitCore.addInRoomMessageReceivedListener(listener);
    }

    public static void removeInRoomMessageReceivedListener(ZegoInRoomMessageListener listener) {
        uiKitCore.removeInRoomMessageReceivedListener(listener);
    }

    public static ZegoUIKitUser getLocalUser() {
        return uiKitCore.getLocalUser();
    }

    public static IZegoUIKitSignalingPlugin getSignalingPlugin() {
        uiKitCore = UIKitCore.getInstance();
        return uiKitCore.getSignalingPlugin();
    }

    public static IBeautyPlugin getBeautyPlugin() {
        uiKitCore = UIKitCore.getInstance();
        return uiKitCore.getBeautyPlugin();
    }

    public static void startPlayingAllAudioVideo() {
        uiKitCore.startPlayingAllAudioVideo();
    }

    public static void stopPlayingAllAudioVideo() {
        uiKitCore.stopPlayingAllAudioVideo();
    }

    public static void sendInRoomCommand(String command, ArrayList<String> toUserList,
        ZegoSendInRoomCommandCallback callback) {
        uiKitCore.sendInRoomCommand(command, toUserList, callback);
    }

    public static void addInRoomCommandListener(ZegoInRoomCommandListener listener) {
        uiKitCore.addInRoomCommandListener(listener);
    }

    public static void removeInRoomCommandListener(ZegoInRoomCommandListener listener) {
        uiKitCore.removeInRoomCommandListener(listener);
    }

    public static void removeUserFromRoom(List<String> userIDs) {
        uiKitCore.removeUserFromRoom(userIDs);
    }

    public static void addOnMeRemovedFromRoomListener(ZegoMeRemovedFromRoomListener listener) {
        uiKitCore.addOnMeRemovedFromRoomListener(listener);
    }

    public static void addRoomStateChangedListener(RoomStateChangedListener listener) {
        uiKitCore.addRoomStateUpdatedListener(listener);
    }

    public static void removeRoomStateChangedListener(RoomStateChangedListener listener) {
        uiKitCore.removeRoomStateUpdatedListener(listener);
    }

    public static void removeOnMeRemovedFromRoomListener(ZegoMeRemovedFromRoomListener listener) {
        uiKitCore.removeOnMeRemovedFromRoomListener(listener);
    }

    public static void addUserCountOrPropertyChangedListener(ZegoUserCountOrPropertyChangedListener listener) {
        uiKitCore.addUserCountOrPropertyChangedListener(listener);
    }

    public static void removeUserCountOrPropertyChangedListener(ZegoUserCountOrPropertyChangedListener listener) {
        uiKitCore.removeUserCountOrPropertyChangedListener(listener);
    }

    public static void addTurnOnYourCameraRequestListener(ZegoTurnOnYourCameraRequestListener listener) {
        uiKitCore.addTurnOnYourCameraRequestListener(listener);
    }

    public static void removeTurnOnYourCameraRequestListener(ZegoTurnOnYourCameraRequestListener listener) {
        uiKitCore.removeTurnOnYourCameraRequestListener(listener);
    }

    public static void addTurnOnYourMicrophoneRequestListener(ZegoTurnOnYourMicrophoneRequestListener listener) {
        uiKitCore.addTurnOnYourMicrophoneRequestListener(listener);
    }

    public static void removeTurnOnYourMicrophoneRequestListener(ZegoTurnOnYourMicrophoneRequestListener listener) {
        uiKitCore.removeTurnOnYourMicrophoneRequestListener(listener);
    }

    public static void addScreenSharingUpdateListener(ZegoScreenSharingUpdateListener listener) {
        uiKitCore.addScreenSharingUpdateListener(listener);
    }

    public static void removeScreenSharingUpdateListener(ZegoScreenSharingUpdateListener listener) {
        uiKitCore.removeScreenSharingUpdateListener(listener);
    }

    public static void setAudioVideoResourceMode(ZegoAudioVideoResourceMode mode) {
        uiKitCore.setAudioVideoResourceMode(mode);
    }

    public static void setVideoConfig(ZegoVideoConfig config) {
        uiKitCore.setVideoConfig(config);
    }

    public static boolean isScreenSharing() {
        return uiKitCore.isScreenSharing();
    }

    public static void startSharingScreen(ZegoPresetResolution resolution) {
        uiKitCore.startSharingScreen(resolution);
    }

    public static void startSharingScreen(ZegoPresetResolution resolution, int bitrate, int fps) {
        uiKitCore.startSharingScreen(resolution, bitrate, fps);
    }

    public static void stopSharingScreen() {
        uiKitCore.stopSharingScreen();
    }

    public static long getNetworkTimestamp() {
        return uiKitCore.getNetworkTimestamp();
    }

    public static void mutePlayStreamAudio(String streamID, boolean mute) {
        uiKitCore.mutePlayStreamAudio(streamID, mute);
    }

    public static void mutePlayStreamVideo(String streamID, boolean mute) {
        uiKitCore.mutePlayStreamAudio(streamID, mute);
    }

    public static void startMixerTask(ZegoMixerTask task, IZegoMixerStartCallback callback) {
        uiKitCore.startMixerTask(task, callback);
    }

    public static void stopMixerTask(ZegoMixerTask task, IZegoMixerStopCallback callback) {
        uiKitCore.stopMixerTask(task, callback);
    }

    public static void addEventHandler(IExpressEngineEventHandler eventHandler, boolean autoDelete) {
        uiKitCore.addEventHandler(eventHandler, autoDelete);
    }

    public static void addEventHandler(IExpressEngineEventHandler eventHandler) {
        uiKitCore.addEventHandler(eventHandler, true);
    }

    public static void removeEventHandler(IExpressEngineEventHandler eventHandler) {
        uiKitCore.removeEventHandler(eventHandler);
    }

    public static void addBarrageMessageListener(ZegoBarrageMessageListener listener) {
        uiKitCore.addBarrageMessageListener(listener);
    }

    public static void sendBarrageMessage(String roomID, String message, IZegoIMSendBarrageMessageCallback callback) {
        uiKitCore.sendBarrageMessage(roomID, message, callback);
    }

    public static void removeBarrageMessageListener(ZegoBarrageMessageListener listener) {
        uiKitCore.removeBarrageMessageListener(listener);
    }

    public static void sendSEI(String seiString) {
        uiKitCore.sendSEI(seiString);
    }

    public static void startPlayingStream(String streamID, ZegoCanvas canvas) {
        uiKitCore.startPlayingStream(streamID, canvas);
    }

    public static void startPlayingStream(String streamID, ZegoCanvas canvas, ZegoPlayerConfig config) {
        uiKitCore.startPlayingStream(streamID, canvas, config);
    }

    public static void stopPlayingStream(String streamID) {
        uiKitCore.stopPlayingStream(streamID);
    }

    public static void startPublishingStream(String streamID) {
        uiKitCore.startPublishingStream(streamID);
    }

    public static void stopPublishingStream() {
        uiKitCore.stopPublishingStream();
    }

    public static void startPreview(ZegoCanvas canvas) {
        uiKitCore.startPreview(canvas);
    }

    public static void stopPreview() {
        uiKitCore.stopPreview();
    }

    public static void openMicrophone(boolean open) {
        uiKitCore.openMicrophone(open);
    }

    public static void openCamera(boolean open) {
        uiKitCore.openCamera(open);
    }

    public static void renewToken(String token) {
        uiKitCore.renewToken(token);
    }

    public static void enable3A(boolean enable) {
        uiKitCore.enable3A(enable);
    }

    public static void setTokenWillExpireListener(ZegoUIKitTokenExpireListener listener) {
        uiKitCore.setTokenWillExpireListener(listener);
    }

    public static void unInitExpressEngine() {
        uiKitCore.unInitExpressEngine();
    }


    public static void setLanguage(ZegoUIKitLanguage language) {
        uiKitCore = UIKitCore.getInstance();
        uiKitCore.setLanguage(language);
    }

    private static boolean showInLogcat = true;
    private static boolean XLogInit = false;

    public static void debugMode(Context context) {
        if (Timber.treeCount() == 0) {
            File dir = context.getExternalFilesDir(null);
            if (dir == null || (!dir.exists() && !dir.mkdirs())) {
                dir = context.getFilesDir();
            }
            if (dir != null) {
                String logFileDir = dir.getAbsolutePath() + File.separator + "uikit_log";
                long logFileExpired = 5 * 24 * 3600 * 1000; // five days
                long logFileMaxSize = 5 * 1024 * 1024; // 5 MB
                Printer filePrinter = new FilePrinter.Builder(logFileDir).fileNameGenerator(new DateFileNameGenerator())
                    .cleanStrategy(new FileLastModifiedCleanStrategy(logFileExpired))
                    .backupStrategy(new FileSizeBackupStrategy2(logFileMaxSize, 3)).flattener(new ClassicFlattener())
                    .build();
                XLog.init(filePrinter);
                XLogInit = true;
            }
        }
        Timber.uprootAll();
        Timber.plant(new Timber.DebugTree() {
            @Override
            protected void log(int priority, String tag, @NonNull String message, Throwable t) {
                if (showInLogcat) {
                    super.log(priority, tag, message, t);
                }
                if (XLogInit) {
                    XLog.tag(tag).log(priority, message, t);
                }
            }
        });
    }

    public static void showInLogcat(boolean show) {
        showInLogcat = show;
    }
}
