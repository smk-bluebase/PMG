<?php
include("../config.php");

$db = new DB_Connect();
$con = $db->connect();

$index = $_POST["index"];
$limit = $_POST["limit"];

$sql_query = "SELECT com.id AS composer_id,
                com.composer_name,

                (SELECT COUNT(*) 
                    FROM movie_composer mov_com
                    INNER JOIN song_master sm ON mov_com.movie_id = sm.movie_id
                    WHERE mov_com.composer_id = com.id) 
                    AS number_of_songs,

                (SELECT COUNT(*)
                    FROM movie_composer mov_com
                    WHERE mov_com.composer_id = com.id)
                    AS number_of_movies
                    
                FROM composers com
                ORDER BY com.composer_name ASC
                LIMIT $index, $limit";

$res = mysqli_query($con, $sql_query);

$result = array('status'=>false);

while($row = mysqli_fetch_array($res)){
    $result['status'] = true;
    $result['composers'][] = array('0'=>$row['composer_id'],
                            '1'=>$row['composer_name'],
                            '2'=>$row['number_of_songs'],
                            '3'=>$row['number_of_movies']
                        );
}

echo json_encode([$result]);

mysqli_close($con);
?>