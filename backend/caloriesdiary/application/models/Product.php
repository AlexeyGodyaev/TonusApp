<?php
defined('BASEPATH') OR exit('No direct script access allowed');
class Product extends CI_Model {

    public function __construct()
    {
        parent::__construct();
        $this->load->database();
    }


    public function getTimestamp()
    {
        $query = 'SELECT * FROM updates LIMIT 1';
        $q = $this->db->query($query);
        if($q->num_rows() > 0)
        {
            return 
        }
    }

    public function get_food_names($timestamp)
    {
        if($this->getTimestamp() < $timestamp)
        {
            $this->db->select('name');
            $query = $this->db->get('Food');
            return $query->result();
        }
        else
        {
            return 0;
        }
    }

     public function get_food($offset)
    {
        $this->db->select('*');
        $this->db->from('Food');
        $this->db->join('categories', 'Food.category = categories.id');
        $this->db->limit(500, $offset);
        $query = $this->db->get();

        return $query->result();
    }

    public function getfoodBycategory($id, $offset)
    {
        $this->db->select('*');
        $this->db->from('Food');
        $this->db->where('category',$id);
        $this->db->join('categories', 'Food.category = categories.id');
        $this->db->limit(500, $offset);
        $query = $this->db->get();

        return $query->result();
    }

    public function getfoodByName($name, $offset)
    {
        $this->db->select('*');
        $this->db->from('Food');
        $this->db->where('name', $name);
        $this->db->join('categories', 'Food.category = categories.id');
        $this->db->limit(500, $offset);
        $query = $this->db->get();

        return $query->result();
    }

}