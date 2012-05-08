<?php
    $hostname = "10.150.0.169:3306";
    $username = "testuser";
    $password = "testpass";
    $database = "labtracker";
    
    $connection = mysql_connect($hostname, $username, $password) 
		OR die('Could not connect to MySQL: ' . mysql_error());
    mysql_select_db($database);

	///////////////////////////////
	//BUILD THE ACTIVITY DATA
	///////////////////////////////

// CHANGE THIS PART to reflect your lab's session ID
// also change the INTERVAL # MINUTE such that # is your lab's start time
// (e.g. the INTERVAL 30 MINUTE for a lab that starts at 10:30)	
	$result =mysql_query("SELECT UserID, MethodName, DATE_SUB(TimeStamp, INTERVAL 30 MINUTE) AS Time
						  FROM activity_logs
						  WHERE SessionID = \"Lab11\"
						  ORDER BY UserID, MINUTE(Time);");

	$activityLabels_string = array();
	$activityUsers_string = array();
	$activityTimes_string = array();
	
    if ($result) {
		
        $labels = array();
        $users   = array();
        $times = array();
    
        while ($row = mysql_fetch_assoc($result)) {
            $labels[] = $row["MethodName"];
            $users[]  = $row["UserID"];
            $times[] = $row["Time"];
        }

        // Now you can aggregate all the data into one string
        $activityLabels_string = "['" . join("', '", $labels) . "']";
        $activityUsers_string = "[" . join(", ", $users) . "]";
        $activityTimes_string = "[" . join(", ", $times) . "]";
    } else {
        print('MySQL query failed with error: ' . mysql_error());
    }

	//make a multidimensional array for everything then encode with json
	$arr = array($activityLabels_string, $activityUsers_string, $activityTimes_string);


	echo json_encode($arr);
	mysql_close($connection);
?>
