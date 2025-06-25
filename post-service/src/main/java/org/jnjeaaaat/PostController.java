package org.jnjeaaaat;

import jakarta.servlet.http.HttpServletRequest;
import org.jnjeaaaat.dto.TestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.jnjeaaaat.global.util.LogUtil.logInfo;

@RequestMapping("/api/posts")
@RestController
public class PostController {

    @GetMapping("")
    public ResponseEntity<TestDto> postTest(HttpServletRequest request) {
        logInfo(request, "Post Test Success");
        return ResponseEntity.ok(
                new TestDto("Post Dto Share Success")
        );
    }

}
