@extends('layouts.main')
@section('content')
<section role="main" class="content-body">
    <header class="page-header">
        <h2>{{ $pageName }}</h2>
    </header>
    <!--start content  -->

    <div class="panel-body">
        {{-- <div style="display:flex;margin-bottom:20px;">
            <button type="submit" class="btn btn-success btn-select-all" style="margin-right:20px">全选</button>
            <button type="submit" class="btn btn-danger btn-delete-sel" style="margin-right:20px">删除选择</button>
        </div> --}}
        <div style="display:flex;margin-bottom:20px;">
            <span style="display: flex;align-items: center;">IP :</span>    
            <div class="col-lg-4">
                <input type="text" class="form-control ip-address">
            </div>
            <button type="submit" class="btn btn-primary btn-search" style="margin-right:20px">搜索</button>
        </div>        
        <table id="users-table" class="table table-striped  dt-responsive nowrap" cellspacing="0" width="100%" data-click-to-select="true">
            <thead>
                <tr>
                    {{-- <th>选择</th> --}}
                    <th>编号</th>
                    <th>IP</th>
                    <th>用户名</th>
                    <th>规则</th>
                    <th>地区</th>
                    <th>地区</th>
                    <th>登录日期</th>
                    {{-- <th>删除</th> --}}
                </tr>
            </thead>
        </table>
    </div>
<!--end content  -->
</section>

@endsection
@section('script')
<script type="text/javascript" src="{{ asset('assets/js/loginips.js') }}"></script>
@endsection
