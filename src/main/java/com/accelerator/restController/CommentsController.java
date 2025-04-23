package com.accelerator.restController;

import com.accelerator.dto.Comment;
import com.accelerator.services.CommentService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/chemistry")
public class CommentsController {

    @Resource
    CommentService commentService;

    @PostMapping(value = "/comments",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public String postComment(@RequestParam String fullName, @RequestParam String country,
                              @RequestParam String content, @RequestParam String rating){
        commentService.prepareCommentAndSave(fullName, country, content, rating);
        return "Done";
    }

    @GetMapping(value = "/comments")
    public List<Comment> getComments() throws InterruptedException {
        List<Comment> comments = commentService.findAlComments();
        return comments;
    }
}
