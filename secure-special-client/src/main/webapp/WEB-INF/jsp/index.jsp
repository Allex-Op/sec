<!-- <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd"> -->
<!DOCTYPE html>

<html lang="en">
<head>
	<meta charset="UTF-8">
	<title>Highly Dependable Location Tracker</title>
	<!-- bootstrap -->
	<link 
		rel="stylesheet" 
		href="https://use.fontawesome.com/releases/v5.7.0/css/all.css"
		integrity="sha384-lZN37f5QGtY3VHgisS14W3ExzMWZxybE1SJSEsQp9S+oqd12jhcu+A56Ebc1zFSJ"
		crossorigin="anonymous"
	>
	
	<style>
		#navbar {
			list-style-type: none;
			margin: 0;
  			padding: 0;
			height: 105px;
			width: 500px;
			border: 1px solid #555;
			background-color: #bdd6ff;
		}
		#navbar li {
			border-bottom: 1px solid #555;
		}
		#navbar li a {
			display: block;
			color: #000;
			padding: 8px 16px;
  			text-decoration: none;
		}
		#navbar li a:hover {
			background-color: #555;
			color: white;
		}
	</style>
</head>
<body>
	<h2><span class="fas fa-shield-alt"></span> Main Menu </h2>
	<ul id="navbar">
		<li><a href="/home"><span class="fas fa-home"> Home</span></a>
		<li><a href="/user_report_form"><span class="fas fa-address-card"> Get report of an user </span></a>
		<li><a href="/users_at_location_form"><span class="fas fa-street-view"> Search for users at a location of a given epoch </span></a>
	</ul>
</body>
</html>