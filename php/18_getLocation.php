<?PHP

	require("pdo.php");
	
	$pdo = new mypdo();
	$cityPDO = new mypdo();
	
	$sql = "SELECT * FROM `location_data`";
	$citySQL = "SELECT DISTINCT `city/county` FROM `location_data`";
	
	$data = $pdo->bindQuery($sql);
	$city = $pdo->bindQuery($citySQL);
	
	$result = array();
	$result["data"] = array();
	$result["city"] = array();
	
	foreach($city as $row)
	{
		array_push($result["city"],$row);
	}
	
	foreach($data as $row)
	{
		array_push($result["data"],$row);
	}
	
	echo json_encode($result);