package com.nexters.teambuilder.user.domain;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nexters.teambuilder.session.domain.SessionUser;
import javafx.geometry.Pos;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "ux_user_id", columnNames = "id")})
public class User implements UserDetails {
    public enum Role{
        ROLE_ADMIN, ROLE_USER
    }

    public enum Position{
        DESIGNER, DEVELOPER
    }

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "VARCHAR(191)",name = "uuid", unique = true, nullable = false)
    private String uuid;

    private String id;

    private String password;

    private String name;

    private Integer nextersNumber;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Position position;

    @CreationTimestamp
    private ZonedDateTime createdAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private Set<SessionUser> sessionUsers = new HashSet<>();

    @Builder
    public User(String id, String password, String name, Integer nextersNumber, Role role, Position position) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.nextersNumber = nextersNumber;
        this.role = role;
        this.position = position;
    }

    public void update(String password, Position position) {
        this.password = password;
        this.position = position;

    }

    @JsonIgnore
    private boolean authenticated = false;

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>();
    }


    @JsonIgnore
    @Override
    public String getPassword() {
        return password;
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return uuid;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }
}
