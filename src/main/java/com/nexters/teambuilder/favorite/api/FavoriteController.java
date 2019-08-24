package com.nexters.teambuilder.favorite.api;

import com.nexters.teambuilder.common.response.BaseResponse;
import com.nexters.teambuilder.favorite.api.dto.FavoriteRequest;
import com.nexters.teambuilder.favorite.api.dto.FavoriteResponse;
import com.nexters.teambuilder.favorite.domain.Favorite;
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

    @GetMapping("/{ideaId}")
    public BaseResponse<FavoriteResponse> get(@PathVariable Integer ideaId) {
        FavoriteResponse favorite = favoriteService.getFavorite(ideaId);

        return new BaseResponse<>(200,0,favorite);
    }

    @PostMapping
    public BaseResponse<FavoriteResponse> create(@AuthenticationPrincipal User user,
                                                 @RequestBody @Valid FavoriteRequest request) {
        FavoriteResponse favorite = favoriteService.createFavorite(user, request);
        return new BaseResponse<>(200, 0, favorite);
    }

    @DeleteMapping("/{ideaId}")
    public BaseResponse delete(@PathVariable Integer ideaId) {
        favoriteService.delete(ideaId);

        return new BaseResponse<>(200, 0, null);
    }
}
