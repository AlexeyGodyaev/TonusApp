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
		$timestamp = $this->input->post('timestamp');
		$act_names_q = $this->Action->get_act_names($timestamp);

        $actNames = array();

        if($act_names_q != false)
        {
            $response['update'] = true;

    	   $i = 0;
        	foreach ($act_names_q as $act)
        	 {
            	$actNames[$i] = $act->name;
            	$i++;
        	 }
        }
        else
        {
            $response['update'] = $act_names_q;
        }

        $response['actNames'] = $actNames;
    	echo json_encode($response, TRUE);
	}

	public function get_activities()
	{
		$activities = $this->Action->get_activities();
    	echo json_encode($activities, TRUE);
	}

}
