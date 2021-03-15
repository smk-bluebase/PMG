<?php
include("../config.php");

$db = new DB_Connect();
$con = $db->connect();

$name = $_POST['name'];
$userName = $_POST['userName'];
$originalUserName = $_POST['originalUserName'];
$email = $_POST['email'];
$title = $_POST['title'];
$gender = $_POST['gender'];
$dateOfBirth = $_POST['dateOfBirth'];
$mobileNo = $_POST['mobileNo'];
$experience = number_format($_POST['experience']);
$tamil = number_format($_POST['tamil']);
$malayalam = number_format($_POST['malayalam']);
$hindi = number_format($_POST['hindi']);

$modifiedOn = date("Y-m-d h:i:s");

$result = array("status"=>"false", "message"=>"");

if($userName == $originalUserName){
    $sql_query = "UPDATE `user_master` 
                    SET `name` = '".$name."', `email` = '".$email."', `title` = '".$title."',
                    `gender` = '".$gender."', `date_of_birth` = '".$dateOfBirth."', `experience` = $experience,
                    `tamil` = $tamil, `malayalam` = $malayalam, `hindi` = $hindi,
                    `mobile_no` = '".$mobileNo."', `modified_on` = '".$modifiedOn."'
                    WHERE `user_name` = '".$userName."'";

    $res1 = $con->query($sql_query);

    if($res1) $result = array("status"=>"true");
}else {
    $sql_query = "SELECT count(*)
                    FROM `user_master`
                    WHERE `user_name` = '".$userName."'";

    $res2 = mysqli_query($con, $sql_query);

    while($row = mysqli_fetch_array($res2)){
        if($row["0"] == 0){
            $sql_query = "UPDATE `user_master`
                            SET `name` = '".$name."', `user_name` = '".$userName."',`email` = '".$email."',
                            `title` = '".$title."', `gender` = '".$gender."', `date_of_birth` = '".$dateOfBirth."',
                            `tamil`= $tamil, `malayalam`= $malayalam, `hindi`= $hindi,
                            `experience` = $experience, `mobile_no` = '".$mobileNo."', `modified_on` = '".$modifiedOn."'
                            WHERE `user_name` = '".$originalUserName."'";
    
            $res3 = $con->query($sql_query);
    
            if($res3) $result = array("status"=>"true");
        }else {
            $result = array("status"=>"false", "message"=>"SomeOne already has\n the username");
        }
    }

}

echo json_encode([$result]);

mysqli_close($con);
?>