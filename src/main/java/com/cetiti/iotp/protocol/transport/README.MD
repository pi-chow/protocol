## MQTT 协议实现

基于 MQTT+JSON 实现MQTT服务端

* 支持用户信息校验

> ${deviceSn}&${deviceModel}

* 支持数据上报与响应

    * publish : /sys/${deviceModel}/${deviceSn}/property/post
    * subscribe : /sys/${deviceModel}/${deviceSn}/property/post_reply