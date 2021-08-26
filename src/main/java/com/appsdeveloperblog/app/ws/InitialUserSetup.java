package com.appsdeveloperblog.app.ws;

import com.appsdeveloperblog.app.ws.io.entity.AuthorityEntity;
import com.appsdeveloperblog.app.ws.io.entity.RoleEntity;
import com.appsdeveloperblog.app.ws.io.repositories.AuthorityRepository;
import com.appsdeveloperblog.app.ws.io.repositories.RoleRepository;
import com.appsdeveloperblog.app.ws.shared.Roles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.management.relation.Role;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Collection;

@Component
public class InitialUserSetup {

    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    RoleRepository roleRepository;

    @EventListener
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        System.out.println("From application ready event...");

        AuthorityEntity readAuthority = createAuthority("READ_AUTHORITY");
        AuthorityEntity writeAuthority = createAuthority("WRITE_AUTHORITY");
        AuthorityEntity deleteAuthority = createAuthority("DELETE_AUTHORITY");

        RoleEntity roleUser = createRole(Roles.ROLE_USER.name(), Arrays.asList(readAuthority, writeAuthority));
        RoleEntity roleAdmin = createRole(Roles.ROLE_ADMIN.name(), Arrays.asList(readAuthority, writeAuthority, deleteAuthority));

    }

    @Transactional
    public AuthorityEntity createAuthority(String name) {
        AuthorityEntity authority = authorityRepository.findByName(name);
        if(authority == null) {
            authority = new AuthorityEntity(name);
            authorityRepository.save(authority);
        }

        return authority;
    }

    @Transactional
    public RoleEntity createRole(String name, Collection<AuthorityEntity> authorities) {

        RoleEntity roleEntity = roleRepository.findByName(name);
        if(roleEntity == null) {
            RoleEntity roleEntity1 = new RoleEntity(name);
            roleEntity1.setAuthorities(authorities);
            roleRepository.save(roleEntity1);
        }

        return roleEntity;
    }
}
