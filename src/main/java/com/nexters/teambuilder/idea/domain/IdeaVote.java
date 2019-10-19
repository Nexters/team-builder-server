package com.nexters.teambuilder.idea.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class IdeaVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer ideaId;
    private Integer sessionNumber;
    private String uuid;

    public IdeaVote(Integer ideaId, Integer sessionNumber, String uuid) {
        this.ideaId = ideaId;
        this.sessionNumber = sessionNumber;
        this.uuid = uuid;
    }
}
