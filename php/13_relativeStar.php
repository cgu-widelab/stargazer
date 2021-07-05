<?PHP

	require("pdo.php");
	
	$keyword = $_GET["keyword"];
	$keyword = "%".$keyword."%";
	
	$pdo = new mypdo();
	
	$sql = "SELECT * FROM `star_data` WHERE `star_description` LIKE :keyword";
	
	$keywordSearch = array();
	$keywordSearch["keyword"] = $keyword;
	
	$data = $pdo->bindQuery($sql,$keywordSearch);
	
	$result = array();
	$result["data"] = array();
	
	if($data)
	{
		foreach($data as $row)
	{
		array_push($result["data"],$row);
	}
	
		echo json_encode($result);
	}
	else
	{
		echo "no";
	}
	