package com.ord.core.user.model

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.Instant
import java.util.*

@Table(name = "users")
data class UserEntity(
    @Id
    val id: UUID? = null,

    @Column("name")
    var name: String,

    @Column("email")
    var email: String,

    @Column("password")
    private var password: String,

    @Column("native_language")
    var nativeLanguage: LanguageName,

    @Column("created_at")
    var createdAt: Instant = Instant.now(),

    @Column("updated_at")
    var updatedAt: Instant = Instant.now()
) : UserDetails {

    // Java Security methods:
    override fun getAuthorities(): Collection<GrantedAuthority> =
        listOf(SimpleGrantedAuthority("USER"))

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