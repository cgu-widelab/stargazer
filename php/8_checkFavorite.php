<?PHP

	require("pdo.php");
	
	$user = $_GET["user"];
	$star = $_GET["star"];
	
	$pdo = new mypdo();
	
	$sql = "SELECT * FROM `user_favorite_star` WHERE `ID`=:user AND `star_name`=:star";
	
	$addArray = array();
	$addArray["user"] = $user;
	$addArray["star"] = $star;
	
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