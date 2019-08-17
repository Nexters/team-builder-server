package com.nexters.teambuilder.idea.domain;

import com.nexters.teambuilder.tag.domain.Tag;

import javax.persistence.*;

@Entity
@IdClass(IdeaTagId.class)
public class IdeaTag {

    @Id
    @ManyToOne
    @JoinColumn(name = "idea_id")
    private Idea idea;

    @Id
    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;
}
