package com.uthaya.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;


import com.uthaya.model.BlogsDTO;

import java.util.List;

@Repository
public interface BlogRepo extends MongoRepository<BlogsDTO, String> {
	
	@Query("{'username' : ?0}")
	Page<BlogsDTO> findAllByUserName(String username, Pageable pageable);

	@Query("{'tags':?0}")
	List<BlogsDTO> findByTag(String tags);
}
