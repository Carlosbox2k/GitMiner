package aiss.gitminer.controller;

import aiss.gitminer.exception.CommentNotFoundException;
import aiss.gitminer.repository.CommentRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import aiss.gitminer.model.Comment;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Tag(name = "GitMiner Comment", description = "GitMiner comment management API")
@RestController
@RequestMapping("/gitminer/comments")
public class CommentController {

    private CommentRepository commentRepository;

    @Autowired
    public CommentController(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Operation(
            summary = "Get All Comments",
            description = "Get a List of Comment object")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of comments", content = { @Content(schema = @Schema(implementation = Comment.class),
                    mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description="Comment not found ", content = { @Content(schema = @Schema()) })
    })
    @GetMapping()
    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    @Operation(
            summary = "Get Comment by id",
            description = "Get a Comment object by specifying its id")
    @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Comment with id", content = { @Content(schema = @Schema(implementation = Comment.class),
    mediaType = "application/json") }),
    @ApiResponse(responseCode = "404", description="Comment not found ", content = { @Content(schema = @Schema()) })
    })

    @GetMapping("/{comment_id}")
    public Comment findById(@Parameter(description = "id of the comment to be founded") @PathVariable String comment_id) throws CommentNotFoundException {
        Optional<Comment> comment = commentRepository.findById(comment_id);

        if (!comment.isPresent())
            throw new CommentNotFoundException();

        return comment.get();
    }

    @Operation(
            summary = "Update a comment",
            description ="Update a Comment object by specifying its id"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Comment updated", content = { @Content(schema = @Schema(),
                mediaType = "application/json") }),
        @ApiResponse(responseCode = "404", description="Comment can not be updated ", content = { @Content(schema = @Schema()) })
    })
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
    @Operation(
            summary = "Delete a Comment by id",
            description = "Delete a Comment object by specifying its id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Comment deleted", content = {@Content(schema = @Schema(),
                    mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", description="Comment not found ", content = { @Content(schema = @Schema()) })

    })
    @DeleteMapping("/{comment_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String comment_id) throws CommentNotFoundException {
        if (commentRepository.existsById(comment_id))
            commentRepository.deleteById(comment_id);
        else
            throw new CommentNotFoundException();
    }
}
