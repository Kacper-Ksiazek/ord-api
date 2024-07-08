package com.backend.ord.domain.entities

import com.backend.ord.domain.entities.abstracts.EntityBase
import com.backend.ord.enums.UserRole
import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.List

@Entity
@Table(name = "users")
class User(
    var name: String,

    @Column(nullable = false, unique = true)
    var email: String,

    @Column(nullable = false, unique = true)
    private var password: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    var role: UserRole = UserRole.USER,
) : EntityBase(), UserDetails {

    // Java Security methods:
    override fun getAuthorities(): Collection<GrantedAuthority> =
        listOf(SimpleGrantedAuthority(role.name))

    override fun getUsername(): String = email

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true

    override fun getPassword(): String = password

    fun setPassword(password: String) {
        this.password = password
    }
}
