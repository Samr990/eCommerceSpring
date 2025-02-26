package com.ecom.ecomwebsite.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails, Serializable {
    
    private static final long serialVersionUID = 1L; // ✅ Required for Serializable classes

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    
    @Column(name = "passwd", nullable = false)
    private String passwd; // ✅ Make sure to store encrypted passwords
    
    @Column(name = "user_name", nullable = false)
    private String userName;
    
    @Column(name = "address")
    private String address;
    
    @Enumerated(EnumType.STRING) // ✅ Stores as a string in DB (CUSTOMER, SELLER, ADMIN)
    @Column(name = "role", nullable = false)
    private RoleType role;
    
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Cart cart;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orders;

    // ✅ Implemented Methods for Spring Security (UserDetails)

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));  // ✅ Removed extra "ROLE_" prefix (not needed)
    }

    @Override
    public String getPassword() {
        return passwd; // ✅ Ensure this is encrypted when saved
    }

    @Override
    public String getUsername() {
        return email; // ✅ Use email for authentication
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true; // ✅ Modify this if you introduce an `isActive` field
    }
}
