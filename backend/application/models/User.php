<?php
defined('BASEPATH') OR exit('No direct script access allowed');
class User extends CI_Model {

    public function __construct()
    {
        parent::__construct();
        $this->load->database();
    }

    public function check($username, $password)
    {
        $query = $this->db->query('SELECT username, password FROM "Users" WHERE username = '. $username . ' WHERE password = '. $password . ';');

        return $query.result();
    }

}