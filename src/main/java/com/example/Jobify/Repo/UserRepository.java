package com.example.Jobify.Repo;

import com.example.Jobify.model.UserData;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<UserData, String> {
    UserData findByEmail(String email);
    boolean existsByEmail(String email);
    @Override
    long count();
}
