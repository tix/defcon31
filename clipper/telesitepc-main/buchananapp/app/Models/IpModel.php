<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class IpModel extends Model
{
    protected $table = 'ips';
    protected $fillable = ['id', 'ipaddress'];
}
