<?php
include("config.php");

$db = new DB_Connect();
$con = $db->connect();

$createdOn = date("Y-m-d h:i:s");

$otp = $_POST['otp'];

$sql_query = "SELECT * 
                FROM `signup` 
                WHERE `otp` = '".$otp."' 
                LIMIT 1";

$res = mysqli_query($con, $sql_query);

$result = array("status"=>"false");

while($row = mysqli_fetch_array($res)){
    $userId = 0;

    $sql_query = "SELECT max(`user_id`) AS user_id
                    FROM `user_master`";

    $res1 = mysqli_query($con, $sql_query);

    while($row1 = mysqli_fetch_array($res1)){
        $userId = number_format($row1['user_id']) + 1;
    }

    $sql_query = "INSERT INTO `user_master`
                    (`user_id`, `user_name`, `password`, `email`, `created_on`) 
                    VALUES (".$userId.", '".$row['user_name']."', '".$row['password']."', '".$row['email']."', '".$createdOn."')";

    $con->query($sql_query);

    $sql_query = "DELETE FROM `signup`
                     WHERE `user_name` = '".$row['user_name']."'";

    $con->query($sql_query);

    $result = array("status"=>"true");
}

echo json_encode([$result]);

mysqli_close($con);
?>