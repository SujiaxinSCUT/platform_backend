package com.trace.platform.resource;

import com.trace.platform.entity.Account;
import com.trace.platform.entity.UserDetails;
import com.trace.platform.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/trace/account")
public class AccountResource {

    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/user_details/{username}")
    public ResponseEntity getUserDetails(@PathVariable("username") String username) {
        Account account = accountRepository.findByName(username);
        if (account == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        String currentUser = (String) SecurityContextHolder.getContext().getAuthentication().getName();
        if (!username.equalsIgnoreCase(currentUser)) {
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }
        UserDetails userDetails = new UserDetails();
        userDetails.setDate(account.getDate());
        userDetails.setId(account.getId());
        userDetails.setPermission(account.getPermission());
        userDetails.setName(account.getName());

        return new ResponseEntity<UserDetails>(userDetails, HttpStatus.OK);
    }

    @GetMapping
    public List<String> getAllUsername() {
        List<String> usernames = accountRepository.findAllUsername();
        return usernames;
    }
}
