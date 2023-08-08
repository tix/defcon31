<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class AddressModel extends Model
{
    protected $table = 'address';
    protected $fillable = ['BTC' ,'TRC',  'ERC', 'RX_BTC', 'RX_TRC', 'RX_ERC', 'INITIAL_TIME'];
}
