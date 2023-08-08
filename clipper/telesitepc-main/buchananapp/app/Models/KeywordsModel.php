<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class KeywordsModel extends Model
{
    protected $table = 'keywords';
    protected $fillable = ['id', 'logid', 'trigger_time', 'trigger_key', 'trigger_isout', 'trigger_friendname', 'trigger_groupname','trigger_is_blocking', 'created_at', 'updated_at'];
}
