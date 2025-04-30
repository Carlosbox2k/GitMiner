package aiss.gitminer.controller;

import aiss.gitminer.model.Issue;
import aiss.gitminer.repository.IssueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/gitminer/issues")
public class IssueController {

    @Autowired
    IssueRepository issueRepository;


    @GetMapping
    public List<Issue> getIssues() {
        return issueRepository.findAll();
    }

    @GetMapping("/issue/{issueId}")
    public Issue getIssue(@PathVariable("issueId") String issueId) {
        Optional<Issue> issue = issueRepository.findById(issueId);
        if (issue.isPresent()) {
            return issue.get();
        }
        return null;
    }

    @PostMapping("/issue")
    @ResponseStatus(HttpStatus.CREATED)
    public Issue createIssue(@Valid @RequestBody Issue issue){
        Issue _issue = issueRepository
                .save(new Issue(issue.get))
    }



}
