package com.nexters.teambuilder.favorite.service;

import com.nexters.teambuilder.favorite.api.dto.FavoriteRequest;
import com.nexters.teambuilder.favorite.api.dto.FavoriteResponse;
import com.nexters.teambuilder.favorite.domain.Favorite;
import com.nexters.teambuilder.favorite.domain.FavoriteRepository;
import com.nexters.teambuilder.favorite.exception.FavoriteNotFoundException;
import com.nexters.teambuilder.idea.domain.IdeaRepository;
import com.nexters.teambuilder.idea.exception.IdeaNotFoundException;
import com.nexters.teambuilder.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final IdeaRepository ideaRepository;

    public FavoriteResponse getFavorite(User user, Integer ideaId){
        Favorite favorite = favoriteRepository.findFavoriteByIdeaIdAndUuid(ideaId, user.getUuid())
                .orElseThrow(() -> new FavoriteNotFoundException(ideaId));

        return FavoriteResponse.of(favorite);
    }

    public List<FavoriteResponse> getFavoriteList(String uuid) {
        List<Favorite> favoriteList = favoriteRepository.findAllByUuid(uuid);

        return favoriteList.stream()
                .map(FavoriteResponse::of).collect(Collectors.toList());
    }

    public FavoriteResponse createFavorite(User user, FavoriteRequest request) {
        if(!ideaRepository.findByIdeaId(request.getIdeaId())){
            throw new IdeaNotFoundException(request.getIdeaId());
        }

        return FavoriteResponse.of(favoriteRepository
                .save(Favorite.of(user, request)));
    }

    public void delete(User user, Integer ideaId) {
        Favorite favorite = favoriteRepository.findFavoriteByIdeaIdAndUuid(ideaId, user.getUuid())
                .orElseThrow(() -> new FavoriteNotFoundException(ideaId));

        favoriteRepository.delete(favorite);
    }
}
