<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Food extends CI_Controller {

	public function __construct()
	{
		parent::__construct();
		$this->load->model('Product');
	}

	public function get_food_names($timestamp)
	{
		$food_names_q = $this->Product->get_food_names($timestamp);

		$response = array();
    	$foodNames = array();

    	$i = 0;
        foreach ($food_names_q as $f) {
            $foodNames[$i] = $f->name;
            $i++;
        }

        $response['foodNames'] = $foodNames;
    	echo json_encode($response, TRUE);
	}

	public function get_food()
	{
        $offset = $this->input->post('offset');
		$food_q = $this->Product->get_food($offset);

		$response = array();
    	$food = array();

    	foreach ($food_q as $f) 
    	{ 
        	$food[] = array(
            	"id"        =>  $f->id,
            	"name"      =>  $f->name,
            	"protein"   =>  $f->protein,
            	"fats"      =>  $f->fats,
            	"carbs"     =>  $f->carbs,
            	"calories"  =>  $f->calories,
                "category"  =>  $f->category
        	);
    	} 

    	$response['food'] = $food;
    	echo json_encode($response, TRUE);
	}

    public function updateCategories()
    {
        $this->Product->updateCategories();
    }

}
