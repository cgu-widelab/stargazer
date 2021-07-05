<?PHP

	require("pdo.php");

	$place = $_GET["place"];
	
	$pdo = new mypdo();
	
	$sql = "SELECT * FROM `place_data` NATURAL JOIN `location_data` WHERE `place_name` = :place";
	
	$placeArray = array();
	$placeArray["place"] = $place;
	$data = $pdo->bindQuery($sql,$placeArray);
	
	$result = array();
	$result["data"] = array();
	foreach($data as $row)
	{
		array_push($result["data"],$row);
	}
	
	
	echo json_encode($result);
?>