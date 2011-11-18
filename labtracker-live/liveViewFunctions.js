var hbar1;
var hbar2;
/**
* Fetches data from the server using json, ajax, and php (I think).  
*/
function fetchData(){
    //alert("looking for data");
    //contact the server
    request = new XMLHttpRequest();
    //set up a handler
    request.onreadystatechange = handleResponse;
    //send the request
    request.open('POST', 'updateData.php', true);
    request.send();
}

/**
* Handles the response from the server for more data.
*/
function handleResponse(){
    if (this.readyState == 4 && this.status == 200) {
	//alert(this.responseText);
	var myDatas = eval('('+this.responseText+')');
	//alert(myDatas);
	fixInts(myDatas);
	fixStrings(myDatas);
	//alert(myDatas[0][0]);
	drawGraphs(myDatas[0], myDatas[1], myDatas[2], myDatas[3]);
    }
}

/**
* Corrects the data type of an array of integers
*/
function fixInts(datas){
    //FIX INTEGERS
    data = datas[0];
    data = data.substring(1, data.length);
    data = data.substring(0, data.length - 1);
    data = data.split(",");
    for(i=0; i<data.length; i=i+1){
	data[i] = parseInt(data[i]);
    }
    datas[0] = data;
    data = datas[2];
    data = data.substring(1, data.length);
    data = data.substring(0, data.length - 1);
    data = data.split(",");
    for(i=0; i<data.length; i=i+1){
	data[i] = parseInt(data[i]);
    }
    datas[2] = data;
}

/**
* Corrects the data type of an array of strings.
*/
function fixStrings(datas){
    //FIX STRINGS
    data = datas[1];
    data = data.substring(2, data.length);
    data = data.substring(0, data.length - 2);
    data = data.split("', '");
    datas[1] = data;
    data = datas[3];
    data = data.substring(2, data.length);
    data = data.substring(0, data.length - 2);
    data = data.split("', '");
    datas[3] = data;
}



/**
* The onload function creates the chart
*/
function drawGraphs(activityData, activityLabels, compileData, compileLabels){
     hbar1 = new RGraph.HBar('activityCanvas', activityData);
      //alert('chart initialized');
      hbar1.Set('chart.background.grid', true);
      hbar1.Set('chart.colors', ['D84704']);
      hbar1.Set('chart.text.color', '#171717');
      //alert('colors initialized');

      hbar1.Set('chart.labels', activityLabels);

      hbar1.Set('chart.gutter.left', 275);
      hbar1.Set('chart.gutter.right', 10);
      hbar1.Set('chart.background.grid.autofit', true);
      hbar1.Set('chart.shadow', true);
      hbar1.Set('chart.shadow.color', 'gray');
      hbar1.Set('chart.shadow.offsetx', 0);
      hbar1.Set('chart.shadow.offsety', 0);
      hbar1.Set('chart.shadow.blur', 15);

      //alert("activities drawn");

     hbar2 = new RGraph.HBar('compilesCanvas', compileData);
      //alert('chart initialized');
      hbar2.Set('chart.background.grid', true);
      hbar2.Set('chart.colors', ['D84704']);
      hbar2.Set('chart.text.color', '#171717');
      //alert('colors initialized');

      hbar2.Set('chart.labels', compileLabels);
      //alert('chart created');

      hbar2.Set('chart.gutter.left', 275);
      hbar2.Set('chart.gutter.right', 10);
      hbar2.Set('chart.background.grid.autofit', true);
      hbar2.Set('chart.shadow', true);
      hbar2.Set('chart.shadow.color', 'gray');
      hbar2.Set('chart.shadow.offsetx', 0);
      hbar2.Set('chart.shadow.offsety', 0);
      hbar2.Set('chart.shadow.blur', 15);

    hbar1.Draw();
    hbar2.Draw();
      //alert("compiles drawn");
} 
