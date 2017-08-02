<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Calories extends CI_Controller {

	public function __construct()
	{
		parent::__construct();
		$this->load->model('CaloriesCalc');
	}

    public function get_per_day()
    {
        if($this->input->post('id'))
        {
            $id = $this->input->post('id');

            $response = $this->CaloriesCalc->caloriesPerday($id);
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Invalid params';
        }
        
        echo json_encode($response, TRUE);

    }

    //TODO: Также в отдельный скрипт

    public function get_random_food_acts()
    {
        if($this->input->post('number_of_elements'))
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
