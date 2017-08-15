<?php
defined('BASEPATH') OR exit('No direct script access allowed');
class Product extends CI_Model {

    public function __construct()
    {
        parent::__construct();
        $this->load->database();
    }

    //Получение продуктов по их id (массив)
    public function getById($ids)
    {
        $this->db->select('*', TRUE);
        $this->db->from('Food');
        $this->db->where_in('food_id', $ids);
        $this->db->join('categories', 'Food.category = categories.id', TRUE);
        $q = $this->db->get();

        if($q->num_rows() > 0)
        {
        	$i = 0;
            foreach ($q->result() as $f)
            {
                $food[$i] = array(
                "food_id"   =>  $f->food_id,
                "name"      =>  $f->name,
                "protein"   =>  $f->protein,
                "fats"      =>  $f->fats,
                "carbs"     =>  $f->carbs,
                "calories"  =>  $f->calories,
                "category"  =>  $f->categ_name,
                "category_id" => $f->category
                    );
                $i++;
            } 

        }

        return $food;
    }



    //Запрос на получение списка еды
    //Указан id - даст продукты + даст кастомные блюда пользователя
    //Указан name - даст продукты по куску имени
    //Указан category_id - даст продукты по категории

    public function getfood($instanceToken, $id, $name, $category_id, $offset, $sort_alphabetical, $sort_calories)
    {
        $sql = 'SELECT * FROM "Food"';
        $sql = $sql . ' JOIN categories ON "Food".category = "categories".id';
       
        if($name)
        {
           $sql = $sql . " WHERE name ILIKE '$name%'";
        }

        if($category_id)
        {
            $sql = $sql . " WHERE category = $category_id";
        }

        switch ($sort_alphabetical) {
    		case 1:
    			$sql = $sql . ' ORDER BY name ASC';
    			break;

    		case 2:
    			$sql = $sql . ' ORDER BY name DESC';
    			break;
    	}

    	switch ($sort_calories) {
    		case 1:
                $sql = $sql . ' ORDER BY calories ASC';
    			break;

    		case 2:
    			$sql = $sql . ' ORDER BY calories DESC';
    			break;
    	}

        if($offset != -1)
        {
          $sql = $sql . " LIMIT 200 OFFSET $offset*200";
    	}

        $queryFood = $this->db->query($sql);

        $this->db->from('Users');
        $this->db->where('instanceToken', $instanceToken);
        $q = $this->db->get();

        $customFood = array();

        if($q->num_rows() > 0)
        {
        	
        	$this->db->from('custom_user_dishes');
        	$this->db->where('user_id', $id);

        
        	$queryCustom = $this->db->get();

        	 if($queryCustom)
            {
                foreach ($queryCustom->result() as $f) 
                { 
                    $cust_ing = $this->getById(json_decode($f->ingredients)->ingredients);
                    	
                    $customFood[] = array(
                        "id"   =>  $f->id,
                        "name" =>  $f->name,
                        "ingredients" => $cust_ing
                    );
                }

                $response['custom_food'] = $customFood;
            }
        }
        else
        {
        	$response['custom_food'] = $customFood;
        }

        $food = array();

        if($queryFood)
        {
            $response['status'] = 1;
            
              // foreach ($queryFood->result() as $f) 
              // { 
              //     $food[] = array(
              //         "food_id"   =>  $f->food_id,
              //         "name"      =>  $f->name,
              //         "protein"   =>  $f->protein,
              //         "fats"      =>  $f->fats,
              //         "carbs"     =>  $f->carbs,
              //         "calories"  =>  $f->calories,
              //         "category"  =>  $f->categ_name,
              //         "category_id" => $f->category
              //     );
              // }

            $response['food'] = $queryFood->result();
 
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Error occured';
            $response['food'] = $food;
            $response['custom_food'] = $customFood;
        }
 
        return $response;

    }

    //Сохранение кастомного блюда пользователя

    public function saveCustomDish($instanceToken, $id, $name, $ingredients)
    {
        $query = $this->db->insert("custom_user_dishes", array("user_id" => $id, "name" => $name, "ingredients" => $ingredients));

        $this->db->from('Users');
        $this->db->where('instanceToken', $instanceToken);
        $q = $this->db->get();

        if($q->num_rows() > 0)
        {
        	if($query)
        	{
            	$response['status'] = 1;
            	$response['msg'] = 'Блюдо добавлено';
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
            $response['msg'] = 'Доступ запрещён';
        }

        return $response;
    }

}