<?php
defined('BASEPATH') OR exit('No direct script access allowed');

require_once APPPATH . 'controllers/Aux.php';

class Food extends CI_Controller {


	public function __construct()
	{
		parent::__construct();
		$this->load->model('Product');

        $this->load->library('form_validation');
	}

    public function is_json($string) {
        json_decode($string);
        return (json_last_error() == JSON_ERROR_NONE);
    }

	public function get_food()
	{
        $this->form_validation->set_rules('id', 'Id', 'integer');
        $this->form_validation->set_rules('categ_id', 'Category ID', 'integer');
        $this->form_validation->set_rules('offset', 'Offset', 'required|integer');
        $this->form_validation->set_rules('sort_calories', 'sort_calories', 'integer');
        $this->form_validation->set_rules('sort_names', 'sort_names', 'integer');

        if($this->form_validation->run())
        {
            $user_id = $this->input->post('id');
            $instanceToken = $this->input->post('instanceToken');
            $query = $this->input->post('query');
            $category_id = $this->input->post('categ_id');
            $offset = $this->input->post('offset');
            $sort_names = $this->input->post('sort_names');
            $sort_calories = $this->input->post('sort_calories');

            $response = $this->Product->getfood($instanceToken, $user_id, $query, $category_id, $offset, $sort_names, $sort_calories);
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

    public function save_custom_dish()
    {
        $this->form_validation->set_rules('id', 'Id', 'required|integer');
        $this->form_validation->set_rules('instanceToken', 'instanceToken', 'required');
        $this->form_validation->set_rules('name', 'Name', 'required');
        $this->form_validation->set_rules('ingredients', 'Ingredients', 'required');

        if($this->form_validation->run() && $this->is_json($this->input->post('ingredients')))
        {
            $user_id = $this->input->post('id');
            $instanceToken = $this->input->post('instanceToken');
            $name = $this->input->post('name');
            $ingredients = $this->input->post('ingredients');

            $response = $this->Product->saveCustomDish($instanceToken, $user_id, $name, $ingredients);
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Invalid params';
        }

        echo json_encode($response, TRUE);
    }   
}