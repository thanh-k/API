package com.example.demo.controller;

import com.example.demo.dto.PostDto;
import com.example.demo.dto.PostFormDto;
import com.example.demo.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService service;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostDto> createJson(@RequestBody PostDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createFromJson(dto));
    }

    @PostMapping(path = "/form", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostDto> createForm(@ModelAttribute PostFormDto form) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createFromForm(form));
    }

    // ⭐ API Update bằng FORM-DATA + FILE
    @PutMapping(path = "/form/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostDto> updateForm(
            @PathVariable Integer id,
            @ModelAttribute PostFormDto form) {
        return ResponseEntity.ok(service.updateFromForm(id, form));
    }

    @GetMapping
    public ResponseEntity<List<PostDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDto> update(@PathVariable Integer id, @RequestBody PostDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
