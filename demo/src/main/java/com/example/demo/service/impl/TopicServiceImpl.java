package com.example.demo.service.impl;

import com.example.demo.dto.TopicDto;
import com.example.demo.entity.Topic;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.TopicMapper;
import com.example.demo.repository.TopicRepository;
import com.example.demo.service.TopicService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;

    @Override
    public TopicDto createTopic(TopicDto dto) {

        // Tạo slug nếu không có
        if (dto.getSlug() == null || dto.getSlug().isBlank()) {
            dto.setSlug(generateSlug(dto.getName()));
            String base = dto.getSlug();
            int i = 1;
            while (topicRepository.existsBySlug(dto.getSlug())) {
                dto.setSlug(base + "-" + i++);
            }
        } else {
            // Nếu client truyền slug lên thì kiểm tra trùng
            if (topicRepository.existsBySlug(dto.getSlug())) {
                throw new IllegalArgumentException("Slug đã tồn tại");
            }
        }

        Topic t = TopicMapper.toEntity(dto);
        Topic saved = topicRepository.save(t);
        return TopicMapper.toDto(saved);
    }

    @Override
    public TopicDto updateTopic(Integer id, TopicDto dto) {
        Topic existing = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id: " + id));

        existing.setName(dto.getName());

        // Nếu có slug truyền lên và khác slug cũ -> kiểm tra trùng rồi cập nhật
        if (dto.getSlug() != null && !dto.getSlug().isBlank() && !dto.getSlug().equals(existing.getSlug())) {
            if (topicRepository.existsBySlug(dto.getSlug())) {
                throw new IllegalArgumentException("Slug đã tồn tại");
            }
            existing.setSlug(dto.getSlug());
        }

        Topic updated = topicRepository.save(existing);
        return TopicMapper.toDto(updated);
    }

    @Override
    public void deleteTopic(Integer id) {
        if (!topicRepository.existsById(id)) {
            throw new ResourceNotFoundException("Topic không tồn tại!");
        }
        topicRepository.deleteById(id);
    }

    @Override
    public TopicDto getTopicById(Integer id) {
        Topic t = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id: " + id));
        return TopicMapper.toDto(t);
    }

    @Override
    public TopicDto getTopicBySlug(String slug) {
        Topic t = topicRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found with slug: " + slug));
        return TopicMapper.toDto(t);
    }

    @Override
    public List<TopicDto> getAllTopics() {
        return topicRepository.findAll()
                .stream()
                .map(TopicMapper::toDto)
                .collect(Collectors.toList());
    }

    // helper: tạo slug đơn giản (loại dấu, khoảng trắng -> '-')
    private String generateSlug(String input) {
        if (input == null) return null;

        String nowhitespace = input.trim()
                .replaceAll("\\s+", "-")
                .toLowerCase(Locale.ROOT);

        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{M}", ""); // bỏ dấu
        normalized = normalized.replaceAll("[^a-z0-9\\-]", ""); // chỉ giữ a-z, 0-9, -

        return normalized;
    }
}
