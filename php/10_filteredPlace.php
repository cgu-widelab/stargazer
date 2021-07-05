<?PHP
	
	require("pdo.php");
	
	$city = $_GET["city"];
	$section = $_GET["section"];
	
	$pdo = new mypdo();
	
	$sql =
	"SELECT 
		`place_data`.`zip_code`,
		`place_data`.`place_name`,
		`location_data`.`city/county`,
		`location_data`.`section/town`,
		CONCAT(`location_data`.`city/county`,`location_data`.`section/town`,`place_data`.`other_Address`) AS `place_address`
	FROM	
		`location_data`NATURAL JOIN `place_data`
	WHERE
		`location_data`.`city/county`= :city AND
		`location_data`.`section/town`= :section";
	
	$filter = array();
	
	$filter["city"] = $city;
	$filter["section"] = $section;
	
	$data = $pdo->bindQuery($sql,$filter);
	
	$result = array();
	$result["data"] = array();
	
	foreach($data as $row)
	{
		array_push($result["data"],$row);
	}
	
	echo json_encode($result);