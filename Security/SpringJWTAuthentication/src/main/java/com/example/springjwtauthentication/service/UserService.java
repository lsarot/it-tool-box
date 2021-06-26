package com.example.springjwtauthentication.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.springjwtauthentication.model.persistence.dao.UserRepository;
import com.example.springjwtauthentication.model.persistence.dto.UserDto;
import com.example.springjwtauthentication.model.persistence.entity.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service(value = "userService")
public class UserService {
	//implements UserDetailsService { //forma antigua, ahora se maneja con la configuración del configure(AuthenticationManagerBuilder auth)... .jdbcAuthentication().dataSource
	
	@Autowired private BCryptPasswordEncoder bcryptEncoder;
	
	@Autowired private UserRepository userDao;
	
	
	public List<User> findAll() {
		List<User> list = new ArrayList<>();
		userDao.findAll().iterator().forEachRemaining(list::add);
		list.stream().map(item -> {
			item.setPassword(null);
			return item;
		})
		.collect(Collectors.toList());
		return list;
	}
	
	public User findById(long id) {
		Optional<User> optionalUser = userDao.findById(id);
		return optionalUser.isPresent() ? optionalUser.get() : null;
	}

	public User findByUsername(String username) {
		return userDao.findByUsername(username);
	}
	
	public User save(UserDto user) throws Exception {
		User dbUser = findByUsername(user.getUsername());
		if (dbUser != null) {
			throw new Exception("Ya existe!");
		}
	    User newUser = new User();
	    BeanUtils.copyProperties(user, newUser, "password");
	    //newUser.setUsername(user.getUsername());
	    //newUser.setFirstName(user.getFirstName());
	    //newUser.setLastName(user.getLastName());
		//newUser.setAge(user.getAge());
		//newUser.setSalary(user.getSalary());
	    newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
        return userDao.save(newUser);
    }
	
	/**
	 * lo mantenemos para mostrar la utilidad BeanUtils.copyProperties
	 * */
	public User update(User userDto) {
        User user = findById(userDto.getId());
        if(user != null) {
            BeanUtils.copyProperties(userDto, user, "password");
            userDao.save(user);
        }
        return userDto;
    }
	
	/* Cuando se extiendía de UserDetailsService, Spring llamaba a este método cuando se llamaba a authenticate del sec context (creo, pero sí era un callback)
	 * 
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userDao.findByUsername(username);
		if(user == null){
			throw new UsernameNotFoundException("Invalid username or password.");
		}
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), getAuthority());
	}
	
	private List<SimpleGrantedAuthority> getAuthority() {
		return Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"));
	}*/

}