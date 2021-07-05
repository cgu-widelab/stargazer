<?PHP

	require("pdo.php");
	
	$account = $_GET["account"];
	$password = $_GET["password"];
	
	$pdo = new mypdo();
	
	$sql = "SELECT * FROM `user` WHERE `ID`=:account";
	
	$filter = array();
	$filter["account"] = $account;
	
	$data = $pdo->bindQuery($sql,$filter);
	
	if($data || !$password)
	{
		echo "failed";
	}
	else
	{
		$CS = $_GET["CS"];
		$otherAddress = $_GET["otherAddress"];

		$userData = array();
		$userData["ID"] = $account;
		$userData["Password"] = $password;
		$userData["user_city_section"] = $CS;
		$userData["user_other_address"] = $otherAddress;
		$pdo->insert("`user`",$userData);
		echo "success";
	}

?>