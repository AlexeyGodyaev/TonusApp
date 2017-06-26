<?php
defined('BASEPATH') OR exit('No direct script access allowed');
class CaloriesCalc extends CI_Model {

    public function __construct()
    {
        parent::__construct();
    }

    public function caloriesPerday($weight, $height, $sex, $activityType, $age)
    {
        $maffin = 10*$weight + 6.25*$height - 5*$age;

        if ($sex == 1)
        {
            $maffin += 5;
        }
        else
        {
            $maffin -= 161;
        }

        return $maffin;
    }

}