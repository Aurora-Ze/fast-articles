package com.tensquare.article.service;

import com.tensquare.article.pojo.Comment;
import com.tensquare.article.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import util.IdWorker;

import java.util.Date;
import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    public Comment findById(String commentId) {
        return commentRepository.findById(commentId).get();
    }

    public void save(Comment comment) {
        comment.set_id(idWorker.nextId() + "");
        // 初始化一些数据
        comment.setThumbup(0);
        comment.setPublishdate(new Date());
        // 保存
        commentRepository.save(comment);
    }

    public void updateById(Comment comment) {
        commentRepository.save(comment);
    }

    public void deleteById(String commentId) {
        commentRepository.deleteById(commentId);
    }

    public List<Comment> findByArticleId(String articleId) {
        List<Comment> list = commentRepository.findByArticleid(articleId);
        return list;
    }

    public void thumbup(String commentId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(commentId));

        Update update = new Update();
        update.inc("thumbup", 1);

        mongoTemplate.updateFirst(query, update, "comment");
    }
}
