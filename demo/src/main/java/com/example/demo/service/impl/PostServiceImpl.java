package com.example.demo.service.impl;

import com.example.demo.dto.PostDto;
import com.example.demo.dto.PostFormDto;
import com.example.demo.entity.Post;
import com.example.demo.entity.Topic;
import com.example.demo.mapper.PostMapper;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.TopicRepository;
import com.example.demo.service.FileStorageService;
import com.example.demo.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final TopicRepository topicRepository;
    private final FileStorageService fileStorageService;

    @Override
    public PostDto createFromJson(PostDto dto) {

        if (dto.getSlug() == null || dto.getSlug().isBlank()) {
            dto.setSlug(generateSlug(dto.getTitle()));
            int i = 1;
            String base = dto.getSlug();
            while (postRepository.existsBySlug(dto.getSlug())) {
                dto.setSlug(base + "-" + i++);
            }
        } else if (postRepository.existsBySlug(dto.getSlug())) {
            throw new IllegalArgumentException("Slug đã tồn tại");
        }

        Post p = Post.builder()
                .title(dto.getTitle())
                .slug(dto.getSlug())
                .content(dto.getContent())
                .imageUrl(dto.getImageUrl())
                .build();

        if (dto.getTopicId() != null) {
            Topic t = topicRepository.findById(dto.getTopicId())
                    .orElseThrow(() -> new RuntimeException("Topic không tồn tại"));
            p.setTopic(t);
        }

        return PostMapper.toDto(postRepository.save(p));
    }

    @Override
    public PostDto createFromForm(PostFormDto form) {
        Post p = new Post();
        p.setTitle(form.getTitle());

        if (form.getSlug() == null || form.getSlug().isBlank()) {
            p.setSlug(generateSlug(form.getTitle()));
        } else {
            p.setSlug(form.getSlug());
        }

        int i = 1;
        String base = p.getSlug();
        while (postRepository.existsBySlug(p.getSlug())) {
            p.setSlug(base + "-" + i++);
        }

        p.setContent(form.getContent());

        if (form.getFile() != null && !form.getFile().isEmpty()) {
            String imageUrl = fileStorageService.store(form.getFile());
            p.setImageUrl(imageUrl);
        }

        if (form.getTopicId() != null) {
            Topic t = topicRepository.findById(form.getTopicId())
                    .orElseThrow(() -> new RuntimeException("Topic không tồn tại"));
            p.setTopic(t);
        }

        return PostMapper.toDto(postRepository.save(p));
    }

    // ⭐⭐⭐ HÀM UPDATE FORM DATA (CÓ FILE)
    @Override
    public PostDto updateFromForm(Integer id, PostFormDto form) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        post.setTitle(form.getTitle());
        post.setSlug(form.getSlug());
        post.setContent(form.getContent());

        if (form.getTopicId() != null) {
            Topic topic = topicRepository.findById(form.getTopicId())
                    .orElseThrow(() -> new RuntimeException("Topic not found"));
            post.setTopic(topic);
        }

        if (form.getFile() != null && !form.getFile().isEmpty()) {
            String imageUrl = fileStorageService.store(form.getFile());
            post.setImageUrl(imageUrl);
        }

        return PostMapper.toDto(postRepository.save(post));
    }

    @Override
    public PostDto getById(Integer id) {
        return PostMapper.toDto(
                postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"))
        );
    }

    @Override
    public List<PostDto> getAll() {
        return postRepository.findAll()
                .stream()
                .map(PostMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PostDto update(Integer id, PostDto dto) {
        Post exist = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        exist.setTitle(dto.getTitle());
        exist.setContent(dto.getContent());
        exist.setImageUrl(dto.getImageUrl());

        if (dto.getTopicId() != null) {
            Topic t = topicRepository.findById(dto.getTopicId())
                    .orElseThrow(() -> new RuntimeException("Topic not found"));
            exist.setTopic(t);
        }

        return PostMapper.toDto(postRepository.save(exist));
    }

    @Override
    public void delete(Integer id) {
        postRepository.deleteById(id);
    }

    private String generateSlug(String input) {
        if (input == null) return "";
        String normalized = Normalizer.normalize(input.toLowerCase(Locale.ROOT), Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{M}", "");
        normalized = normalized.replaceAll("[^a-z0-9\\- ]", "");
        normalized = normalized.trim().replaceAll("\\s+", "-");
        normalized = normalized.replaceAll("[^a-z0-9\\-]", "");
        return normalized;
    }
}
