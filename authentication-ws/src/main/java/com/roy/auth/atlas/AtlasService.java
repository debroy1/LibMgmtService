package com.roy.auth.atlas;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.roy.auth.exceptions.Exceptions;
import com.roy.auth.exceptions.ServiceException;

@Service
public class AtlasService {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private static final Sort sortByEmail = Sort.by(Sort.Direction.DESC, "email");
	private static final Sort sortByTimestamp = Sort.by(Sort.Direction.DESC, "timestamp");
	
	@Autowired
	private UserItemRepository repository;
	
	// CREATE - C
	public UserItem saveUser(UserItem userItem) {
		log.info("saving user");
		userItem.setId(null);
		userItem.setTimestamp(LocalDateTime.now());
		userItem = repository.save(userItem);
		log.info("save successful");
		return userItem;
	}

	// READ - R
	public List<UserItem> getAllUsersSortedByEmail() {
		log.info("retrieving all users by email order");
		return repository.findAll(sortByEmail);
	}

	// READ - R
	public List<UserItem> getAllUsersSortedByTimestamp() {
		log.info("retrieving all users by timestamp order");
		return repository.findAll(sortByTimestamp);
	}

	// READ - R
	public Optional<UserItem> getUserById(String id) {
		log.info("retrieving user with id: " + id);
		if(id == null) {
			return null;
		}
		return repository.findById(id);
	}
	
	// READ - R
	public Optional<UserItem> getUserByEmail(String email) {
		log.info("retrieving user with email: " + email);
		if(email == null) {
			return null;
		}
		return repository.findByEmail(email);
	}

	// UPDATE - U
	public UserItem updateUser(UserItem userItem) {
		log.info("updating user");
		UserItem _userItem = new UserItem();
		if(userItem.getId() != null && repository.existsById(userItem.getId())) {
			Optional<UserItem> user = repository.findById(userItem.getId());
			_userItem = user.get();
			log.info("updating user");
			_userItem.setFirstName(userItem.getFirstName());
			_userItem.setLastName(userItem.getLastName());
			_userItem.setAddress(userItem.getAddress());
			_userItem.setTimestamp(LocalDateTime.now());
			_userItem = repository.save(_userItem);
			log.info("update successful");
		} else {
			log.error("update skipped, user not found");
			throw new ServiceException(Exceptions.USER_NOT_FOUND);
		}
		return _userItem;
	}

	// DELETE - D
	public void deleteUser(String id) {
		log.info("deleting user");
		if(repository.existsById(id)) {
			log.info("deleting user");
			repository.deleteById(id);
			log.info("delete successful");
		} else {
			log.error("delete skipped, user not found");
			throw new ServiceException(Exceptions.USER_NOT_FOUND);
		}
	}
	
}
