package com.tensquare.notice.controller;
import com.baomidou.mybatisplus.plugins.Page;
import com.tensquare.entity.PageResult;
import com.tensquare.entity.Result;
import com.tensquare.entity.StatusCode;
import com.tensquare.notice.pojo.Notice;
import com.tensquare.notice.pojo.NoticeFresh;
import com.tensquare.notice.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/notice")
@CrossOrigin
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    // 根据id查询
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public Result selectById(@PathVariable String id) {
        Notice notice = noticeService.selectById(id);
        noticeService.getInfo(notice);
        return new Result(true, StatusCode.OK, "查询成功", notice);
    }
    // 分页查询
    @RequestMapping(value = "search/{page}/{size}", method = RequestMethod.POST)
    public Result selectByList(@RequestBody Map<String,Object> map,
                               @PathVariable Integer page,
                               @PathVariable Integer size) {

        Page<Notice> pageData = noticeService.search(map, page, size);
        PageResult<Notice> pageResult = new PageResult<>(
                pageData.getTotal(), pageData.getRecords()
        );
        return new Result(true, StatusCode.OK, "查询成功", pageResult);
    }
    // 添加
    @RequestMapping(method = RequestMethod.POST)
    public Result add(@RequestBody Notice notice) {
        noticeService.add(notice);
        return new Result(true, StatusCode.OK, "添加成功");
    }

    //更新
    @RequestMapping(method = RequestMethod.PUT)
    public Result updateById(@RequestBody Notice notice) {
        noticeService.updateById(notice);
        return new Result(true, StatusCode.OK, "更新成功");
    }

    // 根据用户id查询用户所有的待推送消息
    @RequestMapping(method = RequestMethod.GET, value = "fresh/{userId}/{page}/{size}")
    public Result fresh(@PathVariable String userId,
                        @PathVariable Integer page,
                        @PathVariable Integer size) {
        Page<NoticeFresh> pageData = noticeService.freshPage(userId, page, size);

        PageResult<NoticeFresh> pageResult = new PageResult<>(
                pageData.getTotal(), pageData.getRecords()
        );

        return new Result(true, StatusCode.OK, "查询成功", pageResult);
    }

    // 删除推送
    @RequestMapping(value = "fresh",method = RequestMethod.DELETE)
    public Result freshDelete(@RequestBody NoticeFresh noticeFresh) {
        noticeService.freshDelete(noticeFresh);
        return new Result(true, StatusCode.OK, "删除成功");
    }


}
