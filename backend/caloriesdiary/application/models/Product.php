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
        $q = $this->db->query('SELECT * FROM updates ORDER BY timestamp DESC LIMIT 1');
        if($q->num_rows() > 0)
        {
            foreach ($q->result() as $row)
            {
                return strtotime($row->timestamp);
            }
        }
    }

    public function get_food_names($timestamp)
    {
        $foodNames = array();

        if($this->getTimestamp() > $timestamp)
        {
            $this->db->select('name');
            $query = $this->db->get('Food');
            
            $i = 0;
            foreach ($query->result() as $f) 
            {
                $foodNames[$i] = $f->name;
                $i++;
            }

            $response['status'] = 1;
            $response['update'] = true;
            $response['foodNames'] = $foodNames;
        }
        else
        {
            $response['status'] = 1;
            $response['update'] = false;
            $response['foodNames'] = $foodNames;
        }

        return $response;

    }

     public function get_food($offset)
    {
        $this->db->select('*');
        $this->db->from('Food');
        $this->db->join('categories', 'Food.category = categories.id');
        $this->db->limit(500, $offset*500);
        $query = $this->db->get();

        $food = array();

        if($query)
        {
            $response['status'] = 1;
            foreach ($query->result() as $f) 
            { 
                $food[] = array(
                    "food_id"   =>  $f->food_id,
                    "name"      =>  $f->name,
                    "protein"   =>  $f->protein,
                    "fats"      =>  $f->fats,
                    "carbs"     =>  $f->carbs,
                    "calories"  =>  $f->calories,
                    "category"  =>  $f->categ_name,
                    "category_id" => $f->category
                );
            } 

            $response['food'] = $food;
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Error occured';
            $response['food'] = $food;
        }

        return $response;
    }

    public function getFoodcategories()
    {
        $this->db->select('*');
        $this->db->from('categories');
        $query = $this->db->get();

        if($query)
        {
            $response['status'] = 1;
            $response['categories'] = $query->result();
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Error occured';
            $response['categories'] = array();
        }

        return $response;
    }

    public function getfoodBycategory($id, $offset)
    {
        $this->db->select('*');
        $this->db->from('Food');
        $this->db->where('category',$id);
        $this->db->join('categories', 'Food.category = categories.id');
        $this->db->limit(500, $offset*500);
        $query = $this->db->get();

        $food = array();

        if($query)
        {
            $response['status'] = 1;
            foreach ($query->result() as $f) 
            { 
                $food[] = array(
                    "food_id"   =>  $f->food_id,
                    "name"      =>  $f->name,
                    "protein"   =>  $f->protein,
                    "fats"      =>  $f->fats,
                    "carbs"     =>  $f->carbs,
                    "calories"  =>  $f->calories,
                    "category"  =>  $f->categ_name,
                    "category_id" => $f->category
                );
            }
            $response['food'] = $food;
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Error occured';
            $response['food'] = $food;
        }
        
        return $response;
    }

    public function getfoodByName($name, $offset)
    {
        $this->db->select('*');
        $this->db->from('Food');
        $this->db->where('name', $name);
        $this->db->join('categories', 'Food.category = categories.id');
        $this->db->limit(500, $offset*500);
        $query = $this->db->get();

         if($query)
        {
            $response['status'] = 1;
            foreach ($query->result() as $f) 
            { 
                $food = array(
                    "food_id"   =>  $f->food_id,
                    "name"      =>  $f->name,
                    "protein"   =>  $f->protein,
                    "fats"      =>  $f->fats,
                    "carbs"     =>  $f->carbs,
                    "calories"  =>  $f->calories,
                    "category"  =>  $f->categ_name,
                    "category_id" => $f->category
                );
            }
            $response['food'] = $food;
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Error occured';
            $response['food'] = $food; 
        }
        
        return $response;
    }

}