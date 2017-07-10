<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Calories extends CI_Controller {

	public function __construct()
	{
		parent::__construct();
		$this->load->model('CaloriesCalc');

	}

    public function get_calories_per_day()
    {
        if($this->input->post(array('weight','height','sex','age','activityType')))
        {
            $weight = $this->input->post('weight');
            $height = $this->input->post('height');
            $sex = $this->input->post('sex');
            $age = $this->input->post('age');
            $activityType = $this->input->post('activityType');

            $response = $this->CaloriesCalc->caloriesPerday($weight, $height, $sex, $activityType, $age);
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Invalid params';
        }
        echo json_encode($response, TRUE);

    }

}
