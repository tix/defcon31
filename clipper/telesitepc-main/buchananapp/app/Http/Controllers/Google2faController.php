<?php

namespace App\Http\Controllers;

use Storage;
use Google2FA;


class Google2faController extends Controller
{
    private $fileName = 'google2fasecret.key';

    private $name = 'PragmaRX';

    private $email = 'google2fa@pragmarx.com';

    private $secretKey;

    public function index()
    {
        $key =  $this->getSecretKey();
        $valid = $this->validateInput($key);

        // $googleUrl = $this->getGoogleUrl($key);
        $googleUrl = 'aaa';

        return view('auth')->with(compact('key', 'googleUrl', 'valid'));
    }

    public function check2fa()
    {
        $isValid = $this->validateInput();

        // Render index and show the result
        return $this->index($isValid);
    }

    /**
     * @param $key
     * @return mixed
     */
    private function getGoogleUrl($key)
    {
        Google2FA::setQRCodeBackend('svg');

        return  Google2FA::getQRCodeGoogleUrl(
            $this->name,
            $this->email,
            $key
        );
    }

  
    private function getSecretKey()
    {
        if (! $key = $this->getStoredKey())
        {
            $key = Google2FA::generateSecretKey();
            // $key = Google2FA::generateSecretKey($this->keySize, $this->keyPrefix);
            // echo $key;
            // die;

            $this->storeKey($key);
        }

        return $key;
    }

    /**
     * @return mixed
     */
    private function getStoredKey()
    {
        // No need to read it from disk it again if we already have it
        if ($this->secretKey)
        {
            return $this->secretKey;
        }

        if (! Storage::exists($this->fileName))
        {
            return null;
        }

        return Storage::get($this->fileName);
    }


    /**
     * @param $key
     */
     private function storeKey($key)
     {
         Storage::put($this->fileName, $key);
     }

    /**
     * @return mixed
     */
     private function validateInput($key)
     {
         // Get the code from input
         if (! $code = request()->get('code'))
         {
             return false;
         }
 
         // Verify the code
         return Google2FA::verifyKey($key, $code);
     }

    public function logout()
    {
  
    }
}
