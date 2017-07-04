<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Food extends CI_Controller {

	public function __construct()
	{
		parent::__construct();
		$this->load->model('Product');
	}

	public function get_food_names()
	{
        $timestamp = $this->input->post('timestamp');
		$food_names_q = $this->Product->get_food_names($timestamp);

        $foodNames = array();

        if($food_names_q != 0)
        {
            $response['update'] = true;

    	    $i = 0;
            foreach ($food_names_q as $f) 
            {
                $foodNames[$i] = $f->name;
                $i++;
            }
        }
        else
        {
            $response['update'] = $food_names_q;
        }

        $response['foodNames'] = $foodNames;
    	echo json_encode($response, TRUE);
	}

	public function get_food()
	{
        $offset = $this->input->post('offset');
		$food = $this->Product->get_food($offset);

    	echo json_encode($food, TRUE);
	}

    public function get_food_categories()
    {
        $response['categories'] = $this->Product->getFoodcategories();

        echo json_encode($response, TRUE);
    }

    public function get_food_by_category()
    {
        $id = $this->input->post('id');
        $offset = $this->input->post('offset');

        $food = $this->Product->getfoodBycategory($id, $offset);

        echo json_encode($food, TRUE);
    }

    public function get_food_by_name()
    {
        $query = $this->input->post('query');
        $offset = $this->input->post('offset');

        $food = $this->Product->getfoodByname($query, $offset);

        echo json_encode($food, TRUE);

    }

}