<?PHP

	require("pdo.php");
	
	$user = $_GET["user"];
	
	$pdo = new mypdo();
	
	$sql = "SELECT * FROM `user_favorite_star` NATURAL JOIN `star_data` WHERE `ID`=:user";
	$NameSearch = array();
	$NameSearch["user"] = $user;
	
	$data = $pdo->bindQuery($sql,$NameSearch);
	
	$result = array();
	$result["data"] = array();
	
	foreach($data as $row)
	{
		array_push($result["data"],$row);
	}
	echo json_encode($result);