package org.jnjeaaaat.snms;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SnmsController {

    @GetMapping(value = "/", produces = "text/plain;charset=UTF-8")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("Snms 서비스 입니다.");
    }
}
