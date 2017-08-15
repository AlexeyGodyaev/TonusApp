<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Users extends CI_Controller {


	public function __construct()
	{
		parent::__construct();

        $this->load->library('form_validation');

		$this->load->model('User');
        $this->load->model('CaloriesCalc');

	}

	public function is_json($string) {
        json_decode($string);
        return (json_last_error() == JSON_ERROR_NONE);
    }

	public function send_push()
	{
        $this->form_validation->set_rules('title', 'Title', 'required');
        $this->form_validation->set_rules('body', 'Body', 'required');

		if($this->form_validation->run())
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

    public function save_day()
    {
        $this->form_validation->set_rules('id', 'Id', 'required|integer');
        $this->form_validation->set_rules('instanceToken', 'instanceToken', 'required');

        $this->form_validation->set_rules('mass', 'Mass', 'required|integer');
        $this->form_validation->set_rules('active_sum', 'Active_sum', 'required|integer');
        $this->form_validation->set_rules('food_sum', 'Food_sum', 'required|integer');
        $this->form_validation->set_rules('protein', 'Protein', 'required');
        $this->form_validation->set_rules('day_calories', 'dayCalories', 'required');
        $this->form_validation->set_rules('fats', 'Fats', 'required');
        $this->form_validation->set_rules('carbs', 'Carbs', 'required');
        $this->form_validation->set_rules('note', 'Note', 'required');
        $this->form_validation->set_rules('date', 'Date', 'required|regex_match[/^\d{4}-\d{1}-\d{1}$/]');

        $this->form_validation->set_rules('active', 'Active', 'required');
        $this->form_validation->set_rules('food', 'Food', 'required');

        $this->form_validation->set_rules('lHand', 'LHand', 'required|integer');
        $this->form_validation->set_rules('rHand', 'RHand', 'required|integer');
        $this->form_validation->set_rules('chest', 'Chest', 'required|integer');
        $this->form_validation->set_rules('waist', 'Waist', 'required|integer');
        $this->form_validation->set_rules('butt', 'Butt', 'required|integer');
        $this->form_validation->set_rules('rLeg', 'RLeg', 'required|integer');
        $this->form_validation->set_rules('lLeg', 'LLeg', 'required|integer');
        $this->form_validation->set_rules('calves', 'Calfs', 'required|integer');
        $this->form_validation->set_rules('shoulders', 'Shoulders', 'required|integer');

        

        if($this->form_validation->run() && $this->is_json($this->input->post('active')) && $this->is_json($this->input->post('food')))
        {
            $id = $this->input->post('id');
            $instanceToken = $this->input->post('instanceToken');
            $mass = $this->input->post('mass');
            $active_sum = $this->input->post('active_sum');
            $food_sum = $this->input->post('food_sum');
            $note = $this->input->post('note');
            $activities = $this->input->post('active');
            $food = $this->input->post('food');
            $date = $this->input->post('date');
            $protein = $this->input->post('protein');
            $fats = $this->input->post('fats');
            $carbs = $this->input->post('carbs');
            $day_calories = $this->input->post('day_calories');

            $left_hand = $this->input->post('lHand');
            $right_hand = $this->input->post('rHand');
            $breast = $this->input->post('chest');
            $waist = $this->input->post('waist');
            $hiney = $this->input->post('butt');
            $left_thigh = $this->input->post('rLeg');
            $right_thigh = $this->input->post('lLeg');
            $calfs = $this->input->post('calves');
            $shoulders = $this->input->post('shoulders');


            $response = $this->User->save_day($instanceToken, $id, $mass, $active_sum, $food_sum, $note, $activities, $food, $date, $protein, $fats, $carbs, $day_calories, $left_hand, $right_hand, $breast, $waist, $hiney, $left_thigh, $right_thigh, $calfs, $shoulders);
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = validation_errors();
        }
       
        echo json_encode($response, TRUE);
    }

    public function get_days()
    {
        $this->form_validation->set_rules('id', 'Id', 'required|integer');
        $this->form_validation->set_rules('instanceToken', 'instanceToken', 'required');

        if ($this->form_validation->run())
        {
            $id = $this->input->post('id');
            $instanceToken = $this->input->post('instanceToken');
            
            $response = $this->User->get_days($instanceToken, $id);
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
        $this->form_validation->set_rules('ga_id', 'GA_ID', 'required');
        $this->form_validation->set_rules('email', 'email', 'required');
        $this->form_validation->set_rules('username', 'Username', 'required');

        if($this->form_validation->run())
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

    public function save_user_chars()
    {
        $this->form_validation->set_rules('realName', 'Real Name', 'required');
        $this->form_validation->set_rules('weight', 'Weight', 'required|integer');
        $this->form_validation->set_rules('height', 'Height', 'required|integer');
        $this->form_validation->set_rules('sex', 'Sex', 'required|integer');
        $this->form_validation->set_rules('age', 'Age', 'required|integer');
        $this->form_validation->set_rules('activityType', 'activityType', 'required|integer');
        $this->form_validation->set_rules('avg_dream', 'Avg Dream', 'required|integer');
        //TODO: решить вопрос с форматом времени
        $this->form_validation->set_rules('wokeup_time', 'Wokeup time', 'required');

        if($this->form_validation->run())
        {
            $id = $this->input->post('id');
            $instanceToken = $this->input->post('instanceToken');
            $realName = $this->input->post('realName');
            $weight = $this->input->post('weight');
            $height = $this->input->post('height');
            $sex = $this->input->post('sex');
            $age = $this->input->post('age');
            $activityType = $this->input->post('activityType');
            $avgdream = $this->input->post('avg_dream');
            $wokeup = $this->input->post('wokeup_time');

            $response = $this->CaloriesCalc->saveUserChars($instanceToken, $id, $realName, $weight, $height, $sex, $activityType, $age, $avgdream, $wokeup);
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = validation_errors();
        }

        echo json_encode($response, TRUE);
    }

    public function get_user_chars()
    {
        $this->form_validation->set_rules('id', 'Id', 'required|integer');
        $this->form_validation->set_rules('instanceToken', 'instanceToken', 'required');

        if($this->form_validation->run())
        {
            $id = $this->input->post('id');
            $instanceToken = $this->input->post('instanceToken');
            $response = $this->CaloriesCalc->getUserChars($id, $instanceToken);
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
        $this->form_validation->set_rules('username', 'username', 'required');
        $this->form_validation->set_rules('password', 'password', 'required');
        $this->form_validation->set_rules('instanceToken', 'instanceToken', 'required');

        if($this->form_validation->run())
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

    public function change_password()
    {
        $this->form_validation->set_rules('username', 'username', 'required');
        $this->form_validation->set_rules('password', 'password', 'required');
        $this->form_validation->set_rules('oldpassword', 'oldpassword', 'required');

        if($this->form_validation->run())
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
        $this->form_validation->set_rules('id', 'Id', 'required|integer');
        $this->form_validation->set_rules('password', 'password', 'required');

        if($this->form_validation->run())
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

    public function forgot_password()
    {
        $this->form_validation->set_rules('email', 'Email', 'required|valid_email');

        if($this->form_validation->run())
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
