package aiss.gitminer.controller;

import aiss.gitminer.exception.CommentNotFoundException;
import aiss.gitminer.exception.CommitNotFoundException;
import aiss.gitminer.model.Commit;
import aiss.gitminer.repository.CommitRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Tag(name = " GitMiner Commit", description = "GitMiner Commit management API")
@RestController
@RequestMapping("/gitminer/commits")
public class CommitController {

    private CommitRepository commitRepository;

    @Autowired
    public CommitController(CommitRepository commitRepository) {
        this.commitRepository = commitRepository;
    }

    @GetMapping()
    public List<Commit> findAll() {
        return commitRepository.findAll();
    }

    @GetMapping("/{commit_id}")
    public Commit findById(@PathVariable String commit_id) throws CommitNotFoundException {
        Optional<Commit> commit = commitRepository.findById(commit_id);

        if (!commit.isPresent())
            throw new CommitNotFoundException();

        return commit.get();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Commit create(@RequestBody @Valid Commit commit) {
        Commit _commit = commitRepository.save(new Commit(commit.getId(), commit.getTitle(), commit.getMessage(),
                commit.getAuthorName(), commit.getAuthorEmail(), commit.getAuthoredDate(), commit.getWebUrl()));
        return _commit;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{commit_id}")
    public void update(@PathVariable String id, @RequestBody Commit updatedCommit) {
        Optional<Commit>  commitData = commitRepository.findById(id);
        Commit _commit = commitData.get();

        if (commitData.isPresent()) {
            _commit = commitData.get();
            _commit.setTitle(updatedCommit.getTitle());
            _commit.setMessage(updatedCommit.getMessage());
            _commit.setAuthorName(updatedCommit.getAuthorName());
            _commit.setAuthorEmail(updatedCommit.getAuthorEmail());
            _commit.setAuthoredDate(updatedCommit.getAuthoredDate());
        } else
            throw new CommitNotFoundException();

        commitRepository.save(_commit);
    }

    @DeleteMapping("/{commit_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String commit_id) throws CommitNotFoundException {
        if (commitRepository.existsById(commit_id))
            commitRepository.deleteById(commit_id);
        else
            throw new CommitNotFoundException();
    }
}
