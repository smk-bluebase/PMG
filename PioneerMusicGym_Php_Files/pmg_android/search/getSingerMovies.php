<?php
include("../config.php");

$db = new DB_Connect();
$con = $db->connect();

$singerId = $_POST["singerId"];
$languageId = $_POST["languageId"];

$sql_query = "SELECT mov.id AS movie_id, 
                mov.movie_name,
                mov.year,

                (SELECT COUNT(*) 
                    FROM song_master sm 
                    WHERE sm.movie_id = mov.id)
                    AS number_of_songs,

                mov.language_id   
                FROM movie_singer mov_sng
                INNER JOIN movies mov ON mov.id = mov_sng.movie_id
                WHERE mov_sng.singer_id = $singerId AND mov.language_id = $languageId
                ORDER BY mov.movie_name ASC";

$res = mysqli_query($con, $sql_query);

$result = array('status'=>false);

while($row = mysqli_fetch_array($res)){
    $result['status'] = true;
    $result['movies'][] = array('0'=>$row["movie_id"],
                            '1'=>$row['movie_name'],
                            '2'=>$row['year'],
                            '3'=>$row['number_of_songs'],
                            '4'=>$row['language_id']
                        );
}

echo json_encode([$result]);

mysqli_close($con);
?>