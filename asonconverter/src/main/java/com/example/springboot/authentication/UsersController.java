package com.example.springboot.authentication;

import com.example.springboot.authentication.model.User;
import com.example.springboot.authentication.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UsersController {
    @Autowired
    private UserRepository repository;

    @RequestMapping(value = "/registration", method = RequestMethod.PUT)
    public ResponseEntity registrationController(@RequestBody User user) {
        repository.save(user);
        return ResponseEntity.status(HttpStatus.OK).body("Create user successfully");
    }

    @RequestMapping(value = "/authentication", method = RequestMethod.POST)
    public ResponseEntity authenticateUser(@RequestBody User user) {
        User retrieveUser = repository.findByEmail(user.getEmail());
        if (retrieveUser != null && user.getEmail().equals(retrieveUser.getEmail())
                && user.getPassword().equals(retrieveUser.getPassword())) {
            return ResponseEntity.status(HttpStatus.OK).body("Get user successfully");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cannot find user");
    }



}
