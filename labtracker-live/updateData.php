<?php
    //echo("I'm in the php");
    $hostname = "10.150.0.169:3306";
    $username = "testuser";
    $password = "testpass";
    $database = "labtracker";
    
    $connection = mysql_connect($hostname, $username, $password) 
		OR die('Could not connect to MySQL: ' . mysql_error());
    mysql_select_db($database);
///////////////////////////
//BUILD THE COMPILE DATA   
////////////////////////// 
   $result = mysql_query("SELECT errorType, COUNT(DISTINCT userID)
   	     			 FROM compile_errors
				 WHERE NOW() - TIMESTAMP < 500
				 GROUP BY errorType
				 ORDER BY COUNT(DISTINCT userID) DESC;");
    if ($result) {
    
        $compileLabels = array();
        $compileData  = array();
    
        while ($row = mysql_fetch_assoc($result)) {
            $compileLabels[] = $row["MethodName"];
            $compileData[]   = $row["COUNT(*)"];
        }

        // Now you can aggregate all the data into one string
        $compileData_string = "[" . join(", ", $compileData) . "]";
        $compileLabels_string = "['" . join("', '", $compileLabels) . "']";
    } else {
        print('MySQL query failed with error: ' . mysql_error());
    }
///////////////////////////////
//BUILD THE ACTIVITY DATA
///////////////////////////////
   $result = mysql_query("SELECT SUB1.MethodName, COUNT(*)
			 FROM (SELECT UserID, MethodName
			       FROM activity_logs C1
			       WHERE TimeStamp >= ALL(SELECT TimeStamp FROM 
			       	     activity_logs C2 WHERE C1.UserID = 
				     C2.UserID) AND NOW() - TimeStamp < 600) 
				     AS SUB1
			 GROUP BY SUB1.MethodName
			 ORDER BY COUNT(*) DESC;");
    if ($result) {
    
        $labels = array();
        $data   = array();
    
        while ($row = mysql_fetch_assoc($result)) {
            $labels[] = $row["MethodName"];
            $data[]   = $row["COUNT(*)"];
        }

        // Now you can aggregate all the data into one string
        $activityData_string = "[" . join(", ", $data) . "]";
        $activityLabels_string = "['" . join("', '", $labels) . "']";
    } else {
        print('MySQL query failed with error: ' . mysql_error());
    }

//make a multidimensional array for everything then encode with json
$arr = array($activityData_string, $activityLabels_string, $compileData_string, 
     $compileLabels_string);


echo json_encode($arr);
mysql_close($connection)
?>
