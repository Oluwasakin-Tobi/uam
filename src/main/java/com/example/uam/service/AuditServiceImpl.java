package com.example.uam.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.uam.dao.AuditDao;
import com.example.uam.model.*;

@Service("auditService")
public class AuditServiceImpl implements AuditService{
	
	@Autowired
	AuditDao dao;

	@Override
	public Response insertIntoAdminAudit(AuditMasterDetail auditMasterDetail, UserAdmin principal) {
		AuditLog auditLog = new AuditLog();
		auditLog.setUserName(auditMasterDetail.getUserName());
		auditLog.setEventDesc(auditMasterDetail.getEventDesc());
		auditLog.setEventSrc(auditMasterDetail.getEventSrc());
		auditLog.setServerIp(auditMasterDetail.getServerIp());
		auditLog.setExtRef(auditMasterDetail.getExtRef());
		return dao.insertAdminAudit(auditLog);
	}

}
