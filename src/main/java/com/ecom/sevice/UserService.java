package com.ecom.sevice;


import java.util.List;

import org.springframework.stereotype.Service;

import com.ecom.model.UserDtls;
@Service
public interface UserService {

	public UserDtls saveUser(UserDtls user);
	public List<UserDtls> getUsers();
	public List<UserDtls> getAdmins();
	public Boolean updateAccountStatus(int id, Boolean status);
	public UserDtls updateUserProfile(UserDtls user);
	
	public boolean userAccountLockTimeExpired(UserDtls user);
	public void userAccountLock(UserDtls user);
	public void increaseFailedAttempt(UserDtls user);
	public UserDtls updateUser(UserDtls user);

}
