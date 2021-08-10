package com.uthaya.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.uthaya.model.Users;

@Repository
public interface UsersRepository extends MongoRepository<Users, String>{
	
	@Query("{'username':?0}")
	Users isUser(String username);
}
