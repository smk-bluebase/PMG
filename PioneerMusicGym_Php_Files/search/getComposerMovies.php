<?php
include("../config.php");

$db = new DB_Connect();
$con = $db->connect();

$composerId = $_POST["composerId"];

$sql_query = "SELECT mov.id AS movie_id, 
                mov.movie_name,
                mov.year,

                (SELECT COUNT(*) 
                FROM song_master sm 
                WHERE sm.movie_id = mov.id)
                    AS number_of_songs

                FROM movie_composer mov_com
                INNER JOIN movies mov ON mov.id = mov_com.movie_id
                WHERE mov_com.composer_id = $composerId
                ORDER BY mov.movie_name ASC";

$res = mysqli_query($con, $sql_query);

$result = array('status'=>false);

while($row = mysqli_fetch_array($res)){
    $result['status'] = true;
    $result['movies'][] = array('0'=>$row["movie_id"],
                            '1'=>$row['movie_name'],
                            '2'=>$row['year'],
                            '3'=>$row['number_of_songs'],
                        );
}

echo json_encode([$result]);

mysqli_close($con);
?>