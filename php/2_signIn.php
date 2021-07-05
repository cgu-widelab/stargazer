<?PHP

	require("pdo.php");

	$account = $_GET["account"];
	$password = $_GET["password"];
	
	$pdo = new mypdo();
	
	$sql = "SELECT * FROM `user` WHERE `ID`=:ID AND `Password`=:Password";
	
	$filter = array();
	$filter["ID"] = $account;
	$filter["Password"] = $password;
	
	$data = $pdo->bindQuery($sql,$filter);
	
	if($data && $account && $password)
	{
		echo "signIn";
	}
	else
	{
		echo "Sign In Failed";
	}
	
?>