<?php
defined('BASEPATH') OR exit('No direct script access allowed');
class CaloriesCalc extends CI_Model {

    public function __construct()
    {
        parent::__construct();
        $this->load->database();
    }

    //Функция вычисляет суточную норму (изначально зависел от цели)

    public function caloriesPerday($id)
    {
        $this->db->select('*', TRUE);
        $this->db->from('user_chars');
        $this->db->join('ActivityTypes', 'user_chars.activityType = ActivityTypes.id', TRUE);
        
        $queryChars = $this->db->get();

        foreach ($queryChars->result() as $row)
        {
            $weight = $row->weight;
            $height = $row->height;
            $activityType = $row->value;
            $age = $row->age;
            $sex = $row->sex;
            $dreamTime = $row->avgdream;
        }

       // $queryGoal = $this->db->get_where('user_goal', array('user_id' => $id));

      //  foreach ($queryGoal->result() as $row)
        //{
        //    $desiredweight = $row->desired_weight;
         //   $period = $row->period;
           // $goal = $row->goal;
       // }

        $dreamCalories = $dreamTime * 66; 

        $maffin = 10*$weight + 6.25*$height - 5*$age;

        if ($sex == 1)
        {
            $maffin += 5;
        }
        else
        {
            $maffin -= 161;
        }

        $result = $maffin * $activityType;

      //  $kgPerDay = abs($desiredweight-$weight)/$period;


        //switch ($goal) {
           // case 1:
                return array(
                	'status' => 1,
                	'result' => round($result),
                	'dreamCalories' => $dreamCalories,
                	'protein' => round($result/4, 2),
                	'fats' => round($result/9, 2),
                	'carbs' => round($result/4, 2));
               // break;
            // case 2:
            //     return array(
            //     	'status' => 1,
            //     	'result' => round($result - $result*0.2),
            //     	'dreamCalories' => $dreamCalories,
            //     	'protein' => round(($result*0.3)/4, 2),
            //     	'fats' => round(($result*0.3)/9, 2),
            //     	'carbs' => round(($result*0.4)/4, 2));
            //     break;
            // case 3:
            //     return array(
            //     	'status' => 1,
            //     	'result' => round($result),
            //     	'dreamCalories' => $dreamCalories,
            //     	'protein' => round(($result*0.2)/4, 2),
            //     	'fats' => round(($result*0.3)/9, 2),
            //     	'carbs' => round(($result*0.5)/4, 2));
            //     break;
            // case 4:
            //     return array(
            //     	'status' => 1,
            //     	'result' => round($result + $result*0.2),
            //     	'dreamCalories' => $dreamCalories,
            //     	'protein' => round(($result*0.25)/4, 2),
            //     	'fats' => round(($result*0.25)/9, 2),
            //     	'carbs' => round(($result*0.5)/4, 2));
            //     break;
            // case 5:
            //     return array(
            //     	'status' => 1,
            //     	'result' => round($result + $result*0.2),
            //     	'dreamCalories' => $dreamCalories,
            //     	'protein' => round(($result*0.2)/4, 2),
            //     	'fats' => round(($result*0.3)/9, 2),
            //     	'carbs' => round(($result*0.5)/4, 2));
            //     break;
            
        //}
    }

    //Сохранение/Изменение профиля

    public function saveUserChars($user_id, $realName, $weight, $height, $sex, $activityType, $age, $avgdream, $wokeup)
    {
        $query = $this->db->get_where('user_chars', array('user_id' => $user_id));

        if($query->num_rows() > 0)
        {
            $data = array(
                "realName" => $realName,
                "weight" => $weight,
                "height" => $height,
                "sex" => $sex,
                "activityType" => $activityType,
                "age"=> $age,
                "avgdream" => $avgdream,
                "wokeup" => $wokeup
                );

            if(count(array_filter($data)) == count($data)) 
            {
                $this->db->where('user_id', $user_id);
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
            $data = array(
            "user_id" => $user_id,
            "realName" => $realName,
            "weight" => $weight,
            "height" => $height,
            "sex" => $sex,
            "activityType" => $activityType,
            "age"=> $age,
            "avgdream" => $avgdream,
            "wokeup" => $wokeup
            );

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

    //Получение профиля

    public function getUserChars($id)
    {
        $this->db->cache_on();
        $query = $this->db->get_where('user_chars', array('user_id' => $id));
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

    //(На удаление) Задание/Перезапись цели

    public function saveUserGoal($id, $desiredWeight, $activityType, $period, $goal, $name, $begin_date)
    {
        $query = $this->db->get_where('user_goal', array('user_id' => $id));

	    $dataProfile = array("activityType" => $activityType);

        if($query->num_rows() > 0)
        {
            $dataGoal = array("name" => $name, "desired_weight" => $desiredWeight, "period" => $period, "goal" => $goal, "beginDate" => $begin_date);

            if(count(array_filter($dataGoal)) == count($dataGoal)) 
            {
                $this->db->where('user_id', $id);
                $query = $this->db->update("user_goal", $dataGoal);

                $this->db->where('user_id', $id);
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
            $dataGoal = array("user_id" => $id,"name" => $name, "desired_weight" => $desiredWeight,"period" => $period, "goal" => $goal, "beginDate" => $begin_date);

            $query = $this->db->insert("user_goal", $dataGoal);

            $this->db->where('user_id', $id);
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

    //(На удаление) Получение цели

    public function getUserGoal($id)
    {
        $this->db->cache_on();
        $query = $this->db->get_where('user_goal', array('user_id' => $id));
        $this->db->cache_off();

        $this->db->select('activityType');
        $queryType = $this->db->get_where('user_chars', array('user_id' => $id));

        $userGoal = array();

        if($query && $queryType)
        {
            foreach ($query->result() as $f) 
            { 
                foreach ($queryType->result() as $d) 
                { 
                    $userGoal = array(
                        "desired_weight"      =>  $f->desired_weight,
                        "period"              =>  $f->period,
                        "goal"                =>  $f->goal,
                        "activityType"        =>  $d->activityType,
                        "name"                =>  $f->name,
                        "begin_date"          => date_format( date_create($f->beginDate), 'Y-m-d')
                    );
                }
            }

            if(empty($userGoal))
            {
                $response['status'] = 0;
                $response['userGoal'] = $userGoal;
            }
           else
            {
                $response['status'] = 1;
                $response['userGoal'] = $userGoal;
            }   
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Error occured';
            $response['userChars'] = $userGoal;
        }

        return $response;
    }

    //(На удаление) Задание/Перезапись цели

    public function get_archive($user_id)
    {
        $query = $this->db->get_where('user_goal_archive', array('user_id' => $user_id));
        
        $userGoals = array();
        $atr = array();

        if($query)
        {
        	$i = 0;
            foreach ($query->result() as $f) 
            { 
                $userGoals[$i] = array(
                    $goal_id = $f->id,
                   "desired_weight"    =>  $f->desired_weight,
                   "period"            =>  $f->period,
                   "goal"              =>  $f->goal,
                   "name"              =>  $f->name,
                   "endup_weight"      =>  $f->endup_weight,
                   "active"            =>  $f->active,
                   "activityType"      =>  $f->activityType,
                   "begin_date"        =>  date_format( date_create($f->beginDate), 'Y-m-d')
                    );

 
                $atr = $this->getHumanChars($user_id, $goal_id);
                $userGoals[$i] += $atr;
                $i++;
            }

            if(empty($userGoals))
            {
                $response['status'] = 0;
                $response['userGoals'] = $userGoals;
            }
           else
            {
                $response['status'] = 1;
                $response['userGoals'] = $userGoals;
            }   
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Error occured';
            $response['userGoals'] = $userGoals;
        }

        return $response;
    }

    //(На удаление) Сохранение архива целей

    public function save_archive($id, $desired_weight, $period, $begin_date, $goal, $endup_weight, $activityType, $active, $name, $weight, $left_hand, $right_hand, $breast, $waist, $hiney, $left_thigh, $right_thigh, $calfs, $shoulders)
    {
    	$data = array(
    		"user_id" => $id,
    		"desired_weight" => $desired_weight,
    		"endup_weight" => $endup_weight,
    		"period" => $period,
    		"beginDate" => $begin_date,
    		"goal" => $goal,
    		"name" => $name,
    		"activityType" => $activityType,
    		"active" => $active
    		);

    	$query = $this->db->insert('user_goal_archive', $data);

        $query = $this->db->get_where('user_goal_archive', $data);

        foreach ($query->result() as $f) {
            $goal_id = $f->id; 
        }


        $this->saveHumanChars($id, $goal_id, $weight, $left_hand, $right_hand, $breast, $waist, $hiney, $left_thigh, $right_thigh, $calfs, $shoulders);

    	if($query)
    	{
    		$response['status'] = 1;
    		$response['msg'] = 'OK';
            $response['goal_id'] = $goal_id;
    	}
    	else
    	{
    		$response['status'] = 0;
    		$response['msg'] = 'Error occured';
    	}

		return $response;
    }

    //(На удаление) Получение антропометрии ( по id цели)

    public function getHumanChars($id, $goal_id)
    {
        $query = $this->db->get_where('user_human_chars', array('user_id' => $id, 'goal_id' => $goal_id));

        $humanChars = array();

        if($query)
        {
            foreach ($query->result() as $f) 
            { 
                    $humanChars = array(
                    "weight"           =>  $f->weight,
                    "left_hand"        =>  $f->left_hand,
                    "right_hand"       =>  $f->right_hand,
                    "breast"           =>  $f->breast,
                    "waist"            =>  $f->waist,
                    "hiney"            =>  $f->hiney,
                    "left_thigh"       =>  $f->left_thigh,
                    "right_thigh"      =>  $f->right_thigh,
                    "calfs"            =>  $f->calfs,
                    "shoulders"        =>  $f->shoulders
                    );
            }  
    	}

    	return $humanChars; 
	}

    //(На удаление) Сохранение антропометрии (по id цели)

    public function saveHumanChars($id, $goal_id, $weight, $left_hand, $right_hand, $breast, $waist, $hiney, $left_thigh, $right_thigh, $calfs, $shoulders)
    {
        $data = array(
            'user_id' => $id,
            'goal_id' => $goal_id,
            'weight' => $weight,
            'left_hand' => $left_hand,
            'right_hand' => $right_hand,
            'breast' => $breast,
            'waist' => $waist,
            'hiney' => $hiney,
            'left_thigh' => $left_thigh,
            'right_thigh' => $right_thigh,
            'calfs' => $calfs,
            'shoulders' => $shoulders
            );
   
        $this->db->insert('user_human_chars', $data);
    }

    //TODO: Перенести в отдельный скрипт.
    //Получение рандомных продуктов и активностей

    public function getRandom($number)
    {
        $this->db->from('Food');
        $this->db->order_by('name', 'RANDOM');
        $this->db->limit($number);

        $queryFood = $this->db->get();

        $this->db->from('Activities');
        $this->db->order_by('name', 'RANDOM');
        $this->db->limit($number);

        $querySport = $this->db->get();

        if($queryFood->num_rows() > 0)
        {
            $response['status'] = 1;

            $i = 0;
            foreach ($queryFood->result() as $f)
            {
                
                $food[$i] = array(
                "food_id"   =>  $f->food_id,
                "name"      =>  $f->name,
                "protein"   =>  $f->protein,
                "fats"      =>  $f->fats,
                "carbs"     =>  $f->carbs,
                "calories"  =>  $f->calories,
                );
                $i++;
            } 

            $response['food'] = $food;
        }

        if($querySport->num_rows() > 0)
        {
            $i = 0;
            foreach ($querySport->result() as $s)
            {
                $sport[$i] = array(
                "id"        =>  $s->id,
                "name"      =>  $s->name,
                "calories"  =>  $s->calories
                );
                $i++;
            } 

            $response['activities'] = $sport;
        }
        
        return $response;
    }

}