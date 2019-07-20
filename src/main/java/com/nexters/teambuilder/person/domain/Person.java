package com.nexters.teambuilder.person.domain;

import java.time.ZonedDateTime;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.nexters.teambuilder.person.api.dto.PersonRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.util.Assert;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Person {
    public enum Gender{
        MAN, WOOOMAN
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String name;

    private String nickname;

    private Integer age;

    @CreationTimestamp
    private ZonedDateTime bornAt;

    @Builder
    public Person(Gender gender, String name, String nickname, Integer age) {
        Assert.notNull(gender, "gender must not be null");
        Assert.hasLength(name, "name must not be empty");
        Assert.hasLength(nickname, "nikcname must not be empty");

        this.gender = gender;
        this.name = name;
        this.nickname = nickname;
        this.age = age;
    }

    public void update(PersonRequest request) {
        this.gender = request.getGender();
        this.name = request.getName();
        this.nickname = request.getNickname();
        this.age = request.getAge();
    }

    public static Person of(PersonRequest request) {
        return new Person(request.getGender(), request.getName(), request.getNickname(), request.getAge());
    }
}
