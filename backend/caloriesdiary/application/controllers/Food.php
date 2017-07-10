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

	public function get_food()
	{
        if($this->input->post())
        {
            if($this->input->post('offset') == 0)
            {
                $offset = 0;
            }
            else
            {
                $offset = $this->input->post('offset'); 
            }

            $response = $this->Product->get_food($offset);
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