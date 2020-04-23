package com.example.uam.service;

import com.example.uam.model.*;

public interface AuditService {

	Response insertIntoAdminAudit(AuditMasterDetail auditMasterDetail, UserAdmin principal);

}
