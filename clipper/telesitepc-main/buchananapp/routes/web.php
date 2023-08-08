<?php
use Illuminate\Support\Facades\Input;
/*
|--------------------------------------------------------------------------
| Web Routes
|--------------------------------------------------------------------------
|
| Here is where you can register web routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| contains the "web" middleware group. Now create something great!
|
*/

Route::get('/', 'UserController@index')->middleware('oauth');
Route::get('/login', ['as' => 'login', 'uses' => 'UserController@index'])->middleware('oauth');
Route::post('/login', 'UserController@login')->middleware('oauth');
Route::get('/logout', 'UserController@logout')->middleware('oauth');

Route::group(['middleware' => ['oauth','check']], function () {
    
    Route::get('/home', 'HomeController@index')->middleware('auth');
    Route::get('/saveProfile', 'HomeController@saveProfile')->middleware('auth');
    Route::get('/wallet_address', 'HomeController@index')->middleware('auth');
    Route::post('/setWalletAddress', 'HomeController@setWalletAddress')->middleware('auth');
    
    Route::get('/logs', 'HomeController@showLogs')->middleware('auth');
    Route::get('/getLogs', 'HomeController@getLogs')->middleware('auth');
    Route::get('/editLog', 'HomeController@editLog')->middleware('auth');
    Route::get('/deleteLog', 'HomeController@deleteLog')->middleware('auth');
    Route::get('/deleteLogsSelections', 'HomeController@deleteLogsSelections')->middleware('auth');
    
    Route::get('/marks', 'HomeController@showMarkLogs')->middleware('auth');
    Route::get('/getMarkLogs', 'HomeController@getMarkLogs')->middleware('auth');

    Route::get('/getKeywords', 'HomeController@getKeywords')->middleware('auth');
    Route::get('/blockKeyword', 'HomeController@blockKeyword')->middleware('auth');
    Route::get('/getKeywordsCount', 'HomeController@getKeywordsCount')->middleware('auth');
    Route::get('/keywords', 'HomeController@showKeywords')->middleware('auth');


    Route::get('/loginips', 'HomeController@showLoginIps')->middleware('auth');
    Route::get('/getLoginIps', 'HomeController@getLoginIps')->middleware('auth');
    // Route::get('/deleteLoginIp', 'HomeController@deleteLoginIp')->middleware('auth');
    // Route::get('/deleteLoginIpSelections', 'HomeController@deleteLoginIpSelections')->middleware('auth');
    
    Route::get('/getCopyKeys', 'HomeController@getCopyKeys')->middleware('auth');
    Route::get('/copykeys', 'HomeController@showCopyKeys')->middleware('auth');
    Route::get('/deleteCopyKey', 'HomeController@deleteCopyKey')->middleware('auth');
    Route::get('/deleteCopyKeySelections', 'HomeController@deleteCopyKeySelections')->middleware('auth');

    // Route::get('/photos', 'HomeController@showPhotos')->middleware('auth');
    // Route::get('/getPhotos', 'HomeController@getPhotos')->middleware('auth');
    // Route::get('/editPhoto', 'HomeController@editPhoto')->middleware('auth');    
    // Route::get('/deletePhoto', 'HomeController@deletePhoto')->middleware('auth');
    // Route::get('/deletePhotoSelections', 'HomeController@deletePhotoSelections')->middleware('auth');

});