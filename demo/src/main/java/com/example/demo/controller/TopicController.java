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

    // Lấy tất cả topics
    @GetMapping
    public List<TopicDto> getAll() {
        return topicService.getAllTopics();
    }

    // Lấy topic theo slug: /topics/{slug}
    @GetMapping("/{slug}")
    public TopicDto getBySlug(@PathVariable String slug) {
        return topicService.getTopicBySlug(slug);
    }

    // Lấy topic theo id: /topics/id/{id}
    @GetMapping("/id/{id}")
    public TopicDto getById(@PathVariable Integer id) {
        return topicService.getTopicById(id);
    }

    // Tạo mới topic
    @PostMapping
    public ResponseEntity<TopicDto> create(@RequestBody TopicDto dto) {
        TopicDto created = topicService.createTopic(dto);
        return ResponseEntity.status(201).body(created);
    }

    // Cập nhật topic
    @PutMapping("/{id}")
    public TopicDto update(@PathVariable Integer id, @RequestBody TopicDto dto) {
        return topicService.updateTopic(id, dto);
    }

    // Xoá topic theo id
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        topicService.deleteTopic(id);
        return ResponseEntity.ok().body("Topic đã được xoá!");
    }
}
