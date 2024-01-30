package com.partyhub.PartyHub.entities;


import com.partyhub.PartyHub.util.PromoCodeGenerator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String email;
    private String password;
    private String promoCode;
    @OneToOne
    private UserDetails userDetails;

    private boolean verified = false;
    private UUID verificationToken;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<Role> roles = new ArrayList<>();

    public void generatePromoCode() {
        if (userDetails != null && userDetails.getFullName() != null) {
            this.promoCode = PromoCodeGenerator.generatePromoCode(userDetails.getFullName());
        }
    }

}
