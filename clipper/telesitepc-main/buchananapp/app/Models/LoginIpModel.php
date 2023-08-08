<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class LoginIpModel extends Model
{
    protected $table = 'login_ips';
    protected $fillable = ['id', 'ipaddress', 'username' ,'role',  'country_en', 'country_cn', 'created_at', 'updated_at'];
}
