<?php
include("../config.php");

$db = new DB_Connect();
$con = $db->connect();

$userName = $_POST["userName"];
$password = $_POST["password"];
 
$sql_query = "SELECT * 
				FROM `user_master` 
				WHERE `user_name` = '".$userName."' AND `password` = '".$password."'";
 
$res = mysqli_query($con, $sql_query);
 
$result = array("status"=>"false");
 
while($row = mysqli_fetch_array($res)){
	if($row["status"] == 1){
		$result = array("status"=>"true",
					"userId"=>$row["id"],
					"email"=>$row["email"]
				);
	}
}
 
echo json_encode([$result]);
 
mysqli_close($con);
?>