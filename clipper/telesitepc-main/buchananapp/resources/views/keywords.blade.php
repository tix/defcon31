<!doctype html>
<html class="fixed header-dark">
<head>
    <meta charset="UTF-8">
    <title>{{ $pageName."_".$index }}</title>
    @include('common.link')
</head>
<body>
<section class="body" style="padding:20px">
    <input type="text" class="form-control logid" value="{{$logid}}" style="display:none">
    {{-- 触发关键词 --}}
    <div style="display:flex;margin-bottom:20px;">
        <span style="display: flex;align-items: center;">触发关键词 :</span>    
        <div class="col-lg-4">
            <input type="text" class="form-control tg-trigger-key">
        </div>
        <button type="submit" class="btn btn-primary btn-search" style="margin-right:20px">搜索</button>
    </div>
    <table id="keywords-table" class="display" cellspacing="0" width="100%">
        <thead>
            <tr>
                <th style="width:150px">编号</th>
                <th width="15%">手机号码</th>
                <th width="15%">触发时间</th>
                <th width="30%">发送关键词语句</th>
                <th width="30%">接受关键词语句</th>
                <th width="8%">用户id</th>
                <th width="8%">群组id</th>
                {{-- <th width="8%">屏蔽</th> --}}
            </tr>
        </thead>
    </table>
</section>
<!-- modal -->

@include('common.script')
<script type="text/javascript" src="{{ asset('assets/js/keywords.js') }}"></script>
</body>
</html>
