package aiss.gitminer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code= HttpStatus.NOT_FOUND, reason="Comment not found")
public class CommentNotFoundException extends RuntimeException {
}
