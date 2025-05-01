package aiss.gitminer.controller;

import aiss.gitminer.exception.CommentNotFoundException;
import aiss.gitminer.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import aiss.gitminer.model.Comment;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/gitminer/comments")
public class CommentController {

    private CommentRepository commentRepository;

    @Autowired
    public CommentController(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @GetMapping()
    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    @GetMapping("/{comment_id}")
    public Comment findById(@PathVariable String comment_id) throws CommentNotFoundException {
        Optional<Comment> comment = commentRepository.findById(comment_id);

        if (!comment.isPresent())
            throw new CommentNotFoundException();

        return comment.get();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Comment create(@RequestBody @Valid Comment comment) {
        Comment _comment = commentRepository.save(new Comment(comment.getId(), comment.getBody(), comment.getAuthor(),
                comment.getCreatedAt(), comment.getUpdatedAt()));
        return _comment;
    }

    @PutMapping("/{comment_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@RequestBody @Valid Comment updatedComment, @PathVariable String comment_id) throws CommentNotFoundException {
        Optional<Comment> commentData = commentRepository.findById(comment_id);

        Comment _comment;

        if (commentData.isPresent()) {
            _comment = commentData.get();
            _comment.setBody(updatedComment.getBody());
            _comment.setAuthor(updatedComment.getAuthor());
            _comment.setCreatedAt(updatedComment.getCreatedAt());
            _comment.setUpdatedAt(updatedComment.getUpdatedAt());
        } else
            throw new CommentNotFoundException();

        commentRepository.save(_comment);
    }

    @DeleteMapping("/{comment_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String comment_id) throws CommentNotFoundException {
        if (commentRepository.existsById(comment_id))
            commentRepository.deleteById(comment_id);
        else
            throw new CommentNotFoundException();
    }
}
