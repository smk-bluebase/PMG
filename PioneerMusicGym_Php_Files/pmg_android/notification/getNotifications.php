<?php
include("../config.php");

$db = new DB_Connect();
$con = $db->connect();

$sql_query = "SELECT `id`, `message` 
                FROM `messages`
                WHERE `status` = 1";

$res = mysqli_query($con, $sql_query);

$result = array("status"=>false);

while($row = mysqli_fetch_array($res)){
    $result['status'] = true;
    $result['notifications'][] = array('0'=>$row["id"],
                            '1'=>$row["message"]
                        );
}

echo json_encode([$result]);

mysqli_close($con);
?>