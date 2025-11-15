package com.example.demo.mapper;

import com.example.demo.dto.TopicDto;
import com.example.demo.entity.Topic;

public class TopicMapper {

    public static TopicDto toDto(Topic t) {
        if (t == null) return null;
        return TopicDto.builder()
                .id(t.getId())
                .name(t.getName())
                .slug(t.getSlug())
                .build();
    }

    public static Topic toEntity(TopicDto d) {
        if (d == null) return null;
        return Topic.builder()
                .id(d.getId())
                .name(d.getName())
                .slug(d.getSlug())
                .build();
    }
}
