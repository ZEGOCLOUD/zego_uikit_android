package com.zegocloud.uikit.plugin.beauty;

import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import com.zegocloud.uikit.plugin.adapter.ZegoPluginAdapter;
import com.zegocloud.uikit.plugin.adapter.plugins.beauty.LicenceProvider;
import com.zegocloud.uikit.plugin.adapter.plugins.beauty.ZegoBeautyPluginConfig;
import com.zegocloud.uikit.plugin.adapter.plugins.beauty.ZegoBeautyPluginEffectsType;
import com.zegocloud.uikit.plugin.adapter.plugins.beauty.ZegoBeautyPluginInitCallback;
import com.zegocloud.uikit.service.express.ExpressEngineProxy;
import im.zego.zegoexpress.callback.IZegoCustomVideoProcessHandler;
import im.zego.zegoexpress.constants.ZegoPublishChannel;
import im.zego.zegoexpress.constants.ZegoVideoBufferType;
import im.zego.zegoexpress.entity.ZegoCustomVideoProcessConfig;
import im.zego.zegoexpress.entity.ZegoVideoConfig;

public class BeautyPluginBridge implements IBeautyPlugin {

    private ExpressEngineProxy engineProxy;

    public BeautyPluginBridge(ExpressEngineProxy engineProxy) {
        this.engineProxy = engineProxy;
    }

    public String getVersion() {
        if (isPluginExited()) {
            return ZegoPluginAdapter.beautyPlugin().getVersion();
        } else {
            return null;
        }
    }

    @Override
    public void enableBeautyFeature(ZegoBeautyPluginEffectsType beautyType, boolean enable) {
        if (isPluginExited()) {
            ZegoPluginAdapter.beautyPlugin().enableBeautyFeature(beautyType, enable);
        }
    }

    @Override
    public int getBeautyFeatureValue(ZegoBeautyPluginEffectsType beautyType) {
        if (isPluginExited()) {
            return ZegoPluginAdapter.beautyPlugin().getBeautyFeatureValue(beautyType);
        }
        return 0;
    }

    @Override
    public void setBeautyFeatureValue(ZegoBeautyPluginEffectsType beautyType, int value) {
        if (isPluginExited()) {
            ZegoPluginAdapter.beautyPlugin().setBeautyFeatureValue(beautyType, value);
        }
    }

    @Override
    public void resetBeautyValueToDefault(ZegoBeautyPluginEffectsType beautyType) {
        if (isPluginExited()) {
            ZegoPluginAdapter.beautyPlugin().resetBeautyValueToDefault(beautyType);
        }
    }

    @Override
    public void resetBeautyValueToNone(ZegoBeautyPluginEffectsType beautyType) {
        if (isPluginExited()) {
            ZegoPluginAdapter.beautyPlugin().resetBeautyValueToNone(beautyType);
        }
    }

    @Override
    public void init(Application application, long appID, String appSign) {
        if (isPluginExited()) {
            enableBeautyPluginVideoProcessHandler(engineProxy, true);
            ZegoPluginAdapter.beautyPlugin().init(application, appID, appSign, new ZegoBeautyPluginInitCallback() {
                @Override
                public void onResult(int errorCode, String message) {
                    if (errorCode != 0) {
                        enableBeautyPluginVideoProcessHandler(engineProxy, false);
                    }
                }
            });
        }
    }

    @Override
    public void setLicenceProvider(LicenceProvider provider) {
        if (isPluginExited()) {
            ZegoPluginAdapter.beautyPlugin().setLicenceProvider(provider);
        }
    }

    @Override
    public boolean isPluginExited() {
        return ZegoPluginAdapter.beautyPlugin() != null;
    }

    @Override
    public void uninit() {
        if (isPluginExited()) {
            ZegoPluginAdapter.beautyPlugin().unInit();
        }
    }

    @Override
    public void setZegoBeautyPluginConfig(ZegoBeautyPluginConfig config) {
        if (isPluginExited()) {
            ZegoPluginAdapter.beautyPlugin().setZegoBeautyPluginConfig(config);
        }
    }

    @Override
    public Dialog getBeautyDialog(Context context) {
        if (isPluginExited()) {
            return ZegoPluginAdapter.beautyPlugin().getBeautyDialog(context);
        } else {
            return null;
        }
    }

    private void enableBeautyPluginVideoProcessHandler(ExpressEngineProxy engineProxy, boolean enable) {
        if (ZegoPluginAdapter.beautyPlugin() == null) {
            return;
        }
        if (enable) {
            ZegoCustomVideoProcessConfig config = new ZegoCustomVideoProcessConfig();
            config.bufferType = ZegoVideoBufferType.GL_TEXTURE_2D;

            engineProxy.enableCustomVideoProcessing(true, config, ZegoPublishChannel.MAIN);
            engineProxy.setCustomVideoProcessHandler(new IZegoCustomVideoProcessHandler() {
                @Override
                public void onStart(ZegoPublishChannel channel) {
                    ZegoPluginAdapter.beautyPlugin().unInitEnv();
                    ZegoVideoConfig videoConfig = engineProxy.getVideoConfig();
                    ZegoPluginAdapter.beautyPlugin().initEnv(videoConfig.captureWidth, videoConfig.captureHeight);

                }

                @Override
                public void onStop(ZegoPublishChannel channel) {
                    ZegoPluginAdapter.beautyPlugin().unInitEnv();
                }

                @Override
                public void onCapturedUnprocessedTextureData(int textureID, int width, int height,
                    long referenceTimeMillisecond, ZegoPublishChannel channel) {
                    // Process buffer by ZegoEffects
                    int processedTextureID = ZegoPluginAdapter.beautyPlugin().processTexture(textureID, width, height);
                    // Send processed texture to ZegoExpressEngine
                    engineProxy.sendCustomVideoProcessedTextureData(processedTextureID, width, height,
                        referenceTimeMillisecond);
                }
            });
        } else {
            //            engineProxy.setCustomVideoProcessHandler(null);
            engineProxy.enableCustomVideoProcessing(false, null, ZegoPublishChannel.MAIN);
        }
    }
}
