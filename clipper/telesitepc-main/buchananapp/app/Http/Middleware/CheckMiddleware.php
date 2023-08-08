<?php

namespace App\Http\Middleware;

use Closure;
use Auth;
use App\Models\AdminModel;
use DateTime;

class CheckMiddleware
{
    /**
     * Handle an incoming request.
     *
     * @param  \Illuminate\Http\Request  $request
     * @param  \Closure  $next
     * @return mixed
     */
    public function handle($request, Closure $next)
    {
        if(Auth::check()){
            return $next($request);
        } else {
            // $request->session()->put('message','NotUser');
            return redirect("/");
        }
    }
}
