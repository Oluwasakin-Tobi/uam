package com.example.uam;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.uam.model.*;
import com.example.uam.service.*;
import com.example.uam.utility.*;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class WelcomeController {
	
	
	
	static final Logger LOGGER = LoggerFactory.getLogger(WelcomeController.class);
	final static DateFormat MARSHARLLERDATEFORMAT = new SimpleDateFormat("yyyy-MM-dd");
	final static ObjectMapper MAPPER = new ObjectMapper();

	private static final Random RANDOMGENERATOR = new Random();
	@Autowired
	@Lazy(true)
	AuthenticationTrustResolver authenticationTrustResolver;
	@Autowired
	MessageSource messageSource;
	@Autowired
	Environment environment;
	@Autowired
	AdminService adminService;
	@Autowired
	InquiryService inquiryService;
	@Autowired
	AuditService auditService;
	@Autowired
	private UserAdmin user;
	@Autowired
	HttpServletRequest request;

	/**
	 * This method returns the principal[user-name] of logged-in user.
	 */
	private UserAdmin getPrincipal() {
		return user;
	}



	/**
	 * This method handles Access-Denied redirect.
	 */
	@RequestMapping(value = "/Access_Denied", method = RequestMethod.GET)
	public String accessDeniedPage(ModelMap model) throws Exception {
		model.addAttribute("loggedinuser", getPrincipal());
		return "accessDenied";
	}

	/**
	 * This method handles login GET requests. If users is already logged-in and
	 * tries to goto login page again, will be redirected to dashboard page.
	 */
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String loginPage() {
		LOGGER.info("login here");
		if (isCurrentAuthenticationAnonymous()) {
			return "login";
		} else {
			return "redirect:/dashboard";
		}
		// return "redirect:/dashboard";
	}

	/**
	 * This method handles logout requests. Toggle the handlers if you are
	 * RememberMe functionality is useless in your app.
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logoutPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			new SecurityContextLogoutHandler().logout(request, response, auth);
			// persistentTokenBasedRememberMeServices.logout(request, response,
			// auth);
			SecurityContextHolder.getContext().setAuthentication(null);
		}
		return "redirect:/login?logout";
	}

	/**
	 * This method returns true if users is not already authenticated [logged-in],
	 * else false.
	 */
	private boolean isCurrentAuthenticationAnonymous() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authenticationTrustResolver.isAnonymous(authentication);
	}

	/**
	 * This method returns true if users is global admin else false.
	 */
	private boolean isPrincipalGlobalAdmin() {
		for (UserProfileAdmin userProfile : getPrincipal().getUserProfiles())
			if ("ADMIN".equalsIgnoreCase(userProfile.getType()) || "GROUPADMIN".equalsIgnoreCase(userProfile.getType()))
				return true;
		return false;
	}

	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = { "/createuser" }, method = RequestMethod.GET)
	public String createUser(ModelMap model) throws Exception {

		model.addAttribute("loggedinuser", getPrincipal());

		List<Role> userroles = inquiryService.getroles();
		model.addAttribute("userroles", userroles);

		if (getPrincipal().getUserRolesStr().contains("GROUPADMIN")) {
			model.addAttribute("groupAdmin", true);

		}
		return "createuser";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = { "/loadcreateuser" }, method = RequestMethod.POST)
	public String loadCreateUser(@Valid UserWithRole userRoleAccount, RedirectAttributes redirectAttributes,
			ModelMap model) throws Exception {
		LOGGER.info("** loadcreateuser ==> " + userRoleAccount + " **");

		userRoleAccount.setStepName("VALIDATOR");
		User1 user = new User1();
		user.setUserFullName(userRoleAccount.getUserFullName());
		user.setUserName(userRoleAccount.getUserName());
		user.setPassword(userRoleAccount.getPassword());
		user.setUserEmailAdd(userRoleAccount.getUserEmailAdd());
		user.setUserTransactionLimit(BigDecimal.ZERO);
		user.setUserRoles(userRoleAccount.getUserRoles());
		user.setAffiliateCode(userRoleAccount.getAffiliateCode() == null || userRoleAccount.getAffiliateCode().isEmpty()
				? getPrincipal().getAffiliate()
				: userRoleAccount.getAffiliateCode());
		Response creatUserroleresp = adminService.createUser(user, getPrincipal());
		LOGGER.info("** creatUserroleresp ==> " + creatUserroleresp + " **");
		
		if (creatUserroleresp == null || !("00".equals(creatUserroleresp.getResponseCode()))) {
			model.addAttribute("errorMessage",
					(messageSource.getMessage("createrole.unsuccessful", new Object[] { "" }, Locale.getDefault()))
							+ " - "
							+ (creatUserroleresp != null && creatUserroleresp.getResponseMessage() != null
									? creatUserroleresp.getResponseMessage()
									: ""));

			model.addAttribute("loggedinuser", getPrincipal());

			List<Role> userroles = inquiryService.getroles();
			model.addAttribute("userroles", userroles);

			return "createuser";

		}

		redirectAttributes.addFlashAttribute("successMessage",
				(messageSource.getMessage("successful.createuser", new Object[] { "" }, Locale.getDefault())));


		return "redirect:/createuser";
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/enableuser-{userID}", method = RequestMethod.GET)
	public String enableuser(@PathVariable String userID, RedirectAttributes redirectAttributes, ModelMap model)
			throws Exception {

		adminService.authpenduser(userID, "ENABLE", getPrincipal());


		redirectAttributes.addFlashAttribute("successMessage",
				(messageSource.getMessage("successful.enableuser", new Object[] { "" }, Locale.getDefault())));

		return "redirect:/edituser";

	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/diasableuser-{userID}", method = RequestMethod.GET)
	public String diasableuser(@PathVariable String userID, RedirectAttributes redirectAttributes, ModelMap model)
			throws Exception {

		adminService.authpenduser(userID, "DISABLE", getPrincipal());


		redirectAttributes.addFlashAttribute("successMessage",
				(messageSource.getMessage("successful.disableuser", new Object[] { "" }, Locale.getDefault())));

		return "redirect:/edituser";

	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/approveenableuser", method = RequestMethod.GET)
	public String approveenableuser(RedirectAttributes redirectAttributes, ModelMap model) throws Exception {
		 
		List<User> pendusers = inquiryService.getPendAuthEditUser("ENABLE", getPrincipal().getAffiliate());
		model.addAttribute("pendingUsers", pendusers);
		model.addAttribute("loggedinuser", getPrincipal());
		model.addAttribute("enableUser", true);
		return "pendingenable";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/approvedisableuser", method = RequestMethod.GET)
	public String approvedisableuser(RedirectAttributes redirectAttributes, ModelMap model) throws Exception {
		List<User> pendusers = inquiryService.getPendAuthEditUser("DISABLE", getPrincipal().getAffiliate());
		model.addAttribute("pendingUsers", pendusers);
		model.addAttribute("loggedinuser", getPrincipal());
		model.addAttribute("disableUser", true);
		return "pendingenable";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/authenablependuser-{userID}", method = RequestMethod.GET)
	public String authenablependuser(@PathVariable String userID, RedirectAttributes redirectAttributes, ModelMap model)
			throws Exception {
		adminService.authpenduser(userID, "APPENABLE", getPrincipal());
	
		redirectAttributes.addFlashAttribute("successMessage",
				(messageSource.getMessage("successful.enableuser1", new Object[] { "" }, Locale.getDefault())));
		return "redirect:/approveenableuser";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/authdisablependuser-{userID}", method = RequestMethod.GET)
	public String authdisablependuser(@PathVariable String userID, RedirectAttributes redirectAttributes,
			ModelMap model) throws Exception {
		adminService.authpenduser(userID, "APPDISABLE", getPrincipal());
	

		redirectAttributes.addFlashAttribute("successMessage",
				(messageSource.getMessage("successful.disableuser1", new Object[] { "" }, Locale.getDefault())));
		return "redirect:/approvedisableuser";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = { "/createusertorole" }, method = RequestMethod.GET)
	public String createUserToRole(ModelMap model) throws Exception {

		model.addAttribute("loggedinuser", getPrincipal());

		List<Role> userroles = inquiryService.getroles();
		model.addAttribute("userroles", userroles);

		List<UserStepPositionResp> getSteps = inquiryService.getSteps();

		model.addAttribute("getSteps", getSteps);

		return "createusertorole";
	}

	/**
	 * This method will handle createuserrole
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = { "/createusertorole" }, method = RequestMethod.POST)
	public String loadUserToRole(@Valid UsertoRoleReq user, RedirectAttributes redirectAttributes, ModelMap model)
			throws Exception {

		Response resp = adminService.userNameCheck(user.getUserName());

		if ("11".equals(resp.getResponseCode())) {
			redirectAttributes.addFlashAttribute("errorMessage",
					(messageSource.getMessage("system.error", new Object[] { "" }, Locale.getDefault()))
							+ " - USERNAME NOT FOUND");
			return "redirect:/createusertorole";
		}

		List<User1> getUserDetails = inquiryService.getUserDetails(user.getUserName());

		if (!getPrincipal().getUserRolesStr().contains("GROUPADMIN") && getUserDetails.size() > 0
				&& !(getPrincipal().getAffiliate().equalsIgnoreCase(getUserDetails.get(0).getAffiliateCode()))) {

			redirectAttributes.addFlashAttribute("errorMessage",
					(messageSource.getMessage("system.error", new Object[] { "" }, Locale.getDefault()))
							+ " - AFFILIATE ADMIN CANNOT ASSIGN USER IN ANOTHER AFFILIATE TO A ROLE ");
			return "redirect:/createusertorole";

		}

		LOGGER.info("++loadusertorole ==> " + user);
		user.setStepName("VALIDATOR");
		Response Userroleresp = adminService.assignUsertoRole(user, getPrincipal());

		if (Userroleresp == null || !("00".equals(Userroleresp.getResponseCode()))) {
			model.addAttribute("errorMessage",
					(messageSource.getMessage("assignrole.unsuccessful", new Object[] { "" }, Locale.getDefault()))
							+ (" - ")
							+ ((Userroleresp != null && Userroleresp.getResponseMessage() != null)
									? Userroleresp.getResponseMessage()
									: ""));

			model.addAttribute("loggedinuser", getPrincipal());

			List<UserStepPositionResp> getSteps = inquiryService.getSteps();

			model.addAttribute("getSteps", getSteps);

			List<Role> userroles = inquiryService.getroles();
			model.addAttribute("userroles", userroles);
			return "createusertorole";

		}

		redirectAttributes.addFlashAttribute("successMessage",
				(messageSource.getMessage("successful.assignrole", new Object[] { "" }, Locale.getDefault())));
		return "redirect:/createusertorole";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = { "/authusertorole" }, method = RequestMethod.GET)
	public String authusertorole(ModelMap model) throws Exception {
		model.addAttribute("loggedinuser", getPrincipal());

		List<UserToRoleApp> pendingUsertorole = inquiryService.getpendusertorole(getPrincipal().getAdUsername());
		model.addAttribute("pendingUsertorole", pendingUsertorole);
		model.addAttribute("loggedinusername", getPrincipal().getAdUsername().toLowerCase());

		return "pendinguserstorole";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/authpendusertorole-{usertoroleID}", method = RequestMethod.GET)
	public String authpendusertorole(@PathVariable String usertoroleID, RedirectAttributes redirectAttributes,
			ModelMap model) throws Exception {
		List<UserToRoleApp> pendingUsertorole = inquiryService.getpendusertorole(getPrincipal().getAdUsername());

		

		LOGGER.info("++ usertoroleID ==> " + usertoroleID + " ++");
		Response authuserresp = adminService.authpendusertorole(usertoroleID, "APPROVE", getPrincipal());

		if (authuserresp == null || !("00".equals(authuserresp.getResponseCode()))) {
			redirectAttributes.addFlashAttribute("errorMessage",
					(messageSource.getMessage("assignrole.unsuccessful", new Object[] { "" }, Locale.getDefault()))
							+ (" - ")
							+ ((authuserresp != null && authuserresp.getResponseMessage() != null)
									? authuserresp.getResponseMessage()
									: ""));

			List<User> pendusers = inquiryService.getpenduser(getPrincipal().getAffiliate());
			model.addAttribute("pendingUsers", pendusers);

			model.addAttribute("loggedinuser", getPrincipal());
			model.addAttribute("pendingUsertorole", pendingUsertorole);
			return "redirect:/authusertorole";
		}

		redirectAttributes.addFlashAttribute("successMessage",
				(messageSource.getMessage("successful.assignrole1", new Object[] { "" }, Locale.getDefault())));


		return "redirect:/authusertorole";

	}

	
	/**
	 * This method will handle daily volume
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = { "/editusertorole" }, method = RequestMethod.POST)
	public String editusertorole(@Valid EditUserDetails user, RedirectAttributes redirectAttributes, ModelMap model)
			throws Exception {
		LOGGER.info("++EditUserDetails ==> " + user);

		List<User1> getUserDetails = inquiryService.getUserDetails(user.getUserName());

		if (!getPrincipal().getUserRolesStr().contains("GROUPADMIN") && getUserDetails.size() > 0
				&& !(getPrincipal().getAffiliate().equalsIgnoreCase(getUserDetails.get(0).getAffiliateCode()))) {

			model.addAttribute("errorMessage",
					(messageSource.getMessage("system.error", new Object[] { "" }, Locale.getDefault()))
							+ " - AFFILIATE ADMIN CANNOT REMOVE ROLE FROM A USER FOR A USER IN ANOTHER AFFILIATE");
			return "redirect:/editusertorole-" + user.getUserID();

		}
		user.setStepId(1);
		Response Userroleresp = adminService.disableusertorole(user, getPrincipal());

		if (Userroleresp == null || !("00".equals(Userroleresp.getResponseCode()))) {
			model.addAttribute("errorMessage",
					(messageSource.getMessage("disablerole.unsuccessful", new Object[] { "" }, Locale.getDefault()))
							+ " - "
							+ ((Userroleresp != null && Userroleresp.getResponseMessage() != null)
									? Userroleresp.getResponseMessage()
									: ""));

			model.addAttribute("loggedinuser", getPrincipal());

			List<Role> userroles = inquiryService.getroles();
			model.addAttribute("userroles", userroles);
			return "edituserroles";

		}

		redirectAttributes.addFlashAttribute("successMessage",
				(messageSource.getMessage("successful.editrole", new Object[] { "" }, Locale.getDefault())));


		return "redirect:/removeusertorole";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = { "/approveeditrole" }, method = RequestMethod.GET)
	public String approveeditrole(ModelMap model) throws Exception {
		List<User> pendusers = inquiryService.getPendAuthEditUser("APPEDITROLE", getPrincipal().getAffiliate());

		List<UserRemoveRole> responses = inquiryService.getPendingRemoveUser(getPrincipal().getAffiliate());
		LOGGER.info("+++ responses ==> " + responses);
		model.addAttribute("pendingUsers", responses);
		model.addAttribute("approveeditrole", true);
		model.addAttribute("loggedinuser", getPrincipal());
		model.addAttribute("loggedinusername", getPrincipal().getAdUsername().toLowerCase());

		return "pendingusers";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/approveeditrole-{userID}|{roleName}", method = RequestMethod.GET)
	public String approveeditrole(@PathVariable String userID, @PathVariable String roleName,
			RedirectAttributes redirectAttributes, ModelMap model) throws Exception {

		List<User1> getUserDetails = inquiryService.getUserDetails(userID);

		if (!getPrincipal().getUserRolesStr().contains("GROUPADMIN") && getUserDetails.size() > 0
				&& !(getPrincipal().getAffiliate().equalsIgnoreCase(getUserDetails.get(0).getAffiliateCode()))) {

			redirectAttributes.addFlashAttribute("errorMessage", (messageSource.getMessage("system.error",
					new Object[] { "" }, Locale.getDefault()))
					+ " - AFFILIATE ADMIN CANNOT AUTHORIZE ROLE REMOVAL FROM A USER FOR A USER IN ANOTHER AFFILIATE");

			return "redirect:/approveeditrole";

		}

		Response authuserresp = adminService.authdisableusertorole(userID, roleName, "APPROVE", getPrincipal());

		if (authuserresp == null || !("00".equals(authuserresp.getResponseCode()))) {
			redirectAttributes.addFlashAttribute("errorMessage",
					(messageSource.getMessage("edituser.unsuccessful", new Object[] { "" }, Locale.getDefault()))
							+ (" - ")
							+ ((authuserresp != null && authuserresp.getResponseMessage() != null)
									? authuserresp.getResponseMessage()
									: ""));

			List<User> pendusers = inquiryService.getpenduser(getPrincipal().getAffiliate());
			model.addAttribute("pendingUsers", pendusers);
			model.addAttribute("approveeditrole", true);
			model.addAttribute("loggedinuser", getPrincipal());

			return "redirect:/approveeditrole";
		}
		redirectAttributes.addFlashAttribute("successMessage",
				(messageSource.getMessage("successful.edituser2", new Object[] { "" }, Locale.getDefault())));


		return "redirect:/approveeditrole";

	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = { "/edituser" }, method = RequestMethod.GET)
	public String editUser(ModelMap model) throws Exception {

		String pflag = "ALL";
		if (isPrincipalGlobalAdmin()) {
			pflag = "ALL";
		} else {
			pflag = "USER";
		}

		List<User> usersroledetails = inquiryService.getuser("1", pflag, getPrincipal().getAffiliate());
		model.addAttribute("usersroledetails", usersroledetails);

		model.addAttribute("loggedinuser", getPrincipal());

		return "editusers";
	}

	/**
	 * This method will handle daily volume
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = { "/editusertorole-{userID}" }, method = RequestMethod.GET)
	public String editUsertorole(@PathVariable String userID, RedirectAttributes redirectAttributes, ModelMap model)
			throws Exception {

		LOGGER.info("+++ userID ==> " + userID);
		List<User> userroledetails = inquiryService.getuser(userID, "PERUSER", getPrincipal().getAffiliate());
		User user = userroledetails.get(0);
		LOGGER.info("+++ user ==> " + user.toString());
		List<String> finalUserroles = new ArrayList<>();
		UserToRoleResponse rolesperusers = inquiryService.getRolesForUser(getPrincipal().getAdUsername(),
				getPrincipal().getCurrentLoginIPAddress(), Long.valueOf(userID));
		if (rolesperusers != null && rolesperusers.getUserRole() != null && !rolesperusers.getUserRole().isEmpty()) {
			for (UserRole userrole : rolesperusers.getUserRole()) {
				finalUserroles.add(BasicUtil.removeCustom(userrole.getRoleName()));
			}
			LOGGER.info("+++ userroles ==> " + finalUserroles);
			model.addAttribute("userroles", finalUserroles);
		}
		model.addAttribute("user", user);

		model.addAttribute("loggedinuser", getPrincipal());

		return "edituserroles";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = { "/edituserdetails-{userID}" }, method = RequestMethod.GET)
	public String edituserdetails(@PathVariable String userID, RedirectAttributes redirectAttributes, ModelMap model)
			throws Exception {


		LOGGER.info("+++ userID ==> " + userID);
		List<User> userroledetails = inquiryService.getuser(userID, "PERUSER", getPrincipal().getAffiliate());
		User user = userroledetails.get(0);
		LOGGER.info("+++ user ==> " + user);

		if (user == null) {
			redirectAttributes.addFlashAttribute("errorMessage",
					(messageSource.getMessage("user.not.found", new Object[] { "" }, Locale.getDefault())));

			return "redirect:/edituser";
		}

		List<Role> userroles = inquiryService.getroles();
		model.addAttribute("userroles", userroles);

		model.addAttribute("user", user);
		String[] emailArr = (user.getUserEmailAdd() != null ? user.getUserEmailAdd() : "").split(",");
		model.addAttribute("emailArr", emailArr);
		model.addAttribute("loggedinuser", getPrincipal());

		return "edituserdetails";
	}

	/**
	 * This method will handle daily volume
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = { "/edituserdetails" }, method = RequestMethod.POST)
	public String edituserdetails(@Valid EditUserDetails user, RedirectAttributes redirectAttributes, ModelMap model)
			throws Exception {
		LOGGER.info("++i  got here ==> " + user);

		List<User1> getUserDetails = inquiryService.getUserDetails(user.getUserName());

		if (!getPrincipal().getUserRolesStr().contains("GROUPADMIN") && getUserDetails.size() > 0
				&& !(getPrincipal().getAffiliate().equalsIgnoreCase(getUserDetails.get(0).getAffiliateCode()))) {

			redirectAttributes.addFlashAttribute("errorMessage",
					(messageSource.getMessage("system.error", new Object[] { "" }, Locale.getDefault()))
							+ " - AFFILIATE ADMIN CANNOT EDIT USER DETAILS FOR A USER IN ANOTHER AFFILIATE");
			return "redirect:/edituserdetails-" + user.getUserID();

		}
		Response validateEmail = BasicUtil.validateEmail(user.getUserEmailAdd());

		if (!"00".equals(validateEmail.getResponseCode())) {

			redirectAttributes.addFlashAttribute("errorMessage",
					(messageSource.getMessage("system.error", new Object[] { "" }, Locale.getDefault()))
							+ " - EMAIL NOT IN RIGHT FORMAT ");
			return "redirect:/edituserdetails-" + user.getUserID();

		}

		Response Userroleresp = adminService.edituser(user, getPrincipal());

		if (Userroleresp == null || !("00".equals(Userroleresp.getResponseCode()))) {
			redirectAttributes.addFlashAttribute("errorMessage",
					(messageSource.getMessage("edituser.unsuccessful", new Object[] { "" }, Locale.getDefault()))
							+ (" - ")
							+ ((Userroleresp != null && Userroleresp.getResponseMessage() != null)
									? Userroleresp.getResponseMessage()
									: ""));

			model.addAttribute("loggedinuser", getPrincipal());

			List<Role> userroles = inquiryService.getroles();
			model.addAttribute("userroles", userroles);

			model.addAttribute("adduserroles", userroles);
			Response branchname = inquiryService.getbranchname(user.getUserBranch());
			model.addAttribute("brancheinfo", branchname);
			List<Branch> branches = inquiryService.getAuthorisedBranchesForAffiliate(getPrincipal().getAffiliate());
			model.addAttribute("branches", branches);

			List<Affiliate> affiliate = inquiryService.getAuthorisedAffiliates();
			model.addAttribute("affiliate", affiliate);
			model.addAttribute("user", user);

			// return "edituserdetails";
			return "redirect:/edituserdetails-" + user.getUserID();

		}

		redirectAttributes.addFlashAttribute("successMessage",
				(messageSource.getMessage("successful.edituser", new Object[] { "" }, Locale.getDefault())));


		return "redirect:/edituserdetails-" + user.getUserID();
	}

	/**
	 * This method will handle edit user
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = { "/approveuserdetails" }, method = RequestMethod.GET)
	public String approveuserdetails(ModelMap model) throws Exception {

		List<EditUser> pendusers = inquiryService.getpendedituser1("APPUSERDETAILS", getPrincipal().getAffiliate());
		LOGGER.info("pendinguser " + pendusers);
		model.addAttribute("pendingUsers", pendusers);
		model.addAttribute("appuserdetails", true);
		model.addAttribute("loggedinuser", getPrincipal());
		model.addAttribute("loggedinusername", getPrincipal().getAdUsername().toLowerCase());

		return "editpendingusers";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/authapproveuserdetails-{userID}", method = RequestMethod.GET)
	public String authapproveuserdetails(@PathVariable String userID, RedirectAttributes redirectAttributes,
			ModelMap model) throws Exception {

		List<EditUser> editPendusers = inquiryService.getpendedituser1("APPUSERDETAILS", getPrincipal().getAffiliate());

		EditUser editUser = BasicUtil.getEditUserFromList(userID, editPendusers);

		List<User1> getUserDetails = inquiryService.getUserDetails(userID);

		if (!getPrincipal().getUserRolesStr().contains("GROUPADMIN") && getUserDetails.size() > 0
				&& !(getPrincipal().getAffiliate().equalsIgnoreCase(getUserDetails.get(0).getAffiliateCode()))) {

			redirectAttributes.addFlashAttribute("errorMessage",
					(messageSource.getMessage("system.error", new Object[] { "" }, Locale.getDefault()))
							+ " - AFFILIATE ADMIN CANNOT APPROVE EDITED USER DETAILS FOR A USER IN ANOTHER AFFILIATE");
			return "redirect:/edituserdetails-" + user.getUserID();

		}

		Response authuserresp = adminService.authedituser(userID, "APPROVE", getPrincipal());

		if (authuserresp == null || !("00".equals(authuserresp.getResponseCode()))) {
			redirectAttributes.addFlashAttribute("errorMessage",
					(messageSource.getMessage("edituser.unsuccessful", new Object[] { "" }, Locale.getDefault()))
							+ (" - ") + authuserresp.getResponseMessage());

			List<User> pendusers = inquiryService.getpenduser(getPrincipal().getAffiliate());
			model.addAttribute("pendingUsers", pendusers);
			model.addAttribute("appuserdetails", true);
			model.addAttribute("loggedinuser", getPrincipal());

			return "redirect:/approveuserdetails";
		}

		redirectAttributes.addFlashAttribute("successMessage",
				(messageSource.getMessage("successful.edituser.auth", new Object[] { "" }, Locale.getDefault())));


		return "redirect:/approveuserdetails";

	}

	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = { "/", "/dashboard" }, method = RequestMethod.GET)
	public String listUsers(ModelMap model, HttpServletRequest httpRequest) throws Exception {
		LOGGER.info("+++ DASHBOARD ==> ");
		
		UserStepPositionResp response = inquiryService.getUserStepPosition(getPrincipal().getAdUsername());

		LOGGER.info("responseStep" + response);

		model.addAttribute("loggedinuser", getPrincipal());
		model.addAttribute("stepname", response.getStepName());

		return "dashboard";
	}
	
}
