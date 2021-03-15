<?php
include("../config.php");

$db = new DB_Connect();
$con = $db->connect();

$userName = $_POST['userName'];
$currentPassword = $_POST['currentPassword'];
$newPassword = $_POST['newPassword'];

$sql_query = "SELECT `password`
                FROM `user_master`
                WHERE `user_name` = '".$userName."'";

$res = mysqli_query($con, $sql_query);

$result = array("status"=>"false");

while($row = mysqli_fetch_array($res)){
    if($currentPassword == $row['password']){
        $sql_query = "UPDATE `user_master`
                        SET `password` = '".$newPassword."'
                        WHERE `user_name` = '".$userName."'";

        $con->query($sql_query);

        $result = array("status"=>"true");
    }
}

echo json_encode([$result]);
 
mysqli_close($con);
?>