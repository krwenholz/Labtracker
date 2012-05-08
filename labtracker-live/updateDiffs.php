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
	$result =mysql_query("SELECT MethodName, Diff
						FROM code_diffs AS c
						WHERE SessionID = \"Lab11\"
							AND TimeStamp >= ALL (SELECT TimeStamp
													FROM code_diffs
													WHERE UserID=c.UserID
														AND MethodName=c.MethodName)
						ORDER BY MethodName;");

//set up the feature vector
	$terms = array();
	$terms[0] = "for";
	$terms[1] = "while";
	$terms[2] = "if";
	$terms[3] = "else";
	$terms[4] = "return";
	$terms[5] = "try";
	$terms[6] = "catch";
	$terms[7] = "import";
	$counts = array();
        
	$methods_string = array();
	$diffs_string = array();
	$clusters_string = array();
	
    if ($result) {
		
        $methods  = array();
        $diffs    = array();
        $features = array();
        $clusters = array();
    
        while ($row = mysql_fetch_assoc($result)) {
            $methods[] = $row["MethodName"];
            $diffs[]   = $row["Diff"];

			//count the number of times a keyword appears in the diff
			for($i=0; $i<count($terms); $i++) {
				$counts[$i] = 0;
				$lastIdx = 0;
				while(stripos($row["Diff"], $terms[$i], $lastIdx) !== false) {
					$counts[$i]++;
					$lastIdx = stripos($row["Diff"], $terms[$i], $lastIdx) + 1;
				}
			}
			
			$features[] = "[" . join(", ", $counts) . "]";
        }
        
        
        $goodData = array();
        for($i=0; $i<count($features); $i++){
			$vec = $features[$i];
			$vec = trim($vec, "[]"); //get rid of []
			$vec = explode(", ", $vec); //reformat as array
			$goodData[$i] = $vec;
		}
		//find the maximum count of a keyword in order to normalize
		$maxVal = array();
		for($j=0; $j<count($goodData[0]); $j++) {
			$maxVal[$j] = 0;
			for($i=0; $i<count($goodData); $i++) {
				if(intval($goodData[$i][$j]) > $maxVal[$j]) {
					$maxVal[$j] = intval($goodData[$i][$j]);
				}
			}
		}
		//normalize the feature vectors
		for($i=0; $i<count($goodData); $i++) {
			for($j=0; $j<count($goodData[0]); $j++) {
				if($maxVal[$j] > 0) {
					$goodData[$i][$j] = $goodData[$i][$j] / $maxVal[$j];
				}
			}
			$features[$i] = "[" . join(", ", $goodData[$i]) . "]";
		}

		//build up a String[] of args to pass to the .jar
        $args = array();
        $args[0] = "[" . join(", ", $terms) . "]";
        for($i=0; $i<count($features); $i++) {
			$args[$i+1] = $features[$i];
		}

		//set up the path and execute the clustering
		$saved = getenv("PATH");
		$newpd = "C:\xampp\htdocs\labtracker-live";
		if ($saved) { $newpd .= ";$saved"; }
		putenv("PATH=$newpd");
		$cmd = "java -jar clusterer.jar \"" . join("\" \"", $args) . "\"";
		exec($cmd, $clusters);
		putenv("PATH=$saved");
        
        // Now you can aggregate all the data into one string
        $methods_string = "['" . join("', '", $methods) . "']";
        $diffs_string = "[" . join(", ", $diffs) . "]";
		$clusters_string = "[" . join(", ", $clusters) . "]";
    } else {
        print('MySQL query failed with error: ' . mysql_error());
    }

	//make a multidimensional array for everything then encode with json
	$arr = array($methods_string, $diffs_string, $clusters_string);

	echo json_encode($arr);
	mysql_close($connection);
?>
