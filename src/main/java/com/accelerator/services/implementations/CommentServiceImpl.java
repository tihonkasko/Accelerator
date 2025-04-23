package com.accelerator.services.implementations;

import com.accelerator.dto.Comment;
import com.accelerator.repo.CommentRepository;
import com.accelerator.services.CommentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    public static final Integer MAX_COMMENTS_SIZE = 6;

    @Resource
    CommentRepository commentRepository;

    @Override
    public List<Comment> findAlComments(){
        List<Comment> comments = (List<Comment>) commentRepository.findAll();
        comments.sort(Comparator.comparing(Comment::getId));
        if (comments.size() > MAX_COMMENTS_SIZE){
            List<Comment> commentsToRemove = comments.subList(0, comments.size() - MAX_COMMENTS_SIZE);
            for(Comment commentToRemove : commentsToRemove){
                commentRepository.deleteById(commentToRemove.getId());
            }
            return comments.subList(comments.size() - MAX_COMMENTS_SIZE, comments.size());
        }
        return comments;
    }

    @Override
    public void prepareCommentAndSave(String fullName, String country, String content, String rating) {
        String newFullName = fullName.length() > 15 ? fullName.substring(0,15): fullName;
        String newCountry = country.length() > 15 ? country.substring(0,15): country;
        String newContent= content.length() > 100 ? content.substring(0,100): content;
        Comment preparedComment = prepareComment(newFullName, newCountry, newContent, rating);
        commentRepository.save(preparedComment);
    }

    private Comment prepareComment(String fullName, String selectedCountry, String comment, String value) {
        Comment newComment = new Comment();
        newComment.setFullName(fullName);
        newComment.setCountry(selectedCountry);
        newComment.setContent(comment);
        newComment.setRating(value);
        return newComment;
    }
}
