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
@Table(name = "employer_profiles")
public class EmployerProfile extends Profile {

    private String companyName;
    private String companyDescription;
    private String location;

    public EmployerProfile(User user) {
        super(user);
    }
}
