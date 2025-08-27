package com.zegocloud.uikit.service.internal.interfaces;

import android.app.Application;
import com.zegocloud.uikit.components.common.ZegoPresetResolution;
import com.zegocloud.uikit.internal.ZegoUIKitLanguage;
import com.zegocloud.uikit.plugin.beauty.IBeautyPlugin;
import com.zegocloud.uikit.plugin.common.IZegoUIKitSignalingPlugin;
import com.zegocloud.uikit.service.defines.ZegoScenario;
import com.zegocloud.uikit.service.defines.ZegoUIKitTokenExpireListener;
import com.zegocloud.uikit.service.express.ExpressEngineProxy;
import com.zegocloud.uikit.service.express.IExpressEngineEventHandler;
import im.zego.zegoexpress.constants.ZegoPublishChannel;
import im.zego.zegoexpress.entity.ZegoAudioConfig;
import im.zego.zegoexpress.entity.ZegoVideoConfig;

public interface IUIKitCore extends IUserService, IRoomService, IAudioVideoService, IMessageService {

    boolean init(Application application, Long appID, String appSign, ZegoScenario scenario);

    boolean initExpressEngine(Application application, Long appID, String appSign, ZegoScenario scenario);

    boolean isExpressEngineInitSucceed();

    IZegoUIKitSignalingPlugin getSignalingPlugin();

    String getVersion();

    void startSharingScreen(ZegoPresetResolution resolution);

    void startSharingScreen(ZegoPresetResolution resolution, int bitrate, int fps);

    boolean isScreenSharing();

    void stopSharingScreen();

    void setAudioConfig(ZegoAudioConfig config, ZegoPublishChannel channel);

    void setPlayStreamBufferIntervalRange(int minBufferInterval, int maxBufferInterval);

    void setVideoConfig(ZegoVideoConfig config);

    long getNetworkTimestamp();

    public void addEventHandler(IExpressEngineEventHandler eventHandler, boolean autoDelete);

    public void removeEventHandler(IExpressEngineEventHandler eventHandler);

    void sendSEI(String seiString);

    IBeautyPlugin getBeautyPlugin();

    void renewToken(String token);

    void setTokenWillExpireListener(ZegoUIKitTokenExpireListener listener);

    void setLanguage(ZegoUIKitLanguage language);

    void unInitExpressEngine();
}
