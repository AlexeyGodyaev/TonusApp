<?php
defined('BASEPATH') OR exit('No direct script access allowed');
class User extends CI_Model {

    public function __construct()
    {
        parent::__construct();
        $this->load->database();
    }

    public function check($username, $password)
    {
        $query = $this->db->get_where('Users', array('username' => $username));

        if ($query->num_rows() > 0)
        {
            $query = $this->db->get_where('Users', array('password' => $password));

            if($query->num_rows() > 0)
            {
                
                foreach ($query->result() as $row) 
                {
                    return $row->user_id;                
                }
                
            }
            else
            {
                return 'Неверный логин или пароль';
            }
        }
        else
        {   
            return 'Неверный логин или пароль';
        }

    }

    public function reg($username, $email, $password)
    {
        $query = $this->db->get_where('Users', array('username' => $username));

        if($query->num_rows() > 0)
        {
            return 'Имя пользователя уже занято';
        }
        else
        {
            $query = $this->db->get_where('Users', array('email' => $email));

            if($query->num_rows() > 0)
            {
                return 'Адрес электронной почты занят';
            }
            else
            {
                $data = array("username" => $username, "email" => $email, "password" => $password);
            }

        }   

        $query = $this->db->insert("Users", $data);

        if($query)
        {
            return 1;
        }
        else
        {
            return 'Unexpected error';
        }
    }

    public function del($username, $password)
    {
        $query = $this->db->get_where('Users', array('username' => $username));

        if($query->num_rows() > 0)
        {
            $query = $this->db->get_where('Users', array('password' => $username));

            if($query->num_rows() > 0)
            {
                $this->db->delete('Users', array('username' => $username));
                return 1;
            }
            else
            {
                return 'Неправильный пароль';
            }
        }
        else
        {
            return 'Имя пользователя не найдено';
        }
       
    }

    public function changePassword($username, $oldpassword, $newpassword)
    {
        $query = $this->db->get_where('Users', array('username' => $username));
        
        if($query->num_rows() > 0)
        {
            $query = $this->db->get_where('Users', array('password' => $oldpassword));
            if($query->num_rows() > 0)
            {
                $data = array('password' => $newpassword);
                $this->db->where('username',$username);
                $this->db->update('Users',$data);
                return 1;
            }
            else
            {
                return 'Unexpected error';
            }
        }
        else
        {
                return 'Имя пользователя не найдено';
        }
    }

    public function forgot($email)
    {
        $query = $this->db->get_where('Users', array('email' => $email));
        
        if($query->num_rows() > 0)
        {
            $newpassword = '123456';
            $data = array('password' => $newpassword);
            $this->db->where('email',$email);
            $this->db->update('Users',$data);
        }
        else
        {
            return 'Неверный адрес эл. почты';
        }
    }


}