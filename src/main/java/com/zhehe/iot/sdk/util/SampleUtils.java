package com.zhehe.iot.sdk.util;

import com.aliyun.alink.linkkit.api.LinkKit;
import com.aliyun.alink.linksdk.tmp.device.payload.ValueWrapper;
import com.aliyun.alink.linksdk.tmp.listener.IPublishResourceListener;
import com.aliyun.alink.linksdk.tools.AError;

import java.util.Map;

public class SampleUtils {

    public static void reportProperty(Map<String, ValueWrapper> reportData){

        LinkKit.getInstance().getDeviceThing().thingPropertyPost(reportData, new IPublishResourceListener() {

            @Override
            public void onSuccess(String s, Object o) {
                // 属性上报成功
                System.out.println("上报成功 onSuccess() called with: s = [" + s + "], o = [" + o + "]");
            }

            @Override
            public void onError(String s, AError aError) {
                // 属性上报失败
                System.out.println("上报失败onError() called with: s = [" + s + "], aError = [" + getError(aError) + "]");
            }
        });
    }

    protected static String getError(AError error) {
        if (error == null) {
            return null;
        }
        return "[code=" + error.getCode() + ",msg=" + error.getMsg() + ",subCode=" + error.getSubCode() + ",subMsg=" + error.getSubMsg() + "]";
    }
}
