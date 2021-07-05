<?PHP

	require("pdo.php");
	
	$user = $_GET["user"];
	$place = $_GET["place"];
	
	$pdo = new mypdo();
	
	$sql = "INSERT INTO `user_favorite_place` (`ID`, `place_name`) VALUES (:user, :place)";
	
	$addArray = array();
	$addArray["user"] = $user;
	$addArray["place"] = $place;
	
	$data = $pdo->bindQuery($sql,$addArray);

?>