<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Action extends CI_Model {

    public function __construct()
    {
        $this->load->database();
    }

    //Даёт список активностей
    public function getActivities($name, $sort_alphabetical, $sort_calories)
    {
        $this->db->from('Activities');

        switch ($sort_alphabetical) {
            case 1:
                $this->db->order_by('name', 'ASC');
                break;

            case 2:
                $this->db->order_by('name', 'DESC');
                break;
        }

        switch ($sort_calories) {
            case 1:
                $this->db->order_by('calories', 'ASC');
                break;

            case 2:
                $this->db->order_by('calories', 'DESC');
                break;
        }


        if($name != "")
        {
            $this->db->like('name', mb_convert_case($name, MB_CASE_TITLE, 'utf-8'), "after");
            $queryCapital = $this->db->get();
            $this->db->or_like('name', mb_strtolower($name));
            $queryLow = $this->db->get('Activities');

            $query = $queryCapital->result() + $queryLow->result();
        }
        else
        {
            $query = $this->db->get()->result();
        }


        
        if($query)
        {
            $response['status'] = 1;
            $response['activities'] = $query;
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Error occured';
            $response['activities'] = array();
        }

        return $response;
    }


}