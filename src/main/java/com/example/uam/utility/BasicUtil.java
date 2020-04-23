package com.example.uam.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.uam.model.*;

public class BasicUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(BasicUtil.class);

	@Autowired
	private static HttpServletRequest request;

	public static String removeCustom(String roleName) {
		if (roleName == null)
			return "";
		if ((org.apache.commons.lang3.StringUtils.containsIgnoreCase(roleName, "CUSTOM_")))
			return roleName.replace("CUSTOM_", "");
		else
			return roleName;
	}

	public static Response validateEmail(String stepEmail) {
		// Response response = new Response();
		String string = stepEmail;
		if (string != null) {
			String[] split = string.split("@");
			LOGGER.info("SPlit-->" + split[0]);
			if ((split[0].length() > 1) && ((string.contains("@gmail.com")||string.contains("@yahoo.com")))) {
				// DO ACTION FOR CORRECT EMAIL
				LOGGER.info("**Yes==> " + string + " **");
				return new Response("00", "All Valid.");
			} else {// DO ACTION FOR INCORRECT EMAIL
				LOGGER.info("**NoNo==> " + string + " **");
				return new Response("96", "the email address is invalid ==> ");
			}

		} else
			return new Response();
	}

	public static String getClientIp() {

		String remoteAddr = "";

		if (request != null) {
			remoteAddr = request.getHeader("X-FORWARDED-FOR");
			if (remoteAddr == null || "".equals(remoteAddr)) {
				remoteAddr = request.getRemoteAddr();
			}
		}
		return remoteAddr;
	}

	public static boolean containsRole(String userProfile, Set<UserProfileAdmin> userProfiles) {
		if (userProfile == null || userProfile == null || userProfile.isEmpty())
			return false;
		for (UserProfileAdmin profile : userProfiles) {
			if (userProfile.equals(profile.getType()))
				return true;
		}
		return false;
	}

	public static boolean containsCaseInsensitiveInsensitiveStr(String searchStr, List<String> list) {
		if (searchStr == null || searchStr.isEmpty())
			return false;
		for (String string : list) {
			if (org.apache.commons.lang3.StringUtils.containsIgnoreCase(string, searchStr))
				return true;
		}
		return false;
	}

	public static UserToRoleApp getUserRoleAttributes(String userName, List<UserToRoleApp> pendingUsertorole) {
		if (userName == null || pendingUsertorole == null || pendingUsertorole.isEmpty())
			return new UserToRoleApp();
		for (UserToRoleApp userRole : pendingUsertorole) {
			if (userName.equalsIgnoreCase(userRole.getUserName()))
				return userRole;
		}
		return new UserToRoleApp();
	}

	public static UserToRoleApp getEditUserRoleFromList(String usertoroleID, List<UserToRoleApp> pendusers) {
		if (usertoroleID == null || pendusers == null || pendusers.isEmpty())
			return new UserToRoleApp();
		for (UserToRoleApp editUser : pendusers) {
			// LOGGER.info(">>> userTill "+userTill+" <<<");
			if (usertoroleID.equalsIgnoreCase(String.valueOf(editUser.getUsertoroleID())))
				return editUser;
		}
		return new UserToRoleApp();
	}

	public static UserRemoveRole getPendingUser(String userID, List<UserRemoveRole> pendusers) {
		if (userID == null || pendusers == null || pendusers.isEmpty())
			return new UserRemoveRole();
		for (UserRemoveRole editUser : pendusers) {
			// LOGGER.info(">>> userTill "+userTill+" <<<");
			if (userID.equalsIgnoreCase(String.valueOf(editUser.getUserID())))
				return editUser;
		}
		return new UserRemoveRole();
	}

	public static EditUser getEditUserFromList(String userID, List<EditUser> pendusers) {
		if (userID == null || pendusers == null || pendusers.isEmpty())
			return new EditUser();
		for (EditUser editUser : pendusers) {
			// LOGGER.info(">>> userTill "+userTill+" <<<");
			if (userID.equalsIgnoreCase(String.valueOf(editUser.getUserID())))
				return editUser;
		}
		return new EditUser();
	}

	public static String encryptPayload(String clearPayload) {
		return EncryptDecrypt.encrypt(clearPayload);
	}

	public static String decryptPayload(String encryptedPayload) {
		return EncryptDecrypt.decrypt(encryptedPayload);
	}

	public static String splitTrim(String step) {
		if (step == null)
			return "";
		String[] arr = step.split("-");
		return arr[0].trim();
	}

	//////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////


		public static List<Custactivities> getSelectedActivities(List<Custactivities> custactivitiesFinal,
			String[] activities) {
		List <Custactivities> responseObj = new ArrayList<>();
		if (custactivitiesFinal==null || custactivitiesFinal.isEmpty())
			return new ArrayList<>();
		
		if (activities==null || activities.length==0)
			return custactivitiesFinal; 
		
		for (Custactivities activity: custactivitiesFinal)
		{
			activity.setSelected(equalsCaseInsensitiveInsensitiveStr(activity.getActName(),Arrays.asList(activities)));
			responseObj.add(activity);
		} 
		
		return responseObj;
	}
		
		public static boolean equalsCaseInsensitiveInsensitiveStr(String searchStr, List<String> list){
			 if ( searchStr==null|| searchStr.isEmpty())
				 return false;
			 for (String string:list){
					//LOGGER.error("********Oops Something went wrong **********" + searchStr);
				//	LOGGER.error("********Oops Something went wrong **********" + string);
//				 if (org.apache.commons.lang3.StringUtils.containsIgnoreCase(string,searchStr
//							)) 
					 
					 if (string.equalsIgnoreCase(searchStr))
					 return true;
			 }
				 return false;
		// return l.stream().anyMatch(x -> x.trim().equalsIgnoreCase(s));
		 }

	    
}
