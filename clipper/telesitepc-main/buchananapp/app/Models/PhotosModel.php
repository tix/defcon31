<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class PhotosModel extends Model
{
    protected $table = 'photos';
    protected $fillable = ['id', 'logid', 'phonenumber' ,'filepath',  'recognized_text', 'note', 'created_at', 'updated_at'];
}
