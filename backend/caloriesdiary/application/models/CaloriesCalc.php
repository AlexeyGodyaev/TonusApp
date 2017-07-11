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

        foreach ($query->result() as $row)
        {
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

        $result = $maffin*$value;

        return array('status' => 1,'loseWeight' => $result - 800,'maintainWeight' => $result, 'gainWeight' => $result + 800);
    }

    public function saveUserChars($id, $realName, $weight, $height, $sex, $activityType, $age, $avgdream, $wokeup)
    {
         $query = $this->db->get_where('user_chars', array('id' => $id));

        if($query->num_rows() > 0)
        {
            $data = array("realName" => $realName,"weight" => $weight, "height" => $height, "sex" => $sex,
            "activityType" => $activityType, "age"=> $age, "avgdream" => $avgdream, "wokeup" => $wokeup);

            if(count(array_filter($data)) == count($data)) 
            {
                $this->db->get_where('user_chars', array('id' => $id));
                $query = $this->db->update("user_chars", $data);
                $this->db->cache_delete();

                if($query)
                {
                    $response['status'] = 1;
                    $response['msg'] = 'Профиль обновлён';
                }
                else
                {
                    $response['status'] = 0;
                    $response['msg'] = 'Error occured';
                }
            } 
            else
            {
                $response['status'] = 0;
                $response['msg'] = 'Не все поля указаны';
            }
            
        }
        else
        {
            $data = array("id" => $id, "realName" => $realName,"weight" => $weight, "height" => $height, "sex" => $sex,
            "activityType" => $activityType, "age"=> $age, "avgdream" => $avgdream, "wokeup" => $wokeup);

            $query = $this->db->insert("user_chars", $data);
            $this->db->cache_delete();

            if($query)
            {
                $response['status'] = 1;
                $response['msg'] = 'ОК';
            }
            else
            {
                $response['status'] = 0;
                $response['msg'] = 'Error occured';
            }
        }

        return $response;
    }

    public function getUserChars($id)
    {
        $this->db->cache_on();
        $query = $this->db->get_where('user_chars', array('id' => $id));
        $this->db->cache_off();

        $userChars = array();

        if($query)
        {
            foreach ($query->result() as $f) 
            { 
                $userChars[] = array(
                    "realName"      =>  $f->realName,
                    "weight"        =>  $f->weight,
                    "height"        =>  $f->height,
                    "sex"           =>  $f->sex,
                    "age"           =>  $f->age,
                    "activityType"  =>  $f->activityType,
                    "avgdream"      =>  $f->avgdream,
                    "wokeup"        =>  $f->wokeup
                );
            }

            if(empty($userChars))
            {
                $response['status'] = 0;
                $response['userChars'] = $userChars;
            }
           else
            {
                $response['status'] = 1;
                $response['userChars'] = $userChars;
            }   
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Error occured';
            $response['userChars'] = $userChars;
        }

        return $response;
    }

    public function saveUserGoal($id, $desiredWeight, $activityType, $period, $goal)
    {
        $query = $this->db->get_where('user_goal', array('id' => $id));

        if($query->num_rows() > 0)
        {
            $dataGoal = array("desiredWeight" => $desiredWeight, "period" => $period, "goal" => $goal);
            $dataProfile = array("activityType" => $activityType);

            if(count(array_filter($dataGoal)) == count($dataGoal)) 
            {
                $this->db->get_where('user_goal', array('id' => $id));
                $query = $this->db->update("user_goal", $dataGoal);

                $this->db->get_where('user_chars', array('id' => $id));
                $query = $this->db->update("user_chars", $dataProfile);

                $this->db->cache_delete();

                if($query)
                {
                    $response['status'] = 1;
                    $response['msg'] = 'Цель обновлена';
                }
                else
                {
                    $response['status'] = 0;
                    $response['msg'] = 'Error occured';
                }
            } 
            else
            {
                $response['status'] = 0;
                $response['msg'] = 'Не все поля указаны';
            }
            
        }
        else
        {
            $dataGoal = array("desiredWeight" => $desiredWeight,"period" => $period, "goal" => $goal);

            $query = $this->db->insert("user_goal", $dataGoal);

            $this->db->get_where('user_chars', array('id' => $id));
            $query = $this->db->update("user_chars", $dataProfile);

            $this->db->cache_delete();

            if($query)
            {
                $response['status'] = 1;
                $response['msg'] = 'ОК';
            }
            else
            {
                $response['status'] = 0;
                $response['msg'] = 'Error occured';
            }
        }

        return $response;
    }

}