<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Users extends CI_Controller {

	public function __construct()
	{
		parent::__construct();

		$this->load->model('User');
        $this->load->model('CaloriesCalc');
	}

	public function send_push()
	{
		if($this->input->post(array('title', 'body', 'id')))
        {
            $id = $this->input->post('id');
            $title = $this->input->post('title');
            $body = $this->input->post('body');

            $response = $this->User->sendPush($id, $title, $body);
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Invalid params';
        }
       
        echo json_encode($response, TRUE);
	}



    public function save_backup()
    {
        if($this->input->post())
        {
            $id = $this->input->post('user_id');
            $day_json = $this->input->post('day_json');

            $response = $this->User->save_days_backup($id, $day_json);
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Invalid params';
        }
       
        echo json_encode($response, TRUE);
    }

    public function get_backup()
    {
        if($this->input->post('user_id'))
        {
            $id = $this->input->post('user_id');
            $response = $this->User->get_days_backup($id);
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Invalid params';
        }
       
        echo json_encode($response, TRUE);
    }

    public function save_day()
    {
        if($this->input->post())
        {
            $id = $this->input->post('id');
            $mass = $this->input->post('mass');
            $active_sum = $this->input->post('active_sum');
            $food_sum = $this->input->post('food_sum');
            $note = $this->input->post('note');
            $activities = $this->input->post('activities');
            $food = $this->input->post('food');
            $date = $this->input->post('date');
            $protein = $this->input->post('protein');
            $fats = $this->input->post('fats');
            $carbs = $this->input->post('date');


            $left_hand = $this->input->post('left_hand');
            $right_hand = $this->input->post('right_hand');
            $breast = $this->input->post('breast');
            $waist = $this->input->post('waist');
            $hiney = $this->input->post('hiney');
            $left_thigh = $this->input->post('left_thigh');
            $right_thigh = $this->input->post('right_thigh');
            $calfs = $this->input->post('calfs');
            $shoulders = $this->input->post('shoulders');


            $response = $this->User->save_day($id, $mass, $active_sum, $food_sum, $note, $activities, $food, $date, $protein, $fats, $carbs, $left_hand, $right_hand, $breast, $waist, $hiney, $left_thigh, $right_thigh, $calfs, $shoulders);
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Invalid params';
        }
       
        echo json_encode($response, TRUE);
    }

    public function get_days()
    {
        if($this->input->post('id'))
        {
            $id = $this->input->post('id');
            
            $response = $this->User->get_days($id);
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Invalid params';
        }
       
        echo json_encode($response, TRUE);
    }

    public function google_auth()
    {
        if($this->input->post(array('ga_id', 'email', 'username', 'instanceToken')))
        {
            $id = $this->input->post('ga_id');
            $email = $this->input->post('email');
            $username = $this->input->post('username');
            $instanceToken = $this->input->post('instanceToken');

            $response = $this->User->google_check($id, $username, $email, $instanceToken);
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Invalid params';
        }
       
        echo json_encode($response, TRUE);
    }

	public function auth()
	{
        if($this->input->post(array('username', 'password', 'instanceToken')))
        {
		    $username = $this->input->post('username');
            $password = $this->input->post('password');
            $instanceToken = $this->input->post('instanceToken');

            $response = $this->User->check($username, $password, $instanceToken);
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
        if($this->input->post())
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
        if($this->input->post(array('id', 'desired_weight', 'activityType', 'period', 'goal', 'name', 'begin_date')))
        { 
            $id = $this->input->post('id');
            $desiredWeight = $this->input->post('desired_weight');
            $activityType = $this->input->post('activityType');
            $period = $this->input->post('period');
            $goal = $this->input->post('goal');
            $name = $this->input->post('name');
            $begin_date = $this->input->post('begin_date');
            
            
            $response = $this->CaloriesCalc->saveUserGoal($id, $desiredWeight, $activityType, $period, $goal, $name, $begin_date);
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Invalid params';
        }
	
		echo json_encode($response, TRUE);
    }

    public function get_goal()
    {
    	if($this->input->post('id'))
        { 
            $id = $this->input->post('id');

            $response = $this->CaloriesCalc->getUserGoal($id);
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Invalid params';
        }

        echo json_encode($response, TRUE);
    }

    public function get_goal_archive()
    {

        if($this->input->post('id'))
        { 
            $id = $this->input->post('id');

            $response = $this->CaloriesCalc->get_archive($id);
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Invalid params';
        }

        echo json_encode($response, TRUE);
    }

    public function save_goal_archive()
    {
        if($this->input->post())
        { 
            $id = $this->input->post('id');
            $desired_weight = $this->input->post('desired_weight');
            $period = $this->input->post('period');
            $begin_date = $this->input->post('begin_date');
            $goal = $this->input->post('goal');
            $name = $this->input->post('name');
            $endup_weight = $this->input->post('endup_weight');
            $active = $this->input->post('active');
            $activityType = $this->input->post('activityType');
            $weight = $this->input->post('weight');
            $left_hand = $this->input->post('left_hand');
            $right_hand = $this->input->post('right_hand');
            $breast = $this->input->post('breast');
            $waist = $this->input->post('waist');
            $hiney = $this->input->post('hiney');
            $left_thigh = $this->input->post('left_thigh');
            $right_thigh = $this->input->post('right_thigh');
            $calfs = $this->input->post('calfs');
            $shoulders = $this->input->post('shoulders');

            $response = $this->CaloriesCalc->save_archive($id, $desired_weight, $period, $begin_date, $goal, $endup_weight, $activityType, $active, $name, $weight, $left_hand, $right_hand, $breast, $waist, $hiney, $left_thigh, $right_thigh, $calfs, $shoulders);
            
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Invalid params';
        }

        echo json_encode($response, TRUE);
    }

    public function get_human_chars()
    {
        if($this->input->post('id'))
        {
            $id = $this->input->post('id');
            $response = $this->CaloriesCalc->getHumanChars($id);
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Invalid params';
        }

        echo json_encode($response, TRUE);
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
