package aiss.gitminer.controller;

import aiss.gitminer.exception.CommitNotFoundException;
import aiss.gitminer.model.Commit;
import aiss.gitminer.repository.CommitRepository;
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

@Tag(name = "GitMiner Commit", description = "GitMiner Commit management API")
@RestController
@RequestMapping("/gitminer/commits")
public class CommitController {

    private CommitRepository commitRepository;

    @Autowired
    public CommitController(CommitRepository commitRepository) {
        this.commitRepository = commitRepository;
    }

    @Operation(
            summary = "Get all Commits",
            description = "Get a list of Commits"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "List of Commits",
                    content = { @Content(schema = @Schema(implementation = Commit.class),
                    mediaType = "application/json")}
            ),
            @ApiResponse(responseCode = "404",
                    description = "Commits not found",
                    content = { @Content(schema = @Schema())}
            )
    })

    @GetMapping()
    public List<Commit> findAll() throws CommitNotFoundException {
        List<Commit> commits = commitRepository.findAll();
        if (commits.isEmpty()) {
            throw new CommitNotFoundException();
        }
        return commits;
    }

    @Operation(
            summary = "Get a Commit by Id",
            description = "Get a Commit by specifying its Id"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Commit with id",
                    content = { @Content(schema = @Schema(implementation = Commit.class),
                    mediaType = "application/json")}
            ),
            @ApiResponse(responseCode = "404",
                    description = "Commit not found",
                    content = { @Content(schema = @Schema())}
            )
    })

    @GetMapping("/{commitId}")
    public Commit findById(@PathVariable("commitId") String id) throws CommitNotFoundException {
        Optional<Commit> commit = commitRepository.findById(id);

        if (!commit.isPresent())
            throw new CommitNotFoundException();

        return commit.get();
    }
}