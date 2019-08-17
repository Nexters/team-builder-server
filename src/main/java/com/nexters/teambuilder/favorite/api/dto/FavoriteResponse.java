package com.nexters.teambuilder.favorite.api.dto;

import com.nexters.teambuilder.favorite.domain.Favorite;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteResponse {

    private Integer favoriteId;

    private Integer ideaId;

    private String uuid;

    public static FavoriteResponse of(Favorite favorite) {
        return new FavoriteResponse(favorite.getFavoriteId(), favorite.getIdeaId(), favorite.getUuid());
    }
}
