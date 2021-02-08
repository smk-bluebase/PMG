<?php
include("../config.php");

$db = new DB_Connect();
$con = $db->connect();

$composerName = $_POST["composerName"];
$lowerLimit = $_POST["lowerLimit"];
$upperLimit = $_POST["upperLimit"];

$sql_query = "SELECT com.id AS composer_id,
                com.composer_name,

                (SELECT COUNT(*) 
                    FROM album_composer alb_com 
                    INNER JOIN song_master sm ON alb_com.album_id = sm.album_id
                    WHERE alb_com.composer_id = com.id) 
                + 
                (SELECT COUNT(*) 
                    FROM movie_composer mov_com
                    INNER JOIN song_master sm ON mov_com.movie_id = sm.movie_id
                    WHERE mov_com.composer_id = com.id) 
                    AS number_of_songs,

                (SELECT COUNT(*) 
                    FROM album_composer alb_com
                    WHERE alb_com.composer_id = com.id)
                    AS number_of_albums,

                (SELECT COUNT(*)
                    FROM movie_composer mov_com
                    WHERE mov_com.composer_id = com.id)
                    AS number_of_movies
                    
                FROM composers com
                WHERE com.composer_name LIKE '%".$composerName."%'
                LIMIT $lowerLimit, $upperLimit";

$res = mysqli_query($con, $sql_query);

$result = array('status'=>false);

while($row = mysqli_fetch_array($res)){
    $result['status'] = true;
    $result['composers'][] = array('0'=>$row['composer_id'],
                                '1'=>$row['composer_name'],
                                '2'=>$row['number_of_songs'],
                                '3'=>$row['number_of_albums'],
                                '4'=>$row['number_of_movies']
                        );
}

echo json_encode([$result]);

mysqli_close($con);
?>