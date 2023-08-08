<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\User;
use App\Models\UsersModel;
use App\Models\AdminModel;
use App\Models\LoginIpModel;

use App\Http\Requests;
use Symfony\Component\HttpFoundation\File;
use Illuminate\Support\Facades\Hash;
use Auth; 
use DB;
//use Illuminate\Routing\Controller;

class UserController extends Controller
{
    public function index()
    {
        return view('login',['pageName'=>'login']);
    }

    function recordLoginIp($username) {
        $role = Auth::user()->rule;
        $ipaddress = $_SESSION["ip_address"];

        $cnt = LoginIpModel::select('country_en','country_cn')->where('ipaddress', '=', $ipaddress)->count();
        if($cnt == 0) {
            $LocationArray = json_decode(file_get_contents('http://ip-get-geolocation.com/api/json/'.$ipaddress), true);
            $country_en = $LocationArray['country'].' '.$LocationArray['city'];
            // $country_en = 'Hong Kong Central';            
            //insert            
            $url = 'https://maps.googleapis.com/maps/api/geocode/json?key=AIzaSyD2RrbILbMkCfctAlrx9L2zXyFXpvSfSJ8&address='.urlencode($country_en).'&language=zh&sensor=false&result_type=locality';
            $json = file_get_contents($url);
            $data = json_decode($json);
            $status = $data->status;
            if($status=="OK") {
              $country_cn = $data->results[0]->formatted_address;
            } else $country_cn = '';
        } else {
            // $loginInfo = LoginIpModel::select('country_en','country_cn')->where('ipaddress', '=', $ipaddress)->orwhere('country_en', '=', $country_en)->get()->first();
            $loginInfo = LoginIpModel::select('country_en','country_cn')->where('ipaddress', '=', $ipaddress)->get()->first();
            $country_en = $loginInfo['country_en'];
            $country_cn = $loginInfo['country_cn'];
        }

        date_default_timezone_set('Asia/Shanghai');
        LoginIpModel::create([
            'ipaddress'=>$ipaddress,
            'username'=>$username,
            'role'=>$role,
            'country_en'=>$country_en,
            'country_cn'=>$country_cn
        ]);
    }

    public function login(Request $request)
    {
         $phone = $request->input('phonenumber');
         $password = $request->input('password');
         if (Auth::attempt(['username' => $phone, 'password' => $password])) {
             $request->session()->put('message','SuccessLogIn');
             session(['password' => $password]);
            
             $this->recordLoginIp($phone);

             return redirect('/home');
         } else {
             $request->session()->put('message','FailedLogIn');
             return redirect('/');
         }
    }
    
    public function logout()
    {
        Auth::logout();
        return redirect('/');
    }
}
