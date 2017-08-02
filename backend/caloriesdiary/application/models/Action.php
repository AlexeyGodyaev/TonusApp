<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Action extends CI_Model {

    public function __construct()
    {
        $this->load->database();
    }

    //Получение даты последнего обновления БД активностей

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

    //Получение активностей по id (не используется)

    public function getById($id)
    {
        $this->db->where('id', $id);
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

    //Получение имён активностей (не используется). Предпологалось, что будет autocompleteTextView для удобства поиска,
    //но поиск просто осуществляется на устройстве
    
    public function get_act_names($timestamp)
    {
        $actNames = array();
        $response['status'] = 1;

        if($this->getTimestamp() > $timestamp)
        {
            $this->db->select('name', TRUE);
            $query = $this->db->get('Activities');
            
            $i = 0;
            foreach ($query->result() as $act)
            {
                $actNames[$i] = $act->name;
                $i++;
            }

            $response['update'] = true;
            $response['actNames'] = $actNames;
        }
        else
        {
            $response['status'] = 0;
            $response['update'] = false;
            $response['actNames'] = $actNames;
        }

        return $response;
    }

    //Даёт список активностей (GET)
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