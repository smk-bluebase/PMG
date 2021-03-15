<?php
include("../config.php");

$db = new DB_Connect();
$con = $db->connect();

$otp = $_POST['otp'];
$password = $_POST['password'];

$modifiedOn = date("Y-m-d h:i:s");

$sql_query = "SELECT * 
                FROM `forgot_password` 
                WHERE `otp` = '".$otp."' LIMIT 1";

$res = mysqli_query($con, $sql_query);

$result = array("status"=>"false");

while($row = mysqli_fetch_array($res)){
    $sql_query = "UPDATE `user_master`
                    SET `password` = '".$password."', `modified_on` = '".$modifiedOn."'
                    WHERE `user_name` = '".$row['user_name']."'";
    
    $con->query($sql_query);

    $sql_query = "DELETE FROM `forgot_password`
                    WHERE `user_name` = '".$row['user_name']."'";

    $con->query($sql_query);

    $result = array("status"=>"true");
}

echo json_encode([$result]);

mysqli_close($con);
?>