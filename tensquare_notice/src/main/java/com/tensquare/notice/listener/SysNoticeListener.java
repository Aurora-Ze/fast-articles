package com.tensquare.notice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.tensquare.entity.Result;
import com.tensquare.entity.StatusCode;
import com.tensquare.notice.netty.WebSocketHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;

import java.util.HashMap;

public class SysNoticeListener implements ChannelAwareMessageListener {

    private static ObjectMapper mapper = new ObjectMapper();

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        String queueName = message.getMessageProperties().getConsumerQueue();
        String userId = queueName.substring(queueName.lastIndexOf("_") + 1);

        io.netty.channel.Channel wsChannel = WebSocketHandler.userChannelMap.get(userId);

        if (wsChannel != null) {
            HashMap countMap = new HashMap();
            countMap.put("sysNoticeCount", 1);
            Result result = new Result(true, StatusCode.OK, "查询成功", countMap);

            wsChannel.writeAndFlush(new TextWebSocketFrame(mapper.writeValueAsString(result)));
        }
    }
}
