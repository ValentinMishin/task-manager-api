package ru.valentin.model

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import javax.persistence.CollectionTable
import javax.persistence.Column
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.Table

@Entity
@Table(name = "client")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "username", unique = true)
    val clientName: String,

    @Column(name = "password")
    val clientPassword: String,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "client_role", joinColumns = [JoinColumn(name = "client_id")])
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    val roles: Set<ClientRole> = setOf(ClientRole.ROLE_USER)
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return roles.map { SimpleGrantedAuthority(it.name) }
    }

    override fun getPassword(): String = clientPassword
    override fun getUsername(): String = clientName
    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true
}

enum class ClientRole {
    ROLE_USER, ROLE_ADMIN
}