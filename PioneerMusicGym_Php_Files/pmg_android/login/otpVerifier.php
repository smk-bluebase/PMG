<?php
include("../config.php");

$db = new DB_Connect();
$con = $db->connect();

$otp = $_POST['otp'];

$sql_query = "SELECT * 
                FROM `signup` 
                WHERE `otp` = '".$otp."' 
                LIMIT 1";

$res = mysqli_query($con, $sql_query);

$result = array("status"=>"false");

while($row = mysqli_fetch_array($res)){
    $sql_query = "INSERT INTO `user_master`
                    (`user_name`, `password`, `email`) 
                    VALUES ('".$row['user_name']."', '".$row['password']."', '".$row['email']."')";

    $con->query($sql_query);

    $sql_query = "DELETE FROM `signup`
                     WHERE `user_name` = '".$row['user_name']."'";

    $con->query($sql_query);

    $result = array("status"=>"true");
}

echo json_encode([$result]);

mysqli_close($con);
?>