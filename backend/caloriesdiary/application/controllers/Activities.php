<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Activities extends CI_Controller {


	public function __construct()
	{
		parent::__construct();
		$this->load->model('Action');
	}

	public function get_activities()
	{
		$name = $this->input->post('query');
		$sort_alphabetical = $this->input->post('sort_names');
		$sort_calories = $this->input->post('sort_calories');

		$activities = $this->Action->getActivities($name, $sort_alphabetical, $sort_calories);
    	echo json_encode($activities, TRUE);
	}

}
