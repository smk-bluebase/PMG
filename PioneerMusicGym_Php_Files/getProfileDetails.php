<?php
include("config.php");

$db = new DB_Connect();
$con = $db->connect();

$userId = $_POST['userId'];

$sql_query = "SELECT * 
                FROM `user_master`
                WHERE `user_id` = '".$userId."'";

$res = mysqli_query($con, $sql_query);

$result = array("status"=>"false");

while($row = mysqli_fetch_array($res)){
    $result = array("status"=>"true", 
                    "name"=>$row["name"],
                    "userName"=>$row["user_name"],
                    "email"=>$row["email"],
                    "title"=>$row["title"],
                    "gender"=>$row["gender"],
                    "dateOfBirth"=>$row["date_of_birth"],
                    "mobileNo"=>$row["mobile_no"],
                    "experience"=>$row["experience"],
                    "english"=>$row["english"],
                    "tamil"=>$row["tamil"],
                    "hindi"=>$row["hindi"]
                );
}

echo json_encode([$result]);

mysqli_close($con);
?>