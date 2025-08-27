package com.zegocloud.uikit.service.internal;

import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.zegocloud.uikit.ZegoUIKit;
import com.zegocloud.uikit.components.common.ZegoPresetResolution;
import com.zegocloud.uikit.internal.ZegoUIKitLanguage;
import com.zegocloud.uikit.plugin.ZegoUIKitSignalingPluginImpl;
import com.zegocloud.uikit.plugin.adapter.utils.GenericUtils;
import com.zegocloud.uikit.plugin.beauty.BeautyPluginBridge;
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
import com.zegocloud.uikit.service.express.EventHandlerList;
import com.zegocloud.uikit.service.express.ExpressEngineProxy;
import com.zegocloud.uikit.service.express.IExpressEngineEventHandler;
import com.zegocloud.uikit.service.internal.interfaces.IUIKitCore;
import im.zego.uikit.libuikitreport.ReportUtil;
import im.zego.zegoexpress.ZegoExpressEngine;
import im.zego.zegoexpress.callback.IZegoEventHandler;
import im.zego.zegoexpress.callback.IZegoIMSendBarrageMessageCallback;
import im.zego.zegoexpress.callback.IZegoMixerStartCallback;
import im.zego.zegoexpress.callback.IZegoMixerStopCallback;
import im.zego.zegoexpress.constants.ZegoAudioChannel;
import im.zego.zegoexpress.constants.ZegoAudioRoute;
import im.zego.zegoexpress.constants.ZegoAudioSampleRate;
import im.zego.zegoexpress.constants.ZegoAudioSourceType;
import im.zego.zegoexpress.constants.ZegoDeviceExceptionType;
import im.zego.zegoexpress.constants.ZegoDeviceType;
import im.zego.zegoexpress.constants.ZegoPublishChannel;
import im.zego.zegoexpress.constants.ZegoPublisherState;
import im.zego.zegoexpress.constants.ZegoRemoteDeviceState;
import im.zego.zegoexpress.constants.ZegoRoomStateChangedReason;
import im.zego.zegoexpress.constants.ZegoScenario;
import im.zego.zegoexpress.constants.ZegoStreamResourceMode;
import im.zego.zegoexpress.constants.ZegoUpdateType;
import im.zego.zegoexpress.constants.ZegoVideoConfigPreset;
import im.zego.zegoexpress.constants.ZegoVideoSourceType;
import im.zego.zegoexpress.entity.ZegoAudioConfig;
import im.zego.zegoexpress.entity.ZegoBarrageMessageInfo;
import im.zego.zegoexpress.entity.ZegoBroadcastMessageInfo;
import im.zego.zegoexpress.entity.ZegoCanvas;
import im.zego.zegoexpress.entity.ZegoEngineConfig;
import im.zego.zegoexpress.entity.ZegoMixerTask;
import im.zego.zegoexpress.entity.ZegoPlayerConfig;
import im.zego.zegoexpress.entity.ZegoRoomExtraInfo;
import im.zego.zegoexpress.entity.ZegoScreenCaptureConfig;
import im.zego.zegoexpress.entity.ZegoStream;
import im.zego.zegoexpress.entity.ZegoUser;
import im.zego.zegoexpress.entity.ZegoVideoConfig;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import timber.log.Timber;

/**
 * for internal use,DO NOT call it directly.
 */
public class UIKitCore implements IUIKitCore {

    private static UIKitCore sInstance;

    private UIKitCore() {
    }

    public static UIKitCore getInstance() {
        synchronized (UIKitCore.class) {
            if (sInstance == null) {
                sInstance = new UIKitCore();
            }
            return sInstance;
        }
    }

    private RoomService roomService = new RoomService();
    private UserService userService = new UserService();
    private AudioVideoService audioVideoService = new AudioVideoService();
    private MessageService messageService = new MessageService();
    public Handler handler = new Handler(Looper.getMainLooper());
    private UIKitCoreUser localUser;
    private final ZegoUIKitRoom zegoUIKitRoom = new ZegoUIKitRoom();
    private boolean isFrontFacing = true;
    private boolean enableAudioVideoAutoPlaying = true;
    private final List<UIKitCoreUser> remoteUserList = new ArrayList<>();
    private List<ZegoInRoomMessage> inRoomMessages = new ArrayList<>();
    private Map<String, ZegoRoomExtraInfo> roomExtraInfoMap = new HashMap<>();
    private boolean isLargeRoom;
    private boolean markAsLargeRoom;
    private int roomMemberCount = 0;
    private Application application;
    private long lastNotifyTokenTime;
    private final AtomicBoolean isExpressInit = new AtomicBoolean();
    private String token;
    private ZegoUIKitTokenExpireListener tokenExpireListener;
    private ZegoUIKitLanguage language;

    private ActivityLifecycleCallbacks lifecycleCallbacks = new ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {
            signalingPlugin.onActivityStarted();
        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {

        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {

        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {

        }
    };
    private ZegoAudioVideoResourceMode resourceMode;
    private boolean isScreenSharing = false;
    private Enable3AState enable3AState = Enable3AState.default_state;  // 0: default, 1: enable, 2: disable
    private ExpressEngineProxy engineProxy = new ExpressEngineProxy();
    private IZegoEventHandler initEventHandler;
    private EventHandlerList<IExpressEngineEventHandler> eventHandlerList = new EventHandlerList<>();
    private ZegoUIKitSignalingPluginImpl signalingPlugin = new ZegoUIKitSignalingPluginImpl();
    private BeautyPluginBridge beautyPluginBridge = new BeautyPluginBridge(engineProxy);
    private UIKitTranslationText uiKitTranslationText = new UIKitTranslationText();

    public boolean init(Application application, Long appID, String appSign,
        com.zegocloud.uikit.service.defines.ZegoScenario scenario) {

        this.application = application;
        ZegoUIKit.debugMode(application);

        HashMap<String, Object> commonParams = new HashMap<>();
        commonParams.put(ReportUtil.PLATFORM, "android");
        commonParams.put(ReportUtil.PLATFORM_VERSION, android.os.Build.VERSION.SDK_INT + "");
        commonParams.put(ReportUtil.UIKIT_VERSION, getVersion());
        String verify = "";
        if (!TextUtils.isEmpty(appSign)) {
            verify = appSign;
        } else {
            if (!TextUtils.isEmpty(token)) {
                verify = token;
            }
        }
        ReportUtil.create(appID, verify, commonParams);

        boolean initExpressEngine = initExpressEngine(application, appID, appSign, scenario);
        if (!initExpressEngine) {
            return false;
        }
        signalingPlugin.init(application, appID, appSign);
        return true;
    }

    @Override
    public boolean initExpressEngine(Application application, Long appID, String appSign,
        com.zegocloud.uikit.service.defines.ZegoScenario scenario) {
        Timber.d("ZEGO UIKit initExpressEngine() called with: isExpressInit = [" + isExpressInit.get() + "], appID = ["
            + appID + "], isEmpty(appSign) = [" + TextUtils.isEmpty(appSign) + "],getVersion():" + getVersion());
        boolean isSetSucceed = isExpressInit.compareAndSet(false, true);
        if (isSetSucceed) {
            application.registerActivityLifecycleCallbacks(lifecycleCallbacks);
            createExpressEngine(application, appID, appSign, scenario);
        }
        if (ExpressEngineProxy.getEngine() == null) {
            Timber.e("ExpressEngine.getEngine() return false,init Failed");
            return false;
        } else {
            // express will open camera by default.so close here
            ExpressEngineProxy.enableCamera(false);
            return true;
        }
    }

    @Override
    public boolean isExpressEngineInitSucceed() {
        return ExpressEngineProxy.getEngine() != null;
    }

    private void createExpressEngine(Application application, Long appID, String appSign,
        com.zegocloud.uikit.service.defines.ZegoScenario scenario) {
        ZegoEngineConfig config = new ZegoEngineConfig();
        config.advancedConfig.put("notify_remote_device_unknown_status", "true");
        config.advancedConfig.put("notify_remote_device_init_status", "true");
        ZegoExpressEngine.setEngineConfig(config);
        engineProxy.createEngine(application, appID, appSign, ZegoScenario.getZegoScenario(scenario.value()));
        if (enable3AState != Enable3AState.default_state) {
            engineProxy.enable3A(enable3AState == Enable3AState.enable_state);
        }

        initEventHandler = new IZegoEventHandler() {

            @Override
            public void onRoomUserUpdate(String roomID, ZegoUpdateType zegoUpdateType, ArrayList<ZegoUser> userList) {
                super.onRoomUserUpdate(roomID, zegoUpdateType, userList);
                List<UIKitCoreUser> userInfoList = GenericUtils.map(userList,
                    zegoUser -> new UIKitCoreUser(zegoUser.userID, zegoUser.userName));
                if (zegoUpdateType == ZegoUpdateType.ADD) {
                    for (UIKitCoreUser uiKitCoreUser : userInfoList) {
                        if (!remoteUserList.contains(uiKitCoreUser)) {
                            remoteUserList.add(uiKitCoreUser);
                        }
                    }
                    roomMemberCount += userList.size();
                    if (roomMemberCount > 500) {
                        isLargeRoom = true;
                    }
                    dispatchUserJoin(userInfoList);
                } else {
                    for (UIKitCoreUser uiKitCoreUser : userInfoList) {
                        remoteUserList.remove(uiKitCoreUser);
                    }
                    roomMemberCount -= userList.size();
                    dispatchUserLeave(userInfoList);
                    if (remoteUserList.isEmpty()) {
                        dispatchOnlySelfInRoom();
                    }
                }
                dispatchRoomUserCountOrPropertyChanged(getAllUsers());
            }

            @Override
            public void onRoomStreamUpdate(String roomID, ZegoUpdateType zegoUpdateType,
                ArrayList<ZegoStream> streamList, JSONObject jsonObject) {
                super.onRoomStreamUpdate(roomID, zegoUpdateType, streamList, jsonObject);
                if (zegoUpdateType == ZegoUpdateType.ADD) {
                    for (ZegoStream zegoStream : streamList) {
                        UIKitCoreUser uiKitUser = getUserbyUserID(zegoStream.user.userID);
                        if (uiKitUser != null) {
                            uiKitUser.setStreamID(zegoStream.streamID);
                        } else {
                            UIKitCoreUser user = UIKitCoreUser.createFromStream(zegoStream);
                            remoteUserList.add(user);
                        }
                        if (zegoStream.streamID.contains("main")) {
                            if (resourceMode == null) {
                                ExpressEngineProxy.startPlayingStream(zegoStream.streamID);
                            } else {
                                ZegoPlayerConfig config = new ZegoPlayerConfig();
                                config.resourceMode = ZegoStreamResourceMode.getZegoStreamResourceMode(
                                    resourceMode.value());
                                ExpressEngineProxy.startPlayingStream(zegoStream.streamID, config);
                            }
                        }
                    }
                }

                if (zegoUpdateType == ZegoUpdateType.DELETE) {
                    for (ZegoStream zegoStream : streamList) {
                        UIKitCoreUser uiKitUser = getUserbyUserID(zegoStream.user.userID);
                        if (uiKitUser != null) {
                            uiKitUser.deleteStream(zegoStream.streamID);
                            if (zegoStream.streamID.contains("main")) {
                                if (uiKitUser.isCameraOpen || uiKitUser.isMicOpen) {
                                    if (uiKitUser.isCameraOpen) {
                                        uiKitUser.isCameraOpen = false;
                                        dispatchRemoteCameraStateUpdate(uiKitUser, false);
                                    }
                                    if (uiKitUser.isMicOpen) {
                                        uiKitUser.isMicOpen = false;
                                        dispatchRemoteMicStateUpdate(uiKitUser, false);
                                    }
                                }
                                uiKitUser.soundLevel = 0;
                            }
                            dispatchRoomUserCountOrPropertyChanged(getAllUsers());
                        }
                        ExpressEngineProxy.stopPlayingStream(zegoStream.streamID);
                    }
                }

                dispatchStreamUpdate(roomID, zegoUpdateType, streamList, jsonObject);

                if (zegoUpdateType == ZegoUpdateType.ADD) {
                    onRoomStreamExtraInfoUpdate(roomID, streamList);
                }
            }

            @Override
            public void onPublisherStateUpdate(String streamID, ZegoPublisherState state, int errorCode,
                JSONObject extendedData) {
                super.onPublisherStateUpdate(streamID, state, errorCode, extendedData);
                if (state == ZegoPublisherState.PUBLISHING) {
                    ArrayList<ZegoStream> streamList = new ArrayList<>(1);
                    if (localUser != null) {
                        localUser.setStreamID(streamID);
                        ZegoStream zegoStream = new ZegoStream();
                        zegoStream.user = new ZegoUser(localUser.userID, localUser.userName);
                        zegoStream.streamID = streamID;
                        streamList.add(zegoStream);
                    }
                    dispatchStreamUpdate(getRoom().roomID, ZegoUpdateType.ADD, streamList, extendedData);
                } else if (state == ZegoPublisherState.NO_PUBLISH) {
                    ArrayList<ZegoStream> streamList = new ArrayList<>(1);
                    if (localUser != null) {
                        ZegoStream zegoStream = new ZegoStream();
                        zegoStream.user = new ZegoUser(localUser.userID, localUser.userName);
                        zegoStream.streamID = streamID;
                        streamList.add(zegoStream);
                        localUser.deleteStream(zegoStream.streamID);
                    }
                    dispatchStreamUpdate(getRoom().roomID, ZegoUpdateType.DELETE, streamList, extendedData);
                }
            }

            @Override
            public void onRoomStateChanged(String roomID, ZegoRoomStateChangedReason zegoRoomStateChangedReason,
                int errorCode, JSONObject jsonObject) {
                super.onRoomStateChanged(roomID, zegoRoomStateChangedReason, errorCode, jsonObject);
                dispatchRoomStateUpdate(roomID, zegoRoomStateChangedReason, errorCode, jsonObject);
                if (zegoRoomStateChangedReason == ZegoRoomStateChangedReason.KICK_OUT) {
                    userService.notifyRemovedFromRoomCommand();
                }
            }

            @Override
            public void onLocalDeviceExceptionOccurred(ZegoDeviceExceptionType zegoDeviceExceptionType,
                ZegoDeviceType zegoDeviceType, String s) {
                super.onLocalDeviceExceptionOccurred(zegoDeviceExceptionType, zegoDeviceType, s);
                Timber.w("onLocalDeviceExceptionOccurred() called with: zegoDeviceExceptionType = ["
                    + zegoDeviceExceptionType + "], zegoDeviceType = [" + zegoDeviceType + "], s = [" + s + "]");
                UIKitCoreUser localCoreUser = getLocalCoreUser();
                if (localCoreUser != null) {
                    if (zegoDeviceType == ZegoDeviceType.CAMERA) {
                        if (localCoreUser.isCameraOpen) {
                            turnCameraOn(localCoreUser.userID, false);
                            dispatchRoomUserCountOrPropertyChanged(getAllUsers());
                        }
                    } else if (zegoDeviceType == ZegoDeviceType.MICROPHONE) {
                        if (localCoreUser.isMicOpen) {
                            turnMicrophoneOn(localCoreUser.userID, false);
                            dispatchRoomUserCountOrPropertyChanged(getAllUsers());
                        }
                    }
                }
            }

            @Override
            public void onRemoteCameraStateUpdate(String streamID, ZegoRemoteDeviceState zegoRemoteDeviceState) {
                super.onRemoteCameraStateUpdate(streamID, zegoRemoteDeviceState);
                UIKitCoreUser uiKitUser = getUserFromStreamID(streamID);
                if (uiKitUser != null) {
                    if (zegoRemoteDeviceState == ZegoRemoteDeviceState.NOT_SUPPORT) {
                        return;
                    }
                    boolean open = zegoRemoteDeviceState == ZegoRemoteDeviceState.OPEN;
                    uiKitUser.isCameraOpen = open;
                    dispatchRemoteCameraStateUpdate(uiKitUser, open);
                    dispatchRoomUserCountOrPropertyChanged(getAllUsers());
                }
            }

            @Override
            public void onRemoteMicStateUpdate(String streamID, ZegoRemoteDeviceState zegoRemoteDeviceState) {
                super.onRemoteMicStateUpdate(streamID, zegoRemoteDeviceState);
                UIKitCoreUser uiKitUser = getUserFromStreamID(streamID);
                if (uiKitUser != null) {
                    if (zegoRemoteDeviceState == ZegoRemoteDeviceState.NOT_SUPPORT) {
                        return;
                    }
                    boolean open = zegoRemoteDeviceState == ZegoRemoteDeviceState.OPEN;
                    uiKitUser.isMicOpen = open;
                    dispatchRemoteMicStateUpdate(uiKitUser, open);
                    dispatchRoomUserCountOrPropertyChanged(getAllUsers());
                }
            }

            @Override
            public void onAudioRouteChange(ZegoAudioRoute zegoAudioRoute) {
                super.onAudioRouteChange(zegoAudioRoute);
                audioVideoService.notifyAudioRouteChange(zegoAudioRoute);
            }

            @Override
            public void onRemoteSoundLevelUpdate(HashMap<String, Float> hashMap) {
                super.onRemoteSoundLevelUpdate(hashMap);
                for (Entry<String, Float> entry : hashMap.entrySet()) {
                    UIKitCoreUser coreUser = getUserFromStreamID(entry.getKey());
                    if (coreUser != null) {
                        coreUser.soundLevel = entry.getValue();
                        dispatchSoundLevelUpdate(coreUser.userID, coreUser.soundLevel);
                    }
                }
            }

            @Override
            public void onCapturedSoundLevelUpdate(float v) {
                super.onCapturedSoundLevelUpdate(v);
                if (getLocalCoreUser() != null) {
                    getLocalCoreUser().soundLevel = v;
                    dispatchSoundLevelUpdate(getLocalCoreUser().userID, getLocalCoreUser().soundLevel);
                }
            }

            @Override
            public void onIMRecvCustomCommand(String roomID, ZegoUser fromUser, String command) {
                super.onIMRecvCustomCommand(roomID, fromUser, command);
                boolean isInternalCommand = false;
                try {
                    JSONObject jsonObject = new JSONObject(command);
                    if (jsonObject.has("zego_remove_user")) {
                        JSONArray userIDArray = jsonObject.getJSONArray("zego_remove_user");
                        for (int i = 0; i < userIDArray.length(); i++) {
                            String userID = userIDArray.getString(i);
                            if (localUser != null && Objects.equals(userID, localUser.userID)) {
                                notifyRemovedFromRoomCommand();
                                leaveRoom();
                            }
                        }
                        isInternalCommand = true;
                    } else if (jsonObject.has("zego_turn_camera_on")) {
                        JSONArray userIDArray = jsonObject.getJSONArray("zego_turn_camera_on");
                        for (int i = 0; i < userIDArray.length(); i++) {
                            String userID = userIDArray.getString(i);
                            if (localUser != null && Objects.equals(userID, localUser.userID)) {
                                if (!isCameraOn(userID)) {
                                    notifyTurnCameraOnCommand(new ZegoUIKitUser(fromUser.userID, fromUser.userName));
                                }
                            }
                        }
                        isInternalCommand = true;
                    } else if (jsonObject.has("zego_turn_microphone_on")) {
                        JSONArray userIDArray = jsonObject.getJSONArray("zego_turn_microphone_on");
                        for (int i = 0; i < userIDArray.length(); i++) {
                            String userID = userIDArray.getString(i);
                            if (localUser != null && Objects.equals(userID, localUser.userID)) {
                                if (!isMicrophoneOn(userID)) {
                                    notifyTurnMicrophoneOnCommand(
                                        new ZegoUIKitUser(fromUser.userID, fromUser.userName));
                                }
                            }
                        }
                        isInternalCommand = true;
                    } else if (jsonObject.has("zego_turn_camera_off")) {
                        JSONArray userIDArray = jsonObject.getJSONArray("zego_turn_camera_off");
                        for (int i = 0; i < userIDArray.length(); i++) {
                            String userID = userIDArray.getString(i);
                            if (localUser != null && Objects.equals(userID, localUser.userID)) {
                                turnCameraOn(userID, false);
                                notifyTurnCameraOffCommand(new ZegoUIKitUser(fromUser.userID, fromUser.userName));
                            }
                        }
                        isInternalCommand = true;
                    } else if (jsonObject.has("zego_turn_microphone_off")) {
                        JSONArray userIDArray = jsonObject.getJSONArray("zego_turn_microphone_off");
                        for (int i = 0; i < userIDArray.length(); i++) {
                            String userID = userIDArray.getString(i);
                            if (localUser != null && Objects.equals(userID, localUser.userID)) {
                                turnMicrophoneOn(userID, false);
                                notifyTurnMicrophoneOffCommand(new ZegoUIKitUser(fromUser.userID, fromUser.userName));
                            }
                        }
                        isInternalCommand = true;
                    }
                } catch (JSONException e) {
                } finally {
                    if (!isInternalCommand) {
                        roomService.notifyIMRecvCustomCommand(roomID, fromUser, command);
                    }
                }
            }

            @Override
            public void onIMRecvBroadcastMessage(String roomID, ArrayList<ZegoBroadcastMessageInfo> messageList) {
                super.onIMRecvBroadcastMessage(roomID, messageList);
                List<ZegoInRoomMessage> list = GenericUtils.map(messageList, zegoBroadcastMessageInfo -> {
                    ZegoInRoomMessage inRoomMessage = new ZegoInRoomMessage();
                    inRoomMessage.message = zegoBroadcastMessageInfo.message;
                    inRoomMessage.messageID = zegoBroadcastMessageInfo.messageID;
                    inRoomMessage.timestamp = zegoBroadcastMessageInfo.sendTime;
                    inRoomMessage.user = new ZegoUIKitUser(zegoBroadcastMessageInfo.fromUser.userID,
                        zegoBroadcastMessageInfo.fromUser.userName);
                    return inRoomMessage;
                });
                inRoomMessages.addAll(list);
                dispatchBroadcastMessages(roomID, list);
            }

            @Override
            public void onRoomStreamExtraInfoUpdate(String roomID, ArrayList<ZegoStream> streamList) {
                super.onRoomStreamExtraInfoUpdate(roomID, streamList);
                for (ZegoStream zegoStream : streamList) {
                    try {
                        JSONObject jsonObject = new JSONObject(zegoStream.extraInfo);
                        if (jsonObject.has("isCameraOn")) {
                            UIKitCoreUser coreUser = getUserbyUserID(zegoStream.user.userID);
                            if (coreUser == null) {
                                coreUser = new UIKitCoreUser(zegoStream.user.userID, zegoStream.user.userName);
                            }
                            boolean isCameraOn = jsonObject.getBoolean("isCameraOn");
                            if (coreUser.isCameraOpen != isCameraOn) {
                                coreUser.isCameraOpen = isCameraOn;
                                dispatchRemoteCameraStateUpdate(coreUser, coreUser.isCameraOpen);
                                dispatchRoomUserCountOrPropertyChanged(getAllUsers());
                            }
                        }
                        if (jsonObject.has("isMicrophoneOn")) {
                            UIKitCoreUser coreUser = getUserbyUserID(zegoStream.user.userID);
                            if (coreUser == null) {
                                coreUser = new UIKitCoreUser(zegoStream.user.userID, zegoStream.user.userName);
                            }
                            boolean isMicrophoneOn = jsonObject.getBoolean("isMicrophoneOn");
                            if (coreUser.isMicOpen != isMicrophoneOn) {
                                coreUser.isMicOpen = isMicrophoneOn;
                                dispatchRemoteMicStateUpdate(coreUser, coreUser.isMicOpen);
                                dispatchRoomUserCountOrPropertyChanged(getAllUsers());
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onRoomExtraInfoUpdate(String roomID, ArrayList<ZegoRoomExtraInfo> arrayList) {
                super.onRoomExtraInfoUpdate(roomID, arrayList);
                for (ZegoRoomExtraInfo roomExtraInfo : arrayList) {
                    ZegoRoomExtraInfo oldRoomExtraInfo = roomExtraInfoMap.get(roomExtraInfo.key);
                    if (oldRoomExtraInfo != null) {
                        if (Objects.equals(roomExtraInfo.updateUser.userID, getLocalCoreUser().userID)) {
                            continue;
                        }
                        if (roomExtraInfo.updateTime < oldRoomExtraInfo.updateTime) {
                            continue;
                        }
                    }
                    roomExtraInfoMap.put(roomExtraInfo.key, roomExtraInfo);
                    if (Objects.equals("extra_info", roomExtraInfo.key)) {
                        List<String> updateKeys = new ArrayList<>();

                        Map<String, String> oldProperties = roomExtraInfoValueToMap(oldRoomExtraInfo);
                        Map<String, String> currentProperties = roomExtraInfoValueToMap(roomExtraInfo);
                        try {
                            JSONObject temp = new JSONObject(currentProperties);
                            Iterator<String> iterator = temp.keys();
                            while (iterator.hasNext()) {
                                String key = iterator.next();
                                String value = temp.getString(key);
                                String oldValue = oldProperties.get(key);
                                if (Objects.equals(oldValue, value)) {
                                    continue;
                                }
                                updateKeys.add(key);
                            }
                            for (String updateKey : updateKeys) {
                                dispatchRoomPropertyUpdated(updateKey, oldProperties.get(updateKey),
                                    currentProperties.get(updateKey));
                            }
                            if (!updateKeys.isEmpty()) {
                                dispatchRoomPropertiesFullUpdated(updateKeys, oldProperties, currentProperties);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onRoomTokenWillExpire(String roomID, int remainTimeInSecond) {
                super.onRoomTokenWillExpire(roomID, remainTimeInSecond);
                notifyTokenWillExpire(remainTimeInSecond);
            }

            @Override
            public void onIMRecvBarrageMessage(String roomID, ArrayList<ZegoBarrageMessageInfo> messageList) {
                super.onIMRecvBarrageMessage(roomID, messageList);
                notifyIMRecvBarrageMessage(roomID, messageList);
            }
        };
        engineProxy.addEventHandler(initEventHandler);
    }

    private void notifyIMRecvBarrageMessage(String roomID, ArrayList<ZegoBarrageMessageInfo> messageList) {
        messageService.notifyIMRecvBarrageMessage(roomID, messageList);
    }

    public void notifyTokenWillExpire(int seconds) {
        if (System.currentTimeMillis() - lastNotifyTokenTime > 5 * 60 * 1000) {
            if (tokenExpireListener != null) {
                tokenExpireListener.onTokenWillExpire(seconds);
            }
        }
        this.lastNotifyTokenTime = System.currentTimeMillis();
    }

    @Override
    public void addEventHandler(IExpressEngineEventHandler eventHandler, boolean autoDelete) {
        eventHandlerList.addEventHandler(eventHandler, autoDelete);
        engineProxy.addEventHandler(eventHandler);
    }

    @Override
    public void removeEventHandler(IExpressEngineEventHandler eventHandler) {
        eventHandlerList.removeEventHandler(eventHandler);
        engineProxy.removeEventHandler(eventHandler);
    }

    @Override
    public void sendSEI(String seiString) {
        engineProxy.sendSEI(seiString.getBytes());
    }

    private void removeAutoDeleteRoomListeners() {
        engineProxy.removeEventHandlerList(new ArrayList<>(eventHandlerList.getAutoDeleteHandlerList()));
        eventHandlerList.removeAutoDeleteRoomListeners();
    }

    private void notifyTurnMicrophoneOffCommand(ZegoUIKitUser uiKitUser) {
        audioVideoService.notifyTurnMicrophoneCommand(uiKitUser, false);
    }

    private void notifyTurnCameraOffCommand(ZegoUIKitUser uiKitUser) {
        audioVideoService.notifyTurnCameraCommand(uiKitUser, false);
    }

    private void notifyTurnMicrophoneOnCommand(ZegoUIKitUser uiKitUser) {
        audioVideoService.notifyTurnMicrophoneCommand(uiKitUser, true);
    }

    private void notifyTurnCameraOnCommand(ZegoUIKitUser uiKitUser) {
        audioVideoService.notifyTurnCameraCommand(uiKitUser, true);
    }

    private void notifyRemovedFromRoomCommand() {
        userService.notifyRemovedFromRoomCommand();
    }

    @Override
    public IZegoUIKitSignalingPlugin getSignalingPlugin() {
        return signalingPlugin;
    }

    @Override
    public IBeautyPlugin getBeautyPlugin() {
        return beautyPluginBridge;
    }

    @Override
    public void renewToken(String token) {
        Timber.d("renewToken() called with: isEmpty(token) = [" + TextUtils.isEmpty(token) + "]");
        if (!Objects.equals(token, this.token)) {
            if (!TextUtils.isEmpty(zegoUIKitRoom.roomID)) {
                ExpressEngineProxy.renewToken(zegoUIKitRoom.roomID, token);
            }
        }
        getSignalingPlugin().renewToken(token);
        this.token = token;
    }

    private void dispatchBroadcastMessages(String roomID, List<ZegoInRoomMessage> messageList) {
        messageService.notifyInRoomMessageReceived(roomID, messageList);
    }

    @Override
    public String getVersion() {
        return "UIKit: " + "3.5.7" + ",Engine: " + ZegoExpressEngine.getVersion();
    }

    @Override
    public void startSharingScreen(ZegoPresetResolution resolution) {
        startSharingScreen(resolution, 1200, 15);
    }

    @Override
    public void startSharingScreen(ZegoPresetResolution resolution, int bitrate, int fps) {
        if (resolution == null || bitrate < 0 || fps < 0 || fps > 120) {
            return;
        }
        if (isScreenSharing) {
            return;
        }
        isScreenSharing = true;
        ExpressEngineProxy.setVideoSource(ZegoVideoSourceType.SCREEN_CAPTURE, ZegoPublishChannel.AUX);
        ExpressEngineProxy.setAudioSource(ZegoAudioSourceType.SCREEN_CAPTURE, ZegoPublishChannel.AUX);
        ZegoVideoConfigPreset videoConfigPreset = ZegoVideoConfigPreset.getZegoVideoConfigPreset(resolution.value());

        ZegoVideoConfig zegoVideoConfig = new ZegoVideoConfig(videoConfigPreset);
        zegoVideoConfig.bitrate = bitrate;
        zegoVideoConfig.fps = fps;
        ExpressEngineProxy.setVideoConfig(zegoVideoConfig, ZegoPublishChannel.AUX);

        ZegoScreenCaptureConfig config = new ZegoScreenCaptureConfig();
        config.captureVideo = true;
        config.captureAudio = true;
        config.audioParam.sampleRate = ZegoAudioSampleRate.ZEGO_AUDIO_SAMPLE_RATE_16K;
        config.audioParam.channel = ZegoAudioChannel.STEREO;

        ExpressEngineProxy.startScreenCapture(config);
        ExpressEngineProxy.startPublishingStream(generateScreenShareStreamID(zegoUIKitRoom.roomID, localUser.userID),
            ZegoPublishChannel.AUX);
    }

    @Override
    public boolean isScreenSharing() {
        return isScreenSharing;
    }

    @Override
    public void stopSharingScreen() {
        if (!isScreenSharing) {
            return;
        }
        isScreenSharing = false;
        ExpressEngineProxy.setVideoSource(ZegoVideoSourceType.NONE, ZegoPublishChannel.AUX);
        ExpressEngineProxy.setAudioSource(ZegoAudioSourceType.NONE, ZegoPublishChannel.AUX);
        ExpressEngineProxy.stopPublishingStream(ZegoPublishChannel.AUX);
        ExpressEngineProxy.stopScreenCapture();
    }

    public void setAudioConfig(ZegoAudioConfig config, ZegoPublishChannel channel) {
        ExpressEngineProxy.setAudioConfig(config, ZegoPublishChannel.MAIN);
    }

    public void setPlayStreamBufferIntervalRange(int minBufferInterval, int maxBufferInterval) {
        ExpressEngineProxy.setPlayStreamBufferIntervalRange(minBufferInterval, maxBufferInterval);
    }

    @Override
    public void setVideoConfig(ZegoVideoConfig config) {
        ExpressEngineProxy.setVideoConfig(config, ZegoPublishChannel.MAIN);
    }

    @Override
    public long getNetworkTimestamp() {
        return ExpressEngineProxy.getNetworkTimeInfo();
    }

    public void unInitExpressEngine() {
        Timber.d("unInitExpressEngine() called : " + ExpressEngineProxy.getEngine());
        setPlayStreamBufferIntervalRange(0, 4000);
        if (ExpressEngineProxy.getEngine() != null) {
            isExpressInit.set(false);
            engineProxy.removeEventHandler(initEventHandler);
            enable3AState = Enable3AState.default_state;
            application.unregisterActivityLifecycleCallbacks(lifecycleCallbacks);
            ZegoExpressEngine.destroyEngine(() -> {

            });
        }
    }

    private void dispatchOnlySelfInRoom() {
        userService.notifyOnlySelfInRoom();
    }

    private void dispatchSoundLevelUpdate(String userID, float soundLevel) {
        audioVideoService.notifySoundLevelUpdate(userID, soundLevel);
    }

    private void dispatchRemoteCameraStateUpdate(UIKitCoreUser coreUser, boolean open) {
        audioVideoService.notifyCameraStateChange(coreUser, open);
    }

    private void dispatchRemoteMicStateUpdate(UIKitCoreUser coreUser, boolean open) {
        audioVideoService.notifyMicStateChange(coreUser, open);
    }

    private void dispatchRoomStateUpdate(String roomID, ZegoRoomStateChangedReason zegoRoomStateChangedReason,
        int errorCode, JSONObject jsonObject) {
        roomService.notifyRoomStateUpdate(roomID, zegoRoomStateChangedReason, errorCode, jsonObject);
    }

    private void dispatchStreamUpdate(String roomID, ZegoUpdateType zegoUpdateType, ArrayList<ZegoStream> streamList,
        JSONObject jsonObject) {
        roomService.notifyStreamUpdate(roomID, zegoUpdateType, streamList, jsonObject);
    }

    private void dispatchUserLeave(List<UIKitCoreUser> userInfoList) {
        userService.notifyUserLeave(userInfoList);
    }

    private void dispatchUserJoin(List<UIKitCoreUser> userInfoList) {
        userService.notifyUserJoin(userInfoList);
    }

    public void dispatchRoomUserCountOrPropertyChanged(List<ZegoUIKitUser> userList) {
        userService.notifyRoomUserCountOrPropertyChanged(userList);
    }


    public UIKitCoreUser getLocalCoreUser() {
        return localUser;
    }

    public boolean isLargeRoom() {
        return isLargeRoom || markAsLargeRoom;
    }

    public boolean isLocalUser(String userID) {
        if (localUser == null) {
            return false;
        }
        return Objects.equals(userID, localUser.userID);
    }


    public UIKitCoreUser getUserFromStreamID(String streamID) {
        if (getLocalCoreUser() == null) {
            return null;
        }
        if (Objects.equals(getLocalCoreUser().getMainStreamID(), streamID)) {
            return getLocalCoreUser();
        }
        for (UIKitCoreUser uiKitUser : remoteUserList) {
            if (Objects.equals(uiKitUser.getMainStreamID(), streamID)) {
                return uiKitUser;
            }
        }
        return null;
    }

    public UIKitCoreUser getUserbyUserID(String userID) {
        if (getLocalCoreUser() == null) {
            return null;
        }
        if (Objects.equals(getLocalCoreUser().userID, userID)) {
            return getLocalCoreUser();
        }
        for (UIKitCoreUser uiKitUser : remoteUserList) {
            if (Objects.equals(uiKitUser.userID, userID)) {
                return uiKitUser;
            }
        }
        return null;
    }

    @Override
    public void useFrontFacingCamera(boolean isFrontFacing) {
        this.isFrontFacing = isFrontFacing;
        audioVideoService.useFrontFacingCamera(isFrontFacing);
    }

    public boolean isUseFrontCamera() {
        return isFrontFacing;
    }

    @Override
    public boolean isMicrophoneOn(String userID) {
        UIKitCoreUser uiKitCoreUser = getUserbyUserID(userID);
        if (uiKitCoreUser != null) {
            return uiKitCoreUser.isMicOpen;
        }
        return false;
    }

    @Override
    public boolean isCameraOn(String userID) {
        UIKitCoreUser uiKitCoreUser = getUserbyUserID(userID);
        if (uiKitCoreUser != null) {
            return uiKitCoreUser.isCameraOpen;
        }
        return false;
    }

    @Override
    public void setAudioOutputToSpeaker(boolean enable) {
        audioVideoService.setAudioOutputToSpeaker(enable);
    }

    @Override
    public ZegoAudioRoute getAudioRouteType() {
        return audioVideoService.getAudioRouteType();
    }

    /**
     * is speaker or other output:Receiver/Bluetooth/Headphone.
     *
     * @return
     */
    public boolean isSpeakerOn() {
        return ExpressEngineProxy.getAudioRouteType() == ZegoAudioRoute.SPEAKER;
    }

    public static String generateCameraStreamID(String roomID, String userID) {
        return roomID + "_" + userID + "_main";
    }

    public static String generateScreenShareStreamID(String roomID, String userID) {
        return roomID + "_" + userID + "_screensharing";
    }

    @Override
    public void turnMicrophoneOn(String userID, boolean on) {
        UIKitCoreUser localCoreUser = UIKitCore.getInstance().getLocalCoreUser();
        if (localCoreUser != null && Objects.equals(localCoreUser.userID, userID)) {
            boolean stateChanged = (localCoreUser.isMicOpen != on);
            audioVideoService.turnMicrophoneOn(userID, on);
            if (stateChanged) {
                eventHandlerList.notifyAllListener(eventHandler -> {
                    eventHandler.onLocalMicrophoneStateUpdate(on);
                });
            }
        } else {
            audioVideoService.turnMicrophoneOn(userID, on);
        }
    }

    @Override
    public void turnCameraOn(String userID, boolean on) {
        UIKitCoreUser localCoreUser = UIKitCore.getInstance().getLocalCoreUser();
        if (localCoreUser != null && Objects.equals(localCoreUser.userID, userID)) {
            boolean stateChanged = (localCoreUser.isCameraOpen != on);
            audioVideoService.turnCameraOn(userID, on);
            if (stateChanged) {
                eventHandlerList.notifyAllListener(eventHandler -> {
                    eventHandler.onLocalCameraStateUpdate(on);
                });
            }
        } else {
            audioVideoService.turnCameraOn(userID, on);
        }
    }

    @Override
    public void startPlayingAllAudioVideo() {
        audioVideoService.startPlayingAllAudioVideo();
    }

    @Override
    public void stopPlayingAllAudioVideo() {
        audioVideoService.stopPlayingAllAudioVideo();
    }

    @Override
    public void mutePlayStreamAudio(String streamID, boolean mute) {
        engineProxy.mutePlayStreamAudio(streamID, mute);
    }

    @Override
    public void mutePlayStreamVideo(String streamID, boolean mute) {
        engineProxy.mutePlayStreamVideo(streamID, mute);
    }

    @Override
    public void startMixerTask(ZegoMixerTask task, IZegoMixerStartCallback callback) {
        engineProxy.startMixerTask(task, callback);
    }

    @Override
    public void stopMixerTask(ZegoMixerTask task, IZegoMixerStopCallback callback) {
        engineProxy.stopMixerTask(task, callback);
    }

    @Override
    public void startPlayingStream(String streamID, ZegoCanvas canvas) {
        ExpressEngineProxy.startPlayingStream(streamID, canvas);
    }

    public void startPlayingStream(String streamID, ZegoCanvas canvas, ZegoPlayerConfig config) {
        ExpressEngineProxy.startPlayingStream(streamID, canvas, config);
    }

    @Override
    public void addMicrophoneStateListener(ZegoMicrophoneStateChangeListener listener) {
        audioVideoService.addMicrophoneStateListener(listener, false);
    }

    public void addMicrophoneStateListenerInternal(ZegoMicrophoneStateChangeListener listener) {
        audioVideoService.addMicrophoneStateListener(listener, true);
    }

    @Override
    public void removeMicrophoneStateListener(ZegoMicrophoneStateChangeListener listener) {
        audioVideoService.removeMicrophoneStateListener(listener, false);
    }

    public void removeMicrophoneStateListenerInternal(ZegoMicrophoneStateChangeListener listener) {
        audioVideoService.removeMicrophoneStateListener(listener, true);
    }

    @Override
    public void addCameraStateListener(ZegoCameraStateChangeListener listener) {
        audioVideoService.addCameraStateListener(listener, false);
    }

    public void addCameraStateListenerInternal(ZegoCameraStateChangeListener listener) {
        audioVideoService.addCameraStateListener(listener, true);
    }

    @Override
    public void removeCameraStateListener(ZegoCameraStateChangeListener listener) {
        audioVideoService.removeCameraStateListener(listener, false);
    }

    public void removeCameraStateListenerInternal(ZegoCameraStateChangeListener listener) {
        audioVideoService.removeCameraStateListener(listener, true);
    }

    @Override
    public void addAudioOutputDeviceChangedListener(ZegoAudioOutputDeviceChangedListener listener) {
        audioVideoService.addAudioOutputDeviceChangedListener(listener, false);
    }

    public void addAudioOutputDeviceChangedListenerInternal(ZegoAudioOutputDeviceChangedListener listener) {
        audioVideoService.addAudioOutputDeviceChangedListener(listener, true);
    }

    @Override
    public void removeAudioOutputDeviceChangedListener(ZegoAudioOutputDeviceChangedListener listener) {
        audioVideoService.removeAudioOutputDeviceChangedListener(listener, false);
    }

    public void removeAudioOutputDeviceChangedListenerInternal(ZegoAudioOutputDeviceChangedListener listener) {
        audioVideoService.removeAudioOutputDeviceChangedListener(listener, true);
    }

    @Override
    public void addSoundLevelUpdatedListener(ZegoSoundLevelUpdateListener listener) {
        audioVideoService.addSoundLevelUpdatedListener(listener, false);
    }

    public void addSoundLevelUpdatedListenerInternal(ZegoSoundLevelUpdateListener listener) {
        audioVideoService.addSoundLevelUpdatedListener(listener, true);
    }

    @Override
    public void removeSoundLevelUpdatedListener(ZegoSoundLevelUpdateListener listener) {
        audioVideoService.removeSoundLevelUpdatedListener(listener, false);
    }

    @Override
    public void addTurnOnYourCameraRequestListener(ZegoTurnOnYourCameraRequestListener listener) {
        audioVideoService.addTurnOnYourCameraRequestListener(listener, false);
    }

    @Override
    public void removeTurnOnYourCameraRequestListener(ZegoTurnOnYourCameraRequestListener listener) {
        audioVideoService.removeTurnOnYourCameraRequestListener(listener, false);
    }

    public void addTurnOnYourCameraRequestListenerInternal(ZegoTurnOnYourCameraRequestListener listener) {
        audioVideoService.addTurnOnYourCameraRequestListener(listener, true);
    }

    public void removeTurnOnYourCameraRequestListenerInternal(ZegoTurnOnYourCameraRequestListener listener) {
        audioVideoService.removeTurnOnYourCameraRequestListener(listener, true);
    }

    @Override
    public void addTurnOnYourMicrophoneRequestListener(ZegoTurnOnYourMicrophoneRequestListener listener) {
        audioVideoService.addTurnOnYourMicrophoneRequestListener(listener, false);
    }

    @Override
    public void removeTurnOnYourMicrophoneRequestListener(ZegoTurnOnYourMicrophoneRequestListener listener) {
        audioVideoService.removeTurnOnYourMicrophoneRequestListener(listener, false);
    }

    @Override
    public void setAudioVideoResourceMode(ZegoAudioVideoResourceMode mode) {
        this.resourceMode = mode;
    }

    @Override
    public ZegoAudioVideoResourceMode getAudioVideoResourceMode() {
        return resourceMode;
    }

    @Override
    public void stopPlayingStream(String streamID) {
        ExpressEngineProxy.stopPlayingStream(streamID);
    }

    @Override
    public void startPreview(ZegoCanvas canvas) {
        ExpressEngineProxy.startPreview(canvas);
    }

    public void stopPreview() {
        ExpressEngineProxy.stopPreview();
    }

    @Override
    public void startPublishingStream(String streamID) {
        ExpressEngineProxy.startPublishingStream(streamID);
    }

    @Override
    public void stopPublishingStream() {
        ExpressEngineProxy.stopPublishingStream();
    }

    @Override
    public void openMicrophone(boolean open) {
        UIKitCoreUser localCoreUser = UIKitCore.getInstance().getLocalCoreUser();
        if (localCoreUser != null) {
            boolean stateChanged = (localCoreUser.isMicOpen != open);
            audioVideoService.openMicrophone(open);
            if (stateChanged) {
                eventHandlerList.notifyAllListener(eventHandler -> {
                    eventHandler.onLocalMicrophoneStateUpdate(open);
                });
            }
        }
    }

    @Override
    public void openCamera(boolean open) {
        UIKitCoreUser localCoreUser = UIKitCore.getInstance().getLocalCoreUser();
        if (localCoreUser != null) {
            boolean stateChanged = (localCoreUser.isMicOpen != open);
            audioVideoService.openCamera(open);
            if (stateChanged) {
                eventHandlerList.notifyAllListener(eventHandler -> {
                    eventHandler.onLocalCameraStateUpdate(open);
                });
            }
        }
    }

    @Override
    public void enable3A(boolean enable) {
        enable3AState = enable ? Enable3AState.enable_state : Enable3AState.disable_state;
        engineProxy.enable3A(enable);
    }

    public void addTurnOnYourMicrophoneRequestListenerInternal(ZegoTurnOnYourMicrophoneRequestListener listener) {
        audioVideoService.addTurnOnYourMicrophoneRequestListener(listener, true);
    }

    public void removeTurnOnYourMicrophoneRequestListenerInternal(ZegoTurnOnYourMicrophoneRequestListener listener) {
        audioVideoService.removeTurnOnYourMicrophoneRequestListener(listener, true);
    }

    public void removeSoundLevelUpdatedListenerInternal(ZegoSoundLevelUpdateListener listener) {
        audioVideoService.removeSoundLevelUpdatedListener(listener, true);
    }

    @Override
    public void joinRoom(String roomID, ZegoUIKitCallback callback) {
        joinRoom(roomID, false, callback);
    }

    @Override
    public void joinRoom(String roomID, boolean markAsLargeRoom, ZegoUIKitCallback callback) {
        if (ExpressEngineProxy.getEngine() == null) {
            return;
        }
        zegoUIKitRoom.roomID = roomID;
        this.markAsLargeRoom = markAsLargeRoom;
        Timber.d(
            "joinRoom() called with: roomID = [" + roomID + "], markAsLargeRoom = [" + markAsLargeRoom + "], token = ["
                + token + "]");

        roomService.joinRoom(roomID, token, new ZegoUIKitCallback() {
            @Override
            public void onResult(int errorCode) {
                Timber.d("joinRoom onResult() called with: errorCode = [" + errorCode + "]");

                if (errorCode != 0) {
                    zegoUIKitRoom.roomID = "";
                }
                if (callback != null) {
                    callback.onResult(errorCode);
                }
            }
        });
    }

    @Override
    public void leaveRoom() {
        resetRoomData();
        stopSharingScreen();
        if (ExpressEngineProxy.getEngine() == null) {
            return;
        }
        ExpressEngineProxy.stopPreview();
        ExpressEngineProxy.useFrontCamera(true);
        ExpressEngineProxy.setAudioRouteToSpeaker(true);
        audioVideoService.openCamera(false);
        ExpressEngineProxy.stopSoundLevelMonitor();

        roomService.leaveRoom((errorCode, extendedData) -> {
            //            roomService.clearRoomStateListeners();
        });
    }

    /**
     * clear data,not device
     */
    private void resetRoomData() {
        userService.clear();
        audioVideoService.clear();
        roomService.clearOtherListeners();
        messageService.clear();
        remoteUserList.clear();
        inRoomMessages.clear();
        zegoUIKitRoom.roomID = "";
        roomExtraInfoMap.clear();
        isFrontFacing = true;
        markAsLargeRoom = false;
        isLargeRoom = false;
        roomMemberCount = 0;

        setPlayStreamBufferIntervalRange(0, 4000);
        removeAutoDeleteRoomListeners();
    }

    @Override
    public ZegoUIKitRoom getRoom() {
        return zegoUIKitRoom;
    }

    @Override
    public void setRoomProperty(String key, String value) {
        Map<String, String> map = new HashMap<>();
        map.put(key, value);
        updateRoomProperties(map);
    }

    @Override
    public void updateRoomProperties(Map<String, String> map) {
        try {
            String key = "extra_info";
            Map<String, String> currentProperties = roomExtraInfoValueToMap(roomExtraInfoMap.get(key));
            JSONObject tempProperties = new JSONObject(currentProperties);
            for (Entry<String, String> entry : map.entrySet()) {
                tempProperties.put(entry.getKey(), entry.getValue());
            }
            String roomID = zegoUIKitRoom.roomID;
            if (!TextUtils.isEmpty(roomID)) {
                roomService.setRoomProperty(roomID, key, tempProperties.toString(), errorCode -> {
                    if (TextUtils.isEmpty(getRoom().roomID)) {
                        return;
                    }
                    if (errorCode == 0) {
                        List<String> updateKeys = new ArrayList<>();
                        Map<String, String> oldProperties = new HashMap<>(currentProperties);
                        long updateTime = System.currentTimeMillis();
                        for (Entry<String, String> entry : map.entrySet()) {
                            currentProperties.put(entry.getKey(), entry.getValue());
                            ZegoRoomExtraInfo roomExtraInfo = roomExtraInfoMap.get(key);
                            if (roomExtraInfo == null) {
                                roomExtraInfo = new ZegoRoomExtraInfo();
                                roomExtraInfo.key = key;
                                roomExtraInfo.updateUser = new ZegoUser(getLocalCoreUser().userID,
                                    getLocalCoreUser().userName);
                            }
                            roomExtraInfo.updateTime = updateTime;
                            roomExtraInfo.value = currentProperties.toString();
                            roomExtraInfoMap.put(roomExtraInfo.key, roomExtraInfo);

                            updateKeys.add(entry.getKey());
                        }
                        for (String updateKey : updateKeys) {
                            dispatchRoomPropertyUpdated(updateKey, oldProperties.get(updateKey),
                                currentProperties.get(updateKey));
                        }
                        dispatchRoomPropertiesFullUpdated(updateKeys, oldProperties, currentProperties);
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, String> getRoomProperties() {
        return roomExtraInfoValueToMap(roomExtraInfoMap.get("extra_info"));
    }

    private Map<String, String> roomExtraInfoValueToMap(ZegoRoomExtraInfo roomExtraInfo) {
        Map<String, String> map = new HashMap<>();
        try {
            if (roomExtraInfo == null || TextUtils.isEmpty(roomExtraInfo.value)) {
                return map;
            }
            JSONObject jsonObject = new JSONObject(roomExtraInfo.value);
            Iterator<String> iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = jsonObject.getString(key);
                map.put(key, value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }

    private void dispatchRoomPropertiesFullUpdated(List<String> keys, Map<String, String> oldProperties,
        Map<String, String> roomProperties) {
        roomService.notifyRoomPropertiesFullUpdated(keys, oldProperties, roomProperties);
    }

    private void dispatchRoomPropertyUpdated(String key, String oldValue, String value) {
        roomService.notifyRoomPropertyUpdate(key, oldValue, value);
    }

    @Override
    public void addRoomPropertyUpdateListener(ZegoRoomPropertyUpdateListener listener) {
        roomService.addRoomPropertyUpdatedListener(listener, false);
    }

    @Override
    public void removeRoomPropertyUpdateListener(ZegoRoomPropertyUpdateListener listener) {
        roomService.removeRoomPropertyUpdatedListener(listener, false);
    }

    public void addRoomStateUpdatedListener(RoomStateChangedListener listener) {
        roomService.addRoomStateUpdatedListener(listener, false);
    }

    public void addRoomStateUpdatedListenerInternal(RoomStateChangedListener listener) {
        roomService.addRoomStateUpdatedListener(listener, true);
    }

    public void removeRoomStateUpdatedListener(RoomStateChangedListener listener) {
        roomService.removeRoomStateUpdatedListener(listener, false);
    }

    @Override
    public void setTokenWillExpireListener(ZegoUIKitTokenExpireListener listener) {
        this.tokenExpireListener = listener;
    }

    @Override
    public void setLanguage(ZegoUIKitLanguage language) {
        this.language = language;
        uiKitTranslationText = new UIKitTranslationText(language);
    }

    public UIKitTranslationText getTranslationText() {
        return uiKitTranslationText;
    }

    public ZegoUIKitTokenExpireListener getTokenExpireListener() {
        return tokenExpireListener;
    }

    public void removeRoomStateUpdatedListenerInternal(RoomStateChangedListener listener) {
        roomService.removeRoomStateUpdatedListener(listener, true);
    }

    @Override
    public void addAudioVideoUpdateListener(ZegoAudioVideoUpdateListener listener) {
        roomService.addAudioVideoUpdateListener(listener, false);
    }

    public void addAudioVideoUpdateListenerInternal(ZegoAudioVideoUpdateListener listener) {
        roomService.addAudioVideoUpdateListener(listener, true);
    }

    @Override
    public void removeAudioVideoUpdateListener(ZegoAudioVideoUpdateListener listener) {
        roomService.removeAudioVideoUpdateListener(listener, false);
    }

    @Override
    public void addScreenSharingUpdateListener(ZegoScreenSharingUpdateListener listener) {
        roomService.addScreenSharingUpdateListener(listener, false);
    }

    public void addScreenSharingUpdateListenerInternal(ZegoScreenSharingUpdateListener listener) {
        roomService.addScreenSharingUpdateListener(listener, true);
    }

    @Override
    public void removeScreenSharingUpdateListener(ZegoScreenSharingUpdateListener listener) {
        roomService.removeScreenSharingUpdateListener(listener, false);
    }

    public void removeScreenSharingUpdateListenerInternal(ZegoScreenSharingUpdateListener listener) {
        roomService.removeScreenSharingUpdateListener(listener, true);
    }

    @Override
    public void sendInRoomCommand(String command, ArrayList<String> toUserList,
        ZegoSendInRoomCommandCallback callback) {
        roomService.sendInRoomCommand(getRoom().roomID, command, toUserList, callback);
    }

    @Override
    public void addInRoomCommandListener(ZegoInRoomCommandListener listener) {
        roomService.addInRoomCommandListener(listener, false);
    }

    @Override
    public void removeInRoomCommandListener(ZegoInRoomCommandListener listener) {
        roomService.removeInRoomCommandListener(listener, false);
    }

    public void addInRoomCommandListener(ZegoInRoomCommandListener listener, boolean weakRef) {
        roomService.addInRoomCommandListener(listener, weakRef);
    }

    public void removeInRoomCommandListener(ZegoInRoomCommandListener listener, boolean weakRef) {
        roomService.removeInRoomCommandListener(listener, weakRef);
    }

    public void removeAudioVideoUpdateListenerInternal(ZegoAudioVideoUpdateListener listener) {
        roomService.removeAudioVideoUpdateListener(listener, true);
    }

    @Override
    public void addUserUpdateListener(ZegoUserUpdateListener listener) {
        userService.addUserUpdateListener(listener, false);
    }

    public void addUserUpdateListenerInternal(ZegoUserUpdateListener listener) {
        userService.addUserUpdateListener(listener, true);
    }

    @Override
    public void removeUserUpdateListener(ZegoUserUpdateListener listener) {
        userService.removeUserUpdateListener(listener, false);
    }

    @Override
    public void addUserCountOrPropertyChangedListener(ZegoUserCountOrPropertyChangedListener listener) {
        userService.addUserCountOrPropertyChangedListener(listener, false);
    }

    public void addUserCountOrPropertyChangedListenerInternal(ZegoUserCountOrPropertyChangedListener listener) {
        userService.addUserCountOrPropertyChangedListener(listener, true);
    }

    @Override
    public void removeUserCountOrPropertyChangedListener(ZegoUserCountOrPropertyChangedListener listener) {
        userService.removeUserCountOrPropertyChangedListener(listener, true);
    }

    @Override
    public void removeUserFromRoom(List<String> userIDs) {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONArray jsonArray = new JSONArray(userIDs);
            jsonObject.put("zego_remove_user", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String command = jsonObject.toString();
        if (isLargeRoom || markAsLargeRoom) {
            ZegoUIKit.sendInRoomCommand(command, new ArrayList<>(), errorCode -> {

            });
        } else {
            ZegoUIKit.sendInRoomCommand(command, new ArrayList<>(userIDs), errorCode -> {

            });
        }

    }

    @Override
    public void addOnMeRemovedFromRoomListener(ZegoMeRemovedFromRoomListener listener) {
        userService.addOnMeRemovedFromRoomListener(listener, false);
    }

    @Override
    public void removeOnMeRemovedFromRoomListener(ZegoMeRemovedFromRoomListener listener) {
        userService.removeOnMeRemovedFromRoomListener(listener, false);
    }

    public void addOnMeRemovedFromRoomListenerInternal(ZegoMeRemovedFromRoomListener listener) {
        userService.addOnMeRemovedFromRoomListener(listener, true);
    }

    public void removeOnMeRemovedFromRoomListenerInternal(ZegoMeRemovedFromRoomListener listener) {
        userService.removeOnMeRemovedFromRoomListener(listener, true);
    }

    public void removeUserCountOrPropertyChangedListenerInternal(ZegoUserCountOrPropertyChangedListener listener) {
        userService.removeUserCountOrPropertyChangedListener(listener, true);
    }

    public void removeUserUpdateListenerInternal(ZegoUserUpdateListener listener) {
        userService.removeUserUpdateListener(listener, true);
    }

    @Override
    public void login(String userID, String userName, ZegoUIKitCallback callback) {
        HashMap<String, Object> commonParams = new HashMap<>();
        commonParams.put(ReportUtil.USER_ID, userID);
        ReportUtil.updateCommonParams(commonParams);

        this.localUser = new UIKitCoreUser(userID, userName);
        if (callback != null) {
            callback.onResult(0);
        }
    }

    @Override
    public void logout() {
        resetRoomData();
        roomService.clearRoomStateListeners();
        this.localUser = null;
        this.token = null;
    }

    @Override
    public ZegoUIKitUser getUser(String userID) {
        UIKitCoreUser coreUser = getUserbyUserID(userID);
        if (coreUser != null) {
            return coreUser.getUIKitUser();
        } else {
            return null;
        }
    }

    @Override
    public ZegoUIKitUser getLocalUser() {
        UIKitCoreUser localCoreUser = getLocalCoreUser();
        if (localCoreUser == null) {
            return null;
        }
        return localCoreUser.getUIKitUser();
    }

    @Override
    public List<ZegoUIKitUser> getAllUsers() {
        List<ZegoUIKitUser> uiKitUsers = GenericUtils.map(remoteUserList, UIKitCoreUser::getUIKitUser);
        uiKitUsers.add(0, localUser.getUIKitUser());
        return uiKitUsers;
    }

    public List<UIKitCoreUser> getRemoteUsers() {
        return new ArrayList<>(remoteUserList);
    }

    @Override
    public void addOnOnlySelfInRoomListener(ZegoOnlySelfInRoomListener listener) {
        userService.addOnOnlySelfInRoomListener(listener, false);
    }

    @Override
    public void removeOnOnlySelfInRoomListener(ZegoOnlySelfInRoomListener listener) {
        userService.removeOnOnlySelfInRoomListener(listener, false);
    }

    @Override
    public List<ZegoInRoomMessage> getInRoomMessages() {
        return inRoomMessages;
    }

    @Override
    public void sendInRoomMessage(String message) {
        messageService.sendInRoomMessage(message, null);
    }

    @Override
    public void sendInRoomMessage(String message, ZegoInRoomMessageSendStateListener listener) {
        messageService.sendInRoomMessage(message, listener);
    }

    public void resendInRoomMessage(ZegoInRoomMessage message, ZegoInRoomMessageSendStateListener listener) {
        messageService.resendInRoomMessage(message, listener);
    }

    @Override
    public void addInRoomMessageReceivedListener(ZegoInRoomMessageListener listener) {
        messageService.addInRoomMessageReceivedListener(listener, false);
    }

    @Override
    public void removeInRoomMessageReceivedListener(ZegoInRoomMessageListener listener) {
        messageService.removeInRoomMessageReceivedListener(listener, false);
    }

    @Override
    public void sendBarrageMessage(String roomID, String message, IZegoIMSendBarrageMessageCallback callback) {
        engineProxy.sendBarrageMessage(roomID, message, callback);
    }

    @Override
    public void addBarrageMessageListener(ZegoBarrageMessageListener listener) {
        messageService.addBarrageMessageListener(listener, false);
    }

    @Override
    public void removeBarrageMessageListener(ZegoBarrageMessageListener listener) {
        messageService.removeBarrageMessageListener(listener, false);
    }

    public void addInRoomMessageReceivedListenerInternal(ZegoInRoomMessageListener listener) {
        messageService.addInRoomMessageReceivedListener(listener, true);
    }

    public void removeInRoomMessageReceivedListenerInternal(ZegoInRoomMessageListener listener) {
        messageService.removeInRoomMessageReceivedListener(listener, true);
    }

    public static List<ZegoUIKitUser> sortUsers(List<ZegoUIKitUser> userList) {
        List<ZegoUIKitUser> sortUsers = new ArrayList<>();
        ZegoUIKitUser self = UIKitCore.getInstance().getLocalCoreUser().getUIKitUser();
        userList.remove(self);
        Collections.reverse(userList);
        sortUsers.add(self);
        sortUsers.addAll(userList);
        return sortUsers;
    }

    private enum Enable3AState {
        default_state, enable_state, disable_state
    }
}
