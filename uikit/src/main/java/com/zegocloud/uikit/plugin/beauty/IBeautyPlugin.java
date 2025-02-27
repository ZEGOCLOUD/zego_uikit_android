package com.zegocloud.uikit.plugin.beauty;

import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import com.zegocloud.uikit.plugin.adapter.plugins.beauty.LicenceProvider;
import com.zegocloud.uikit.plugin.adapter.plugins.beauty.ZegoBeautyPluginConfig;
import com.zegocloud.uikit.plugin.adapter.plugins.beauty.ZegoBeautyPluginEffectsType;

public interface IBeautyPlugin {

    void init(Application application, long appID, String appSign);

    void setLicenceProvider(LicenceProvider provider);

    void uninit();

    boolean isPluginExited();
    void setZegoBeautyPluginConfig(ZegoBeautyPluginConfig config);

    Dialog getBeautyDialog(Context context);

    String getVersion();

    void enableBeautyFeature(ZegoBeautyPluginEffectsType beautyType, boolean enable);

    int getBeautyFeatureValue(ZegoBeautyPluginEffectsType beautyType);

    void setBeautyFeatureValue(ZegoBeautyPluginEffectsType beautyType, int value);

    void resetBeautyValueToDefault(ZegoBeautyPluginEffectsType beautyType);

    void resetBeautyValueToNone(ZegoBeautyPluginEffectsType beautyType);
}
