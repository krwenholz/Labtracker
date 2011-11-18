/**
* Fetches data from the server using json, ajax, and php (I think).  
*/
function fetchData(){
    alert("looking for data");
    //json grabs my data and spits it into the graph drawer
    $.getJSON('updateData.php', function(data){
	alert('I got data!');
	drawGraphs(data[0], data[1], data[2], data[3]);
    });
   // alert("fetching data");
   // request = new XMLHttpRequest();
   // request.onreadystatechange = handleResponse;
   // request.open("POST", "updateData.php", true);
   // request.send();
}

function handleResponse(){
    if (this.readyState == 4 && this.status == 200) {
        // Everything is good, the response is received
	myDatas = this.responseText.split('###');
	alert("drawing graphs");
	alert(myDatas
	drawGraphs(myDatas[0], myDatas[1], myDatas[2], myDatas[3]);
    }
}

/**
* The onload function creates the chart
*/
function drawGraphs(activityData, activityLabels, compileData, compileLabels){
    var hbar1 = new RGraph.HBar('activityCanvas', activityData);
      alert('chart initialized');
      hbar1.Set('chart.background.grid', true);
      hbar1.Set('chart.colors', ['D84704']);
      hbar1.Set('chart.text.color', '#171717');
      alert('colors initialized');

      hbar1.Set('chart.labels', activityLabels);

      hbar1.Set('chart.gutter.left', 275);
      hbar1.Set('chart.gutter.right', 10);
      hbar1.Set('chart.background.grid.autofit', true);
      hbar1.Set('chart.shadow', true);
      hbar1.Set('chart.shadow.color', 'gray');
      hbar1.Set('chart.shadow.offsetx', 0);
      hbar1.Set('chart.shadow.offsety', 0);
      hbar1.Set('chart.shadow.blur', 15);

      alert("activities drawn");

    var hbar2 = new RGraph.HBar('compilesCanvas', compileData);
      alert('chart initialized');
      hbar2.Set('chart.background.grid', true);
      hbar2.Set('chart.colors', ['D84704']);
      hbar2.Set('chart.text.color', '#171717');
      alert('colors initialized');

      hbar2.Set('chart.labels', compileLabels);
      alert('chart created');

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
      }alert("compiles drawn");
}