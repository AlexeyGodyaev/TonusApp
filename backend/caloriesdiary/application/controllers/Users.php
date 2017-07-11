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
        if($this->input->post(array('username', 'password')))
        {
		    $username = $this->input->post('username');
            $password = $this->input->post('password');

            $response = $this->User->check($username, $password);
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Invalid params';
        }
       
    	echo json_encode($response, TRUE);
	}

	public function register()
	{
        if($this->input->post(array('username', 'email', 'password')))
        {
		  $username = $this->input->post('username');
		  $email = $this->input->post('email');
          $password = $this->input->post('password');

          $response = $this->User->reg($username, $email, $password);
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Invalid params';
        }

    	echo json_encode($response, TRUE);
	}

    public function change_password()
    {
        if($this->input->post(array('username', 'oldpassword', 'newpassword')))
        {
            $username = $this->input->post('username');
            $oldpassword = $this->input->post('oldpassword');
            $newpassword = $this->input->post('newpassword');

            $response = $this->User->changePassword($username, $oldpassword, $newpassword);
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Invalid params';
        }

       echo json_encode($response, TRUE);
    }

    public function delete()
    {
        if($this->input->post(array('id', 'password')))
        {
            $id = $this->input->post('id');
            $password = $this->input->post('password');
            $response = $this->User->del($id, $password);
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Invalid params';
        }

        echo json_encode($response, TRUE);
    }

    public function save_user_chars()
    {
        if($this->input->post(array('id', 'realName', 'weight', 'height', 'sex', 'age','activityType','avg_dream','wokeup_time')))
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
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Invalid params';
        }

        echo json_encode($response, TRUE);
    }

    public function get_user_chars()
    {
        if($this->input->post('id'))
        {
            $id = $this->input->post('id');
            $response = $this->CaloriesCalc->getUserChars($id);
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Invalid params';
        }

        echo json_encode($response, TRUE);
    }

    public function save_goal()
    {
         if($this->input->post('id, desiredWeight, activityType, period, goal'))
        { 
            
            $id = $this->input->post('id');
            $desiredWeight = $this->input->post('desiredWeight');
            $activityType = $this->input->post('activityType');
            $period = $this->input->post('period');
            $goal = $this->input->post('goal');
            
            $response = $this->CaloriesCalc->saveUserGoal($id, $desiredWeight, $activityType, $period, $goal);
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Invalid params';
        }
    }

    public function forgot_password()
    {
        if($this->input->post('email'))
        { 
            $email = $this->input->post('email');
            $response = $this->User->forgot($email);
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Invalid params';
        }
        
        echo json_encode($response, TRUE);
    }

}
