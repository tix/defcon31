<!doctype html>
<html class="fixed dark">
<head>
    <title>LogIn</title>
    @include('common.link')
</head>
<body>
<!-- start: page -->
<section class="body-sign">
    <div class="center-sign">
        <div class="logo pull-left">
            <img src="assets/images/logo.png" height="54" alt="Porto Admin" />
            <span class="logotitle h2"><strong>{{ env('APP_NAME') }}</strong></span>
        </div>

        <div class="panel panel-sign">
            <div class="panel-title-sign mt-xl text-right">
                <h2 class="title text-uppercase text-weight-bold m-none"><i class="fa fa-user mr-xs"></i> Login</h2>
            </div>
            <div class="panel-body">
                <form action="{{url('login')}}" method="post" id="myform">
                    @csrf
                    <div class="form-group mb-lg">
                        <label>Username</label>
                        <section class="form-group-vertical">
                            <div class="input-group input-group-icon">
                                <!-- <span class="input-group-addon" >
                                    <span class="icon icon-lg" style="font-size:14px;padding-top:12px;font-weight:normal;">+1</span>
                                </span> -->
                                <input name="phonenumber" type="text" class="form-control input-lg" autofocus/>
                                <span class="input-group-addon">
                                    <span class="icon icon-lg">
                                        <i class="fa fa-user"></i>
                                    </span>
                                </span>
                            </div>
                        </section>
                    </div>

                    <div class="form-group mb-lg">
                        <div class="clearfix">
                            <label class="pull-left">Password</label>
                        </div>
                        <div class="input-group input-group-icon">
                            <input name="password" type="password" class="form-control input-lg" />
                            <span class="input-group-addon">
								<span class="icon icon-lg">
									<i class="fa fa-lock"></i>
								</span>
							</span>				
                        </div>
                    </div>

                    <div class="text-center">
                        <button type="submit" class="btn btn-primary hidden-xs btn-login">Log In</button>
                        <button type="submit" class="btn btn-primary btn-block btn-lg visible-xs mt-lg btn-login">Log In</button>
                    </div>
                    <!-- <p class="text-center" style="margin-top: 10px">Don't have an account yet? <a href="{{ url('register') }}">Sign Up!</a></p> -->

                </form>
            </div>
        </div>

        <p class="text-center text-muted mt-md mb-md">&copy; Full Company 2022.</p>
    </div>
</section>
<!-- end: page -->
@include('common/script')
<script type="text/javascript" src="{{ asset('assets/js/login.js')}}"></script>
</body>
</html>
