<?php

   /**
    * Change these to your own credentials
    */
	//echo("I'm in the php");
    $hostname = "10.150.0.169:3306";
    $username = "testuser";
    $password = "testpass";
    $database = "labtracker";
    
    $connection = mysql_connect($hostname, $username, $password) 
					OR die('Could not connect to MySQL: ' . mysql_error());
    mysql_select_db($database);
    
   $result = mysql_query("SELECT SUB1.MethodName, COUNT(*)
			 FROM (SELECT UserID, MethodName
			       FROM compile_errors C1
			       WHERE TimeStamp >= ALL(SELECT TimeStamp FROM compile_errors C2 WHERE C1.UserID = C2.UserID) AND NOW() - TimeStamp < 500) AS SUB1
			 GROUP BY SUB1.MethodName
			 ORDER BY COUNT(*) DESC;");
    if ($result) {
    
        $compileLabels = array();
        $compileData  = array();
    
        while ($row = mysql_fetch_assoc($result)) {
            $compileLabels[] = $row["methodName"];
            $compileData[]   = $row["COUNT(*)"];
        }

        // Now you can aggregate all the data into one string
        $compileData_string = "[" . join(", ", $compileData) . "]";
        $compileLabels_string = "['" . join("', '", $compileLabels) . "']";
    } else {
        print('MySQL query failed with error: ' . mysql_error());
    }
   $result = mysql_query("SELECT MethodName, COUNT(*) FROM activity_logs 
				WHERE NOW() - TIMESTAMP < 500 GROUP BY MethodName ORDER BY COUNT(*) DESC;");
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
?>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <link rel="stylesheet" href="bootstrap.css" type="text/css" media="screen" title="no title" charset="utf-8">
   
    <!--Don\'t forget to update these paths -->

    <script src="libraries/RGraph.common.core.js" ></script>
    <script src="libraries/RGraph.common.tooltips.js" ></script>

    <script src="libraries/RGraph.common.effects.js" ></script>
    <script src="libraries/RGraph.hbar.js" ></script>

    <script language="javascript" type="text/javascript" charset="utf-8" src="labtracker-live-main.js"></script>
    <script language="javascript" type="text/javascript" charset="utf-8" src="labtracker-live-server.js"></script>
    <script language="javascript" type="text/javascript" charset="utf-8" src="labtracker-live-visualization.js"></script>

    <title>LabTracker-Live</title>    

</head>
<body>
    
    <h1><span>Labtracker-Live: Live Client</span></h1>
      <!--black = 171717 23,23,23
      dark blue = 0C5A81 12,90,129
      light grey = D7E0E5 215,224,229
      white = FFFFFF 255,255,255
      orange = D84704 216,71,4
      -->
    <h2>Current Activity</h2>
    <canvas id="activityCanvas" width="900" height="400">[No canvas support]</canvas>
    <h2>Recent Compile Errors</h2>
    <canvas id="compilesCanvas" width="900" height="400">[No canvas support]</canvas>
    <script>
        /**
        * The onload function creates the chart
        */
        window.onload = function ()
        {
            var hbar1 = new RGraph.HBar('activityCanvas', <?php print($activityData_string) ?>);
            //alert('chart initialized');
	    hbar1.Set('chart.background.grid', true);
	    hbar1.Set('chart.colors', ['D84704']);
	    hbar1.Set('chart.text.color', '#171717');
            //alert('colors initialized');
            
            hbar1.Set('chart.labels', <?php print($activityLabels_string) ?>);
			
            hbar1.Set('chart.gutter.left', 275);
            hbar1.Set('chart.gutter.right', 10);
            hbar1.Set('chart.background.grid.autofit', true);
            hbar1.Set('chart.shadow', true);
            hbar1.Set('chart.shadow.color', 'gray');
            hbar1.Set('chart.shadow.offsetx', 0);
            hbar1.Set('chart.shadow.offsety', 0);
            hbar1.Set('chart.shadow.blur', 15);
	    
	    //alert("activities drawn");
        

            var hbar2 = new RGraph.HBar('compilesCanvas', <?php print($compileData_string) ?>);
            //alert('chart initialized');
	    hbar2.Set('chart.background.grid', true);
	    hbar2.Set('chart.colors', ['D84704']);
	    hbar2.Set('chart.text.color', '#171717');
            //alert('colors initialized');
            
            hbar2.Set('chart.labels', <?php print($compileLabels_string) ?>);
	    //alert('chart created');
			
            hbar2.Set('chart.gutter.left', 275);
            hbar2.Set('chart.gutter.right', 10);
            hbar2.Set('chart.background.grid.autofit', true);
            hbar2.Set('chart.shadow', true);
            hbar2.Set('chart.shadow.color', 'gray');
            hbar2.Set('chart.shadow.offsetx', 0);
            hbar2.Set('chart.shadow.offsety', 0);
            hbar2.Set('chart.shadow.blur', 15);
            
	    if (RGraph.isIE8()) {
                hbar1.Draw();
                hbar2.Draw();
            } else {
                RGraph.Effects.HBar.Grow(hbar1);
		RGraph.Effects.HBar.Grow(hbar2);
            }//alert("compiles drawn");
        }
    </script>
</body>
</html>