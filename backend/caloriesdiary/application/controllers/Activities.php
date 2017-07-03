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
		$act_names_q = $this->Action->get_act_names();
    	
        $i = 0;
        foreach ($act_names_q as $act) {
            $actNames[$i] = $act->name;
            $i++;
        }

        $response['actNames'] = $actNames;
    	echo json_encode($response, TRUE);

	}

	public function get_activities()
	{
		$activities_q = $this->Action->get_activities();
    	$response['activities'] = $activities_q;
    	echo json_encode($response, TRUE);
	}

}
