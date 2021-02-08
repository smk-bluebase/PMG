<?php
include("../config.php");

$db = new DB_Connect();
$con = $db->connect();

$singerId = $_POST["singerId"];

$sql_query = "SELECT alb.id AS album_id, 
                alb.album_name,
                alb.year,

                (SELECT COUNT(*) 
                    FROM song_master sm 
                    WHERE sm.album_id = alb.id)
                    AS number_of_songs
                    
                FROM album_singer alb_sng
                INNER JOIN albums alb ON alb.id = alb_sng.album_id
                WHERE alb_sng.singer_id = $singerId
                ORDER BY alb.album_name ASC";

$res = mysqli_query($con, $sql_query);

$result = array('status'=>false);

while($row = mysqli_fetch_array($res)){
    $result['status'] = true;
    $result['albums'][] = array('0'=>$row["album_id"],
                            '1'=>$row['album_name'],
                            '2'=>$row['year'],
                            '3'=>$row['number_of_songs'],
                        );
}

echo json_encode([$result]);

mysqli_close($con);
?>