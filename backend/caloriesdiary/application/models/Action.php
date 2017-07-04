<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Action extends CI_Model {

    public function __construct()
    {
        $this->load->database();
    }

    public function getTimestamp()
    {
        $q = $this->db->query('SELECT * FROM updates ORDER BY timestamp DESC LIMIT 1');
        if($q->num_rows() > 0)
        {
            foreach ($q->result() as $row)
            {
                return strtotime($row->timestamp);
            }
        }
    }

    public function get_act_names($timestamp)
    {
        if($this->getTimestamp() > $timestamp)
        {
            $this->db->select('name');
            $query = $this->db->get('Activities');
            return $query->result();
        }
        else
        {
            return false;
        }
    }

    public function get_activities()
    {
        $query = $this->db->get('Activities');

        if($query)
        {
            $response['status'] = 1;
            $response['activities'] = $query->result();
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