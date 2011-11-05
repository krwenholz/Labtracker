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
    
   $result = mysql_query("SELECT MethodName, COUNT(*) FROM compile_errors 
				WHERE NOW() - TIMESTAMP < 2300 GROUP BY MethodName ORDER BY COUNT(*) DESC;");
    if ($result) {
    
        $labels = array();
        $data   = array();
    
        while ($row = mysql_fetch_assoc($result)) {
            $labels[] = $row["MethodName"];
            $data[]   = $row["COUNT(*)"];
        }

        // Now you can aggregate all the data into one string
        $data_string = "[" . join(", ", $data) . "]";
        $labels_string = "['" . join("', '", $labels) . "']";
    } else {
        print('MySQL query failed with error: ' . mysql_error());
    }
?>
<html>
	<Title>
	Activity Logging
	</Title>
<head>

    <!-- Don't forget to update these paths -->

	<script src="libraries/RGraph.common.core.js" ></script>
    <script src="libraries/RGraph.common.tooltips.js" ></script>

    <script src="libraries/RGraph.common.effects.js" ></script>
    <script src="libraries/RGraph.hbar.js" ></script>
    
</head>
<body>
    
    <h1><span>Labtracker-Live:Compile Errors</span></h1>
    <canvas id="myCanvas" width="900" height="400">[No canvas support]</canvas>
    <script>
        /**
        * The onload function creates the chart
        */
        window.onload = function ()
        {
            var hbar1 = new RGraph.HBar('myCanvas', <?php print($data_string) ?>);
            //alert('chart initialized');
            var grad = hbar1.context.createLinearGradient(275,0,900, 0);
            grad.addColorStop(0, 'white');
            grad.addColorStop(1, 'black');
            //alert('colors initialized');
            
			hbar1.Set('chart.labels', <?php print($labels_string) ?>);
			//alert('chart created');
			
            hbar1.Set('chart.strokestyle', 'rgba(0,0,0,0)');
            hbar1.Set('chart.gutter.left', 275);
            hbar1.Set('chart.gutter.right', 10);
            hbar1.Set('chart.background.grid.autofit', true);
            hbar1.Set('chart.shadow', true);
            hbar1.Set('chart.shadow.color', 'gray');
            hbar1.Set('chart.shadow.offsetx', 0);
            hbar1.Set('chart.shadow.offsety', 0);
            hbar1.Set('chart.shadow.blur', 15);
            hbar1.Set('chart.colors', [grad]);
            
            if (RGraph.isIE8()) {
                hbar1.Draw();
            } else {
                RGraph.Effects.HBar.Grow(hbar1);
            }
        }
    </script>

</body>
</html>
