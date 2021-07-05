<?PHP
	
	require("pdo.php");
	
	$season = $_GET["season"];

	$pdo = new mypdo();
	
	$sql = "SELECT `star_season`.`season`,`star_data`.`star_name_ch` FROM `star_season` NATURAL JOIN `star_data` WHERE `season` = :targetSeason" ;
	
	$seasonSearch = array();
	$seasonSearch["targetSeason"] = $season;
	
	$data = $pdo->bindQuery($sql,$seasonSearch);
	
	$result = array();
	$result["data"] = array();
	
	foreach($data as $row)
	{
		array_push($result["data"],$row);
	}
	
	echo json_encode($result);
?>