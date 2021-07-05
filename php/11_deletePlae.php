<?PHP

	require("pdo.php");
	
	$user = $_GET["user"];
	$place = $_GET["place"];
	
	$pdo = new mypdo();
	
	$sql = "DELETE FROM `user_favorite_place` WHERE `user_favorite_place`.`ID` = :user AND `user_favorite_place`.`place_name` = :place";
	
	$delArray = array();
	$delArray["user"] = $user;
	$delArray["place"] = $place;
	
	$data = $pdo->bindQuery($sql,$delArray);
	
	
?>