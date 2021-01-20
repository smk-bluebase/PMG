<?php
include("config.php");

$db = new DB_Connect();
$con = $db->connect();

$id = $_POST["id"];

$sql_query = "SELECT `lyrics_location`
                FROM `song_master`
                WHERE `id` = '".$id."'";

$res = mysqli_query($con, $sql_query);

$result = array('status'=>false);

while($row = mysqli_fetch_array($res)){
    $result['status'] = true;
    $result['lyrics'] = file_get_contents($row['lyrics_location']);
}

echo json_encode([$result]);

mysqli_close($con);
?>