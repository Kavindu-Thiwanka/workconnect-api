package com.workconnect.api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "worker_profiles")
public class WorkerProfile extends Profile {

    private String firstName;
    private String lastName;
    private String location;
    private String skills;
    private String experience;
    private String availability;

    public WorkerProfile(User user) {
        super(user);
    }
}
