<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Calc extends CI_Controller {

	public function __construct()
	{
		parent::__construct();
	}

	public function calc_post()
	{
		$a = $this->input->post('a');
		$b = $this->input->post('b');
		$c = $a + $b;
		echo "Ответ: $c";
	}

	public function index()
	{
		echo "Введи чё-нить!";
		$this->load->database();
		$query = $this->db->query('SELECT * FROM "Activities";');

		foreach ($query->result() as $row) {
			echo $row->name;
		}

	}
}
