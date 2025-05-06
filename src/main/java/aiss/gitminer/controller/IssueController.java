package aiss.gitminer.controller;

import aiss.gitminer.exception.CommentNotFoundException;
import aiss.gitminer.exception.IssueNotFoundException;
import aiss.gitminer.model.Comment;
import aiss.gitminer.model.Issue;
import aiss.gitminer.repository.IssueRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Tag(name = "GitMiner Issue", description = "GitMiner Issue management API")
@RestController
@RequestMapping("/gitminer/issues")
public class IssueController {

    private IssueRepository issueRepository;

    @Autowired
    public IssueController(IssueRepository issueRepository) {
        this.issueRepository = issueRepository;
    }

    @Operation(
            summary = "Get all Issues",
            description = "Get a list of Issues"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "List of Issues",
                    content = { @Content(schema = @Schema(implementation = Issue.class),
                    mediaType = "application/json")}
            ),
            @ApiResponse(responseCode = "404",
                    description = "Issues not found",
                    content = { @Content(schema = @Schema())}
            )
    })

    @GetMapping
    public List<Issue> findALl(@RequestParam(required = false) String state, @RequestParam(required = false) String authorId) throws IssueNotFoundException {
        List<Issue> issues = issueRepository.findAll();
        if (issues.isEmpty())
            throw new IssueNotFoundException();
        else if (state != null && authorId != null)
            return issues.stream().filter(issue -> issue.getState().equals(state) && issue.getAuthor().getId().equals(authorId)).collect(Collectors.toList());
        else if (state != null)
            return issues.stream().filter(issue -> issue.getState().equals(state)).collect(Collectors.toList());
        else if (authorId != null)
            return issues.stream().filter(issue -> issue.getAuthor().getId().equals(authorId)).collect(Collectors.toList());
        return issues;
    }

    @Operation(
            summary = "Get an Issue by Id",
            description = "Get an Issue by specifying its Id"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Issue with id",
                    content = { @Content(schema = @Schema(implementation = Issue.class),
                    mediaType = "application/json")}
            ),
            @ApiResponse(responseCode = "404",
                    description = "Issue not found",
                    content = { @Content(schema = @Schema())}
            )
    })

    @GetMapping("/{issueId}")
    public Issue findById(@PathVariable("issueId") String issueId) throws IssueNotFoundException {
        Optional<Issue> issue = issueRepository.findById(issueId);
        if (!issue.isPresent())
            throw new IssueNotFoundException();
        return issue.get();
    }

    @Operation(
            summary = "Get all Comments of an Issue",
            description = "Get a list of Comments of an Issue"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "List of Comments of an Issue",
                    content = { @Content(schema = @Schema(implementation = Issue.class),
                    mediaType = "application/json")}
            ),
            @ApiResponse(responseCode = "404",
                    description = "Comments not found",
                    content = { @Content(schema = @Schema())}
            )
    })

    @GetMapping("/{issueId}/comments")
    public List<Comment> findAllComments(@PathVariable("issueId") String id) throws CommentNotFoundException {
        Issue issue = findById(id);
        List<Comment> comments = issue.getComments();
        if (comments.isEmpty())
            throw new CommentNotFoundException();
        return comments;
    }
}
