<?php
include("../config.php");

$db = new DB_Connect();
$con = $db->connect();

$lowerLimit = $_POST["lowerLimit"];
$upperLimit = $_POST["upperLimit"];

$sql_query = "SELECT sm.id AS song_id,
                sm.title,
                sm.description,

                (SELECT alb.album_name 
                    FROM albums alb
                    WHERE alb.id = sm.album_id)
                    AS album_name,

                (SELECT mov.movie_name
                    FROM movies mov
                    WHERE mov.id = sm.movie_id)
                    AS movie_name,

                (SELECT GROUP_CONCAT(sng.singer_name SEPARATOR ', ')
                    FROM album_singer alb_sng
                    INNER JOIN singers sng ON sng.id = alb_sng.singer_id
                    WHERE alb_sng.album_id = sm.album_id)
                    AS album_singer,

                (SELECT GROUP_CONCAT(com.composer_name SEPARATOR ', ')
                    FROM album_composer alb_com
                    INNER JOIN composers com ON com.id = alb_com.composer_id
                    WHERE alb_com.album_id = sm.album_id)
                    AS album_composer,   

                (SELECT GROUP_CONCAT(sng.singer_name SEPARATOR ', ')
                    FROM movie_singer mov_sng
                    INNER JOIN singers sng ON sng.id = mov_sng.singer_id
                    WHERE mov_sng.movie_id = sm.movie_id)
                    AS movie_singer,

                (SELECT GROUP_CONCAT(com.composer_name SEPARATOR ', ')
                    FROM movie_composer mov_com
                    INNER JOIN composers com ON com.id = mov_com.composer_id
                    WHERE mov_com.movie_id = sm.movie_id)
                    AS movie_composer, 

                (SELECT lan.language_code
                    FROM languages lan
                    WHERE lan.id = sm.language_id)
                    AS language_code,
                    
                sm.year,
                sm.duration,
                sm.file_location,
                sm.lyrics_location
                FROM song_master sm
                ORDER BY sm.id, sm.title ASC
                LIMIT $lowerLimit, $upperLimit";

$res = mysqli_query($con, $sql_query);

$result = array('status'=>false);

while($row = mysqli_fetch_array($res)){
    $result['status'] = true;
    $result['songs'][] = array('0'=>$row['song_id'],
                            '1'=>$row['title'],
                            '2'=>$row['album_name'],
                            '3'=>$row['movie_name'],
                            '4'=>$row['album_singer'],
                            '5'=>$row['movie_singer'],
                            '6'=>$row['year'],
                            '7'=>$row['duration']
                        );
}

echo json_encode([$result]);

mysqli_close($con);
?>