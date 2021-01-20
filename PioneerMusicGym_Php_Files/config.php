<?php
define("DB_HOST", "localhost");
define("DB_USER", "root");
define("DB_PASSWORD", "");
define("DB_DATABASE", "pmg");

class DB_Connect {
    public function connect() {
        require_once 'config.php';
        $con = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE);
        return $con;
    }
}

// error_reporting(0);

?>