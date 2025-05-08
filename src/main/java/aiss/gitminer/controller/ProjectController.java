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
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Tag(name = "GitMiner Project", description = "GitMiner Project management API")
@RestController
@RequestMapping("/gitminer/projects")
public class ProjectController {

    private RestTemplate restTemplate;
    private ProjectRepository projectRepository;
    /*
    private CommitRepository commitRepository;
    private IssueRepository issueRepository;
    private CommentRepository commentRepository;
    private UserRepository userRepository;
    */

    // CommitRepository commitRepository, IssueRepository issueRepository, CommentRepository commentRepository, UserRepository userRepository
    @Autowired
    public ProjectController(RestTemplate restTemplate, ProjectRepository projectRepository) {
        this.restTemplate = restTemplate;
        this.projectRepository = projectRepository;
        /*
        this.commitRepository = commitRepository;
        this.issueRepository = issueRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
         */
    }

    @Operation(
            summary = "Get all projects",
            description = "Get all projects saved at the DB"
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
            summary = "Get a project by Id",
            description = "Get a project by specifying its Id"
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
            summary = "Get all issues of a project",
            description = "Get a list of issues of a project")
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
            summary = "Update a project",
            description = "Update a project by specifying its Id"
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
    @PutMapping("/{projectId}")
    public void update(@PathVariable("projectId") String id, @Valid @RequestBody Project project) throws ProjectNotFoundException {
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
            summary = "Update a BitBucket project",
            description = "Update a BitBucket project by specifying its workspace and repo slug"
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
            )
    })

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/bitbucket/{workspace}/{repoSlug}")
    public void updateBitBucket(@PathVariable String workspace, @PathVariable String repoSlug,
                                @RequestParam(defaultValue = "5")String nCommits,
                                @RequestParam(defaultValue = "5")String nIssues,
                                @RequestParam(defaultValue = "2")String maxPages) throws ProjectNotFoundException {
        String uri = "http://localhost:8081/bitbucket" + workspace + "/" + repoSlug + "?nCommits=" + nCommits + "&nIssues=" + nIssues + "&maxPages=" + maxPages;
        ResponseEntity<Project> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, null, Project.class);
        Project project = responseEntity.getBody();
        update(project.id, project);
    }

    @Operation(
            summary = "Update a GitHub project",
            description = "Update a GitHub project by specifying its owner and repo"
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
            )
    })

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/github/{owner}/{repo}")
    public void updateGitHub(@PathVariable String owner, @PathVariable String repo,
                             @RequestParam(defaultValue="2")String sinceCommits,
                             @RequestParam(defaultValue="20") String sinceIssues,
                             @RequestParam(defaultValue="2")String maxPages) throws ProjectNotFoundException {
        String uri = "http://localhost:8082/github/" + owner + "/" + repo + "?sinceCommits=" + sinceCommits + "&sinceIssues=" + sinceIssues + "&maxPages=" + maxPages;
        ResponseEntity<Project> response = restTemplate.exchange(uri, HttpMethod.GET , null,Project.class);
        Project project = response.getBody();
        update(project.id, project);
    }

    @Operation(
            summary = "Delete a project by id",
            description = "Delete a project object by specifying its Id")
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
