<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="webInterface.HTMLFooter"%>
<html>
	<head>
		<script type="text/javascript">
			function checkFile(thisForm) {
				with (thisForm){ 
					with (fileLoc) {
						if (value == null || value == "") {
							alert('You must select a file first!');
							return false;
						} else if (value.substring(value.lastIndexOf(".")) != ".mp3") {
							alert('You must only select mp3 files!');
							return false;
						}
					}
				}
				return true;
			}
		</script>
		<title>Upload a file to the server</title>
		<link rel="stylesheet" type="text/css" href="../styles/mainstylesheet.css" />
		<link rel="stylesheet" type="text/css" href="../styles/buttons.css" />
		<link rel="icon" href="../images/favicon.png" type="image/png" />
		<link rel="shortcut icon" href="../favicon.ico" />
	</head>
	<body>
		<div class="group" >
			<div class="center plainText" title="Hi, I'm tool-tip">
				<table class="center" border="0" >
					<tr>
						<td width="15%" ></td>
						<td style="padding: 0">
							<a href="mainMenu.jsp">
								<img class="picLink" src="../images/iTunesLogo.PNG" title="Online-iTunes Logo" />
							</a>
						</td>
						<td>
							<h2>Find an MP3 file that you would like to upload to our servers.</h2>
						</td>
						<td width="15%" ></td>
					</tr>
				</table>
			</div>
			<div class="input-form center" title="MP3 File location">
				<form method="post" action="uploadresult.jsp" method="get" enctype="multipart/form-data" onsubmit="return checkFile(this)">
					<input class="formButton" type="file" name="fileLoc" size="40" style="margin-top: .2cm" />
					<br>
					<input class="button" type="submit" value="Submit" title="Submit File for Upload" style="margin-top: .2cm; margin-bottom: .1cm" >
				</form>
			</div>
		</div>
		<%= HTMLFooter.getFooter() %>
	</body>
</html>
