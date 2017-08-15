<?php
defined('BASEPATH') OR exit('No direct script access allowed');
//Ключ для отправки push-ведомлений
define( 'API_ACCESS_KEY', 'AAAArq32y8U:APA91bGu4Q2dWi2gt0a44Bh07bgg_yq2hV-dsZNCIQJgRS-fncogzgJJjAkU5Z--LG6gL5CwXUZC344eL0JHa_9OnvfFGsRHL5346fotJNwnFyrTRHPHRo3JMxsm2S2GFmPa4t5Hof75' );

//Работа с пользователем

class User extends CI_Model {

    public function __construct()
    {
        parent::__construct();

        $config = Array(
        'protocol' => 'smtp',
        'smtp_host' => 'ssl://smtp.yandex.ru',
        'smtp_port' => 465,
        'smtp_user' => 'noreply@caloriesdiary.ru', 
        'smtp_pass' => 'Nakamuram390', 
        'mailtype' => 'text',
        'charset' => 'utf-8',
        'wordwrap' => TRUE,
        'newline' => "\r\n",
		'crlf' => "\r\n",
        'mailtype' => 'html'
        );

        $this->load->library('email', $config);
        $this->load->helper('email');
        $this->load->database();
    }

    //Генерация пароля

    public function generatePassword($length = 8)
    {
        $chars = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
        $count = mb_strlen($chars);

        for ($i = 0, $result = ''; $i < $length; $i++) 
        {
            $index = rand(0, $count - 1);
            $result .= mb_substr($chars, $index, 1);
        }

        return $result;
    }

    //Отправка push-уведомлений

    public function sendPush($id, $title, $body)
    {
        $this->db->select('instanceToken');
        $this->db->where('user_id', $id);
        $query = $this->db->get('Users');

        if ($query->num_rows() > 0)
        {
            $msg = array(
                'body'   => $body,
                'title'  => $title,
            );

            foreach ($query->result() as $k) {
                $to = $k->instanceToken;
            }

            $notification= array('title' => $title,'body' => $body );

            $fields = array(
                'to' => $to,
                'notification' => $notification
            );
 
            $headers = array
            (
                'Authorization: key=' . API_ACCESS_KEY,
                'Content-Type: application/json'
            );
 
            $ch = curl_init();
            curl_setopt( $ch,CURLOPT_URL, 'https://fcm.googleapis.com/fcm/send' );
            curl_setopt( $ch,CURLOPT_POST, true );
            curl_setopt( $ch,CURLOPT_HTTPHEADER, $headers );
            curl_setopt( $ch,CURLOPT_RETURNTRANSFER, true );
            curl_setopt( $ch,CURLOPT_SSL_VERIFYPEER, false );
            curl_setopt( $ch,CURLOPT_POSTFIELDS, json_encode( $fields ) );
            $result = curl_exec($ch );
            curl_close( $ch );

            return $result;
        }
    }

    //Вход через google
    public function google_check($id, $username, $email, $instanceToken)
    {
        $query = $this->db->get_where('Users', array('ga_id' => $id, 'email' => $email));

        if ($query->num_rows() > 0)
        {
            $data = array('email' => $email, 'username' => $username);
            $this->db->where('ga_id', $id);
            $query = $this->db->update('Users', $data);
        }
        else
        {
            $query = $this->db->insert('Users', array('ga_id' => $id, 'email' => $email, 'username' => $username, 'password' => $this->generatePassword()));
        }

        if($query)
        {
            $response['status'] = 1;

            $query = $this->db->get_where('Users', array('ga_id' => $id, 'email' => $email));

            $data = array("instanceToken" => $instanceToken);
        
            foreach ($query->result() as $row) 
            {
                $response["user_id"] = $row->user_id;
                $response['email'] = $row->email;
                $response['username'] = $row->username;

                $this->db->where('user_id', $row->user_id);
                $this->db->update('Users', $data);
            }

            $response['msg'] = 'OK';
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Error occured';
        }

        return $response;
    }

    //Класическая авторизация

    public function check($username, $password, $instanceToken)
    {
        if(valid_email($username))
        {
            $this->db->cache_on();
            $query = $this->db->get_where('Users', array('email' => $username, 'password' => $password));
            $this->db->cache_off();
        }
        else
        {
            $this->db->cache_on();
            $query = $this->db->get_where('Users', array('username' => $username, 'password' => $password));
            $this->db->cache_off();
        }
        
        if ($query->num_rows() > 0)
        {  
            $response['status'] = 1;
            $response['msg'] = 'OK';

            $data = array("instanceToken" => $instanceToken);

            foreach ($query->result() as $row) 
            {
                $response["user_id"] = $row->user_id;
                $response['email'] = $row->email;
                $response['username'] = $row->username;

                $this->db->where('user_id', $row->user_id);
            	$this->db->update('Users', $data);
            }
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Неверный логин или пароль';
        }

        return $response;
    }

    //Классическая регистрация 

    public function reg($username, $email, $password)
    {
        $query = $this->db->get_where('Users', array('username' => $username));

        if($query->num_rows() > 0)
        {
            $response['status'] = 0;
            $response['msg'] = 'Имя пользователя уже занято';
        }
        else
        {
            $query = $this->db->get_where('Users', array('email' => $email));

            if($query->num_rows() > 0)
            {
                $response['status'] = 0;
                $response['msg'] = 'Адрес электронной почты занят';
            }
            else
            {
                if(preg_match("/[^A-Za-z0-9]/", $username))
                {
                    $response['status'] = 0;
                    $response['msg'] = 'Логин может содержать только буквы латинского алфавита или цифры';
                }
                else
                {
                    $data = array("username" => $username, "email" => $email, "password" => $password);
                    $query = $this->db->insert("Users", $data);
                    $this->db->cache_delete();

                    if($query)
                    {
                        $response['status'] = 1;
                        $response['msg'] = 'OK';
                    }
                    else
                    {
                        $response['status'] = 0;
                        $response['msg'] = 'Error occured';
                    }
                }
            }

        }   

        return $response;
    }

    //Удаление аккаунта

    public function del($id, $password)
    {
        $query = $this->db->get_where('Users', array('user_id' => $id, 'password' => $password));

        if($query->num_rows() > 0)
        {
            $response['status'] = 1;
            $response['msg'] = 'OK';
            
            $tables = array('user_chars','user_days','user_human_chars');
            $this->db->where('user_id', $id);
            $this->db->delete($tables);
            $this->db->delete('Users', array('user_id' => $id, 'password' => $password));
            $this->db->cache_delete();
            
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Неверное имя пользователя или пароль';
        }

        return $response;
    }

    //Смена пароля

    public function changePassword($username, $oldpassword, $newpassword)
    {
        $query = $this->db->get_where('Users', array('username' => $username, 'password' => $oldpassword));
        
        if($query->num_rows() > 0)
        {
            $response['status'] = 1;
            $response['msg'] = "OK";

            $data = array('password' => $newpassword);
            $this->db->where('username', $username);
            $this->db->update('Users', $data);
            $this->db->cache_delete();
        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Неверное имя пользователя или пароль';
        }

        return $response;
    }

    //Запрос на "Забыли пароль" (высылает новый пароль на почту)
    public function forgot($email)
    {
        $query = $this->db->get_where('Users', array('email' => $email));
        
        if($query->num_rows() > 0)
        {
        	$response['status'] = 1;
            $response['msg'] = 'OK';

            $newpassword = $this->generatePassword();

            $data = array('password' => $newpassword);

            $this->db->where('email', $email);
            $this->db->update('Users', $data);
            $this->db->cache_delete();

            $this->email->from('noreply@caloriesdiary.ru', 'noreply');
            $this->email->to($email);

            $this->email->subject('Ваш новый пароль для аккаунта');

            $this->email->message($this->setLetter($newpassword));

            $this->email->send();

        }
        else
        {
            $response['status'] = 0;
            $response['msg'] = 'Нет пользователя с таким email';
        }

        return $response;
    }

    //Само письмо

    public function setLetter($newpassword)
    {
        return '<!DOCTYPE html>
<html>
  <head>
    <meta name="viewport" content="width=device-width">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title></title>
    <style type="text/css">
    /* -------------------------------------
        INLINED WITH https://putsmail.com/inliner
    ------------------------------------- */
    /* -------------------------------------
        RESPONSIVE AND MOBILE FRIENDLY STYLES
    ------------------------------------- */
    @media only screen and (max-width: 620px) {
      table[class=body] h1 {
        font-size: 28px !important;
        margin-bottom: 10px !important; }
      table[class=body] p,
      table[class=body] ul,
      table[class=body] ol,
      table[class=body] td,
      table[class=body] span,
      table[class=body] a {
        font-size: 16px !important; }
      table[class=body] .wrapper,
      table[class=body] .article {
        padding: 10px !important; }
      table[class=body] .content {
        padding: 0 !important; }
      table[class=body] .container {
        padding: 0 !important;
        width: 100% !important; }
      table[class=body] .main {
        border-left-width: 0 !important;
        border-radius: 0 !important;
        border-right-width: 0 !important; }
      table[class=body] .btn table {
        width: 100% !important; }
      table[class=body] .btn a {
        width: 100% !important; }
      table[class=body] .img-responsive {
        height: auto !important;
        max-width: 100% !important;
        width: auto !important; }}
    /* -------------------------------------
        PRESERVE THESE STYLES IN THE HEAD
    ------------------------------------- */
    @media all {
      .ExternalClass {
        width: 100%; }
      .ExternalClass,
      .ExternalClass p,
      .ExternalClass span,
      .ExternalClass font,
      .ExternalClass td,
      .ExternalClass div {
        line-height: 100%; }
      .apple-link a {
        color: inherit !important;
        font-family: inherit !important;
        font-size: inherit !important;
        font-weight: inherit !important;
        line-height: inherit !important;
        text-decoration: none !important; }
      .btn-primary table td:hover {
        background-color: #34495e !important; }
      .btn-primary a:hover {
        background-color: #34495e !important;
        border-color: #34495e !important; } }
    </style>
  </head>
  <body class="" style="background-color:#f6f6f6;font-family:sans-serif;-webkit-font-smoothing:antialiased;font-size:14px;line-height:1.4;margin:0;padding:0;-ms-text-size-adjust:100%;-webkit-text-size-adjust:100%;">
    <table border="0" cellpadding="0" cellspacing="0" class="body" style="border-collapse:separate;mso-table-lspace:0pt;mso-table-rspace:0pt;background-color:#f6f6f6;width:100%;">
      <tr>
        <td style="font-family:sans-serif;font-size:14px;vertical-align:top;">&nbsp;</td>
        <td class="container" style="font-family:sans-serif;font-size:14px;vertical-align:top;display:block;max-width:580px;padding:10px;width:580px;Margin:0 auto !important;">
          <div class="content" style="box-sizing:border-box;display:block;Margin:0 auto;max-width:580px;padding:10px;">
            <!-- START CENTERED WHITE CONTAINER -->
            <span class="preheader" style="color:transparent;display:none;height:0;max-height:0;max-width:0;opacity:0;overflow:hidden;mso-hide:all;visibility:hidden;width:0;">This is preheader text. Some clients will show this text as a preview.</span>
            <table class="main" style="border-collapse:separate;mso-table-lspace:0pt;mso-table-rspace:0pt;background:#fff;border-radius:3px;width:100%;">
              <!-- START MAIN CONTENT AREA -->
              <tr>
                <td class="wrapper" style="font-family:sans-serif;font-size:14px;vertical-align:top;box-sizing:border-box;padding:20px;">
                  <table border="0" cellpadding="0" cellspacing="0" style="border-collapse:separate;mso-table-lspace:0pt;mso-table-rspace:0pt;width:100%;">
                    <tr>
                      <td style="font-family:sans-serif;font-size:14px;vertical-align:top;">
                        <p style="font-family:sans-serif;font-size:14px;font-weight:normal;margin:0;Margin-bottom:15px;">Здравствуйте,</p>
                        <p style="font-family:sans-serif;font-size:14px;font-weight:normal;margin:0;Margin-bottom:15px;">Ваш пароль для аккаунта: '. $newpassword .' </p>
                        <table border="0" cellpadding="0" cellspacing="0" class="btn btn-primary" style="border-collapse:separate;mso-table-lspace:0pt;mso-table-rspace:0pt;box-sizing:border-box;width:100%;">
                          
                        </table>
                        <p style="font-family:sans-serif;font-size:14px;font-weight:normal;margin:0;Margin-bottom:15px;"></p>
                        <p style="font-family:sans-serif;font-size:14px;font-weight:normal;margin:0;Margin-bottom:15px;"></p>
                      </td>
                    </tr>
                  </table>
                </td>
              </tr>
              <!-- END MAIN CONTENT AREA -->
            </table>
            <!-- START FOOTER -->
            <div class="footer" style="clear:both;padding-top:10px;text-align:center;width:100%;">
              <table border="0" cellpadding="0" cellspacing="0" style="border-collapse:separate;mso-table-lspace:0pt;mso-table-rspace:0pt;width:100%;">
                <tr>
                  <td class="content-block" style="font-family:sans-serif;font-size:14px;vertical-align:top;color:#999999;font-size:12px;text-align:center;">
                    <span class="apple-link" style="color:#999999;font-size:12px;text-align:center;">ООО "Инвольта"</span>
                    <br>
                  </td>
                </tr>
                <tr>
                  <td class="content-block powered-by" style="font-family:sans-serif;font-size:14px;vertical-align:top;color:#999999;font-size:12px;text-align:center;">
                    Powered by <a href="http://htmlemail.io" style="color:#3498db;text-decoration:underline;color:#999999;font-size:12px;text-align:center;text-decoration:none;">HTMLemail</a>.
                  </td>
                </tr>
              </table>
            </div>
            <!-- END FOOTER -->
            <!-- END CENTERED WHITE CONTAINER -->
          </div>
        </td>
        <td style="font-family:sans-serif;font-size:14px;vertical-align:top;">&nbsp;</td>
      </tr>
    </table>
  </body>
</html>';
    }


    //Сохранение информации на сегодня.  

    public function save_day($instanceToken, $id, $mass, $active_sum, $food_sum, $note, $activities, $food, $date, $protein, $fats, $carbs, $day_calories, $left_hand, $right_hand, $breast, $waist, $hiney, $left_thigh, $right_thigh, $calfs, $shoulders)
    {

        $day = array(
            "user_id" => $id,
            "mass" => $mass,
            "active_sum" => $active_sum,
            "food_sum" => $food_sum,
            "note" => $note,
            "activities" => $activities,
            "food" => $food,
            "date" => $date,
            "protein" => $protein,
            "fats" => $fats,
            "carbs" => $carbs,
            "day_calories" => $day_calories
            );

        $this->db->from('Users');
        $this->db->where('user_id', $id);
        $this->db->where('instanceToken', $instanceToken);
        $q = $this->db->get();

        if($q->num_rows() > 0)
        {
            
            $this->db->from('user_days');
            $this->db->where(array('user_id' => $id, 'date' => $date));

            $query = $this->db->get();
        
            if($query->num_rows() > 0)
            {
                foreach ($query->result() as $k) {
                    $day_id = $k->id;
                }

                $human_chars = array(
                    'user_id' => $id,
                    'day_id' => $day_id,
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

                $this->db->where('user_id', $id);
                $this->db->where('id', $day_id);
                $this->db->update('user_days', $day);


                $this->db->where('user_id', $id);
                $this->db->where('day_id', $day_id);
                $this->db->update('user_human_chars', $human_chars);

                $response['status'] = 1;
                $response['msg'] = 'OK'; 
            }
            else
            {
                $this->db->insert('user_days', $day);
                $day_id = $this->db->insert_id('user_days_id_seq');

                $human_chars = array(
                    'user_id' => $id,
                    'day_id' => $day_id,
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

        	   $this->db->insert('user_human_chars', $human_chars);
        	   
                $response['status'] = 1;
                $response['msg'] = 'OK';
            }
    }
    else
    {
        $response['status'] = 0;
        $response['msg'] = 'Доступ запрещён';
    }

        return $response;
    }

    //Получение списка дней от начала и до сегодня

    public function get_days($instanceToken, $id)
    {
        $this->db->from('Users');
        $this->db->where('user_id', $id);
        $this->db->where('instanceToken', $instanceToken);
        $q = $this->db->get();

        $days = array();
        $human_chars = array();

        if($q->num_rows() > 0)
        {

    	    $this->db->from('user_days');
            $this->db->where('user_id', $id);
            $this->db->order_by('date', 'ASC');
            $query = $this->db->get();

            if($query->num_rows() > 0)
            {
                $response['status'] = 1;

                foreach ($query->result() as $k)
                {

    		      $this->db->from('user_human_chars');
        	      $this->db->where('user_id', $id);
        	      $this->db->where('day_id', $k->id);
        	      $queryHuman = $this->db->get();

        	       foreach ($queryHuman->result() as $a)
        	       {
            		
                        $days[] =
                        array(
                        "mass" => $k->mass,
                        "active_sum" => $k->active_sum,
                        "food_sum" => $k->food_sum,
                        "note" => $k->note,
                        "active" => $k->activities,
                        "food" => $k->food,
                        "date" => date_format( date_create($k->date), 'Y-m-d'),
                        "protein" => $k->protein,
                        "fats" => $k->fats,
                        "carbs" => $k->carbs,
                        "day_calories" => $k->day_calories,

                        'lHand' => $a->left_hand,
                        'rHand' => $a->right_hand,
                        'chest' => $a->breast,
                        'waist' => $a->waist,
                        'butt' => $a->hiney,
                        'lLeg' => $a->left_thigh,
                        'rLeg' => $a->right_thigh,
                        'calves' => $a->calfs,
                        'shoulders' => $a->shoulders
                        );
                    }

            }

                $response['days'] = $days;
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