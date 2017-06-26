<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Users extends CI_Controller {

	public function __construct()
	{
		parent::__construct();
		$this->load->model('User');
	}

	public function auth()
	{
		$username = $this->input->post('username');
        $password = $this->input->post('password');

        $status = $this->User->check($username, $password);

        $response['status'] = $status;
    	echo json_encode($response, TRUE);
	}

	public function register()
	{
		$username = $this->input->post('username');
		$email = $this->input->post('email');
        $password = $this->input->post('password');

        $status = $this->User->reg($username, $email, $password);

        $response['status'] = $status;
    	echo json_encode($response, TRUE);
	}

}
