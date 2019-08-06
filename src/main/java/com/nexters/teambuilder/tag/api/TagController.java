package com.nexters.teambuilder.tag.api;

import java.util.List;
import javax.validation.Valid;

import com.nexters.teambuilder.common.response.BaseResponse;
import com.nexters.teambuilder.tag.api.dto.TagRequest;
import com.nexters.teambuilder.tag.api.dto.TagResponse;
import com.nexters.teambuilder.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("tags")
public class TagController {
    private final TagService tagService;

    @PostMapping
    public BaseResponse<TagResponse> create(@RequestBody @Valid TagRequest tagRequest) {
        TagResponse tag = tagService.createTag(tagRequest);
        return new BaseResponse<>(200, 0, tag);
    }

    @GetMapping("{tagId}")
    public BaseResponse<TagResponse> get(@PathVariable Integer tagId) {
        TagResponse tag = tagService.getTag(tagId);
        return new BaseResponse<>(200, 0, tag);
    }

    @GetMapping
    public BaseResponse<List<TagResponse>> list() {
        List<TagResponse> tags = tagService.getTagList();
        return new BaseResponse<>(200, 0, tags);
    }

    @PutMapping("{tagId}")
    public BaseResponse<TagResponse> update(@PathVariable Integer tagId, @RequestBody TagRequest request) {
        TagResponse tag = tagService.updateTag(tagId, request);
        return new BaseResponse<>(200, 0, tag);
    }

    @DeleteMapping("{tagId}")
    public BaseResponse delete(@PathVariable Integer tagId){
        tagService.delete(tagId);
        return new BaseResponse<>(200, 0, null);
    }

}
