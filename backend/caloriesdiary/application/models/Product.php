<?php
defined('BASEPATH') OR exit('No direct script access allowed');
class Product extends CI_Model {

    public function __construct()
    {
        parent::__construct();
        $this->load->database();
    }

    //Получение даты последнего обновления БД продуктов

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

    //Получение имён активностей (не используется).

    public function get_food_names($timestamp)
    {
        $foodNames = array();

        if($this->getTimestamp() > $timestamp)
        {
            $this->db->select('name');
            $query = $this->db->get('Food');
            
            $i = 0;
            foreach ($query->result() as $f) 
            {
                $foodNames[$i] = $f->name;
                $i++;
            }

            $response['status'] = 1;
            $response['update'] = true;
            $response['foodNames'] = $foodNames;
        }
        else
        {
            $response['status'] = 1;
            $response['update'] = false;
            $response['foodNames'] = $foodNames;
        }

        return $response;

    }

    //Получение списка категорий продуктов
    public function getFoodcategories()
    {
        $this->db->select('*', TRUE);
        $this->db->from('categories');
        $query = $this->db->get();

        if($query)
        {
            $response['status'] = 1;
            $response['categories'] = $query->result();
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Error occured';
            $response['categories'] = array();
        }

        return $response;
    }

    //Получение еды по категории (не используется)

    public function getfoodBycategory($id, $offset)
    {
        $this->db->select('*', TRUE);
        $this->db->from('Food');
        $this->db->where('category',$id);
        $this->db->join('categories', 'Food.category = categories.id', TRUE);
        $this->db->limit(500, $offset*500);
        $query = $this->db->get();

        $food = array();

        if($query)
        {
            $response['status'] = 1;
            foreach ($query->result() as $f) 
            { 
                $food[] = array(
                    "food_id"   =>  $f->food_id,
                    "name"      =>  $f->name,
                    "protein"   =>  $f->protein,
                    "fats"      =>  $f->fats,
                    "carbs"     =>  $f->carbs,
                    "calories"  =>  $f->calories,
                    "category"  =>  $f->categ_name,
                    "category_id" => $f->category
                );
            }
            $response['food'] = $food;
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Error occured';
            $response['food'] = $food;
        }
        
        return $response;
    }

    //Запрос на получение списка еды
    //Указан id - даст продукты + даст кастомные блюда пользователя
    //Указан name - даст продукты по куску имени
    //Указан category_id - даст продукты по категории

    public function getfood($id, $name, $category_id, $offset, $sort_alphabetical, $sort_calories)
    {
        $this->db->select('*');
        $this->db->from('Food');

        if($name != "")
        {
            $this->db->like('name', $name);
        }

        $this->db->join('categories', 'Food.category = categories.id');

        if(!empty($category_id))
        {
        	$this->db->where('category', $category_id);
        }

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

        if($offset != -1)
        {
            $this->db->limit(500, $offset*500);
    	}

        $queryFood = $this->db->get();

        $this->db->select('*');
        $this->db->from('custom_user_dishes');
        $this->db->where('user_id', $id);

        $queryCustom = $this->db->get();

        $food = array();
        $customFood = array();

        if($queryFood)
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
                    "category"  =>  $f->categ_name,
                    "category_id" => $f->category
                );
                $i++;
            }

            $response['food'] = $food;

            if($queryCustom)
            {
                $i = 0;
                foreach ($queryCustom->result() as $f) 
                { 
                    $cust_ing = $this->getById(json_decode($f->ingredients)->ingredients);
                    	
                    $customFood[$i] = array(
                        "id"   =>  $f->id,
                        "name" =>  $f->name,
                        "ingredients" => $cust_ing
                    );

                    $i++;

                }

                $response['custom_food'] = $customFood;
            }
 
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

    public function saveCustomDish($id, $name, $ingredients)
    {
        $query = $this->db->insert("custom_user_dishes", array("user_id" => $id, "name" => $name, "ingredients" => $ingredients));

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

        return $response;
    }

}