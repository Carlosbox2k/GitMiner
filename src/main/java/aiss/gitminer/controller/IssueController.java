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
                .save(new Issue(issue.getId(), issue.getTitle(), issue.getDescription(), issue.getState(), issue.getCreatedAt(),
                        issue.getUpdatedAt(), issue.getClosedAt(), issue.getLabels(), issue.getAuthor(), issue.getAssignee(),
                        issue.getVotes(), issue.getComments()
                ));
        return _issue;
    }

    @PutMapping("/{issueId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateIssue(@PathVariable("issueId") String issueId, @Valid @RequestBody Issue updatedIssue){
        Optional<Issue> _issue = issueRepository.findById(issueId);
        Issue __issue = _issue.get();

        if (_issue.isPresent()) {
            __issue.setTitle(updatedIssue.getTitle());
            __issue.setDescription(updatedIssue.getDescription());
            __issue.setState(updatedIssue.getState());
            __issue.setCreatedAt(updatedIssue.getCreatedAt());
            __issue.setUpdatedAt(updatedIssue.getUpdatedAt());
            __issue.setClosedAt(updatedIssue.getClosedAt());
            __issue.setLabels(updatedIssue.getLabels());
            __issue.setAuthor(updatedIssue.getAuthor());
            __issue.setAssignee(updatedIssue.getAssignee());
            __issue.setVotes(updatedIssue.getVotes());
            __issue.setComments(updatedIssue.getComments());
        } else {
            throw new IssueNotFoundException();
        }
        issueRepository.save(__issue);
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
