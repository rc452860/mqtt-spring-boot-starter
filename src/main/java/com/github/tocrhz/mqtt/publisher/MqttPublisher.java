package com.github.tocrhz.mqtt.publisher;

import com.github.tocrhz.mqtt.autoconfigure.MqttConnector;
import com.github.tocrhz.mqtt.autoconfigure.MqttConversionService;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.Objects;

/**
 * Used to publish message
 *
 * @author tocrhz
 */
public class MqttPublisher {
    private final static Logger log = LoggerFactory.getLogger(MqttPublisher.class);
    private MqttConnector connector;

    public MqttPublisher(MqttConnector connector) {

        this.connector = connector;
    }

    /**
     * 发送消息到指定主题 qos=1
     *
     * @param topic   主题
     * @param payload 消息内容
     * @throws IllegalArgumentException if topic is empty
     * @throws NullPointerException     if client not exists
     */
    public void send(String topic, Object payload) {
        send(MqttConnector.DefaultClientId, topic, payload, MqttConnector.DefaultPublishQos, false, null);
    }

    /**
     * 发送消息到指定主题 qos=1
     *
     * @param topic    主题
     * @param payload  消息内容
     * @param callback 消息发送完成后的回调
     * @throws IllegalArgumentException if topic is empty
     * @throws NullPointerException     if client not exists
     */
    public void send(String topic, Object payload, IMqttActionListener callback) {
        send(MqttConnector.DefaultClientId, topic, payload, MqttConnector.DefaultPublishQos, false, callback);
    }

    /**
     * 发送消息到指定主题 qos=1
     *
     * @param clientId 客户端ID
     * @param topic    主题
     * @param payload  消息内容
     * @throws IllegalArgumentException if topic is empty
     * @throws NullPointerException     if client not exists
     */
    public void send(String clientId, String topic, Object payload) {
        send(clientId, topic, payload, connector.getDefaultQosById(clientId), false, null);
    }

    /**
     * 发送消息到指定主题 qos=1
     *
     * @param clientId 客户端ID
     * @param topic    主题
     * @param payload  消息内容
     * @param callback 消息发送完成后的回调
     * @throws IllegalArgumentException if topic is empty
     * @throws NullPointerException     if client not exists
     */
    public void send(String clientId, String topic, Object payload, IMqttActionListener callback) {
        send(clientId, topic, payload, connector.getDefaultQosById(clientId), false, callback);
    }


    /**
     * 发送消息到指定主题, 指定qos, retained
     *
     * @param topic    主题
     * @param payload  消息内容
     * @param qos      服务质量
     * @param retained 保留消息
     * @throws IllegalArgumentException if topic is empty
     * @throws NullPointerException     if client not exists
     */
    public void send(String topic, Object payload, int qos, boolean retained) {
        send(MqttConnector.DefaultClientId, topic, payload, qos, retained, null);
    }

    /**
     * 发送消息到指定主题, 指定qos, retained
     *
     * @param clientId 客户端ID
     * @param topic    主题
     * @param payload  消息内容
     * @param qos      服务质量
     * @param retained 保留消息
     * @throws IllegalArgumentException if topic is empty
     * @throws NullPointerException     if client not exists
     */
    public void send(String clientId, String topic, Object payload, int qos, boolean retained) {
        send(clientId, topic, payload, qos, retained, null);
    }

    /**
     * 发送消息到指定主题, 指定qos, retained
     *
     * @param topic    主题
     * @param payload  消息内容
     * @param qos      服务质量
     * @param retained 保留消息
     * @param callback 消息发送完成后的回调
     * @throws IllegalArgumentException if topic is empty
     * @throws NullPointerException     if client not exists
     */
    public void send(String topic, Object payload, int qos, boolean retained, IMqttActionListener callback) {
        send(MqttConnector.DefaultClientId, topic, payload, qos, retained, callback);
    }


    /**
     * 发送消息到指定主题, 指定qos, retained
     *
     * @param clientId 客户端ID
     * @param topic    主题
     * @param payload  消息内容
     * @param qos      服务质量
     * @param retained 保留消息
     * @param callback 消息发送完成后的回调
     * @throws IllegalArgumentException if topic is empty
     * @throws NullPointerException     if client not exists
     */
    public void send(String clientId, String topic, Object payload, int qos, boolean retained, IMqttActionListener callback) {
        Assert.isTrue(topic != null && !topic.trim().isEmpty(), "topic cannot be blank.");
        IMqttAsyncClient client = Objects.requireNonNull(connector.getClientById(clientId));
        byte[] bytes = MqttConversionService.getSharedInstance().toBytes(payload);
        if (bytes == null) {
            return;
        }
        MqttMessage message = toMessage(bytes, qos, retained);
        try {
            client.publish(topic, message, null, callback);
        } catch (Throwable throwable) {
            log.error("message publish error: {}", throwable.getMessage(), throwable);
        }
    }

    private MqttMessage toMessage(byte[] payload, int qos, boolean retained) {
        MqttMessage message = new MqttMessage();
        message.setPayload(payload);
        message.setQos(qos);
        message.setRetained(retained);
        return message;
    }
}
