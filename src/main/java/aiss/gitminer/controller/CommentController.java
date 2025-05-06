package aiss.gitminer.controller;

import aiss.gitminer.exception.CommentNotFoundException;
import aiss.gitminer.repository.CommentRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import aiss.gitminer.model.Comment;
import java.util.List;
import java.util.Optional;

@Tag(name = "GitMiner Comment", description = "GitMiner Comment management API")
@RestController
@RequestMapping("/gitminer/comments")
public class CommentController {

    private CommentRepository commentRepository;

    @Autowired
    public CommentController(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Operation(
        summary = "Get all Comments",
        description = "Get a list of Comments"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200",
                description = "List of Comments",
                content = { @Content(schema = @Schema(implementation = Comment.class),
                mediaType = "application/json")}
        ),
        @ApiResponse(responseCode = "404",
                description = "Comments not found",
                content = { @Content(schema = @Schema())}
        )
    })

    @GetMapping()
    public List<Comment> findAll() throws CommentNotFoundException {
        List<Comment> comments = commentRepository.findAll();
        if (comments.isEmpty()) {
            throw new CommentNotFoundException();
        }
        return comments;
    }

    @Operation(
        summary = "Get a Comment by Id",
        description = "Get a Comment by specifying its Id")
    @ApiResponses({
        @ApiResponse(responseCode = "200",
                description = "Comment with Id",
                content = { @Content(schema = @Schema(implementation = Comment.class),
                mediaType = "application/json")}
        ),
        @ApiResponse(responseCode = "404",
                description="Comment not found",
                content = { @Content(schema = @Schema())}
        )
    })

    @GetMapping("/{commentId}")
    public Comment findById(@PathVariable("commentId") String id) throws CommentNotFoundException {
        Optional<Comment> comment = commentRepository.findById(id);

        if (!comment.isPresent())
            throw new CommentNotFoundException();

        return comment.get();
    }
}
