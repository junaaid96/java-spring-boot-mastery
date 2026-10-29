package dev.junaaid96.mvc.web;

import dev.junaaid96.mvc.service.Programmer;
import dev.junaaid96.mvc.service.ProgrammerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// @RestController = @Controller + @ResponseBody: return values are written as JSON.
@RestController
@RequestMapping("/programmers")
public class ProgrammerController {

    // Constructor injection: dependencies are explicit, final, and easy to test (no @Autowired field needed).
    private final ProgrammerService service;

    public ProgrammerController(ProgrammerService service) { this.service = service; }

    // GET /programmers
    @GetMapping
    public List<Programmer> all() { return service.all(); }

    // GET /programmers/{id} -> 200 with body, or 404.
    @GetMapping("/{id}")
    public ResponseEntity<Programmer> one(@PathVariable int id) {
        return service.find(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // POST /programmers with a JSON body -> 201 Created.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Programmer create(@RequestBody Programmer p) { return service.save(p); }
}
