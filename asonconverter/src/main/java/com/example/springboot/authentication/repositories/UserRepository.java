package com.example.springboot.authentication.repositories;


import com.example.springboot.authentication.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    User findByEmail(String email);
}
