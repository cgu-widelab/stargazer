<?PHP
	
	require("pdo.php");
	
	$star = $_GET["StarName"];
	
	$pdo = new mypdo();
	
	$sql = "SELECT * FROM `star_data` WHERE `star_name_ch`=:CHName";
	
	$NameSearch = array();
	$NameSearch["CHName"] = $star;
	
	$data = $pdo->bindQuery($sql,$NameSearch);
	
	$result = array();
	$result["data"] = array();
	
	foreach($data as $row)
	{
		array_push($result["data"],$row);
	}
		
	echo json_encode($result);