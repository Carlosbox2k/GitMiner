package aiss.gitminer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code= HttpStatus.NOT_FOUND, reason="Issue not found")
public class IssueNotFoundException extends RuntimeException {
}
