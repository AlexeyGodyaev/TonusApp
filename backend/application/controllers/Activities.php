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
		$data['actNames'] = $act_names_q;

		$response = array();
    	$actNames = array();

    	foreach ($act_names_q as $a) 
    	{ 
        	$response['actNames'] = array(
            	$a->name
        	);
    	} 

        //$response['status'] = $status;
    	echo json_encode($response, TRUE);

	}

	public function get_activities()
	{
		$activities_q = $this->Action->get_activities();
		$data['activities'] = $activities_q;

		$response = array();
    	$activities = array();
    	foreach ($activities_q as $a) 
    	{ 
        	$activities[] = array(
            	"id"        =>  $a->id,
            	"name"      =>  $a->name,
            	"calories"  =>  $a->calories
        	);
    	} 

    	$response['activities'] = $activities;
    	echo json_encode($response, TRUE);
	}

}
