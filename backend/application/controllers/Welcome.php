<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Welcome extends CI_Controller {

	public function calc()
	{
		$a = $this->input->post('a');
		$b = $this->input->post('b');
		$c = $a + $b;
		echo "Ответ: $c";
	}

	public function index()
	{
		echo "Здорово!";
	}
}
