package com.tensquare.article.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.tensquare.article.pojo.Article;
import com.tensquare.article.service.ArticleService;
import com.tensquare.entity.PageResult;
import com.tensquare.entity.Result;
import com.tensquare.entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/article")
@CrossOrigin
public class ArticleController {
    @Autowired
    ArticleService articleService;

    @Resource(name = "restTemplate")
    private RedisTemplate redisTemplate;

    // 处理异常
    @RequestMapping(value = "/exception", method = RequestMethod.GET)
    public Result exception() throws Exception{
        throw new Exception("统一异常处理");
    }
    // 找到所有文章
    @RequestMapping(method = RequestMethod.GET)
    public Result findAll() {
        List<Article> list = articleService.findAll();
        return new Result(true, StatusCode.OK, "查询成功", list);
    }
    // 根据id查询文章
   @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Result findById(@PathVariable String id) {
        Article article = articleService.findById(id);
        return new Result(true, StatusCode.OK, "查询成功", article);
    }
    // 添加文章
    @RequestMapping(method = RequestMethod.POST)
    public Result add(@RequestBody Article article) {
        articleService.add(article);

        return new Result(true, StatusCode.OK, "添加成功");
    }
    // 删除文章
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Result delete(@PathVariable String id) {
        articleService.delete(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }
    // 更新文章
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Result update(@PathVariable String id, @RequestBody Article article) {
        article.setId(id);
        articleService.update(article);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    @RequestMapping(value = "search/{page}/{size}", method = RequestMethod.POST)
    public Result findByPage(@PathVariable Integer page,
                             @PathVariable Integer size,
                             @RequestBody Map<String,Object> map) {
        // 根据条件进行分页查询
        Page<Article> pageData = articleService.search(map, page, size);
        // 封装分页返回对象
        PageResult<Article> pageResult = new PageResult<>(
                pageData.getTotal(), pageData.getRecords()
        );
        // 返回结果
        return new Result(true, StatusCode.OK, "查询成功", pageResult);
    }

    @RequestMapping(value = "/subscribe", method = RequestMethod.POST)
    private Result subscribe(@RequestBody Map map) {
        //根据文章id，订阅文章作者，返回订阅状态，true表示订阅成功，false表示取消订阅成功
        Boolean flag = articleService.subscribe(map.get("userId").toString(), map.get("articleId").toString());

        if (flag) {
            return new Result(true, StatusCode.OK, "订阅成功");
        } else {
            return new Result(true, StatusCode.OK, "订阅取消");
        }
    }

    @RequestMapping(value = "thumbup/{articleId}", method = RequestMethod.PUT)
    public Result thumbup(@PathVariable String articleId) {
        //模拟用户id
        String userId = "4";
        String key = "thumbup_article_" + userId + "_" + articleId;

        //查询用户点赞信息，根据用户id和文章id
        Object flag = redisTemplate.opsForValue().get(key);

        //判断查询到的结果是否为空
        if (flag == null) {
            //如果为空，表示用户没有点过赞，可以点赞
            articleService.thumbup(articleId, userId);

            //点赞成功，保存点赞信息
            redisTemplate.opsForValue().set(key, 1);

            return new Result(true, StatusCode.OK, "点赞成功");
        }

        //如果不为空，表示用户点过赞，不可以重复点赞
        return new Result(false, StatusCode.REPERROR, "不能重复点赞");
    }
}
