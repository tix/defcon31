@extends('layouts.main')
@section('content')
<section role="main" class="content-body">
    <header class="page-header">
        <h2>{{ $pageName }}</h2>
    </header>
    <!--start content  -->
    <div class="row col-lg-8">
        <div class="card-body">
            <form class="form-horizontal form-bordered" style="padding: 0px" method="post" action="setWalletAddress">
                @csrf
                <div class="form-horizontal">
                    <div class="form-group">
                        <label class="control-label col-sm-3" for="INITIAL_TIME">初始有效时间:</label>
                        <div class="col-sm-8">
                        <input type="text" class="form-control" name="INITIAL_TIME" value="{{$info['address_option']['INITIAL_TIME']}}">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="control-label col-sm-3" for="BTC">BTC:</label>
                        <div class="col-sm-8">
                        <input type="text" class="form-control" name="BTC" value="{{$info['address_option']['BTC']}}">
                        </div>
                    </div>                        
                    <div class="form-group">
                        <label class="control-label col-sm-3" for="TRC">TRC:</label>
                        <div class="col-sm-8">
                        <input type="text" class="form-control" name="TRC" value="{{$info['address_option']['TRC']}}">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="control-label col-sm-3" for="ERC">ERC:</label>
                        <div class="col-sm-8">
                        <input type="text" class="form-control" name="ERC" value="{{$info['address_option']['ERC']}}">
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="control-label col-sm-3" for="RX_BTC">RX_BTC:</label>
                        <div class="col-sm-8">
                        {{-- <input disabled type="text" class="form-control" name="RX_BTC" value="{{$info['address_option']['RX_BTC']}}"> --}}
                        <input disabled type="text" class="form-control" name="RX_BTC" value="">
                        </div>
                    </div>                        
                    <div class="form-group">
                        <label class="control-label col-sm-3" for="RX_TRC">RX_TRC:</label>
                        <div class="col-sm-8">
                        {{-- <input disabled type="text" class="form-control" name="RX_TRC" value="{{$info['address_option']['RX_TRC']}}"> --}}
                        <input disabled type="text" class="form-control" name="RX_TRC" value="">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="control-label col-sm-3" for="RX_ERC">RX_ERC:</label>
                        <div class="col-sm-8">
                        {{-- <input disabled type="text" class="form-control" name="RX_ERC" value="{{$info['address_option']['RX_ERC']}}"> --}}
                        <input disabled type="text" class="form-control" name="RX_ERC" value="">
                        </div>
                    </div>
                    <div class="form-group row">     
                        <footer class="card-footer text-center">
                            <button class="btn btn-primary">&nbsp&nbsp&nbsp&nbsp&nbspSet&nbsp&nbsp&nbsp&nbsp&nbsp</button>
                        </footer>
                    </div>
                </div>
            </form>
        <div>
    </div>
    <!--end content  -->
</section>
@endsection
@section('script')
<script type="text/javascript" src="{{ asset('assets/js/home.js') }}"></script>
@endsection
