<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Action extends CI_Model {

    public function __construct()
    {
        $this->load->database();
    }

    public function get_act_names()
    {
        $query = $this->db->query('SELECT name FROM "Activities";');
        return $query->result();
    }

     public function get_activities()
    {
        $query = $this->db->query('SELECT * FROM "Activities";');
        return $query->result();
    }

}