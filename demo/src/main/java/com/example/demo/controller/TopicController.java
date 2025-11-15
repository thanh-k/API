package com.example.demo.controller;

import com.example.demo.dto.TopicDto;
import com.example.demo.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/topics")
@RequiredArgsConstructor
@CrossOrigin("*")
public class TopicController {

    private final TopicService topicService;

    @GetMapping
    public List<TopicDto> getAll() {
        return topicService.getAllTopics();
    }

    @GetMapping("/{slug}")
    public TopicDto getBySlug(@PathVariable String slug) {
        return topicService.getTopicBySlug(slug);
    }

    @PostMapping
    public ResponseEntity<TopicDto> create(@RequestBody TopicDto dto) {
        TopicDto created = topicService.createTopic(dto);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    public TopicDto update(@PathVariable Integer id, @RequestBody TopicDto dto) {
        return topicService.updateTopic(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        topicService.deleteTopic(id);
        return ResponseEntity.ok().body("Topic đã được xoá!");
    }

    @GetMapping("/id/{id}")
    public TopicDto getById(@PathVariable Integer id) {
        return topicService.getTopicById(id);
    }
}
