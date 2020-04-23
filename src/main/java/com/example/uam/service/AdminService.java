package com.example.uam.service;

import com.example.uam.model.*;

public interface AdminService {


	Response createUser(User1 user, UserAdmin principal);

	Response assignUsertoRole(UsertoRoleReq userToRole, UserAdmin principal);

	Response authpendusertorole(String userToRoleID, String flag, UserAdmin principal);

	Response userNameCheck(String userName);

	Response disableusertorole(EditUserDetails user, UserAdmin principal);

	Response authdisableusertorole(String userID, String roleName, String flag, UserAdmin principal);

	Response edituser(EditUserDetails user, UserAdmin principal);

	Response authedituser(String userID, String flag, UserAdmin principal);

	Response authpenduser(String userID, String flag, UserAdmin principal);

}
