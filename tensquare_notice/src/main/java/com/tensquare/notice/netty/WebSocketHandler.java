package com.tensquare.notice.netty;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tensquare.entity.Result;
import com.tensquare.entity.StatusCode;
import com.tensquare.notice.config.ApplicationContextProvider;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;

import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    // 转换json工具
    private static ObjectMapper mapper = new ObjectMapper();
    // map存放连接
    public static ConcurrentHashMap<String, Channel> userChannelMap = new ConcurrentHashMap();

    private RabbitTemplate rabbitTemplate = ApplicationContextProvider.getApplicationContext().getBean(RabbitTemplate.class);

    // 送Spring容器中获取消息监听器容器,处理订阅消息sysNotice
    SimpleMessageListenerContainer sysNoticeContainer = (SimpleMessageListenerContainer) ApplicationContextProvider.getApplicationContext()
            .getBean("sysNoticeContainer");

    // 送Spring容器中获取消息监听器容器,处理点赞消息sysNotice
    SimpleMessageListenerContainer userNoticeContainer = (SimpleMessageListenerContainer) ApplicationContextProvider.getApplicationContext()
            .getBean("userNoticeContainer");

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // 约定第一次访问传过来的数据为 {"userId":"123"}
        String json = msg.text();
        String userId = mapper.readTree(json).get("userId").asText();

        // 第一次连接，建立请求
        Channel channel = userChannelMap.get(userId);
        if (channel == null) {
            channel = ctx.channel();
            userChannelMap.put(userId, channel);
        }

        // 获取MQ中的消息
        RabbitAdmin rabbitAdmin = new RabbitAdmin(rabbitTemplate);
        String queueName = "article_subscribe_" + userId;
        Properties queueProperties = rabbitAdmin.getQueueProperties(queueName);

        int noticeCount = 0;
        if (queueProperties != null) {
            noticeCount = (int) queueProperties.get("QUEUE_MESSAGE_COUNT");
        }
        // ------------------------------------------------------------------
        //这里是点赞消息处理
        String userQueueName = "article_thumbup_" + userId;
        Properties userQueueProperties = rabbitAdmin.getQueueProperties(userQueueName);

        int userNoticeCount = 0;
        if (userQueueProperties != null) {
            userNoticeCount = (int) userQueueProperties.get("QUEUE_MESSAGE_COUNT");
        }

        //封装返回的数据
        HashMap countMap = new HashMap();
        countMap.put("sysNoticeCount", noticeCount);
        countMap.put("userNoticeCount", userNoticeCount);

        Result result = new Result(true, StatusCode.OK, "查询成功", countMap);

        //把数据发送给用户
        channel.writeAndFlush(new TextWebSocketFrame(mapper.writeValueAsString(result)));

        //把消息从队列里面清空，否则MQ消息监听器会再次消费一次
        if (noticeCount > 0) {
            rabbitAdmin.purgeQueue(queueName, true);
        }
        if (userNoticeCount > 0) {
            rabbitAdmin.purgeQueue(userQueueName, true);
        }

        sysNoticeContainer.addQueueNames(queueName);
        userNoticeContainer.addQueueNames(userQueueName);

    }
}
