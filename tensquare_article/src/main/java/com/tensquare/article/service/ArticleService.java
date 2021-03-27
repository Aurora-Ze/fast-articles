package com.tensquare.article.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.tensquare.article.client.NoticeClient;
import com.tensquare.article.dao.ArticleDao;
import com.tensquare.article.pojo.Article;
import com.tensquare.article.pojo.Notice;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import util.IdWorker;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ArticleService {
    @Autowired
    private ArticleDao articleDao;

    @Autowired
    private IdWorker idWorker;

    @Resource(name = "restTemplate")
    private RedisTemplate redisTemplate;

    @Autowired
    private NoticeClient noticeClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public List<Article> findAll() {
        return articleDao.selectList(null);
    }

    public Article findById(String id) {
        return articleDao.selectById(id);
    }

    public void add(Article article) {
        //TODO 根据jwt获得用户id, 即作者
        String userId = "2";
        article.setUserid("2");

        // 生成id
        String id = idWorker.nextId() + "";
        article.setId(id);
        // 初始化属性：评论数、点赞数和浏览量
        article.setComment(0);
        article.setThumbup(0);
        article.setVisits(0);
        articleDao.insert(article);

        // 通知所有订阅者

        String authorKey = "article_author_" + article.getUserid();
        Set<String> set = redisTemplate.boundSetOps(authorKey).members();

        for (String uid : set) {
            Notice notice = new Notice();
            notice.setReceiverId(uid);
            notice.setOperatorId(userId);
            notice.setAction("publish");
            notice.setTargetType("article");
            notice.setTargetId(id);
            notice.setType("sys");

            noticeClient.add(notice);
        }
        //入库成功后，发送mq消息，内容是消息通知id
        rabbitTemplate.convertAndSend("article_subscribe", userId, id);
    }

    public void delete(String id) {
        articleDao.deleteById(id);
    }

    public void update(Article article) {
        articleDao.updateById(article);
    }

    public Page<Article> search(Map<String, Object> map, Integer page, Integer size) {
        EntityWrapper<Article> wrapper = new EntityWrapper<Article>();
        for (String field : map.keySet()) {
            wrapper.eq(null != map.get(field), field, map.get(field));
        }
        Page<Article> pageData = new Page<>(page, size);// 分页参数
        List list = articleDao.selectPage(pageData, wrapper); // 结果集
        pageData.setRecords(list);
        return pageData;
    }

    public Boolean subscribe(String userId, String articleId) {
        // 根据文章id查询文章作者id
        String authorId = articleDao.selectById(articleId).getUserid();

        // 创建管理器
        RabbitAdmin rabbitAdmin = new RabbitAdmin(rabbitTemplate.getConnectionFactory());
        // 声明交换机
        DirectExchange exchange = new DirectExchange("article_subscribe");
        rabbitAdmin.declareExchange(exchange);
        // 创建消息队列
        Queue queue = new Queue("article_subscribe_" +  userId, true);
        // 绑定交换机与队列
        Binding binding = BindingBuilder.bind(queue).to(exchange).with(authorId);

        String userKey = "article_subscribe_" + userId;
        String authorKey = "article_author_" + authorId;

        // 查询该用户是否已经订阅作者
        Boolean flag = redisTemplate.boundSetOps(userKey).isMember(authorId);

        if (flag) {
            // 取消订阅
            redisTemplate.boundSetOps(userKey).remove(authorId);
            redisTemplate.boundSetOps(authorKey).remove(userId);
            // 删除绑定关系
            rabbitAdmin.removeBinding(binding);

            return false;
        } else {
            // 进行订阅
            redisTemplate.boundSetOps(userKey).add(authorId);
            redisTemplate.boundSetOps(authorKey).add(userId);
            // 声明队列、添加绑定关系
            rabbitAdmin.declareQueue(queue);
            rabbitAdmin.declareBinding(binding);

            return true;
        }
    }

    public void thumbup(String articleId,String userid) {
        //文章点赞
        Article article = articleDao.selectById(articleId);
        article.setThumbup(article.getThumbup() + 1);
        articleDao.updateById(article);

        //消息通知
        Notice notice = new Notice();
        notice.setReceiverId(article.getUserid());
        notice.setOperatorId(userid);
        notice.setAction("thumbup");
        notice.setTargetType("article");
        notice.setTargetId(articleId);
        notice.setType("user");

        noticeClient.add(notice);

        //1 创建Rabbit管理器
        RabbitAdmin rabbitAdmin = new RabbitAdmin(rabbitTemplate.getConnectionFactory());

        //2 创建队列，每个用户都有自己的队列，通过用户id进行区分
        Queue queue = new Queue("article_thumbup_" + article.getUserid(), true);
        rabbitAdmin.declareQueue(queue);

        //3 发送消息
        rabbitTemplate.convertAndSend("article_thumbup_"+article.getUserid(),articleId);
    }
}
