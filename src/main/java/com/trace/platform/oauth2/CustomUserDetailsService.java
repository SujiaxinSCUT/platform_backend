package com.trace.platform.oauth2;

import com.trace.platform.entity.Account;
import com.trace.platform.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Account account = accountRepository.findByName(s);
        if (account == null) {
            throw new UsernameNotFoundException("不存在该用户:" + s);
        }
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String permission : account.getPermission().split(" ")) {
            if (permission.isEmpty()) continue;
            authorities.add(new SimpleGrantedAuthority(permission));
        }

        return new CustomUser(s, account.getPassword(), true, true, true, account.isVerified(), authorities);

    }

    public UserDetails loadUserByUsername(String s, String client_id) throws UsernameNotFoundException {
        Account account = accountRepository.findByName(s);
        if (account == null) {
            throw new UsernameNotFoundException("不存在该用户:" + s);
        }
        if ("client_user".equalsIgnoreCase(client_id)) {
            if (!account.getPermission().contains("ROLE_USER")) {
                throw new UsernameNotFoundException("不存在该商户:" + s);
            }
        } else {
            if (!account.getPermission().contains("ROLE_ADMIN")) {
                throw new UsernameNotFoundException("不存在该管理员:" + s);
            }
        }
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String permission : account.getPermission().split(" ")) {
            if (permission.isEmpty()) continue;
            authorities.add(new SimpleGrantedAuthority(permission));
        }

        return new CustomUser(s, account.getPassword(), true, true, true, account.isVerified(), authorities);

    }
}
