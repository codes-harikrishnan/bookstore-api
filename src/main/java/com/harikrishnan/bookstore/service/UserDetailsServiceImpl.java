package com.harikrishnan.bookstore.service;

import com.harikrishnan.bookstore.entity.Customer;
import com.harikrishnan.bookstore.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer customer = customerRepository.findCustomerByEmail(username).orElseThrow( () -> new UsernameNotFoundException("Unable to find user with email "+ username));

        return User.builder()
                .username(customer.getEmail())
                .password(customer.getPasswordHash())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_"+customer.getRole())))
                .build();
    }
}
