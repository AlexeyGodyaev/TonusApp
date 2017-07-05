<?php
defined('BASEPATH') OR exit('No direct script access allowed');
class User extends CI_Model {

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
        $this->load->helper('email');
        $this->load->database();
    }

    public function generatePassword($length = 8)
    {
        $chars = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
        $count = mb_strlen($chars);

        for ($i = 0, $result = ''; $i < $length; $i++) 
        {
            $index = rand(0, $count - 1);
            $result .= mb_substr($chars, $index, 1);
        }

        return $result;
    }

    public function check($username, $password)
    {
        if(valid_email($username))
        {
            $this->db->cache_on();
            $query = $this->db->get_where('Users', array('email' => $username, 'password' => $password));
            $this->db->cache_off();
        }
        else
        {
            $this->db->cache_on();
            $query = $this->db->get_where('Users', array('username' => $username, 'password' => $password));
            $this->db->cache_off();
        }
        
        if ($query->num_rows() > 0)
        {  
            $response['status'] = 1;
            $response['msg'] = 'OK';
            foreach ($query->result() as $row) 
            {
                $response["user_id"] = $row->user_id;                
            }
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Неверный логин или пароль';
        }

        return $response;
    }

    public function reg($username, $email, $password)
    {
        $query = $this->db->get_where('Users', array('username' => $username));

        if($query->num_rows() > 0)
        {
            $response['status'] = 0;
            $response['msg'] = 'Имя пользователя уже занято';
        }
        else
        {
            $query = $this->db->get_where('Users', array('email' => $email));

            if($query->num_rows() > 0)
            {
                $response['status'] = 0;
                $response['msg'] = 'Адрес электронной почты занят';
            }
            else
            {
                $data = array("username" => $username, "email" => $email, "password" => $password);
                $query = $this->db->insert("Users", $data);
                $this->db->cache_delete();

                if($query)
                {
                    $response['status'] = 1;
                    $response['msg'] = 'OK';
                }
                else
                {
                    $response['status'] = 0;
                    $response['msg'] = 'Error occured';
                }
            }

        }   

        return $response;
    }

    public function del($id, $password)
    {
        $query = $this->db->get_where('Users', array('user_id' => $id, 'password' => $password));

        if($query->num_rows() > 0)
        {
            $response['status'] = 1;
            $response['msg'] = 'OK';
            $this->db->delete('Users', array('user_id' => $id, 'password' =>$password));
            $this->db->delete('user_chars', array('id' => $id ));
            $this->db->cache_delete();
            
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Неверное имя пользователя или пароль';
        }

        return $response;
    }

    public function changePassword($username, $oldpassword, $newpassword)
    {
        $query = $this->db->get_where('Users', array('username' => $username, 'password' => $oldpassword));
        
        if($query->num_rows() > 0)
        {
            $response['status'] = 1;
            $response['msg'] = "OK";

            $data = array('password' => $newpassword);
            $this->db->where('username', $username);
            $this->db->update('Users', $data);
            $this->db->cache_delete();
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Неверное имя пользователя или пароль';
        }

        return $response;
    }

    public function forgot($email)
    {
        $query = $this->db->get_where('Users', array('email' => $email));
        
        if($query->num_rows() > 0)
        {
            $newpassword = $this->generatePassword();

            $data = array('password' => $newpassword);

            $this->db->where('email', $email);
            $this->db->update('Users', $data);
            $this->db->cache_delete();

            $this->email->from('ml-98@mail.ru', 'noreply');
            $this->email->to($email);

            $this->email->subject('Ваш новый пароль для аккаунта');

            $this->email->message('Ваш новый пароль: '. $newpassword);

            $response['status'] = 1;
            $response['msg'] = 'OK';

            $this->email->send();

        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Нет пользователя с таким email';
        }

        return $response;
    }
}