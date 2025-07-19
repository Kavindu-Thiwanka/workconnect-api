package com.workconnect.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "worker_profiles")
public class WorkerProfile extends Profile {

    private String firstName;
    private String lastName;
    private String location;
    private String experience;
    private String availability;
    private String profileImageUrl;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
            name = "worker_profile_skills",
            joinColumns = @JoinColumn(name = "worker_profile_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> skills = new HashSet<>();

    public WorkerProfile(User user) {
        super(user);
    }
}
