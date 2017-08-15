<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Calories extends CI_Controller {

	public function __construct()
	{
		parent::__construct();

        $this->load->library('form_validation');
		$this->load->model('CaloriesCalc');
	}

    public function get_per_day()
    {
        $this->form_validation->set_rules('id', 'Id', 'required|integer');
        $this->form_validation->set_rules('instanceToken', 'instanceToken', 'required');
        
        if($this->form_validation->run())
        {
            $id = $this->input->post('id');
            $instanceToken = $this->input->post("instanceToken");

            $response = $this->CaloriesCalc->caloriesPerday($id, $instanceToken);
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Invalid params';
        }
        
        echo json_encode($response, TRUE);
    }

    //TODO: В отдельный скрипт

    public function get_random_food_acts()
    {
        $this->form_validation->set_rules('number_of_elements', 'Number of elements', 'required|integer|greater_than[0]');

        if($this->form_validation->run())
        {
            $number = $this->input->post('number_of_elements');
            $response = $this->CaloriesCalc->getRandom($number);
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Invalid params';
        }
        
        echo json_encode($response, TRUE);
    }

}
