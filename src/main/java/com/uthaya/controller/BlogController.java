package com.uthaya.controller;



//import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.uthaya.blogService.BlogService;
import com.uthaya.model.BlogsDTO;
import com.uthaya.model.Users;


@RestController
@CrossOrigin(origins = "*")
public class BlogController {
	
	@Autowired
	private BlogService blogService;
	
	@GetMapping("/blogs")
	public Page<BlogsDTO> blogsPageable(Pageable pageable) {
		return blogService.blogsPageable(pageable);
	}

	@PostMapping("/blogs")
	public ResponseEntity<?> createBlog(@RequestBody(required=false)BlogsDTO blog) {
		return blogService.createBlog(blog);
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Users users) {
		return blogService.login(users);
	}
	
	@GetMapping("/myblogs/{username}")
	public Page<BlogsDTO> myBlogs(@PathVariable String username, Pageable pageable){
		return blogService.myBlogs(username, pageable);
	}

	@GetMapping("/like/{id}/{username}")
	public ResponseEntity<?> addLike(@PathVariable String id,@PathVariable String username){
		return blogService.addLike(id,username);
	}

	@GetMapping("/dislike/{id}/{username}")
	public ResponseEntity<?> disLike(@PathVariable String id,@PathVariable String username){
		return blogService.disLike(id,username);
	}

	@GetMapping("/tags/{tagname}")
	public ResponseEntity<?> findTags(@PathVariable String tagname){
		return blogService.findTags(tagname);
	}
	
	@PostMapping("/signup")
	public ResponseEntity<?> signUp(@RequestBody Users users) {
		return blogService.signUp(users);
	}

	
}
