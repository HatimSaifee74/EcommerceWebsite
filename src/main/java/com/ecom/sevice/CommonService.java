package com.ecom.sevice;

import org.springframework.stereotype.Service;

@Service
public interface CommonService {

	public void removeSessionAttribute();
	public void removeSessionMessage();
}
