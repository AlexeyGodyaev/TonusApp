<?php
defined('BASEPATH') OR exit('No direct script access allowed');
class CaloriesCalc extends CI_Model {

    public function __construct()
    {
        parent::__construct();
        $this->load->database();
    }

    public function caloriesPerday($weight, $height, $sex, $activityType, $age)
    {
        $query = $this->db->get_where('ActivityTypes', array('id' => $activityType));

        foreach ($query->result() as $row) {
            $value = $row->value;
        }

        $maffin = 10*$weight + 6.25*$height - 5*$age;

        if ($sex == 1)
        {
            $maffin += 5;
        }
        else
        {
            $maffin -= 161;
        }
        return $maffin*$value;
    }

    public function saveUserChars($id, $weight, $height, $sex, $activityType, $age)
    {
         $query = $this->db->get_where('user_chars', array('id' => $id));

        if($query->num_rows() > 0)
        {
            return 'Данные нельзя изменить';
        }
        else
        {
            $data = array("id" => $id, "weight" => $weight, "height" => $height, "sex" => $sex,
            "activityType" => $activityType, "age"=> $age);

        $query = $this->db->insert("user_chars", $data);

        if($query)
        {
            return 1;
        }
        else
        {
            return 'Unexpected error';
        }
        }

    }

}