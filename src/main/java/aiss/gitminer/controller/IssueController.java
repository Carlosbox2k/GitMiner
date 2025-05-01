package aiss.gitminer.controller;

import aiss.gitminer.exception.IssueNotFoundException;
import aiss.gitminer.model.Comment;
import aiss.gitminer.model.Issue;
import aiss.gitminer.repository.IssueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/gitminer/issues")
public class IssueController {

    @Autowired
    IssueRepository issueRepository;


    @GetMapping
    public List<Issue> getIssues(@RequestParam(required = false) String state) {    // TODO Add Pageable, lab 8
        if (state != null) {
            return issueRepository.findAll().stream().filter(issue -> issue.getState().equals(state)).collect(Collectors.toList());
        }
        return issueRepository.findAll();
    }

    @GetMapping("/{issueId}")
    public Issue getIssue(@PathVariable("issueId") String issueId) {
        Optional<Issue> issue = issueRepository.findById(issueId);
        if (issue.isPresent()) {
            return issue.get();
        }
        return null;
    }

    @GetMapping("/{issueId}/comments")
    public List<Comment> getIssueComments(@PathVariable("issueId") String issueId) {
        Issue issue = getIssue(issueId);
        return issue.getComments();
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Issue createIssue(@Valid @RequestBody Issue issue){
        Issue _issue = issueRepository
                .save(new Issue(issue.getTitle(), issue.getDescription(), issue.getState(), issue.getCreatedAt(),
                        issue.getUpdatedAt(), issue.getClosedAt(), issue.getLabels(), issue.getAuthor(), issue.getAssignee(),
                        issue.getVotes(), issue.getComments()
                ));
        return _issue;
    }

    @PutMapping("/{issueId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateIssue(@PathVariable("issueId") String issueId, @Valid @RequestBody Issue issue){
        Optional<Issue> _issue = issueRepository.findById(issueId);
        if (_issue.isPresent()) {
            Issue __issue = _issue.get();
            __issue.setTitle(issue.getTitle());
            __issue.setDescription(issue.getDescription());
            __issue.setState(issue.getState());
            __issue.setCreatedAt(issue.getCreatedAt());
            __issue.setUpdatedAt(issue.getUpdatedAt());
            __issue.setClosedAt(issue.getClosedAt());
            __issue.setLabels(issue.getLabels());
            __issue.setAuthor(issue.getAuthor());
            __issue.setAssignee(issue.getAssignee());
            __issue.setVotes(issue.getVotes());
            __issue.setComments(issue.getComments());
        }
        throw new IssueNotFoundException();
    }

    @DeleteMapping("/{issueId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String issueId) throws IssueNotFoundException {
        if (issueRepository.existsById(issueId))
            issueRepository.deleteById(issueId);
        else
            throw new IssueNotFoundException();
    }
}
