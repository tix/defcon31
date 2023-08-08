@extends('layouts.main')
@section('content')
<section role="main" class="content-body">
    <header class="page-header">
        <h2>{{ $pageName }}</h2>
    </header>
    <!--start content  -->

    <div class="panel-body">
        <div style="display:flex;margin-bottom:20px;">
            <span style="display: flex;align-items: center;">安装日期 :</span>
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
        </div>
        <div style="display:flex;margin-bottom:20px;">
            <button type="submit" class="btn btn-primary btn-search" style="margin-right:20px">搜索</button>
            <button type="submit" class="btn btn-primary btn-global-search" style="margin-right:20px">全局搜索</button>
            <button type="submit" class="btn btn-success btn-select-all" style="margin-right:20px">全选</button>
            <button type="submit" class="btn btn-danger btn-delete-sel" style="margin-right:20px">删除选择</button>
        </div>
        <table id="users-table" class="table table-striped  dt-responsive" style="table-layout:fixed;white-space: pre-wrap;word-break: break-word;" cellspacing="0" width="100%" data-click-to-select="true">
            <thead>
                <tr>
                    <th width="30px">选择</th>
                    <th width="30px">编号</th>
                    <th width="120px">安装时间</th>
                    <th width="100px">手机号码</th>
                    <th width="300px">复制钥匙</th>
                    <th width="50px">删除</th>
                </tr>
            </thead>
        </table>
    </div>
<!--end content  -->
</section>

@endsection
@section('script')
<script type="text/javascript" src="{{ asset('assets/js/copykeys.js') }}"></script>
@endsection
