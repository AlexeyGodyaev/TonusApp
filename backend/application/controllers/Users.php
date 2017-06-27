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

        if(is_string($status))
        {
        	$response['msg'] = $status;
        	$response['status'] = 0;
        }
        else
        {
            $response['msg'] = "ОК";
			$response['status'] = 1;
        }
        
    	echo json_encode($response, TRUE);
	}


	public function register()
	{
		$username = $this->input->post('username');
		$email = $this->input->post('email');
        $password = $this->input->post('password');

        $status = $this->User->reg($username, $email, $password);

        if(is_string($status))
        {
        	$response['msg'] = $status;
        	$response['status'] = 0;
        }
        else
        {
            $response['msg'] = "ОК";
        	$response['status'] = 1;
        }
        
    	echo json_encode($response, TRUE);
	}

    public function change_password()
    {
       $username = $this->input->post('username');
       $oldpassword = $this->input->post('oldpassword');
       $newpassword = $this->input->post('newpassword');

       $status = $this->User->changePassword($username, $oldpassword, $newpassword);

       if(is_string($status))
        {
            $response['msg'] = $status;
            $response['status'] = 0;
        }
        else
        {
            $response['msg'] = "ОК";
            $response['status'] = 1;
        }
        
        echo json_encode($response, TRUE);


    }

    public function delete()
    {
        $username = $this->input->post('username');
        $password = $this->input->post('password');
        $status = $this->User->del($username, $password);

        if(is_string($status))
        {
            $response['msg'] = $status;
            $response['status'] = 0;
        }
        else
        {
            $response['msg'] = "ОК";
            $response['status'] = 1;
        }
        
        echo json_encode($response, TRUE);

    }

}
