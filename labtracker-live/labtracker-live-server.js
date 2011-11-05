/*SELECT methodName, COUNT(*)
FROM compile_errors
GROUP BY methodName
ORDER BY COUNT(*) DESC;

SELECT errorType, COUNT(*)
FROM compile_errors
WHERE NOW() - TIMESTAMP < 2300
GROUP BY errorType
ORDER BY COUNT(*) DESC;

SELECT TimeStamp - NOW()
FROM compile_errors;*/
var connection = new ActiveXObject("ADODB.Connection") ;

var connectionstring="Data Source='localhost:3306/labtracker';Initial Catalog=<catalog>;User ID=<user>;Password=<password>;Provider=SQLOLEDB";

connection.Open(connectionstring);
var rs = new ActiveXObject("ADODB.Recordset");

rs.Open("SELECT * FROM table", connection);
rs.MoveFirst
while(!rs.eof)
{
   document.write(rs.fields(1));
   rs.movenext;
}

rs.close;
connection.close;
