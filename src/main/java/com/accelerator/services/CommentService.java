package com.accelerator.services;

import com.accelerator.dto.Comment;

import java.util.List;

public interface CommentService {

    List<Comment> findAlComments() throws InterruptedException;

    void prepareCommentAndSave(String fullName, String country, String content, String rating);
}
