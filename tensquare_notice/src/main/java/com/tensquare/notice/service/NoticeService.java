package com.tensquare.notice.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.tensquare.entity.Result;
import com.tensquare.notice.client.ArticleClient;
import com.tensquare.notice.client.UserClient;
import com.tensquare.notice.dao.NoticeDao;
import com.tensquare.notice.dao.NoticeFreshDao;
import com.tensquare.notice.pojo.Notice;
import com.tensquare.notice.pojo.NoticeFresh;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import util.IdWorker;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NoticeService {

    @Autowired
    private NoticeDao noticeDao;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private NoticeFreshDao noticeFreshDao;

    @Autowired
    private UserClient userClient;

    @Autowired
    private ArticleClient articleClient;

    public Notice selectById(String id) {
        return noticeDao.selectById(id);
    }

    public Page<Notice> search(Map<String,Object> map, Integer page, Integer size) {
        EntityWrapper<Notice> wrapper = new EntityWrapper<>();
        for (String field : map.keySet()) {
            wrapper.eq(null != map.get(field), field, map.get(field));
        }
        Page<Notice> pageData = new Page<>(page, size);// 分页参数
        List<Notice> list = noticeDao.selectPage(pageData, wrapper); // 结果集
        // 完善notice
        for (Notice notice : list) {
            getInfo(notice);
        }
        pageData.setRecords(list);
        return pageData;
    }

    public void add(Notice notice) {
        String id = idWorker.nextId() + "";
        notice.setId(id);

        notice.setCreatetime(new Date());
        notice.setState("0"); // 表示未读
        noticeDao.insert(notice);

        //待推送消息入库，新消息提醒
//        NoticeFresh noticeFresh = new NoticeFresh();
//        noticeFresh.setNoticeId(id);//消息id
//        noticeFresh.setUserId(notice.getReceiverId());//待通知用户的id
//        noticeFreshDao.insert(noticeFresh);
    }

    public void updateById(Notice notice) {
        noticeDao.updateById(notice);
    }

    public Page<NoticeFresh> freshPage(String userId, Integer page, Integer size) {
        //封装查询条件
        NoticeFresh noticeFresh = new NoticeFresh();
        noticeFresh.setUserId(userId);

        //创建分页对象
        Page<NoticeFresh> pageData = new Page<>(page, size);

        //执行查询
        List<NoticeFresh> list = noticeFreshDao.selectPage(pageData, new EntityWrapper<>(noticeFresh));

        //设置查询结果集到分页对象中
        pageData.setRecords(list);

        //返回结果
        return pageData;
    }

    public void freshDelete(NoticeFresh noticeFresh) {
        noticeFreshDao.delete(new EntityWrapper<>(noticeFresh));
    }

    public void getInfo(Notice notice) {
        // 查询并添加操作者名称
        Result userResult = userClient.selectById(notice.getOperatorId());
        HashMap userMap = (HashMap) userResult.getData();
        notice.setOperatorName(userMap.get("nickname").toString());
        // 查询并添加文章名称
        Result articleResult = articleClient.findById(notice.getTargetId());
        HashMap articleMap = (HashMap) articleResult.getData();
        notice.setTargetName(articleMap.get("title").toString());
    }
}
