package com.example.demo.mapper;

import com.example.demo.dto.PostDto;
import com.example.demo.entity.Post;

public class PostMapper {
    public static PostDto toDto(Post p) {
        if (p == null) return null;
        return PostDto.builder()
                .id(p.getId())
                .title(p.getTitle())
                .slug(p.getSlug())
                .content(p.getContent())
                .imageUrl(p.getImageUrl())
                .topicId(p.getTopic() != null ? p.getTopic().getId() : null)
                .topicName(p.getTopic() != null ? p.getTopic().getName() : null)
                .createdAt(p.getCreatedAt())
                .build();
    }
}
