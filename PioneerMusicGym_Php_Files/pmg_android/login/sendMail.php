<?php
require 'phpmailer/class.phpmailer.php';
require 'phpmailer/class.smtp.php';
require 'phpmailer/PHPMailerAutoload.php';

function sendMail($email, $subject, $message){
   $mail = new PHPMailer;
   $mail->isSMTP();
   $mail->Host = 'smtp.zoho.com';
   $mail->Port = 465;
   $mail->SMTPAuth = true;
   $mail->SMTPSecure = 'ssl';
   $mail->Pool = true;
   $mail->Mailer   = 'smtp';
   $mail->Username = '';
   $mail->Password = '';

   $mail->setFrom('', 'Pioneer Music Gym App');
   $mail->addReplyTo('', 'Pioneer Music Gym App');
   $mail->addAddress($email);

   $mail->isHTML(true);	
   $mail->Subject = $subject;
   $mail->Body = $message;

   if($mail->send()){
       return true;
   }else{
       return false;
   }
}

?>
