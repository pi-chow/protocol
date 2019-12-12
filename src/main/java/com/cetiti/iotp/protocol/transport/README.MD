## MQTT 协议实现

* 基于 MQTT+JSON 实现MQTT服务端
* 支持属性上报与响应

## 使用流程

### 设备信息

* User-name:${deviceSn}&${deviceModel}
* password: 目前仅非空即可，未做校验。

### 设备属性上报

> 属性一般是设备的运行状态，如当前温度等

**publish:** /sys/${deviceModel}/${deviceSn}/thing/event/property/post
```json
{
	"id": "001",
	"version": "1.0",
	"params": {
		"LightSwitch": 1,
		"des": "001"
	},
	"method": "thing.event.property.post"
}
```
**subscribe:** /sys/${deviceModel}/${deviceSn}/thing/event/property/post_reply
```json
{
	"code": 200,
	"data": {},
	"id": "001",
	"message": "success",
	"method": "thing.event.property.post",
	"version": "1.0"
}
```