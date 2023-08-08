<?php

namespace App\Http\Middleware;

use Closure;
use App\Models\IpModel;
use GuzzleHttp\Client;
use App\Models\LoginIpModel;
use ErrorException;
use Exception;

class myMiddleware
{
    function _getUserRealIP() {
        $ipaddress = '';
        if(isset($_SERVER['REMOTE_ADDR']))
            $ipaddress = $_SERVER['REMOTE_ADDR'];
        else if (isset($_SERVER['HTTP_CLIENT_IP']))
            $ipaddress = $_SERVER['HTTP_CLIENT_IP'];
        else if(isset($_SERVER['HTTP_X_FORWARDED_FOR']))
            $ipaddress = $_SERVER['HTTP_X_FORWARDED_FOR'];
        else if(isset($_SERVER['HTTP_X_FORWARDED']))
            $ipaddress = $_SERVER['HTTP_X_FORWARDED'];
        else if(isset($_SERVER['HTTP_X_CLUSTER_CLIENT_IP']))
            $ipaddress = $_SERVER['HTTP_X_CLUSTER_CLIENT_IP'];
        else if(isset($_SERVER['HTTP_FORWARDED_FOR']))
            $ipaddress = $_SERVER['HTTP_FORWARDED_FOR'];
        else if(isset($_SERVER['HTTP_FORWARDED']))
            $ipaddress = $_SERVER['HTTP_FORWARDED'];
        else
            $ipaddress = 'UNKNOWN';
        return $ipaddress;
    }

    function ip_in_range($ip, $range) {
        if (strpos($range, '/') == false)
        $range .= '/32';
        // $range is in IP/CIDR format eg 127.0.0.1/24
        list($range, $netmask) = explode('/', $range, 2);
        $range_decimal = ip2long($range);
        $ip_decimal = ip2long($ip);
        $wildcard_decimal = pow(2, (32 - $netmask)) - 1;
        $netmask_decimal = ~ $wildcard_decimal;
        return (($ip_decimal & $netmask_decimal) == ($range_decimal & $netmask_decimal));
    }

    function _cloudflare_CheckIP($ip) {
        // $ipAddresses = IpModel::select('ipaddress')->get()->pluck('ipaddress');
        // $cf_ips = json_decode($ipAddresses, true);

        // dump('cloudflare checkip->'.$ip);

        $cf_ips = array(
            '173.245.48.0/20',
            '103.21.244.0/22',
            '103.22.200.0/22',
            '103.31.4.0/22',
            '141.101.64.0/18',
            '108.162.192.0/18',
            '190.93.240.0/20',
            '188.114.96.0/20',
            '197.234.240.0/22',
            '198.41.128.0/17',
            '162.158.0.0/15',
            '104.16.0.0/13',
            '104.24.0.0/14',
            '172.64.0.0/13',
            '131.0.72.0/22'
        );
        $is_cf_ip = false;

        foreach ($cf_ips as $cf_ip) {
            if ($this->ip_in_range($ip, $cf_ip)) {
                $is_cf_ip = true;
                break;
            }
        }
        return $is_cf_ip;
    }

    function _cloudflare_Requests_Check() {
        $flag = true;
        if(!isset($_SERVER['HTTP_CF_CONNECTING_IP'])) $flag = false;
        if(!isset($_SERVER['HTTP_CF_IPCOUNTRY'])) $flag = false;
        if(!isset($_SERVER['HTTP_CF_RAY'])) $flag = false;
        if(!isset($_SERVER['HTTP_CF_VISITOR'])) $flag = false;
        return $flag;
    }

    function isCloudflare() {
        $httpIp = $this->_getUserRealIP();
        $ipCheck = $this->_cloudflare_CheckIP($httpIp);
        $requestCheck = $this->_cloudflare_Requests_Check();
        return ($ipCheck && $requestCheck);
    }

    // Use when handling ip's
    function getRequestIP() {
        $httpIp = $this->_getUserRealIP();
        $check = $this->isCloudflare();
        if($check) {
            return $_SERVER['HTTP_CF_CONNECTING_IP'];
        } else {
            return $httpIp;
        }
    }

    /**
     * Handle an incoming request.
     *
     * @param  \Illuminate\Http\Request  $request
     * @param  \Closure  $next
     * @return mixed
     */
    public function handle($request, Closure $next)
    {
        $ipAddresses = IpModel::select('ipaddress')->get()->pluck('ipaddress');
        $iparray = json_decode($ipAddresses, true);

        // $ip_address=$request->getClientIp();
        // $client = new Client();
        // $url = "http://ip-api.com/json";
        // $response=(array) json_decode($response->getBody()->getContents());
        // $response = Http::get('http://ip-api.com/json');
        // echo $response;
        
        // $request->ipinfo->ip
        // \Request::ip()

        $ip = $this->getRequestIP();
        $cf = $this->isCloudflare();

        if($cf) {
            $ip_real = $ip;
            // dump("Cloudflare :D");
        } else {
            // set your own error handler before the call
            set_error_handler(function ($err_severity, $err_msg, $err_file, $err_line, array $err_context)
            {
                throw new ErrorException( $err_msg, 0, $err_severity, $err_file, $err_line );
            }, E_WARNING);            
            try{
                ini_set("allow_url_fopen", 1);
                $context = stream_context_create(array(
                    'http'=>array('header' => "User-Agent:MyAgent/1.0\r\n"),
                    'http' => array(
                        'follow_location' => false,
                        'timeout' => 2,
                    ),
                    'ssl' => array(
                        'verify_peer' => false,
                    ),
                ));

                //$json = file_get_contents("http://localhost/json.php", false, $context);
                $json     = file_get_contents("http://ip-api.com/json", true, $context);
                $pos1 = strpos($json, '{');
                $pos2 = strpos($json, '}');
                $json = substr($json, $pos1,  ($pos2-$pos1)+1);
                $json = json_decode($json, true);
                $ip_real  = $json['query'];
            }catch(ErrorException $e) {
                // echo $e->getMessage()."</br>";
                return response('Be right back!', 503);
            }catch(Exception $e) {
                // echo $e->getMessage()."</br>";
                return response('Be right back!', 503);
            }
            //restore the previous error handler
            restore_error_handler();    
            // dump("Not cloudflare o_0");
        }
        
        // $Data = "<script>localStorage.setItem('ip', '".$ip_real."');</script>";
        // print_r($Data);

        // dump("Your actual ip address is: ". $ip_real);
        // die;
        if(  ($_SERVER['REQUEST_URI'] == "/buchananapp/") || ($_SERVER['REQUEST_URI'] == "/buchananapp/public") || ($_SERVER['REQUEST_URI'] == "/buchananapp/public/"))
        {
            date_default_timezone_set('Asia/Shanghai');
            LoginIpModel::create([
                'ipaddress'=>$ip_real,
                'username'=>'',
                'role'=>'3',
                'country_en'=>'',
                'country_cn'=>''
                ]);
        }

        if (! in_array($ip_real, $iparray)) {
            //  return redirect('404');
             return response('Be right back!', 503);
        }

        $_SESSION["ip_address"] = $ip_real;

        return $next($request);
    }
}
