
<!doctype html>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="mvc" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<html class="no-js" lang="en">
<!--<![endif]-->

<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title>HOME FELLOWSHIP -</title>
<meta name="description" content="Sufee Admin - HTML5 Admin Template">
<meta name="viewport" content="width=device-width, initial-scale=1">

<link rel="apple-touch-icon" href="apple-icon.png">
<link rel="shortcut icon" href="favicon.ico">

<link rel="stylesheet" type="text/css"
	href="<c:url value='/vendors/bootstrap/dist/css/bootstrap.min.css'/>">
<link rel="stylesheet" type="text/css"
	href="<c:url value='/vendors/font-awesome/css/font-awesome.min.css'/>">
<link rel="stylesheet" type="text/css"
	href="<c:url value='/vendors/themify-icons/css/themify-icons.css'/>">
<link rel="stylesheet" type="text/css"
	href="<c:url value='/vendors/flag-icon-css/css/flag-icon.min.css'/>">
<link rel="stylesheet" type="text/css"
	href="<c:url value='/vendors/selectFX/css/cs-skin-elastic.css'/>">

<link rel="stylesheet" type="text/css"
	href="<c:url value='/vendors/datatables.net-bs4/css/dataTables.bootstrap4.min.css'/>">
<link rel="stylesheet" type="text/css"
	href="<c:url value='/vendors/datatables.net-buttons-bs4/css/buttons.bootstrap4.min.css'/>">

<link rel="stylesheet" type="text/css"
	href="<c:url value='/assets/css/style.css'/>">
<link rel="stylesheet" type="text/css"
	href="<c:url value='/assets/css/robot_tff.css'/>">

<link rel="stylesheet" type="text/css"
	href="<c:url value='/assets/css/font_awesome4.css'/>">
<link rel="stylesheet" type="text/css"
	href="<c:url value='/assets/css/material-dashboard.css?v=2.1.1" rel="stylesheet'/>">
	
<link rel="stylesheet" href="<c:url value = '/css/main.css'/>" />





</head>

<body>


	<!-- Left Panel -->

	<!-- Left Panel -->


	<aside id="left-panel" class="left-panel">
		<nav class="navbar navbar-expand-sm navbar-default">

			<div class="navbar-header">
				<button class="navbar-toggler" type="button" data-toggle="collapse"
					data-target="#main-menu" aria-controls="main-menu"
					aria-expanded="false" aria-label="Toggle navigation">
					<i class="fa fa-bars"></i>
				</button>

			</div>

			<div id="main-menu" class="main-menu collapse navbar-collapse"
				style="height: 140vh !important; margin-top: -344px !important;">
				<ul class="nav navbar-nav">
					<li class="active"><a href="<c:url value='/dashboard'/>">
							<i class="menu-icon fa fa-dashboard"></i>Dashboard
					</a></li>

					<h3 class="menu-title"></h3>

					<sec:authorize access="hasRole('ADMIN') or hasRole('CUSTOM')">
						<h3 class="menu-title">USER MANAGEMENT</h3>
						<!-- /.menu-title -->
						<li class="menu-item-has-children dropdown"><a href="#"
							class="dropdown-toggle" data-toggle="dropdown"
							aria-haspopup="true" aria-expanded="false"> <i
								class="menu-icon fa fa-users"></i>User Settings
						</a>
							<ul class="sub-menu children dropdown-menu">
								<li><i class="fa fa-puzzle-piece"></i><a
									href="<c:url value='/createuser'/>"><spring:message
											code="create.user.setting" /></a></li>
								
								<li><i class="fa fa-id-badge"></i><a
									href="<c:url value='/edituser'/>"><spring:message
											code="edit.usertorole.setting" /></a></li>
											
								<li><i class="fa fa-id-badge"></i><a
									href="<c:url value='/chat'/>"><spring:message
											code="edit.usertorole.setting" /></a></li>
											

							</ul></li>

						
								<%-- <li><i class="menu-icon fa fa-chain"></i><a
									href="<c:url value="/createcustom" />"><spring:message
											code="create.cust.feat" /></a></li>

								<li><i class="menu-icon fa fa-chain"></i><a
									href="<c:url value="/authcustomrole" />"><spring:message
											code="auth.pend.custom" /></a></li>
								<li><i class="menu-icon fa fa-chain"></i><a
									href="<c:url value="/modifycreatecustom" />"><spring:message
											code="modify.cust.feat" /></a></li>

								<li><i class="menu-icon fa fa-chain"></i><a
									href="<c:url value="/authmodcustomrole" />"><spring:message
											code="auth.pend.mod.custom" /></a></li> --%>

							</ul></li> 
					</sec:authorize>

					<h3 class="menu-title"></h3>
					<!-- /.menu-title -->
					<li class=""><a class="nav-link"
						href="<c:url value ='/logout'/>"><i
							class="menu-icon fa fa-power-off"></i> Logout</a></li>

				</ul>
			</div>
			<!-- /.navbar-collapse -->
		</nav>
	</aside>
	<!-- /#left-panel -->
	<!-- Left Panel -->

	<!-- Right Panel -->

	<div id="right-panel" class="right-panel">

		<!-- Header-->
		<header id="header" class="header">

			<div class="header-menu">

				<div class="col-sm-7">
					<a id="menuToggle" class="menutoggle pull-left"><i
						class="fa fa fa-tasks"></i></a>

				</div>



				<div class="col-sm-5">

					<div class="user-area dropdown float-right">
						<a href="#" class="dropdown-toggle" data-toggle="dropdown"
							aria-haspopup="true" aria-expanded="false"> <!-- <img class="user-avatar rounded-circle" src="images/4.png" alt="User Avatar"> -->
							<button class="btn"
								style="background-image: linear-gradient(-45deg, #00578a, #9496a1) !important;">
								<b>${loggedinuser.fullName} | ${loggedinuser.userRolesStr}</b>
							</button>
						</a>


					</div>


				</div>
			</div>

		</header>
		<!-- /header -->
		<!-- Header-->


		<!--</div> /#right-panel -->

		<!-- Right Panel -->


		<script src="<c:url value='/vendors/jquery/dist/jquery.min.js'/>"></script>
		<script
			src="<c:url value='/vendors/popper.js/dist/umd/popper.min.js'/>"></script>
		<script
			src="<c:url value='/vendors/bootstrap/dist/js/bootstrap.min.js'/>"></script>
		<script src="<c:url value='/assets/js/main.js'/>"></script>

		<script src="<c:url value='/js/jquery.js' />"></script>
		<script src="<c:url value='/js/angular.min.js' />"></script>


		<script
			src="<c:url value='/vendors/datatables.net/js/jquery.dataTables.min.js'/>"></script>
		<script
			src="<c:url value='/vendors/datatables.net-bs4/js/dataTables.bootstrap4.min.js'/>"></script>
		<script
			src="<c:url value ='/vendors/datatables.net-buttons/js/dataTables.buttons.min.js'/>"></script>
		<script
			src="<c:url value='/vendors/datatables.net-buttons-bs4/js/buttons.bootstrap4.min.js'/>"></script>
		<script src="<c:url value='/vendors/jszip/dist/jszip.min.js'/>"></script>
		<script src="<c:url value='/vendors/pdfmake/build/pdfmake.min.js'/>"></script>
		<script src="<c:url value='/vendors/pdfmake/build/vfs_fonts.js'/>"></script>
		<script
			src="<c:url value='/vendors/datatables.net-buttons/js/buttons.html5.min.js'/>"></script>
		<script
			src="<c:url value='/vendors/datatables.net-buttons/js/buttons.print.min.js'/>"></script>
		<script
			src="<c:url value='/vendors/datatables.net-buttons/js/buttons.colVis.min.js'/>"></script>
		<script
			src="<c:url value='/assets/js/init-scripts/data-table/datatables-init.js'/>"></script>
		<script>
			(function($) {
				"use strict";

				jQuery('#vmap').vectorMap({
					map : 'world_en',
					backgroundColor : null,
					color : '#ffffff',
					hoverOpacity : 0.7,
					selectedColor : '#1de9b6',
					enableZoom : true,
					showTooltip : true,
					values : sample_data,
					scaleColors : [ '#1de9b6', '#03a9f5' ],
					normalizeFunction : 'polynomial'
				});
			})(jQuery);
		</script>
</body>

</html>
