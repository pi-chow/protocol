package com.cetiti.iotp.protocol.transport.mqtt.model;

import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.UUID;

/**
 * 设备信息
 * @author zhouliyu
 * @since 2019-12-09 11:13:57
 */
@Data
public class DeviceInfo implements Serializable {
    private static final long serialVersionUID = 182333511902309278L;

    private UUID sessionId;

    private String deviceModel;

    private String deviceSn;

    /**
     * 登录用户名：${deviceSn}&${deviceModel}
     * */
    public static DeviceInfo parse(String userName){
        if (StringUtils.isEmpty(userName)){
            throw new NullPointerException("userName");
        }
        String[] arr = userName.split("&");
        if (arr.length != 2) {
            throw new IllegalArgumentException("userName");
        }
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDeviceSn(arr[0]);
        deviceInfo.setDeviceModel(arr[1]);
        return deviceInfo;
    }
}
