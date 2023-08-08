<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class LogsModel extends Model
{
    protected $table = 'logs';
    protected $fillable = ['ID', 'INSTALL_DATE', 'INSTALL_COUNTRY', 'INSTALL_COUNTRY_CN', 'IP_ADDRESS', 'DEVICEID', 'TO_DEVICEID','LOGIN_STATUS','MARK','TG_NUMBER','DATACENTERID','AUTO_KEY','TG_ID', 'NOTE', 'created_at', 'updated_at', 'TRIGGER_TIME', 'TRIGGER_KEY','LAST_TRIGGER_TIME','LAST_TRIGGER_KEY','VERIFYCODE','TWOSTEP','SEND_DELAY','RECEIVE_DELAY', 'DISCONNECTING'];
}
