<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Aux {

	public function __construct()
	{
		parent::__construct();
	}

	public function is_json($string) {
        json_decode($string);
        return (json_last_error() == JSON_ERROR_NONE);
    }
}
