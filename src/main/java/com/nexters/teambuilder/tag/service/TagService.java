package com.nexters.teambuilder.tag.service;

import java.util.List;
import java.util.stream.Collectors;

import com.nexters.teambuilder.tag.api.dto.TagRequest;
import com.nexters.teambuilder.tag.api.dto.TagResponse;
import com.nexters.teambuilder.tag.domain.Tag;
import com.nexters.teambuilder.tag.domain.TagRepository;
import com.nexters.teambuilder.tag.exception.TagNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    public TagResponse createTag(TagRequest request) {
        Tag tag = tagRepository.save(Tag.of(request));

        return TagResponse.of(tagRepository.save(tag));
    }

    public TagResponse getTag(Integer tagId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new TagNotFoundException(tagId));
        return TagResponse.of(tag);
    }

    public TagResponse updateTag(Integer ideaId, TagRequest request) {
        Tag tag = tagRepository.findById(ideaId)
                .orElseThrow(() -> new TagNotFoundException(ideaId));

        tag.update(request);

        return TagResponse.of(tagRepository.save(tag));
    }

    public List<TagResponse> getTagList() {
        List<Tag> tagList = tagRepository.findAll();
        return tagList.stream().map(TagResponse::of).collect(Collectors.toList());
    }

    public void delete(Integer tagId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new TagNotFoundException(tagId));
        tagRepository.delete(tag);
    }
}
