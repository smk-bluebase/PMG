<?php
include("config.php");
require 'sendmail.php';

$db = new DB_Connect();
$con = $db->connect();

$userName = $_POST['userName'];

$otp = rand(1000000, 10000000);

$subject = "PMG Music App Forgot Password OTP";

$message = "Dear ".$userName.",<br/><br/>

Looks like you have forgotten your password. Please enter<br/>
the OTP below to authenticate yourself.<br/><br/>

OTP - ".$otp."<br/><br/>

Continue with the Pioneer Music Gym App!<br/><br/>

Regards,<br/>
Bluebase Team";

$sql_query = "SELECT count(*), email
                FROM `user_master`
                WHERE `user_name` = '".$userName."'";

$res = mysqli_query($con, $sql_query);

$result = array("status"=>"false");

while($row = mysqli_fetch_array($res)){
    if($row['0'] == 1){

        $sql_query = "DELETE FROM `forgot_password`
                        WHERE `user_name` = '".$userName."'";

        $con->query($sql_query);

        $sql_query = "INSERT INTO `forgot_password` 
                        (`user_name`, `otp`) 
                        VALUES ('".$userName."', '".$otp."')";

        $con->query($sql_query);

        if(sendMail($row['email'], $subject, $message)){
            $result = array("status"=>"true");
        }
    }
}

echo json_encode([$result]);

mysqli_close($con);
?>