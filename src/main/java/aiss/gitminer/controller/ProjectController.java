package aiss.gitminer.controller;

import aiss.gitminer.exception.ProjectNotFoundException;
import aiss.gitminer.model.*;
import aiss.gitminer.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

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

    @GetMapping
    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    @GetMapping("/{id}")
    public Project findOne(@PathVariable String id) {
        Optional<Project> project = projectRepository.findById(id);
        if(!project.isPresent()){
            throw new ProjectNotFoundException();
        }
        return project.get();
    }

    @GetMapping("/{id}/commits")
    public List<Commit> findAllCommits(@PathVariable String id)  {
        Project project = findOne(id);
        return project.getCommits();
    }

    @GetMapping("/{id}/issues")
    public List<Issue> findAllIssues(@PathVariable String id){
        Project project = findOne(id);
        return project.getIssues();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Project create(@Valid @RequestBody Project project) {
        Project savedProject = projectRepository.save(new Project(project.getId(), project.getName(),project.getWebUrl(), project.getCommits(), project.getIssues()));
        for (Commit commit : project.getCommits()) {
            commitRepository.save(new Commit(commit.getId(), commit.getTitle(), commit.getMessage(), commit.getAuthorName(), commit.getAuthorEmail(), commit.getAuthoredDate(), commit.getWebUrl()));
        }
        for (Issue issue : project.getIssues()) {
            issueRepository.save(new Issue(issue.getId(),issue.getTitle(), issue.getDescription(), issue.getState(), issue.getCreatedAt(), issue.getUpdatedAt(), issue.getClosedAt(), issue.getLabels(), issue.getAuthor(), issue.getAssignee(), issue.getVotes(), issue.getComments()));
            User author = issue.getAuthor();
            userRepository.save(new User(author.getId(), author.getUsername(), author.getName(), author.getAvatarUrl(), author.getWebUrl()));
            User assignee = issue.getAssignee();
            if (assignee != null) {
                userRepository.save(new User(assignee.getId() ,assignee.getUsername(), assignee.getName(), assignee.getAvatarUrl(), assignee.getWebUrl()));
            }
            for (Comment comment : issue.getComments()) {
                commentRepository.save(new Comment(comment.getId(),comment.getBody(), comment.getAuthor(), comment.getCreatedAt(), comment.getUpdatedAt()));
                User user = comment.getAuthor();
                userRepository.save(new User(user.getId(), user.getUsername(), user.getName(), user.getAvatarUrl(), user.getWebUrl()));
            }
        }

        return savedProject;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    public void update(@PathVariable String id, @Valid @RequestBody Project project) {
        Optional<Project> projectData = projectRepository.findById(id);

        Project _project = projectData.get();

        if(projectData.isPresent()) {
            _project.setName(project.getName());
            _project.setWebUrl(project.getWebUrl());
        }else {
            throw new ProjectNotFoundException();
        }
        projectRepository.save(_project);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        if(projectRepository.existsById(id))
            projectRepository.deleteById(id);
        else
            throw new ProjectNotFoundException();
    }
}
