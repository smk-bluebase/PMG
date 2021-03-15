<?php
include("../config.php");

$db = new DB_Connect();
$con = $db->connect();

$songId = $_POST["songId"];

$sql_query = "SELECT sm.id AS song_id,
                sm.title,
                sm.description,

                (SELECT mov.movie_name
                    FROM movies mov
                    WHERE mov.id = sm.movie_id)
                    AS movie_name,

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

                (SELECT lng.language_code
                    FROM languages lng
                    WHERE lng.id = sm.language_id)
                    AS language_code,

                sm.year,
                sm.duration,
                sm.file_location,
                sm.lyrics_location,
                sm.english_lyrics_location
                FROM song_master sm
                WHERE sm.id = $songId";

$res = mysqli_query($con, $sql_query);

$result = array('status'=>false);

while($row = mysqli_fetch_array($res)){
    $result['status'] = true;
    $result['songDetails'] = array('0'=>$row['title'],
                            '1'=>$row['description'],
                            '2'=>$row['movie_name'],
                            '3'=>$row['movie_singer'],
                            '4'=>$row['movie_composer'],
                            '5'=>$row['language_code'],
                            '6'=>$row['year'],
                            '7'=>$row['duration'],
                            '8'=>$row['file_location'],
                            '9'=>$row['lyrics_location'],
                            '10'=>$row['english_lyrics_location']
                        );
}

echo json_encode([$result]);

mysqli_close($con);
?>