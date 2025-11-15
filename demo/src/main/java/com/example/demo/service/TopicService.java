package com.example.demo.service;

import com.example.demo.dto.TopicDto;

import java.util.List;

public interface TopicService {
    TopicDto createTopic(TopicDto dto);
    TopicDto updateTopic(Integer id, TopicDto dto);
    void deleteTopic(Integer id);
    TopicDto getTopicById(Integer id);
    TopicDto getTopicBySlug(String slug);
    List<TopicDto> getAllTopics();
}
