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
        if($this->input->post('timestamp'))
        {
            $timestamp = $this->input->post('timestamp');
		    $response = $this->Product->get_food_names($timestamp);
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Invalid params';
        }

    	echo json_encode($response, TRUE);
	}

    public function get_food_by_id()
    {
        if($this->input->post('id'))
        {
            $id = $this->input->post('id');
            $response = $this->Product->getById($id);
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Invalid params';
        }
        echo json_encode($response, TRUE);
    }

	public function get_food()
	{
        if($this->input->post())
        {
            $user_id = $this->input->post('id');
            $query = $this->input->post('query');
            $category_id = $this->input->post('categ_id');
            $offset = $this->input->post('offset');
            $sort_names = $this->input->post('sort_names');
            $sort_calories = $this->input->post('sort_calories');

            $response = $this->Product->getfood($user_id, $query, $category_id, $offset, $sort_names, $sort_calories);
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Invalid params';
        }

    	echo json_encode($response, TRUE);
	}

    public function save_custom_dish()
    {
        if($this->input->post())
        {
            $user_id = $this->input->post('id');
            $name = $this->input->post('name');
            $ingredients = $this->input->post('ingredients');

            $response = $this->Product->saveCustomDish($user_id, $name, $ingredients);
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Invalid params';
        }

        echo json_encode($response, TRUE);
    }

    public function get_food_categories()
    {
        $response = $this->Product->getFoodcategories();

        echo json_encode($response, TRUE);
    }

    public function get_food_by_category()
    {
        if($this->input->post(array('id', 'offset')))
        {
            $id = $this->input->post('id');
            $offset = $this->input->post('offset');

            $response = $this->Product->getfoodBycategory($id, $offset);
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Invalid params';
        }

        echo json_encode($response, TRUE);
    }

    public function get_food_by_name()
    {
        if($this->input->post(array('query', 'offset')))
        {
            $query = $this->input->post('query');
            $offset = $this->input->post('offset');

            $food = $this->Product->getfoodByname($query, $offset);
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Invalid params';
        }

        echo json_encode($food, TRUE);

    }
}