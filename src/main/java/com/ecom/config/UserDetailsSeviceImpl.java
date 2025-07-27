package com.ecom.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.ecom.model.UserDtls;
import com.ecom.repository.UserRepository;
@Service
public class UserDetailsSeviceImpl implements UserDetailsService{
@Autowired
	UserRepository userRepo;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserDtls user=userRepo.findByEmail(username);
		if(ObjectUtils.isEmpty(user))
			throw new UsernameNotFoundException(username);
		return new CustomUser(user);
	}

}
