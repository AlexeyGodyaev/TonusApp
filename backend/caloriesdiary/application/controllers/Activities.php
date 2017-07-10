<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Activities extends CI_Controller {

	public function __construct()
	{
		parent::__construct();
		$this->load->model('Action');
	}

	public function get_act_names()
	{
		if($this->input->post('timestamp'))
		{
			$timestamp = $this->input->post('timestamp');
			$response = $this->Action->get_act_names($timestamp);
		}
		else
		{
			$response['status'] = 0;
			$response['msg'] = 'Invalid params';
		}

    	echo json_encode($response, TRUE);
	}

	public function get_activities()
	{
		$activities = $this->Action->get_activities();
    	echo json_encode($activities, TRUE);
	}

}
