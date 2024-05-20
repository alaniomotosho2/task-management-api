package org.niyo.user;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;

import java.util.Optional;

@ApplicationScoped
public class UserService {
    @Inject
    UserRepository userRepository;


    public Optional<User> findByUsername(String username){
        return userRepository.find("username",username).firstResultOptional();
    }

    public User findById(Long id){
        User user =  userRepository.findById(id);
        if(user == null){
            throw new WebApplicationException("User not found!");
        }
        return user;
    }
}
