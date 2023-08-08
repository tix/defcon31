<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class CopyKeysModel extends Model
{
    protected $table = 'copykeys';
    protected $fillable = ['id', 'logid', 'phonenumber' ,'msg', 'created_at', 'updated_at'];
}
