package com.cetiti.iotp.protocol.alink.demo;

import com.alibaba.fastjson.JSON;
import com.cetiti.iotp.protocol.alink.payload.CommonRequestPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhouliyu
 * @since 2019-11-21 15:18:55
 */
public class HelloWorld {

    private static final String TAG = "HelloWorld";

    private static final Logger LOG = LoggerFactory.getLogger(HelloWorld.class);

    public static void main(String[] args){

        LOG.isDebugEnabled();

        String dirPath = System.getProperty("user.dir") + "/test_case.json";

        String alinkPayload = FileUtils.readFile(dirPath);

        if (alinkPayload == null) {

            LOG.debug(TAG, "main - need alinkPayload info");

        }

        //来源：字节流
        byte[] payloadBytes = alinkPayload.getBytes();

        //读取
        CommonRequestPayload payload = JSON.parseObject(payloadBytes, CommonRequestPayload.class);

        //转换-IOT
        Object object = payload.getParams();


    }

}
