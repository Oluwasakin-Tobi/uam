package com.example.uam.service;

import java.util.List;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.example.uam.dao.InquiryDao;
import com.example.uam.model.*;
import com.example.uam.utility.*;

@Service("inquiryService")
public class InquiryServiceImpl implements InquiryService {
	static final Logger LOGGER = LoggerFactory.getLogger(InquiryServiceImpl.class);

	@Autowired
	Environment environment;

	@Autowired
	private InquiryDao dao;

	
	@Override
	public List<Role> getroles() {
		LOGGER.info("logg");
		return dao.get_roles();
	}

	@Override
	public List<Branch> getAuthorisedBranchesForAffiliate(String affiliateCode) {
		return dao.get_branch(affiliateCode);
	}

	@Override
	public List<User> getpenduser(String affiliate) {
		return dao.get_auth_users(affiliate);
	}

	@Override
	public List<UserToRoleApp> getpendusertorole(String adUsername) {
		return dao.get_PEND_USERTOROLE(adUsername);
	}

	@Override
	public List<User> getPendAuthEditUser(String flag, String affiliate) {
		Flag pflag = new Flag();
		pflag.setPflag(flag);
		pflag.setAffiliate(affiliate);
		return dao.get_auth_edit_users(pflag);
	}

	@Override
	public List<User1> getUserDetails(String userName) {
		return dao.getUserDetails(userName);
	}

	@Override
	public List<UserStepPositionResp> getSteps() {
		return dao.getSteps();
	}

	@Override
	public List<User> getuser(String userID, String pflag, String affiliate) {
		UserReq req = new UserReq();
		req.setPflag(pflag);
		req.setAffiliate(affiliate);
		req.setUserID(Long.valueOf(userID));
		return dao.get_users(req);
	}

	@Override
	public List<UserRemoveRole> getPendingRemoveUser(String affiliate) {
		return dao.getPendingRemoveUser(affiliate);
	}

	@Override
	public UserToRoleResponse getRolesForUser(String adUsername, String currentLoginIPAddress, long userID) {
		RoleForUser roleforuser = new RoleForUser();
		roleforuser.setServerIP(currentLoginIPAddress);
		roleforuser.setUserID(userID);
		roleforuser.setUserName(adUsername);
		return dao.get_user_role_opps(roleforuser);
	}

	@Override
	public Response getbranchname(String userBranch) {
		return dao.getbranchname(userBranch);
	}

	@Override
	public List<Affiliate> getAuthorisedAffiliates() {
		return dao.getAuthorisedAffiliates();
	}
	
	@Override
	public List<UserToRoleResp> getUserRoles(UserProfile user) {
		return dao.get_users_roles(user);
	}

	@Override
	public List<EditUser> getpendedituser1(String pflag, String affiliate) {
		Flag flag = new Flag();
		flag.setPflag(pflag);
		flag.setAffiliate(affiliate);
		return dao.get_edit_users1(flag);
	}

	@Override
	public UserStepPositionResp getUserStepPosition(String adUsername) {
		UserStepPosition request = new UserStepPosition();
		request.setUserName(adUsername);
		request.setServerIP(BasicUtil.getClientIp());
		return dao.get_user_step_position(request);
	}
	///////////////////////////////////////// END OF
	///////////////////////////////////////// ADMIN//////////////////////////////////////
	
	///////////////////REPORTS////////////////////////////
	@Override
	public List<UserReport> getAllUserReportByAffiliate(String affiliate) {
		return dao.getAllUserReportByAffiliate(affiliate);
	}
	//////////////////REPORT ENDS///////////////////////////////

	@Override
	public List<AuditLog> getAuditReportByAffiliate(String affiliate) {
		return dao.getAuditReportByAffiliate(affiliate);
	}
	
	@Override
	public List<Affiliate> getAffiliateDetails(String username){
		return dao.getAffiliateDetails(username);
	}

	@Override
	public List<Custactivities> getcustactivities(String flag) {
		return dao.getcustactivities(flag);
	}


}
