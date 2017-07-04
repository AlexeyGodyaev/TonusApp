<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Users extends CI_Controller {

	public function __construct()
	{
		parent::__construct();

		$this->load->model('User');
        $this->load->model('CaloriesCalc');
	}

	public function auth()
	{
		$username = $this->input->post('username');
        $password = $this->input->post('password');

        $response = $this->User->check($username, $password);
        
    	echo json_encode($response, TRUE);
	}

	public function register()
	{
		$username = $this->input->post('username');
		$email = $this->input->post('email');
        $password = $this->input->post('password');

        $response = $this->User->reg($username, $email, $password);

    	echo json_encode($response, TRUE);
	}

    public function change_password()
    {
       $username = $this->input->post('username');
       $oldpassword = $this->input->post('oldpassword');
       $newpassword = $this->input->post('newpassword');

       $response = $this->User->changePassword($username, $oldpassword, $newpassword);
       echo json_encode($response, TRUE);
    }

    public function delete()
    {
        $username = $this->input->post('id');
        $password = $this->input->post('password');
        $response = $this->User->del($username, $password);

        echo json_encode($response, TRUE);

    }

    public function save_user_chars()
    {
        $id = $this->input->post('id');
        $realName = $this->input->post('realName');
        $weight = $this->input->post('weight');
        $height = $this->input->post('height');
        $sex = $this->input->post('sex');
        $age = $this->input->post('age');
        $activityType = $this->input->post('activityType');
        $avgdream = $this->input->post('avg_dream');
        $wokeup = $this->input->post('wokeup_time');

        $response = $this->CaloriesCalc->saveUserChars($id, $realName, $weight, $height, $sex, $activityType, $age, $avgdream, $wokeup);
        echo json_encode($response, TRUE);
    }

    public function get_user_chars()
    {
        $id = $this->input->post('id');
        $chars = $this->CaloriesCalc->getUserChars($id);  

        echo json_encode($chars, TRUE);
    }

    public function forgot_password()
    {
        $email = $this->input->post('email');

        $response = $this->User->forgot($email);
        echo json_encode($response, TRUE);
    }

}
