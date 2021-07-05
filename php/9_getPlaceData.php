<?PHP
	
	require("pdo.php");
	
	$city = $_GET["city"];
	$section = $_GET["section"];
	if(isset($city) && isset($section))
	{
		$pdo = new mypdo();
	
		$sql = "SELECT * FROM `place_data` NATURAL JOIN `location_data` WHERE `location_data`.`city/county`=:city AND `location_data`.`section/town`=:section";
		
		$placeSearch = array();
		$placeSearch["city"] = $city;
		$placeSearch["section"] = $section;
		
		$data = $pdo->bindQuery($sql,$placeSearch);
		
		if($data)
		{
			$result = array();
			$result["data"] = array();
			
			
			
			foreach($data as $row)
			{
				array_push($result["data"],$row);
			}
			
			echo json_encode($result);
		}
		else
		{
			echo "NoResult";
		}
		
		
	}
	else
	{
		echo "error";
	}
	
	