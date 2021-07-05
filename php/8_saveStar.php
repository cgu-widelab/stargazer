<?PHP

	require("pdo.php");
	
	$user = $_GET["user"];
	$star = $_GET["star"];
	
	$pdo = new mypdo();
	
	$sql = "INSERT INTO `user_favorite_star` (`ID`, `star_name`) VALUES (:user, :star)";
	
	$addArray = array();
	$addArray["user"] = $user;
	$addArray["star"] = $star;
	
	$data = $pdo->bindQuery($sql,$addArray);

?>