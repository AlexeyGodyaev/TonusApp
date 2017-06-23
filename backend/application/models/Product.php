<?php
defined('BASEPATH') OR exit('No direct script access allowed');
class Product extends CI_Model {

    public function __construct()
    {
        parent::__construct();
        $this->load->database();
    }

    public function get_food_names()
    {
        $query = $this->db->query('SELECT name FROM "Food";');
        return $query->result();
    }

     public function get_food()
    {
        $query = $this->db->query('SELECT * FROM "Food";');
        return $query->result();
    }

}