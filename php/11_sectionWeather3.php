<?PHP

	require("pdo.php");
	
	$placeName = $_GET["placeName"];
	$city = $_GET["city"];
	$date = $_GET["date"]."%";
	
	$wpdo = new mypdo();
	$spdo = new mypdo();
	
	$weathersql = "SELECT * FROM `place_data` NATURAL JOIN `dynamic_data_3` WHERE `place_data`.`place_name`=:placeName AND `dd_DateTime` LIKE :date";
	$placeSearch = array();
	$placeSearch["placeName"] = $placeName;
	$placeSearch["date"] = $date;
	$weatherData = $wpdo->bindQuery($weathersql,$placeSearch);
	
	$sunsetsql = "SELECT * FROM `sunset_time` WHERE `area` LIKE :city AND `ss_date`=:date";
	$citySearch = array();
	$citySearch["city"] = mb_substr($city,0,2,"utf8");
	$citySearch["date"] = $date;
	$sunsetData = $spdo->bindQuery($sunsetsql,$citySearch);
	
	$result = array();
	$result["weather"] = array();
	$result["sunset"] = array();
	
	
	foreach ($weatherData as $row)
	{
		array_push($result["weather"],$row);
	}
	foreach($sunsetData as $row)
	{
		array_push($result["sunset"],$row);
	}
	
	echo json_encode($result);