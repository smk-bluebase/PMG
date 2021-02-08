<?php
include("../config.php");

$db = new DB_Connect();
$con = $db->connect();

$playlistName = $_POST["playlistName"];
$lowerLimit = $_POST["lowerLimit"];
$upperLimit = $_POST["upperLimit"];

$sql_query = "SELECT pl.id AS playlist_id, 
                pl.playlist_name,

                (SELECT COUNT(*) 
                    FROM playlist_song pl_sng
                    WHERE pl_sng.playlist_id = pl.id)
                    AS number_of_songs,

                pl.created_on
                FROM playlists pl
                WHERE pl.playlist_name LIKE '%".$playlistName."%'
                LIMIT $lowerLimit, $upperLimit";

$res = mysqli_query($con, $sql_query);

$result = array('status'=>false);

while($row = mysqli_fetch_array($res)){
    $result['status'] = true;
    $result['playlist'][] = array('0'=>$row["playlist_id"],
                            '1'=>$row["playlist_name"],
                            '2'=>$row['number_of_songs'],
                            '3'=>$row['created_on']
                        );
}

echo json_encode([$result]);

mysqli_close($con);
?>