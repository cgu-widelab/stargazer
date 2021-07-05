<?PHP

	require("pdo.php");
	
	$user = $_GET["user"];
	$star = $_GET["star"];
	
	$pdo = new mypdo();
	
	$sql = "DELETE FROM `user_favorite_star` WHERE `user_favorite_star`.`ID` = :user AND `user_favorite_star`.`star_name` = :star";
	
	$delArray = array();
	$delArray["user"] = $user;
	$delArray["star"] = $star;
	
	$data = $pdo->bindQuery($sql,$delArray);
	
	
?>