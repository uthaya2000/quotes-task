package com.uthaya.blogService;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.uthaya.model.BlogsDTO;
import com.uthaya.model.ErrorMessage;
import com.uthaya.model.Users;
import com.uthaya.repo.BlogRepo;
import com.uthaya.repo.UsersRepository;


@Service
public class BlogService {
	
	@Autowired
	private BlogRepo blogRepos;
	
	@Autowired
	private UsersRepository usersRepos;

	
	public static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
	
	//Get All data Service
	public Page<BlogsDTO> blogsPageable(Pageable pageable) {
		return blogRepos.findAll(pageable);
	}
	
	//Encrypt
	public static String encrypt(String plainText)
    {
        plainText = plainText.toLowerCase();
        int shiftKey = 3;
        char[] plainTextArray = plainText.toCharArray();
        String cipherText = "";
        for (int i = 0; i < plainText.length(); i++)
        {
        	if(!Character.isLetter(plainTextArray[i])) {
        		cipherText+=plainTextArray[i];
        		continue;
        	}
            int charPosition = ALPHABET.indexOf(plainText.charAt(i));
            int keyVal = (shiftKey + charPosition) % 26;
            char replaceVal = ALPHABET.charAt(keyVal);
            cipherText += replaceVal;
        }
        return cipherText;
    }
	
	//Create Service
	public ResponseEntity<?> createBlog(BlogsDTO blog){
		try {
			ErrorMessage e=new ErrorMessage("");
			if(blog==null) {
				//throw new IllegalAccessException();
				e.setError("Fill eVERY FIELDS");
				return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
			}
			else {
				
				if (blog.getUsername()==null || blog.getUsername().equals("")) {
					System.out.println(blog.getUsername()+"Uthaya");
					//throw new IllegalAccessException();
					e.setError("Fill the user name");
					return new ResponseEntity<>(
							e, HttpStatus.BAD_REQUEST
					);
				}
			
				if (blog.getQuote()==null || blog.getQuote().equals("")) {
					//throw new IllegalAccessException();
					e.setError("Fill the Quote");
					return new ResponseEntity<>(
							e, HttpStatus.BAD_REQUEST
					);
				} 
				
			}
			//throw new IllegalAccessException();
			blog.setCreatedAt(new Date(System.currentTimeMillis()));
			blogRepos.save(blog);
			return new ResponseEntity<BlogsDTO>(blog, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	//Login Service
	public ResponseEntity<?> login(@RequestBody Users users) {
		try {
			Users currentUser = usersRepos.isUser(users.getUsername());
			if(currentUser == null)
			{
				users.setToken(0);
				return new ResponseEntity<Users>(users, HttpStatus.NOT_FOUND);
			}
			if(currentUser.getPassword().equals(encrypt(users.getPassword()))) {
				return new ResponseEntity<Users>(currentUser, HttpStatus.OK);
			}
			ErrorMessage error = new ErrorMessage("User Not Found");
			return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
		    
		} catch (Exception e) {
			ErrorMessage er=new ErrorMessage(e.getMessage());
			return new ResponseEntity<>(er, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	//add like
	public ResponseEntity<?> addLike(String id,String username){
		Optional<BlogsDTO> postToLike=blogRepos.findById(id);
		if(postToLike.isEmpty()){
			ErrorMessage error = new ErrorMessage("Id Not Found");
			return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
		}else{

				if(postToLike.get().getLikedName().add(username)){
					postToLike.get().setLikes(postToLike.get().getLikes()+1);
					Users userToSave=usersRepos.isUser(username);
					userToSave.getPosts().add(postToLike.get());
					usersRepos.save(userToSave);
				}
				blogRepos.save(postToLike.get());
				return new ResponseEntity<>(postToLike,HttpStatus.OK);
			}
	}

	//dislike

	public ResponseEntity<?> disLike(String qid,String username){
		Optional<BlogsDTO>  disLike=blogRepos.findById(qid);
		if(disLike.isEmpty()){
			ErrorMessage error = new ErrorMessage("ID not Found");
			return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
		}else{
			if(disLike.get().getLikes()>0) {
				if(disLike.get().getLikedName().remove(username)){
					disLike.get().setLikes(disLike.get().getLikes() - 1);
					Users userToSave=usersRepos.isUser(username);
					userToSave.getPosts().removeIf(t->t.getId().equals(qid));
					usersRepos.save(userToSave);
				}
				blogRepos.save(disLike.get());

			}
			return new ResponseEntity<>(disLike,HttpStatus.OK);
		}
	}

	//SortBY Tagname
	public ResponseEntity<?> findTags(String tagname){
		List<BlogsDTO> tagToSend=blogRepos.findByTag(tagname);
		return new ResponseEntity<>(tagToSend,HttpStatus.OK);
	}


	//Sign-up Service
	public ResponseEntity<?> signUp(@RequestBody Users users) {
		String plainText = users.getPassword();
		String encryptText = encrypt(plainText);
		Users currentUser = usersRepos.isUser(users.getUsername());
		if(currentUser != null)
		{
			users.setToken(0);
			return new ResponseEntity<Users>(users, HttpStatus.CONFLICT);
		}
		int min = 10000;
		int max = 10000000;
		Random r = new Random();
		int token = min + r.nextInt(max);
		users.setToken(token);
		users.setPassword(encryptText);
		usersRepos.save(users);
		return new ResponseEntity<Users>(users, HttpStatus.OK);
	}

	//Particular data selection service
	public Page<BlogsDTO> myBlogs(String username, Pageable pageable){
		return blogRepos.findAllByUserName(username, pageable);
	}
}
