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
        $query = $this->db->get_where('Users', array('username' => $username), 10, 10);

        if ($query->num_rows() > 0)
        {
            $query = $this->db->get_where('Users', array('password' => $password), 10, 10);

            if($query->num_rows() > 0)
            {
                return 1;
            }
            else
            {
                return 0;
            }
        }
        else
        {   
            return 0;
        }

    }

    public function reg($username, $email, $password)
    {
        $data = array("username" => $username, "email" => $email, "password" => $password);
        $query = $this->db->insert("Users", $data);
        if($query)
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }

}