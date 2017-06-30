<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Users extends CI_Controller {

	public function __construct()
	{
		parent::__construct();

         $config = Array(
        'protocol' => 'smtp',
        'smtp_host' => 'ssl://smtp.mail.ru',
        'smtp_port' => 465,
        'smtp_user' => 'ml-98@mail.ru', 
        'smtp_pass' => 'ubuntu', 
        'mailtype' => 'text',
        'charset' => 'utf-8',
        'wordwrap' => TRUE
        );

        $this->load->library('email', $config);
		$this->load->model('User');
        $this->load->model('CaloriesCalc');
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
            $response['user_id'] = $status;
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

        $status = $this->CaloriesCalc->saveUserChars($id, $realName, $weight, $height, $sex, $activityType, $age, $avgdream, $wokeup);

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

    public function get_user_chars()
    {
        $id = $this->input->post('id');
        $chars_q = $this->CaloriesCalc->getUserChars($id);  

        $response = array();

        foreach ($chars_q as $f) 
        { 
            $chars[] = array(
                "realName" => $f->realName,
                "weight"        =>  $f->weight,
                "height"      =>  $f->height,
                "sex"   =>  $f->sex,
                "age"      =>  $f->age,
                "activityType" => $f->activityType,
                "avgdream"     =>  $f->avgdream,
                "wokeup"  =>  $f->wokeup
            );
        } 

        $response['userChars'] = $chars;
        echo json_encode($response, TRUE);

    }

    public function forgot_password()
    {
        $email = $this->input->post('email');

        $status = $this->User->forgot($email);

        $this->email->from('ml-98@mail.ru', 'noreply');
        $this->email->to($email);

        $this->email->subject('Ваш новый пароль для аккаунта');
       
        if(!is_string($status))
        {
            $response['msg'] = 'Нет такого e-mail';
            $response['status'] = 0;
            echo json_encode($response, TRUE);
        }
        else
        {
            $response['msg'] = "ОК";
            $response['status'] = 1;
            echo json_encode($response, TRUE);
            $this->email->message($status);
            $this->email->send();
        }
        
        

    }

}
