<!-- <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd"> -->
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>

<html lang="en">
<head>
	<meta charset="UTF-8">
	<title>Highly Dependable Location Tracker: Get Report of an User</title>
	<!-- bootstrap -->
	<link 
		rel="stylesheet" 
		href="https://use.fontawesome.com/releases/v5.7.0/css/all.css"
		integrity="sha384-lZN37f5QGtY3VHgisS14W3ExzMWZxybE1SJSEsQp9S+oqd12jhcu+A56Ebc1zFSJ"
		crossorigin="anonymous"
	>
</head>
<body>
	<div>
		<h2><span class="fas fa-address-card"> Get Report of an User </span></h2>
		
		<form:form action="users_at_location_form" method="post" modelAttribute="appModel">
			<form:label path="userId">User ID:</form:label>
			<form:input path="userId"/><br/>
			
			<form:label path="epoch">Epoch:</form:label>
			<form:input path="epoch"/><br/>
			
			<form:label path="x">X Location:</form:label>
			<form:input path="x"/><br/>
			
			<form:label path="y">Y Location:</form:label>
			<form:input path="y"/><br/>
			
			<form:button>Register</form:button>
		</form:form>
	</div>
</body>
</html>