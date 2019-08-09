package com.zhehe.iot.sdk;

import com.aliyun.alink.apiclient.threadpool.ThreadPool;
import com.aliyun.alink.dm.api.DeviceInfo;
import com.aliyun.alink.dm.api.InitResult;
import com.aliyun.alink.linkkit.api.ILinkKitConnectListener;
import com.aliyun.alink.linkkit.api.IoTMqttClientConfig;
import com.aliyun.alink.linkkit.api.LinkKit;
import com.aliyun.alink.linkkit.api.LinkKitInitParams;
import com.aliyun.alink.linksdk.tmp.device.payload.ValueWrapper;
import com.aliyun.alink.linksdk.tools.AError;
import com.google.gson.Gson;
import com.zhehe.iot.FileUtils;
import com.zhehe.iot.sdk.pojo.DeviceInfoData;
import com.zhehe.iot.sdk.util.SampleUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Application {



    public static void main(String[] args) {
        String path = System.getProperty("user.dir") + "/device_id.json";
        String deviceInfo = FileUtils.readFile(path);
        if (deviceInfo == null || deviceInfo.isEmpty()){
            System.out.println("请配置device.json");
        }
        Gson mGson = new Gson();
        DeviceInfoData deviceInfoData = mGson.fromJson(deviceInfo, DeviceInfoData.class);
        if(deviceInfoData != null && deviceInfoData.checkDeviceInfo()){
            Application app = new Application();
            app.init(deviceInfoData);
        }
    }

    public void init(final DeviceInfoData deviceInfoData) {
        LinkKitInitParams params = new LinkKitInitParams();
        /**
         * 设置 Mqtt 初始化参数
         */
        IoTMqttClientConfig config = new IoTMqttClientConfig();
        config.productKey = deviceInfoData.productKey;
        config.deviceName = deviceInfoData.deviceName;
        config.deviceSecret = deviceInfoData.deviceSecret;
        config.channelHost = deviceInfoData.productKey + ".iot-as-mqtt." + deviceInfoData.region + ".aliyuncs.com:1883";
        /**
         * 是否接受离线消息
         * 对应 mqtt 的 cleanSession 字段
         */
        config.receiveOfflineMsg = false;
        params.mqttClientConfig = config;

        /**
         * 设置初始化三元组信息，用户传入
         */
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.productKey = deviceInfoData.productKey;
        deviceInfo.deviceName = deviceInfoData.deviceName;
        deviceInfo.deviceSecret = deviceInfoData.deviceSecret;

        LinkKit.getInstance().init(params, new ILinkKitConnectListener() {
            public void onError(AError aError) {
                System.out.println("Init Error error=" + aError);
            }

            public void onInitDone(InitResult initResult) {
                System.out.println("onInitDone result=" + initResult);
                executeScheduler(deviceInfoData);
            }
        });
    }

    public void executeScheduler(DeviceInfoData deviceInfoData){
        ThreadPool.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Map<String, ValueWrapper> reportData = new HashMap<String, ValueWrapper>();
                reportData.put("CT22B5",new ValueWrapper(2));
                SampleUtils.reportProperty(reportData);
            }
        }, 3, 5, TimeUnit.SECONDS);
    }
}
