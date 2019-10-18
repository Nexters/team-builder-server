package com.nexters.teambuilder.favorite.api;

import com.fasterxml.jackson.annotation.JsonView;
import com.nexters.teambuilder.common.response.BaseResponse;
import com.nexters.teambuilder.common.view.Views;
import com.nexters.teambuilder.favorite.api.dto.FavoriteRequest;
import com.nexters.teambuilder.favorite.api.dto.FavoriteResponse;
import com.nexters.teambuilder.favorite.service.FavoriteService;
import com.nexters.teambuilder.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/apis/favorites")
public class FavoriteController {
    private final FavoriteService favoriteService;

    @GetMapping
    @JsonView(Views.List.class)
    public BaseResponse<List<FavoriteResponse>> list(@AuthenticationPrincipal User user){
        List<FavoriteResponse> favorites = favoriteService.getFavoriteList(user.getUuid());

        return new BaseResponse<>(200,0,favorites);
    }

    @GetMapping("/{ideaId}")
    public BaseResponse<FavoriteResponse> get(@AuthenticationPrincipal User user,
                                              @PathVariable Integer ideaId) {
        FavoriteResponse favorite = favoriteService.getFavorite(user, ideaId);

        return new BaseResponse<>(200,0, favorite);
    }

    @PostMapping
    public BaseResponse<FavoriteResponse> create(@AuthenticationPrincipal User user,
                                                 @RequestBody @Valid FavoriteRequest request) {
        FavoriteResponse favorite = favoriteService.createFavorite(user, request);
        return new BaseResponse<>(200, 0, favorite);
    }

    @DeleteMapping("/{ideaId}")
    public BaseResponse delete(@AuthenticationPrincipal User user,
                               @PathVariable Integer ideaId) {
        favoriteService.delete(user, ideaId);

        return new BaseResponse<>(200, 0, null);
    }
}
