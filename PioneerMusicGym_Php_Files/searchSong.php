<?php
include("config.php");

$db = new DB_Connect();
$con = $db->connect();

$title = $_POST["title"];

if(isset($title)){
    $title = str_replace("%", "", $title);
    $title = strtolower($title);
}

$sql_query = "SELECT `id`, `title`
                FROM `song_master`
                WHERE `title` LIKE '%".$title."%'
                LIMIT 30";

$res = mysqli_query($con, $sql_query);

$result = array('status'=>false);

while($row = mysqli_fetch_array($res)){
    $result['status'] = true;
    $result['titles'][] = array('id'=>$row['id'],
                            'title'=>$row['title']
                        );
}

echo json_encode([$result]);

mysqli_close($con);
?>