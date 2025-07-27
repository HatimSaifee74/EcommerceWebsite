package com.ecom.sevice.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.ecom.model.UserDtls;
import com.ecom.repository.UserRepository;
import com.ecom.sevice.UserService;
@Service
public class UserServiceImpl implements UserService {
	@Autowired
	PasswordEncoder encoder;
@Autowired
	UserRepository userRepo;
	@Override
	public UserDtls saveUser(UserDtls user) {
		String  enPass=encoder.encode(user.getPassword());
		user.setPassword(enPass);
		user.setEnable(true);
		user.setAccountNonLocked(true);
		UserDtls newuser=userRepo.save(user);
		if(ObjectUtils.isEmpty(newuser))
		return null;
		else return newuser;
	}
	@Override
	public List<UserDtls> getUsers()
{
    return userRepo.findByRole("ROLE_USER");
     
}
	@Override
	public List<UserDtls> getAdmins()
{
    return userRepo.findByRole("ROLE_ADMIN");
     
}
	public Boolean updateAccountStatus(int id, Boolean status) {
		UserDtls user=userRepo.findById(id).orElse(null);
		user.setEnable(status);
		if(userRepo.save(user)!=null)
			return true;
		else return false;	
	}
	
	
	public void increaseFailedAttempt(UserDtls user) {
		int attempt=user.getFailedAttempt()+1;
		user.setFailedAttempt(attempt);
		userRepo.save(user);
	}
	public void userAccountLock(UserDtls user) {
		user.setAccountNonLocked(false);
		user.setLockedDate(new Date());
		userRepo.save(user);
	}
	public boolean userAccountLockTimeExpired(UserDtls user) {
		if(user.getLockedDate()==null)
			return true;
		long locktime=user.getLockedDate().getTime();
		long unlockTime=locktime+60*60*60*1000;
		
		long currTime=System.currentTimeMillis();
		if(currTime>unlockTime) {
			user.setAccountNonLocked(true);
			user.setLockedDate(null);
			user.setFailedAttempt(0);
			return true;
			}
		return false;
	}
	
	public UserDtls updateUserProfile(UserDtls user) {
		UserDtls dbUser=userRepo.findById(user.getId()).get();
		if(!ObjectUtils.isEmpty(dbUser)) {
			dbUser.setName(user.getName());
			dbUser.setAddress(user.getAddress());
			dbUser.setCity(user.getCity());
			dbUser.setMobilenumber(user.getMobilenumber());
			dbUser.setPincode(user.getPincode());
			dbUser.setProfileImage(user.getProfileImage());
			userRepo.save(dbUser);
		}
		return dbUser;
	}
	public UserDtls updateUser(UserDtls user) {
		return userRepo.save(user);
		
	}
}
