package com.appsdeveloperblog.app.ws.io.entity;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name="roles")
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @ManyToMany(mappedBy = "roles")
    private Collection<UserEntity> users;

    @ManyToMany(cascade = { CascadeType.PERSIST }, fetch = FetchType.EAGER)
    @JoinTable(name="roles_authorities",
            joinColumns = @JoinColumn(name="roles_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name="authorities_id", referencedColumnName = "id"))
    private Collection<AuthorityEntity> authorities;

    public RoleEntity(String name) {
        this.name = name;
    }

    public RoleEntity() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<UserEntity> getUsers() {
        return users;
    }

    public void setUsers(Collection<UserEntity> users) {
        this.users = users;
    }

    public Collection<AuthorityEntity> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Collection<AuthorityEntity> authorities) {
        this.authorities = authorities;
    }
}
