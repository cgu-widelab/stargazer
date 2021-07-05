<?PHP

	require("pdo.php");
	
	$user = $_GET["user"];
	$place = $_GET["place"];
	
	$pdo = new mypdo();
	
	$sql = "SELECT * FROM `user_favorite_place` WHERE `ID`=:user AND `place_name`=:place";
	
	$addArray = array();
	$addArray["user"] = $user;
	$addArray["place"] = $place;
	
	$data = $pdo->bindQuery($sql,$addArray);

	if($data)
	{
		echo "exist";
	}
	else
	{
		echo "not";
	}
?>