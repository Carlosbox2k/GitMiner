package aiss.gitminer.controller;

import aiss.gitminer.exception.CommitNotFoundException;
import aiss.gitminer.exception.IssueNotFoundException;
import aiss.gitminer.exception.ProjectNotFoundException;
import aiss.gitminer.model.*;
import aiss.gitminer.repository.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Tag(name = "GitMiner Project", description = "GitMiner Project management API")
@RestController
@RequestMapping("/gitminer/projects")
public class ProjectController {

    private ProjectRepository projectRepository;
    private CommitRepository commitRepository;
    private IssueRepository issueRepository;
    private CommentRepository commentRepository;
    private UserRepository userRepository;

    @Autowired
    public ProjectController(ProjectRepository projectRepository, CommitRepository commitRepository, IssueRepository issueRepository, CommentRepository commentRepository, UserRepository userRepository) {
        this.projectRepository=projectRepository;
        this.commitRepository = commitRepository;
        this.issueRepository = issueRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    @Operation(
            summary = "Get all Projects",
            description = "Get all Projects saved at the DB"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "ALl Projects",
                    content = { @Content(schema = @Schema(implementation = Project.class),
                            mediaType = "application/json")}
            ),
            @ApiResponse(responseCode = "404",
                    description="Project not found ",
                    content = { @Content(schema = @Schema())}
            )
    })

    @GetMapping
    public List<Project> findAll() throws ProjectNotFoundException {
        List<Project> projects = projectRepository.findAll();
        if (projects.isEmpty())
            throw new ProjectNotFoundException();
        return projects;
    }

    @Operation(
            summary = "Get a Project by Id",
            description = "Get a Project by specifying its Id"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Project with Id",
                    content = { @Content(schema = @Schema(implementation = Project.class),
                            mediaType = "application/json")}
            ),
            @ApiResponse(responseCode = "404",
                    description="Project not found",
                    content = { @Content(schema = @Schema())}
            )
    })

    @GetMapping("/{projectId}")
    public Project findById(@PathVariable("projectId") String id) throws ProjectNotFoundException {
        Optional<Project> project = projectRepository.findById(id);
        if(!project.isPresent())
            throw new ProjectNotFoundException();
        return project.get();
    }

    @Operation(
            summary = "Get all commits of a project",
            description = "Get a list of commits of a project")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "List of commits of a project",
                    content = { @Content(schema = @Schema(implementation = Comment.class),
                    mediaType = "application/json")}
            ),
            @ApiResponse(responseCode = "404",
                    description="Commits not found",
                    content = { @Content(schema = @Schema())}
            )
    })

    @GetMapping("/{projectId}/commits")
    public List<Commit> findAllCommits(@PathVariable("projectId") String id) throws CommitNotFoundException {
        Project project = findById(id);
        List<Commit> commits = project.getCommits();
        if (commits.isEmpty())
            throw new CommitNotFoundException();
        return commits;
     }

    @Operation(
            summary = "Get all Issues of a Project",
            description = "Get a list of Issues of a Project")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "List of Issues of a Project",
                    content = { @Content(schema = @Schema(implementation = Comment.class),
                    mediaType = "application/json")}
            ),
            @ApiResponse(responseCode = "404",
                    description="Issues not found",
                    content = { @Content(schema = @Schema())}
            )
    })

    @GetMapping("/{projectId}/issues")
    public List<Issue> findAllIssues(@PathVariable("projectId") String id) throws IssueNotFoundException {
        Project project = findById(id);
        List<Issue> issues = project.getIssues();
        if (issues.isEmpty())
            throw new IssueNotFoundException();
        return issues;
    }

    @Operation(
            summary = "Create a project",
            description = "Create a project to be saved at the DB"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    description = "Project created",
                    content = { @Content(schema = @Schema(implementation = Project.class),
                            mediaType = "application/json")}
            ),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request",
                    content = { @Content(schema = @Schema()) })
    })

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Project create(@Valid @RequestBody Project project) {
        Project savedProject = projectRepository.save(new Project(project.getId(), project.getName(),project.getWebUrl(), project.getCommits(), project.getIssues()));
        return savedProject;
    }

    @Operation(
            summary = "Update a Project",
            description = "Update a Project by specifying its Id"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204",
                    description = "Project updated",
                    content = { @Content(schema = @Schema(),
                    mediaType = "application/json")}
            ),
            @ApiResponse(responseCode = "404",
                    description="Project not found",
                    content = { @Content(schema = @Schema())}
            ),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request",
                    content = { @Content(schema = @Schema())}
            )
    })

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    public void update(@PathVariable String id, @Valid @RequestBody Project project) throws ProjectNotFoundException {
        Optional<Project> projectData = projectRepository.findById(id);

        Project _project;

        if(projectData.isPresent()) {
            _project = projectData.get();
            _project.setName(project.getName());
            _project.setWebUrl(project.getWebUrl());
            _project.setCommits(project.getCommits());
            _project.setIssues(project.getIssues());
        } else
            throw new ProjectNotFoundException();
        projectRepository.save(_project);
    }

    @Operation(
            summary = "Delete a Project by id",
            description = "Delete a Project object by specifying its Id")
    @ApiResponses({
            @ApiResponse(responseCode = "204",
                    description = "Project deleted",
                    content = {@Content(schema = @Schema(),
                    mediaType = "application/json")}
            ),
            @ApiResponse(responseCode = "404",
                    description="Project not found",
                    content = { @Content(schema = @Schema())}
            )

    })

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{projectId}")
    public void delete(@PathVariable("projectId") String id) throws ProjectNotFoundException {
        if(projectRepository.existsById(id))
            projectRepository.deleteById(id);
        else
            throw new ProjectNotFoundException();
    }
}
