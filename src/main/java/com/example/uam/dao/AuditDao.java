package com.example.uam.dao;

import java.util.List;

import com.example.uam.model.*;

public interface AuditDao {
	

	public Response insertAdminAudit(AuditLog auditLog);
	
	List<AuditLog> getAuditLog(Affiliate request);
	

}
