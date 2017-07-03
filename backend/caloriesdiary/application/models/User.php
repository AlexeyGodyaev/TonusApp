<?php
defined('BASEPATH') OR exit('No direct script access allowed');
class User extends CI_Model {

    public function __construct()
    {
        parent::__construct();
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
        $query = $this->db->get_where('Users', array('username' => $username, 'password' => $password));

        if ($query->num_rows() > 0)
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

    public function del($id, $password)
    {
        $query = $this->db->get_where('Users', array('id' => $id, 'password' => $password));

        if($query->num_rows() > 0)
        {
            $this->db->delete('Users', array('id' => $id, 'password' =>$password));
            $this->db->delete('user_chars', array('id' => $id ));
            return 1;
        }
        else
        {
            return 'Неверное имя пользователя или пароль';
        }
        
       
    }

    public function changePassword($username, $oldpassword, $newpassword)
    {
        $query = $this->db->get_where('Users', array('username' => $username, 'password' => $oldpassword));
        
        if($query->num_rows() > 0)
        {
                $data = array('password' => $newpassword);
                $this->db->where('username',$username);
                $this->db->update('Users',$data);
                return 1;
        }
        else
        {
            return 'Неверное имя пользователя или пароль';
        }
    }

    public function forgot($email)
    {
        $query = $this->db->get_where('Users', array('email' => $email));
        
        if($query->num_rows() > 0)
        {
            $newpassword = $this->generatePassword();
            $data = array('password' => $newpassword);
            $this->db->where('email',$email);
            $this->db->update('Users',$data);
            return 'Ваш новый пароль: '. $newpassword;
        }
        else
        {
            return 'Нет пользователя с таким email';
        }
    }
}