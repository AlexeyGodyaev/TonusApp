<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Parser extends CI_Controller {

	public function __construct()
	{
		parent::__construct();
	}

    public function parse()
    {
       $data = file_get_contents('http://www.calorizator.ru/product/all');

    $dom = new domDocument;

    @$dom->loadHTML($data);
    $dom->preserveWhiteSpace = false;
    $tables = $dom->getElementsByTagName('table');

    $rows = $tables->item(1)->getElementsByTagName('tr');

    foreach ($rows as $row) {
        $cols = $row->getElementsByTagName('td');
        echo $cols[2];
        }
    }

}
