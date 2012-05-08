<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<?php
	$Result = $_GET['Result'];
	//print($Result); //when this line gets put in I get unterminated string constant
    /**
    * Change these to your own credentials
    */
	//echo("I'm in the php");
    $hostname = "10.150.0.169:3306";
    $username = "testuser";
    $password = "testpass";
    $database = "labtracker";
    
    $connection = mysql_connect($hostname, $username, $password) OR die('Could not connect to MySQL: ' . mysql_error());
    mysql_select_db($database);
        
    mysql_query("ALTER TABLE `compile_errors`  CHANGE COLUMN `SessionID` 
			`SessionID` VARCHAR(50) NOT NULL DEFAULT"." '".$Result."' AFTER `UserID`;");

    mysql_query("ALTER TABLE `activity_logs`  CHANGE COLUMN `SessionID` 
			`SessionID` VARCHAR(50) NOT NULL DEFAULT"." '". $Result."' AFTER `UserID`;");
	
    mysql_query("ALTER TABLE `code_diffs`  CHANGE COLUMN `SessionID` 
			`SessionID` VARCHAR(50) NOT NULL DEFAULT"." '". $Result."' AFTER `UserID`;");
 ?>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <link rel="stylesheet" href="bootstrap.css" type="text/css" media="screen" title="no title" charset="utf-8">
	<script language="javascript" type="text/javascript" charset="utf-8" src="labtracker-live-main.js"></script>
	<script language="javascript" type="text/javascript" charset="utf-8" src="labtracker-live-server.js"></script>
	<script language="javascript" type="text/javascript" charset="utf-8" src="labtracker-live-visualization.js"></script>
	<title>LabTracker-Live</title>
</head>
<body>
      <div class="topbar">
      <div class="topbar-inner">
        <div class="container-fluid">
          <a class="brand" href="#">LabTracker Live</a>

          <ul class="nav">
            <li class="active"><a href="#">Home</a></li>
            <li><a href="liveClient.html">Live View</a></li>
          </ul>
        </div>
      </div>
    </div>

</body>
</html>
