<?php
include("../config.php");

$db = new DB_Connect();
$con = $db->connect();

$search = $_POST["search"];

$sql_query = "(SELECT sng.singer_name AS search_result, sng.id AS id, 'singers' AS category
                FROM singers sng
                WHERE sng.singer_name LIKE '%".$search."%'
                LIMIT 5)

                UNION

            (SELECT com.composer_name AS search_result, com.id AS id, 'composers' AS category
                FROM composers com 
                WHERE com.composer_name LIKE '%".$search."%'
                LIMIT 5)

                UNION

            (SELECT alb.album_name AS search_result, alb.id AS id, 'albums' AS category
                FROM albums alb 
                WHERE alb.album_name LIKE '%".$search."%'
                LIMIT 5)

                UNION

            (SELECT mov.movie_name AS search_result, mov.id AS id, 'movies' AS category
                FROM movies mov 
                WHERE mov.movie_name LIKE '%".$search."%'
                LIMIT 5)";

$res = mysqli_query($con, $sql_query);

$result = array('status'=>false);

while($row = mysqli_fetch_array($res)){
    $result['status'] = true;
    $result['results'][] = array('0'=>$row['search_result'],
                            '1'=>$row['id'],
                            '2'=>$row['category']
                        );
}

echo json_encode([$result]);

mysqli_close($con);
?>