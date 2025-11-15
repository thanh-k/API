package com.example.demo.service;

import com.example.demo.dto.PostDto;
import com.example.demo.dto.PostFormDto;

import java.util.List;

public interface PostService {

    PostDto createFromJson(PostDto dto);

    PostDto createFromForm(PostFormDto form);

    // ⭐ Thêm API update bằng Form Data
    PostDto updateFromForm(Integer id, PostFormDto form);

    PostDto getById(Integer id);

    List<PostDto> getAll();

    PostDto update(Integer id, PostDto dto);

    void delete(Integer id);
}
