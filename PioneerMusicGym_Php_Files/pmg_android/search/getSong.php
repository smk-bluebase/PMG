<?php
include("../config.php");

$db = new DB_Connect();
$con = $db->connect();

$songId = $_POST["songId"];

$sql_query = "SELECT sm.id AS song_id,
                sm.title,

                (SELECT mov.movie_name
                    FROM movies mov
                    WHERE mov.id = sm.movie_id)
                    AS movie_name,

                (SELECT GROUP_CONCAT(sng.singer_name SEPARATOR ', ')
                    FROM movie_singer mov_sng
                    INNER JOIN singers sng ON sng.id = mov_sng.singer_id
                    WHERE mov_sng.movie_id = sm.movie_id)
                    AS movie_singer,
                    
                sm.year,
                sm.duration
                FROM song_master sm
                WHERE sm.id = $songId";

$res = mysqli_query($con, $sql_query);

$result = array('status'=>false);

while($row = mysqli_fetch_array($res)){
    $result['status'] = true;
    $result['song'] = array('0'=>$row['song_id'],
                            '1'=>$row['title'],
                            '2'=>$row['movie_name'],
                            '3'=>$row['movie_singer'],
                            '4'=>$row['year'],
                            '5'=>$row['duration']
                        );
}

echo json_encode([$result]);

mysqli_close($con);
?>