package com.accelerator.repo;

import com.accelerator.dto.Comment;
import org.springframework.data.repository.CrudRepository;

public interface CommentRepository extends CrudRepository<Comment, Integer>{
}
