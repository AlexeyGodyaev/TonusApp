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
        $weight = $this->input->post('weight');
        $height = $this->input->post('height');
        $sex = $this->input->post('sex');
        $age = $this->input->post('age');
        $activityType = $this->input->post('activityType');

        $result = $this->CaloriesCalc->caloriesPerday($weight, $height, $sex, $activityType, $age);

        $response = array('caloriesPerDay' => $result, 'status' => 1);

        echo json_encode($response, TRUE);

    }

}
