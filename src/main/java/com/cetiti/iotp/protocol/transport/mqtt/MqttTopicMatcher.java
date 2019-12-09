package com.cetiti.iotp.protocol.transport.mqtt;

import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 * @author zhouliyu
 * @since 2019-12-06 09:55:49
 */
public class MqttTopicMatcher {

    @Getter
    private final String topic;

    private final Pattern topicRegex;

    public MqttTopicMatcher(String topic) {

        if (StringUtils.isEmpty(topic)) {

            throw new NullPointerException("topic");
        }

        this.topic = topic;

        //单层通配符"+";多层通配符"#"
        this.topicRegex = Pattern.compile(topic.replace("+", "[^/]+").replace("#", ".+") + "&");
    }

    public boolean matches(String topic) {

        return this.topicRegex.matcher(topic).matches();

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if(o == null || getClass() != o.getClass()) {

            return false;
        }

        MqttTopicMatcher that = (MqttTopicMatcher) o;

        return topic.equalsIgnoreCase(that.topic);
    }

    @Override
    public int hashCode() {
        return topic.hashCode();
    }
}
