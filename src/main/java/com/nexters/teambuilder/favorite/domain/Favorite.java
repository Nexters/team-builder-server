package com.nexters.teambuilder.favorite.domain;

import com.nexters.teambuilder.favorite.api.dto.FavoriteRequest;
import com.nexters.teambuilder.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"uuid", "ideaId"})
})
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer favoriteId;

    private String uuid;

    private Integer ideaId;

    @Builder
    public Favorite(String uuid, Integer ideaId) {
        this.uuid = uuid;
        this.ideaId = ideaId;
    }

    public static Favorite of(User user, FavoriteRequest request) {
        return new Favorite(user.getUuid(), request.getIdeaId());
    }


}
