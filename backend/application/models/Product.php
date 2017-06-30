<?php
defined('BASEPATH') OR exit('No direct script access allowed');
class Product extends CI_Model {

    public function __construct()
    {
        parent::__construct();
        $this->load->database();
    }

    public function get_food_names($timestamp)
    {
        //Вернуть массив, если время последнего обновления БД - время обновления (в секундах) > 0 
        $this->db->select('name');
        $query = $this->db->get('Food');
        return $query->result();
    }

     public function get_food($offset)
    {
        $this->db->select('name');
        //$this->db->get('category');
        //$this->db->select('*');
        //$query =  $this->db->get('Food', 500, $offset);
        $query = $this->db->query('SELECT * FROM "Food", "category"
                                   WHERE category = "category".id
                                   ');
        return $query->result();
    }

}